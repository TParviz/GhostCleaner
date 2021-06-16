package com.ghostcleaner.screen.main

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ghostcleaner.Preferences
import com.ghostcleaner.R
import com.ghostcleaner.REQUEST_ADS
import com.ghostcleaner.screen.PowerActivity
import com.ghostcleaner.screen.base.BaseFragment
import com.ghostcleaner.screen.main.GlobalClickedVars.Companion.BTN_BATTERY_CLICKED
import com.ghostcleaner.service.AdmobClient
import com.ghostcleaner.service.BatteryMode
import com.ghostcleaner.service.EnergyManager
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds
import com.yandex.metrica.YandexMetrica
import kotlinx.android.synthetic.main.activity_change_done.*
import kotlinx.android.synthetic.main.fragment_battery.*
//import kotlinx.android.synthetic.main.fragment_battery.tv_time
import kotlinx.android.synthetic.main.fragment_battery_one.*
import org.jetbrains.anko.intentFor
import org.jetbrains.anko.textColorResource

@Suppress("DEPRECATION")
@SuppressLint("SetTextI18n")
class BatteryFragment : BaseFragment<Int>(), View.OnClickListener {

    override var titleKey = "titleBattery"

    private lateinit var energyManager: EnergyManager
    lateinit var mAdView11 : AdView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        energyManager = EnergyManager(requireContext())
        lifecycle.addObserver(energyManager)
    }

    override fun onCreateView(inflater: LayoutInflater, root: ViewGroup?, bundle: Bundle?): View {
//        return inflater.inflate(R.layout.fragment_battery, root, false)
        return inflater.inflate(R.layout.fragment_battery_one, root, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        pb_outer1.setOnClickListener(this)
//        pb_outer2.setOnClickListener(this)
//        pb_outer3.setOnClickListener(this)

        btn_normal_click.setOnClickListener(this)
        btn_ultra_click.setOnClickListener(this)
        btn_extreme_click.setOnClickListener(this)

        mAdView11 = view.findViewById(R.id.ads_banner_11)

        val preffs = Preferences(requireContext())
        if (preffs.enableAds) {
            MobileAds.initialize(requireActivity()) {}
            val adRequest = AdRequest.Builder().build()
            mAdView11.loadAd(adRequest)
            lifecycle.addObserver(AdmobClient.getInstance(requireContext()))

        }


        energyManager.batteryChanges.observe(viewLifecycleOwner, {
            onOptimize(it)
        })
    }

    override fun onOptimize(value: Int) {
//        tv_percent.text = "$value%"
//        afterOptimize()
//        tv_time1.text = energyManager.getBatteryTime(BatteryMode.NORMAL)
//        tv_time2.text = energyManager.getBatteryTime(BatteryMode.ULTRA)
//        tv_time3.text = energyManager.getBatteryTime(BatteryMode.EXTREME)
        pb_main_progress.progress = value
        tv_percent_pb.text = "$value%"
        afterOptimize()
//        tv_normal_time.text = energyManager.getBatteryTime(BatteryMode.NORMAL)
//        tv_ultra_time.text = energyManager.getBatteryTime(BatteryMode.ULTRA)
//        tv_extreme_time.text = energyManager.getBatteryTime(BatteryMode.EXTREME)


    }

    override fun afterOptimize() {
//        tv_time.text = energyManager.getBatteryTime()
        tv_time_main.text = energyManager.getBatteryTime()

    }

    override fun onClick(v: View?) {
        context?.let {
            if (energyManager.checkPermission(it.applicationContext, this)) {
                val mode = when (v?.id) {
//                    R.id.pb_outer2 -> BatteryMode.ULTRA
//                    R.id.pb_outer3 -> BatteryMode.EXTREME
                    R.id.btn_ultra_click -> BatteryMode.ULTRA
                    R.id.btn_extreme_click -> BatteryMode.EXTREME
                    else -> BatteryMode.NORMAL
                }
                BTN_BATTERY_CLICKED = true
                YandexMetrica.reportEvent("click_battery")
                startActivityForResult(
                    it.intentFor<PowerActivity>("mode" to mode.name),
                    REQUEST_ADS

                )
            }
        }

    }

    override fun onDestroyView() {
        lifecycle.removeObserver(energyManager)
        super.onDestroyView()
    }

    companion object {

        fun newInstance(): BatteryFragment {
            return BatteryFragment().apply {
                arguments = Bundle().apply {
                }
            }
        }
    }
}