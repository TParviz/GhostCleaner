package com.ghostcleaner.screen.main

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ghostcleaner.Preferences
import com.ghostcleaner.R
import com.ghostcleaner.screen.OfferActivity
import com.ghostcleaner.screen.base.BaseFragment
import com.ghostcleaner.service.AdmobClient
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds
import kotlinx.android.synthetic.main.fragment_menu.*


class MenuFragment: BaseFragment<Int>(){

    lateinit var mAdView12 : AdView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(inflater: LayoutInflater, root: ViewGroup?, bundle: Bundle?): View {

        return inflater.inflate(R.layout.fragment_menu, root, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        showGoogleBanner()

        mAdView12 = view.findViewById(R.id.ads_banner_12)

        val preffs = Preferences(requireContext())
        if (preffs.enableAds) {
            MobileAds.initialize(requireActivity()) {}
            val adRequest = AdRequest.Builder().build()
            mAdView12.loadAd(adRequest)
            lifecycle.addObserver(AdmobClient.getInstance(requireContext()))
        }


        btn_share.setOnClickListener {
            shareIt()
        }

        btn_rate.setOnClickListener {
            val uri: Uri = Uri.parse("market://details?id=com.WhiteWolfStudio.GhostCleaner2")
            val goToMarket = Intent(Intent.ACTION_VIEW, uri)
            // To count with Play market back stack, After pressing back button,
            // to taken back to our application, we need to add following flags to intent.
            goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY or
                    Intent.FLAG_ACTIVITY_NEW_DOCUMENT or
                    Intent.FLAG_ACTIVITY_MULTIPLE_TASK)
            try {
                startActivity(goToMarket)
            } catch (e: ActivityNotFoundException) {
                startActivity(Intent(Intent.ACTION_VIEW,
                        Uri.parse("http://play.google.com/store/apps/details?id=com.WhiteWolfStudio.GhostCleaner2")))
            }
        }

        btn_contact.setOnClickListener {
            callSendMeMail()
        }


        if (!preffs.enableAds) {
            btn_subscribe.visibility = View.GONE
        }

        btn_subscribe.setOnClickListener {
            val intent = Intent(requireContext(), OfferActivity::class.java)
            startActivity(intent)
        }

    }

    private fun callSendMeMail() {
        val Email = Intent(Intent.ACTION_SEND)
        Email.type = "text/email"
        Email.putExtra(Intent.EXTRA_EMAIL, arrayOf("tdgamedev7@gmail.com"))
        Email.putExtra(Intent.EXTRA_SUBJECT, "Feedback")
        startActivity(Intent.createChooser(Email, "Send mail to Developer:"))
    }

    private fun showGoogleBanner(){
        val mAdView7: AdView = view!!.findViewById(R.id.ads_banner_7)
        val preffs = Preferences(requireContext())
        if (!preffs.enableAds) {
            mAdView7.visibility = View.GONE
//            mAdView5 = findViewById(R.id.ads_banner_5)
        } else {
            MobileAds.initialize(requireContext())
            val adRequest = AdRequest.Builder().build()
            mAdView7.loadAd(adRequest)
        }
    }

    private fun shareIt() {
        val sharingIntent = Intent(Intent.ACTION_SEND)
        sharingIntent.type = "text/plain"
        val shareBody = "Use this app to optimize your phone. http://play.google.com/store/apps/details?id=com.WhiteWolfStudio.GhostCleaner2"
        sharingIntent.putExtra(Intent.EXTRA_SUBJECT, "Ghost Cleaner")
        sharingIntent.putExtra(Intent.EXTRA_TEXT, shareBody)
        context!!.startActivity(Intent.createChooser(sharingIntent, "Share via"))
    }


    companion object {
        fun newInstance(): MenuFragment {
            return MenuFragment().apply {
                arguments = Bundle().apply {
                }
            }
        }
    }

}