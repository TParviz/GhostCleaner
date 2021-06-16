package com.ghostcleaner.screen.main

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.opengl.Visibility
import android.os.Bundle
import android.text.format.MyFormatter
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import com.ghostcleaner.*
import com.ghostcleaner.extension.areGranted
import com.ghostcleaner.extension.setTintCompat
import com.ghostcleaner.screen.OfferActivity
import com.ghostcleaner.screen.ScanningActivity
import com.ghostcleaner.screen.base.BaseFragment
import com.ghostcleaner.screen.main.GlobalClickedVars.Companion.BTN_JUNK_CLICKED
import com.ghostcleaner.service.AdmobClient
import com.ghostcleaner.service.D
import com.ghostcleaner.service.JunkManager
import com.ghostcleaner.view.CircleBar
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds
import com.yandex.metrica.YandexMetrica
import kotlinx.android.synthetic.main.fragment_junk.*
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.launch
import org.jetbrains.anko.intentFor
import org.jetbrains.anko.textColorResource


@Suppress("DEPRECATION")
@SuppressLint("SetTextI18n")
class JunkFragment : BaseFragment<Int>() {

    override var titleKey = "titleJunk"

    private lateinit var junkManager: JunkManager
    lateinit var sharedPreferences: SharedPreferences
    var isOpenedPermission = false
    lateinit var mAdView9 : AdView

    private val circleBar by lazy { CircleBar(pb_outer, pb_inner) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        junkManager = JunkManager(requireContext())

    }

    override fun onCreateView(inflater: LayoutInflater, root: ViewGroup?, bundle: Bundle?): View {

        return inflater.inflate(R.layout.fragment_junk, root, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        val myInt = arguments!!.getInt("storageask", 0)
//        Log.d("TAG_ERROR", "STORAGE ASK: " + myInt)

        mAdView9 = view.findViewById(R.id.ads_banner_9)

        val preffs = Preferences(requireContext())
        if (preffs.enableAds) {
            MobileAds.initialize(requireActivity()) {}
            val adRequest = AdRequest.Builder().build()
            mAdView9.loadAd(adRequest)
            lifecycle.addObserver(AdmobClient.getInstance(requireContext()))
        }
        //add this in the next Update App(deleting banner after subscription)
        else {
            linearButton.visibility = View.GONE
        }

        sharedPreferences = this.activity!!.getSharedPreferences("SHARED_PREF", Context.MODE_PRIVATE)
        isOpenedPermission = sharedPreferences.getBoolean("permissionask", false)

        if (isOpenedPermission) {
            if (context?.areGranted(Manifest.permission.READ_EXTERNAL_STORAGE) != true) {
                requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), REQUEST_READ)
            }
        }

        btn_clean.setOnClickListener {
            BTN_JUNK_CLICKED = true
            YandexMetrica.reportEvent("click_clean")
            context?.let {
                if (junkManager.checkPermission(it.applicationContext, this)) {
                    startActivityForResult(
                        it.intentFor<ScanningActivity>("junk" to true),
                        REQUEST_ADS
                    )
                }
            }
        }

        btn_remove_banner.setOnClickListener {
            linearButton.visibility = View.GONE
        }

        linearButton.setOnClickListener {
            val intent = Intent(requireContext(), OfferActivity::class.java)
            startActivity(intent)
        }

        if (BuildConfig.DEBUG) {
            btn_cleaned.setOnClickListener {
                btn_clean?.performClick()
            }
        }
        beforeOptimize()
        if (BTN_JUNK_CLICKED){
            afterOptimize()
        }
    }

    override fun setUserVisibleHint(isVisible: Boolean) {
        super.setUserVisibleHint(isVisible)
        if (isVisible && isAdded) {
            if (context?.areGranted(Manifest.permission.READ_EXTERNAL_STORAGE) != true) {
                requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), REQUEST_READ)
            }
        }
    }

    override fun beforeOptimize() {
        readSizes(true)
        circleBar.progress = 0f
        iv_temperature.drawable?.setTintCompat(resources.getColor(R.color.colorRed))
        tv_size.textColorResource = R.color.colorRed
        btn_clean.isVisible = true
        btn_cleaned.isInvisible = true
    }

    override fun afterOptimize() {
        readSizes(false)
        circleBar.progress = 100f
        iv_temperature.drawable?.setTintCompat(resources.getColor(R.color.colorTeal))
        tv_size.text = D["junkClear"]
        tv_size.textColorResource = R.color.colorTeal
        btn_clean.isInvisible = true
        btn_cleaned.isVisible = true
    }

    private fun readSizes(withAll: Boolean) {
        if (context?.areGranted(Manifest.permission.READ_EXTERNAL_STORAGE) == true) {
            job.cancelChildren()
            launch {
                val sizes = junkManager.getFileSizes(withAll)
                if (withAll) {
                    val allCount = sizes.run { first + second + third + fourth }
                    tv_size.text = MyFormatter.formatFileSize(context, allCount).replace(" ", "")
                }
                tv_value1.text = MyFormatter.formatFileSize(context, sizes.first).replace(" ", "")
                tv_value2.text = MyFormatter.formatFileSize(context, sizes.second).replace(" ", "")
                tv_value3.text = MyFormatter.formatFileSize(context, sizes.third).replace(" ", "")
                tv_value4.text = MyFormatter.formatFileSize(context, sizes.fourth).replace(" ", "")
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            REQUEST_READ -> readSizes(true)
            REQUEST_WRITE -> btn_clean?.performClick()
        }
    }

    companion object {
        fun newInstance(): JunkFragment {
            return JunkFragment().apply {
                arguments = Bundle().apply {
                }
            }
        }
    }

//    override fun onBackPressed() {
//        if (AdmobClient.getInstance(applicationContext).showInterstitial()) {
//            finish()
//        }
//    }
//
//    override fun onDestroy() {
//        lifecycle.removeObserver(AdmobClient.getInstance(applicationContext))
//        AdmobClient.getInstance(applicationContext)
//                .hideBanner(cl_done)
//        super.onDestroy()
//
//    }
}