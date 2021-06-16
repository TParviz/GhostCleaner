package com.ghostcleaner.service

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Handler
import androidx.core.content.ContextCompat.startActivity
import androidx.lifecycle.MutableLiveData
import com.anjlab.android.iab.v3.BillingProcessor
import com.anjlab.android.iab.v3.TransactionDetails
import com.ghostcleaner.BuildConfig
import com.ghostcleaner.SKU_ADS
import com.ghostcleaner.SUB_ADS2
import com.ghostcleaner.screen.OfferActivity
import com.ghostcleaner.screen.main.MainActivity
import com.yandex.metrica.YandexMetrica

import org.jetbrains.anko.intentFor

import timber.log.Timber
import java.lang.ref.WeakReference

@Suppress("unused")
class GPayClient private constructor(context: Context) : BillingProcessor.IBillingHandler {

    private val reference = WeakReference(context)

    private val billing = BillingProcessor(context, null, this)

    val purchases = MutableLiveData<String>()

    fun init() {
        if (!billing.isInitialized) {
            billing.initialize()
        }
    }

    fun purchase(activity: Activity, productId: String) {
        if (billing.isOneTimePurchaseSupported) {
            billing.purchase(activity, productId)
//            YandexMetrica.reportEvent("af_purchase")

        }
    }

    fun subscribe(activity: Activity, productId: String) {
        if (billing.isSubscriptionUpdateSupported) {
            billing.subscribe(activity, productId)
//            YandexMetrica.reportEvent("af_subscribe")


        }
    }

    override fun onBillingInitialized() {
        billing.loadOwnedPurchasesFromGoogle()
    }

    override fun onPurchaseHistoryRestored() {
        if (billing.isPurchased(SKU_ADS) || billing.isSubscribed(SUB_ADS2)) {
            reference.get()?.run {
                AdmobClient.getInstance(applicationContext).disableAds()
            }
//            YandexMetrica.reportEvent("af_restored")
        }
    }

    override fun onProductPurchased(id: String, details: TransactionDetails?) {
        if (id == SKU_ADS || id == SUB_ADS2) {
            reference.get()?.run {
                AdmobClient.getInstance(applicationContext).disableAds()
                purchases.postValue(id)
            }
//            YandexMetrica.reportEvent("af_product_purchased")
//            YandexMetrica.reportEvent("af_add_to_card")
        }
    }

    override fun onBillingError(errorCode: Int, error: Throwable?) {
        Timber.e("onBillingError errorCode=$errorCode")
        Timber.e(error)
        if (BuildConfig.DEBUG) {
            onProductPurchased(SKU_ADS, null)
        }
    }

    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        billing.handleActivityResult(requestCode, resultCode, data)
    }

    companion object : Singleton<GPayClient, Context>(::GPayClient)
}