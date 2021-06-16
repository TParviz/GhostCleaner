package com.ghostcleaner.screen.main

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import com.ghostcleaner.BuildConfig
import com.ghostcleaner.Preferences
import com.ghostcleaner.R
import com.ghostcleaner.REQUEST_ADS
import com.ghostcleaner.extension.setTintCompat
import com.ghostcleaner.screen.ScanningActivity
import com.ghostcleaner.screen.base.BaseFragment
import com.ghostcleaner.screen.main.GlobalClickedVars.Companion.BTN_COOLER_CLICKED
import com.ghostcleaner.service.AdmobClient
import com.ghostcleaner.service.CoolManager
import com.ghostcleaner.service.D
import com.ghostcleaner.view.CircleBar
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds
import com.yandex.metrica.YandexMetrica
import kotlinx.android.synthetic.main.activity_change_done.*
import kotlinx.android.synthetic.main.activity_done.*
import kotlinx.android.synthetic.main.fragment_cool.*
import kotlinx.android.synthetic.main.fragment_cool.pb_inner
import kotlinx.android.synthetic.main.fragment_cool.pb_outer
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.launch
import org.jetbrains.anko.intentFor
import org.jetbrains.anko.textColorResource
import org.jetbrains.anko.wrapContent

@Suppress("DEPRECATION")
@SuppressLint("SetTextI18n")
class CoolFragment : BaseFragment<Int>() {

    override var titleKey = "titleCooler"

    private lateinit var coolManager: CoolManager
    lateinit var mAdView10 : AdView


    private val circleBar by lazy { CircleBar(pb_outer, pb_inner) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        coolManager = CoolManager(requireContext())

    }

    override fun onCreateView(inflater: LayoutInflater, root: ViewGroup?, bundle: Bundle?): View {
        return inflater.inflate(R.layout.fragment_cool, root, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mAdView10 = view.findViewById(R.id.ads_banner_10)

        val preffs = Preferences(requireContext())
        if (preffs.enableAds) {
            MobileAds.initialize(requireActivity()) {}
            val adRequest = AdRequest.Builder().build()
            mAdView10.loadAd(adRequest)
            lifecycle.addObserver(AdmobClient.getInstance(requireContext()))
        }


        btn_cool.setOnClickListener {
            context?.let {
                startActivityForResult(it.intentFor<ScanningActivity>(), REQUEST_ADS)
                BTN_COOLER_CLICKED = true
                YandexMetrica.reportEvent("click_cooler")
            }
        }

        if (BuildConfig.DEBUG) {
            btn_cooled.setOnClickListener {
                btn_cool?.performClick()
            }
        }
        beforeOptimize()
        if(BTN_COOLER_CLICKED){
            afterOptimize()
        }
    }



    override fun beforeOptimize() {
        checkTemp((40..45).random())
        circleBar.progress = 0f
        iv_temperature.drawable?.setTintCompat(resources.getColor(R.color.colorRed))
        tv_status.text = D["coolOverheat"]
        tv_status.textColorResource = R.color.colorRed
        btn_cool.isVisible = true
        btn_cooled.isInvisible = true
        tv_bottom.text = D["coolLarge"]

    }

    override fun afterOptimize() {
        checkTemp((35..40).random())
        circleBar.progress = 100f
        iv_temperature.drawable?.setTintCompat(resources.getColor(R.color.colorTeal))
        tv_status.text = D["coolNormal"]
        tv_status.textColorResource = R.color.colorTeal
        btn_cool.isInvisible = true
        btn_cooled.isVisible = true
        tv_bottom.text = D["coolGood"]

    }

    private fun checkTemp(random: Int) {
        job.cancelChildren()
        launch {
            tv_temperature.text = "${coolManager.getCPUTemp(random)}°С"
        }
    }
    companion object {

        fun newInstance(): CoolFragment {
            return CoolFragment().apply {
                arguments = Bundle().apply {
                }
            }
        }
    }

}