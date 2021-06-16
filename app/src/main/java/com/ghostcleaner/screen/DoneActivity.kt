package com.ghostcleaner.screen

import android.R.attr.fragment
import android.R.attr.key
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.Toast
import com.ghostcleaner.Preferences
import com.ghostcleaner.R
import com.ghostcleaner.screen.base.BaseActivity
import com.ghostcleaner.screen.main.GlobalClickedVars.Companion.BTN_BATTERY_CLICKED
import com.ghostcleaner.screen.main.GlobalClickedVars.Companion.BTN_BOOST_CLICKED
import com.ghostcleaner.screen.main.GlobalClickedVars.Companion.BTN_COOLER_CLICKED
import com.ghostcleaner.screen.main.GlobalClickedVars.Companion.BTN_JUNK_CLICKED
import com.ghostcleaner.screen.main.MainActivity
import com.ghostcleaner.service.AdmobClient
import com.google.android.gms.ads.*
import kotlinx.android.synthetic.main.activity_change_done.*
import org.jetbrains.anko.intentFor
import org.jetbrains.anko.textColorResource


class DoneActivity : BaseActivity() {

    lateinit var mAdView2 : AdView
    lateinit var mAdView3 : AdView
    lateinit var mAdView4 : AdView
    lateinit var sharedPreferences: SharedPreferences
    private lateinit var mInterstitialAd1: InterstitialAd


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_done)
        setContentView(R.layout.activity_change_done)

        showGoogleAds()
        afterButtonClicked()


        button_jump_subscribe.setOnClickListener {
            startActivity(intentFor<OfferActivity>())
        }

        button_jump_clean.setOnClickListener {

            val intent = Intent(this, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_TASK_ON_HOME
                putExtra("fragkey", 4)
            }
            startActivity(intent)
        }

        button_jump_boost.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_TASK_ON_HOME
                putExtra("fragkey", 1)
            }
            startActivity(intent)
        }

        button_jump_battery.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_TASK_ON_HOME
                putExtra("fragkey", 2)
            }
            startActivity(intent)
        }

        button_jump_temperature.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_TASK_ON_HOME
                putExtra("fragkey", 3)
            }
            startActivity(intent)
        }

//        showBanner()
        btn_home.setOnClickListener {
           onBackPressed()
            showBannerAfterSec()
        }

        val preffs = Preferences(applicationContext)
        if (!preffs.enableAds){
            afterSubcribe()
        }

/*        mInterstitialAd1 = InterstitialAd(this).apply {
            adUnitId = AD_UNIT_ID
            adListener = (
                    object : AdListener() {
                        override fun onAdLoaded() {
                            Toast.makeText(applicationContext, "onAdLoaded()", Toast.LENGTH_SHORT).show()
                        }

                        override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                            val error = "domain: ${loadAdError.domain}, code: ${loadAdError.code}, " +
                                    "message: ${loadAdError.message}"
                        }
                        override fun onAdClosed() {
                            finish()
                        }
                    }
                    )
        }*/

        MobileAds.initialize(this) {}
        mInterstitialAd1 = InterstitialAd(this)

        mInterstitialAd1.adUnitId = "ca-app-pub-8957338923046618/8619847544"
        val adRequest = AdRequest.Builder().build()
        mInterstitialAd1.loadAd(adRequest)

    }

    override fun onBackPressed() {
        val preffs = Preferences(applicationContext)
        if (preffs.enableAds){
            showInter()
            finish()
        }
        finish()
    }

    override fun onDestroy() {
        lifecycle.removeObserver(AdmobClient.getInstance(applicationContext))
        AdmobClient.getInstance(applicationContext)
            .hideBanner(cl_done)
        super.onDestroy()

    }

    private fun showGoogleAds(){
        val preffs = Preferences(applicationContext)
        mAdView2 = findViewById(R.id.ads_banner_2)
        mAdView3 = findViewById(R.id.ads_banner_3)
        mAdView4 = findViewById(R.id.ads_banner_4)
        if (!preffs.enableAds){
            mAdView2.visibility = View.GONE
            mAdView3.visibility = View.GONE
            mAdView4.visibility = View.GONE

//            val params = ConstraintLayout.LayoutParams(wrapContent, wrapContent).apply {
//                bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID
//                startToStart = ConstraintLayout.LayoutParams.PARENT_ID
//                endToEnd = ConstraintLayout.LayoutParams.PARENT_ID
//            }
//            AdmobClient.getInstance(applicationContext)
//                    .showBanner(cl_done, params)

        } else {
            MobileAds.initialize(this) {}
            val adRequest = AdRequest.Builder().build()
            mAdView2.loadAd(adRequest)
            mAdView3.loadAd(adRequest)
            mAdView4.loadAd(adRequest)
            lifecycle.addObserver(AdmobClient.getInstance(applicationContext))
        }
    }

    fun afterSubcribe(){
        shield_card_iv2.setImageResource(R.drawable.shield_button_blue)
        tv_card_title.textColorResource = R.color.blueOne
        button_jump_subscribe.setTextColor(Color.parseColor("#84F8FF"));
    }

    private fun afterOptimizeBoost() {
        rocket_card_iv2.setImageResource(R.drawable.rocket_button_blue)
        tv_card_title_2.textColorResource = R.color.blueOne
        button_jump_boost.setTextColor(Color.parseColor("#84F8FF"));
    }

    fun afterOptimizeBattery() {
        battery_card_iv2.setImageResource(R.drawable.battery_button_blue)
        tv_card_title_3.textColorResource = R.color.blueOne
        button_jump_battery.setTextColor(Color.parseColor("#84F8FF"));
    }

    private fun afterOptimizeCool() {
        temperature_card_iv2.setImageResource(R.drawable.cool_button_blue)
        tv_card_title_4.textColorResource = R.color.blueOne
        button_jump_temperature.setTextColor(Color.parseColor("#84F8FF"));
    }

    private fun afterOptimizeClean() {
        clean_card_iv2.setImageResource(R.drawable.clean_button_blue)
        tv_card_title_1.textColorResource = R.color.blueOne
        button_jump_clean.setTextColor(Color.parseColor("#84F8FF"));
    }

    private fun afterButtonClicked() {

        //button boost clicked - working
        if(BTN_BOOST_CLICKED){
            AdmobClient.getInstance(applicationContext).showInterstitial()
            afterOptimizeBoost()
        }

        //button battery clicked - working
        if (BTN_BATTERY_CLICKED){
            AdmobClient.getInstance(applicationContext).showInterstitial()
            afterOptimizeBattery()
        }

        //button temperature clicked - working
        if(BTN_COOLER_CLICKED){
            AdmobClient.getInstance(applicationContext).showInterstitial()
            afterOptimizeCool()
        }

        //button trash clicked - working
        if(BTN_JUNK_CLICKED){
            AdmobClient.getInstance(applicationContext).showInterstitial()
            afterOptimizeClean()
        }
    }

    private fun showInter(){
            if (mInterstitialAd1.isLoaded) {
                mInterstitialAd1.show()
            } else {
                Log.d("TAG_ERROR", "The interstitial wasn't loaded yet.")
            }
        }

    private fun showBannerAfterSec() {
        val preffs = Preferences(applicationContext)
        if (preffs.enableAds) {
            val someThread = Runnable {
                startActivity(intentFor<OfferActivity>())
            }
            Handler().postDelayed(someThread, 20000);
        }

    }

//    companion object {
//        const val AD_UNIT_ID = "ca-app-pub-8957338923046618/8619847544"
//    }

}

