package com.ghostcleaner.screen

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.lifecycle.observeFreshly
import com.ghostcleaner.EXTRA_TITLE
import com.ghostcleaner.Preferences
import com.ghostcleaner.R
import com.ghostcleaner.screen.base.BaseActivity
import com.ghostcleaner.service.AdmobClient
import com.ghostcleaner.service.CoolManager
import com.ghostcleaner.service.JunkManager
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds
import kotlinx.android.synthetic.main.activity_scanning.*
import org.jetbrains.anko.intentFor

class ScanningActivity : BaseActivity() {
    lateinit var mAdView4 : AdView


    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scanning)

        if (intent.getBooleanExtra("junk", false)) {
            setSubtitle("titleJunk")

            val manager = JunkManager(applicationContext)

            manager.optimization.observeFreshly(this, {
                showGoogleAds()
                if (it != null) {
                    if (!tv_line1.text.isNullOrBlank()) {
                        if (!tv_line2.text.isNullOrBlank()) {
                            if (!tv_line3.text.isNullOrBlank()) {
                                if (!tv_line4.text.isNullOrBlank()) {
                                    tv_line1.text = tv_line2.text
                                    tv_line2.text = tv_line3.text
                                    tv_line3.text = tv_line4.text
                                    tv_line4.text = "• $it"
                                } else {
                                    tv_line4.text = "• $it"
                                }
                            } else {
                                tv_line3.text = "• $it"
                            }
                        } else {
                            tv_line2.text = "• $it"
                        }
                    } else {
                        tv_line1.text = "• $it"
                    }
                } else {
                    startActivity(intentFor<DoneActivity>(EXTRA_TITLE to "titleJunk"))
                    finish()
//                    val intent = Intent(this, DoneActivity::class.java)
//                    intent.putExtra(EXTRA_TITLE, "titleJunk")
//                    intent.putExtra("junk", 1)
//                    startActivity(intent)
//                    finish()
                }
            })
            manager.optimize()
        } else {
            setSubtitle("titleCooler")
            showGoogleAds()
            val manager = CoolManager(applicationContext)
            manager.optimization.observeFreshly(this, {
                if (it < 0) {
                    startActivity(intentFor<DoneActivity>(EXTRA_TITLE to "titleCooler"))
                    finish()
//                    val intent = Intent(this, DoneActivity::class.java)
//                    intent.putExtra(EXTRA_TITLE, "titleCooler")
//                    intent.putExtra("cooler", 1)
//                    startActivity(intent)
//                    finish()
                }
            })
            manager.optimize()
        }
    }

    private fun showGoogleAds(){
        val preffs = Preferences(applicationContext)
        if (preffs.enableAds) {

            MobileAds.initialize(this)
            mAdView4 = findViewById(R.id.ads_banner_5)
            val adRequest = AdRequest.Builder().build()
            mAdView4.loadAd(adRequest)
            lifecycle.addObserver(AdmobClient.getInstance(applicationContext))

        }

    }
}