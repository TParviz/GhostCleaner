package com.ghostcleaner.screen.main

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.ghostcleaner.Preferences
import com.ghostcleaner.R
import com.ghostcleaner.screen.DoneActivity
import com.google.android.gms.ads.*
import com.yandex.metrica.YandexMetrica


class AppsAdapter(private val context1: Context, private val appInfoList: MutableList<AppInfo>)
    : androidx.recyclerview.widget.RecyclerView.Adapter<AppsAdapter.ViewHolder>() {

    lateinit var mInterstitialAd2: InterstitialAd

    class ViewHolder(view: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(view) {
        var imageView: ImageView = view.findViewById(R.id.imageviewAppManager)
        var textViewAppName: TextView = view.findViewById(R.id.Apk_Name)
        var textViewInstalledOn: TextView = view.findViewById(R.id.installed_On)

/*        var textViewAppPackageName: TextView = view.findViewById(R.id.Apk_Package_Name)
        var textViewAppVersion: TextView = view.findViewById(R.id.app_VersionName)
        var textViewLastUpdateed: TextView = view.findViewById(R.id.last_Updated_Text)*/
        var openAppDetails: Button = view.findViewById(R.id.buttontv_OpenAppDetails)
        var openApps: Button = view.findViewById(R.id.buttontv_OpenApp)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AppsAdapter.ViewHolder {
        val view2 = LayoutInflater.from(context1).inflate(R.layout.card_view_layout, parent, false)
        return ViewHolder(view2)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {

        val applicationPackageName = appInfoList[position].appPackage
        viewHolder.textViewAppName.text = appInfoList[position].appName
//        viewHolder.textViewAppPackageName.text = applicationPackageName

        val uri = appInfoList[position].appDrawableURI
        try {
            if (uri != Uri.EMPTY)
                Glide.with(context1).load(uri).apply(RequestOptions().error(R.drawable.ic_android)).into(viewHolder.imageView)
            else {
                val img = ContextCompat.getDrawable(context1, R.drawable.ic_android)
                viewHolder.imageView.setImageDrawable(img)
            }
        } catch (e: Exception) {
            val img = ContextCompat.getDrawable(context1, R.drawable.ic_android)
            viewHolder.imageView.setImageDrawable(img)
        }
        //interstitialAd init
        mInterstitialAd2 = InterstitialAd(context1).apply {
            adUnitId = AD_UNIT_ID
            adListener = (
                    object : AdListener() {
                        override fun onAdLoaded() {
                            Toast.makeText(context1, "onAdLoaded()", Toast.LENGTH_SHORT).show()

                        }
                        override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                            val error = "domain: ${loadAdError.domain}, code: ${loadAdError.code}, " +
                                    "message: ${loadAdError.message}"
                        }
                        override fun onAdClosed() {

                        }
                    })
        }

        MobileAds.initialize(context1) {}
        mInterstitialAd2 = InterstitialAd(context1)
        mInterstitialAd2.adUnitId = "ca-app-pub-8957338923046618/8619847544"
        val adRequest = AdRequest.Builder().build()
        mInterstitialAd2.loadAd(adRequest)

        viewHolder.textViewInstalledOn.text = appInfoList[position].installedOn

//        viewHolder.textViewAppVersion.text = appInfoList[position].appVersion
//        viewHolder.textViewLastUpdateed.text = appInfoList[position].lastUpdated

        viewHolder.openAppDetails.setOnClickListener {
            try {
                //Open the specific App Info page:
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                intent.data = Uri.parse("package:" + applicationPackageName!!)
                //make report in yandex metrica in this button fow watching clicks
                YandexMetrica.reportEvent("click_manager")
                context1.startActivity(intent)
            } catch (e: ActivityNotFoundException) {
                //e.printStackTrace();
                val intent = Intent(Settings.ACTION_MANAGE_APPLICATIONS_SETTINGS)
                context1.startActivity(intent)
            }
            showInterstitialBanner()
        }

        viewHolder.openApps.setOnClickListener {
            try {
                val intent = context1.packageManager.getLaunchIntentForPackage(applicationPackageName!!)
                if (intent != null) {
                    context1.startActivity(intent)
                } else {
                    Log.d("Exception", "Exception Handled")
                }
            } catch (e: Exception) {
                Log.d("Exception", "Exception Handled")
            }
        }
    }

    override fun getItemCount(): Int {
        return appInfoList.size
    }

    fun updateList(items: List<AppInfo>?) {
        if (items != null) {
            if (items.isNotEmpty()) {
                appInfoList.clear()
                appInfoList.addAll(items)
                notifyDataSetChanged()
            }
        }
    }

    //interstitialAd
    fun showInterstitialBanner(){

        val preffs = Preferences(context1)
        if (preffs.enableAds) {
            if (mInterstitialAd2.isLoaded) {
                mInterstitialAd2.show()
            } else {
                Log.d("TAG_ERROR", "The interstitial wasn't loaded yet.")
            }
        }
    }

    companion object {
        const val AD_UNIT_ID = "ca-app-pub-8957338923046618/8619847544"
    }
}