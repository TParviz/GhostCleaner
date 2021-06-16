package com.ghostcleaner.screen.main

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.text.format.MyFormatter
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.lifecycle.observeFreshly
import com.ghostcleaner.*
import com.ghostcleaner.extension.setTintCompat
import com.ghostcleaner.screen.DoneActivity
import com.ghostcleaner.screen.OfferActivity
import com.ghostcleaner.screen.SplashActivity
import com.ghostcleaner.screen.base.BaseFragment
import com.ghostcleaner.screen.main.GlobalClickedVars.Companion.BTN_BOOST_CLICKED
import com.ghostcleaner.service.AdmobClient
import com.ghostcleaner.service.BoostManager
import com.ghostcleaner.service.D
import com.ghostcleaner.view.CircleBar
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.InterstitialAd
import com.google.android.gms.ads.MobileAds
import com.yandex.metrica.YandexMetrica
import kotlinx.android.synthetic.main.fragment_booster.*
import kotlinx.android.synthetic.main.fragment_booster.pb_inner
import kotlinx.android.synthetic.main.fragment_booster.pb_outer
import kotlinx.android.synthetic.main.fragment_junk.*
import org.jetbrains.anko.backgroundDrawable
import org.jetbrains.anko.intentFor
import org.jetbrains.anko.textColorResource
import kotlin.math.roundToInt

@Suppress("DEPRECATION")
@SuppressLint("SetTextI18n")
class BoosterFragment : BaseFragment<Float>() {

    override var titleKey = "titleBooster"

    private lateinit var boostManager: BoostManager
    lateinit var sharedPreferences: SharedPreferences
    var isOpenInter = false
    lateinit var mAdView8 : AdView


    private val circleBar by lazy { CircleBar(pb_outer, pb_inner) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        boostManager = BoostManager(requireContext())
    }

    override fun onCreateView(inflater: LayoutInflater, root: ViewGroup?, bundle: Bundle?): View {

        return inflater.inflate(R.layout.fragment_booster, root, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        //show inter after splashActivity
        AdmobClient.getInstance(requireContext()).loadInterstitial()
        sharedPreferences = requireContext().getSharedPreferences("SHARED_PREF", Context.MODE_PRIVATE)
        isOpenInter = sharedPreferences.getBoolean("STATEOF_INTER", false)
        val preffs = Preferences(requireContext())


        if(isOpenInter) {
            val someThread = Runnable {
                AdmobClient.getInstance(requireContext()).showInterstitial()
            }
            Handler().postDelayed(someThread, 5000)
            removeShared()
        }

        mAdView8 = view.findViewById(R.id.ads_banner_8)

        if (preffs.enableAds) {

            MobileAds.initialize(requireActivity()) {}
            val adRequest = AdRequest.Builder().build()
//            mAdView = layoutInflater.inflate(R.id.ads_banner, root,true)
            mAdView8.loadAd(adRequest)
            lifecycle.addObserver(AdmobClient.getInstance(requireContext()))

        }
        //add this in the next Update App(deleting banner after subscription)
        else {
            linearButtonBoost.visibility = View.GONE
        }

        btn_optimize.setOnClickListener {
            boostManager.optimize()
            BTN_BOOST_CLICKED = true
            YandexMetrica.reportEvent("click_booster")
        }


        btn_remove_banner_boost.setOnClickListener {
            linearButtonBoost.visibility = View.GONE

        }

        linearButtonBoost.setOnClickListener {
            val intent = Intent(requireContext(), OfferActivity::class.java)
            startActivity(intent)
        }

        if (BuildConfig.DEBUG) {
            btn_optimized.setOnClickListener {
                btn_optimize?.performClick()
            }
        }

        boostManager.optimization.observeFreshly(viewLifecycleOwner, { value ->
            if (value >= 0) {
                onOptimize(value)
            } else {
                context?.let {
//                    GlobalClickedVars.BTN_BOOST_CLICKED == true
//                    AdmobClient.getInstance(requireActivity().applicationContext).showInterstitial()

                    startActivityForResult(
                        it.intentFor<DoneActivity>(EXTRA_TITLE to titleKey),
                        REQUEST_ADS
                    )
//                    val intent = Intent(context, DoneActivity::class.java)
//                    intent.putExtra(EXTRA_TITLE, titleKey)
//                    intent.putExtra("boost", 1)
//                    startActivityForResult(intent, REQUEST_ADS)

                }
            }
        })
        beforeOptimize()
//        AdmobClient.getInstance(requireContext()).showInterstitial()
        if(BTN_BOOST_CLICKED) {
            afterOptimize()
        }
    }


    override fun beforeOptimize() {
        val (used, total) = boostManager.memorySizes
        val percent = 100f * used / total
        fl_clock.backgroundDrawable?.setTintCompat(Color.parseColor("#535353"))
        circleBar.colorInner = R.color.colorRed
        circleBar.progress = percent
        tv_storage.textColorResource = R.color.colorRed
        tv_memory.text = MyFormatter.formatFileSize(context, used)
        tv_memory.textColorResource = R.color.colorAccent
        tv_status.isVisible = true
        tv_status.text = D["boostFound"]
        tv_status.textColorResource = R.color.colorRed
        btn_optimize.isInvisible = false
        tv_scanning.isInvisible = true
        btn_optimized.isInvisible = true
        updateBottom(used, total)
//        rocket_card_iv2.setImageResource(R.drawable.rocket_button_red1)
//        tv_card_title_2.textColorResource = R.color.colorRed2
//        button_jump_boost.setTextColor(Color.parseColor("#FF4656"));
    }

    override fun onOptimize(value: Float) {
        fl_clock.backgroundDrawable?.setTintCompat(resources.getColor(R.color.colorTeal))
        circleBar.colorInner = R.color.colorTeal
        circleBar.progressInner = value
        tv_storage.textColorResource = R.color.colorTeal
        tv_memory.text = D["boostOptimizing"]
        tv_memory.setTextColor(Color.WHITE)
        tv_status.isVisible = true
        tv_status.text = D["boostFound"]
        tv_status.textColorResource = R.color.colorTeal
        btn_optimize.isInvisible = true
        tv_scanning.isInvisible = false
        btn_optimized.isInvisible = true
    }

    override fun afterOptimize() {
        val (used, total) = boostManager.memorySizes
        val percent = 100f * used / total
        fl_clock.backgroundDrawable?.setTintCompat(Color.parseColor("#535353"))
        circleBar.colorInner = R.color.colorRed
        circleBar.progress = percent
        tv_storage.textColorResource = R.color.colorTeal
        tv_memory.text = MyFormatter.formatFileSize(context, used)
        tv_memory.setTextColor(Color.WHITE)
        tv_status.isVisible = false
        btn_optimize.isInvisible = true
        tv_scanning.isInvisible = true
        btn_optimized.isInvisible = false
        updateBottom(used, total)

    }

    private fun updateBottom(used: Long, total: Long) {
        val percent = 100f * used / total
        val count = boostManager.list3rdPartyApps().size
        val ratio = "${MyFormatter.formatFileSize(context, used)}/ ${MyFormatter.formatFileSize(context, total)}"
        tv_ratio1.text = ratio
        tv_ratio2.text = ratio
        tv_count.text = count.toString()
        tv_percent.text = "${percent.roundToInt()}%"
    }
    fun removeShared(){
        sharedPreferences = requireContext().getSharedPreferences("SHARED_PREF", Context.MODE_PRIVATE)
        val editor : SharedPreferences.Editor = sharedPreferences.edit()
        editor.remove("STATEOF_INTER")
        editor.apply()
    }

    companion object {

        fun newInstance(): BoosterFragment {
            return BoosterFragment().apply {
                arguments = Bundle().apply {
                }
            }
        }
    }

}