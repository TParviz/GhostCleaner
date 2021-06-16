package com.ghostcleaner.screen.main

import android.content.Context
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.InterstitialAd

class AdManager(private val ctx: Context) {
    fun createAd() {
        // Create an ad.
        val ad: InterstitialAd = InterstitialAd(ctx)
        ad.setAdUnitId("ca-app-pub-8957338923046618/6303876634")
        val adRequest = AdRequest.Builder().build()

        // Load the interstitial ad.
        ad.loadAd(adRequest)
    }


    companion object {
        // Static fields are shared between all instances.
        private lateinit var ad: InterstitialAd
    }

    init {
        createAd()
    }

    fun getAd(): InterstitialAd {
        return ad
    }
}



//class AdManager(ctx: Context) {
//    private lateinit var mInterstitialAd3: InterstitialAd
//
//    fun AdManager(ctx: Context){
//
//        createAd()
//    }
//    fun createAd() {
//        // Create an ad.
//        mInterstitialAd3 = InterstitialAd(ctx);
//        mInterstitialAd3.adUnitId = "ca-app-pub-8957338923046618/6303876634"
//        val adRequest: AdRequest = AdRequest.Builder().build()
//
//        Log.d("TAG_ERROR", "ADMANAGER SUCCEED")
//        // Load the interstitial ad.
//        mInterstitialAd3.loadAd(adRequest)
//
////        MobileAds.initialize(context) {}
////        mInterstitialAd3 = InterstitialAd(context)
////
////        mInterstitialAd3.adUnitId = "ca-app-pub-8957338923046618/6303876634"
////        val adRequest = AdRequest.Builder().build()
////        mInterstitialAd3.loadAd(adRequest)
//    }
//
//    fun getAd(): InterstitialAd {
//        return mInterstitialAd3
//    }
//
//}