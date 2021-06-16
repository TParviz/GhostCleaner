package com.ghostcleaner.screen

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.core.content.ContentProviderCompat.requireContext
import com.ghostcleaner.Preferences
import com.ghostcleaner.R
import com.ghostcleaner.extension.setTintCompat
import com.ghostcleaner.screen.base.BaseActivity
import com.ghostcleaner.screen.main.AdManager
import com.ghostcleaner.screen.main.MainActivity
import com.ghostcleaner.service.AdmobClient
import com.ghostcleaner.service.D
import com.ghostcleaner.service.GPayClient
import com.github.jorgecastillo.State
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.InterstitialAd
import com.google.android.gms.ads.MobileAds
import kotlinx.android.synthetic.main.activity_splash.*
import org.jetbrains.anko.intentFor


class SplashActivity : BaseActivity() {

    private var state = State.NOT_STARTED
    private val STATEOF_INTER = "inter"
    lateinit var sharedPreferences: SharedPreferences



    @Suppress("DEPRECATION")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        sharedPreferences = getSharedPreferences("SHARED_PREF", Context.MODE_PRIVATE)
        val editor : SharedPreferences.Editor = sharedPreferences.edit()
        editor.putBoolean("STATEOF_INTER", true)
        editor.apply()

        iv_broom.drawable?.setTintCompat(resources.getColor(R.color.colorDarkGray))
        fl_broom.setSvgPath(
                """
            M98.4505 81.4404L108.162 9.72146C108.318 8.57356 108.239 7.40598 107.931 6.2893C107.623 5.17262 107.091 4.13003 106.369 3.22454C105.646 2.31905 104.748 1.56946 103.727 1.02108C102.707 0.472705 101.586 0.136925 100.432 0.0340369C99.2781 -0.0691161 98.1152 0.0627795 97.0138 0.421748C95.9123 0.780716 94.8951 1.3593 94.0236 2.12254C93.152 2.88577 92.4443 3.8178 91.9432 4.86229C91.4421 5.90678 91.158 7.04204 91.108 8.19944L87.9721 80.5056L98.4505 81.4404ZM99.8807 6.19804C100.87 6.28653 101.811 6.66636 102.584 7.28951C103.357 7.91266 103.928 8.75114 104.225 9.69891C104.522 10.6467 104.531 11.6612 104.251 12.6141C103.972 13.567 103.416 14.4156 102.653 15.0525C101.891 15.6894 100.958 16.086 99.9702 16.1922C98.9827 16.2984 97.9859 16.1094 97.1059 15.6491C96.2258 15.1888 95.5021 14.4779 95.0261 13.6062C94.5501 12.7346 94.3432 11.7413 94.4317 10.7521C94.5504 9.42564 95.1911 8.20063 96.213 7.34657C97.2349 6.49252 98.5542 6.07937 99.8807 6.19804Z
            M43.7855 121.009L45.0718 119.036L35.5841 126.73C30.532 130.827 25.1918 134.556 19.6049 137.887L13.6126 141.482L27.0112 142.681C33.6626 136.517 38.5922 128.939 43.7855 121.009Z
            M84.6444 90.201L100.12 91.5832L98.4066 85.2035L87.4648 84.2248L84.6444 90.201Z
            M24.037 122.516C24.3425 119.093 21.8147 116.069 18.391 115.764C14.9672 115.458 11.944 117.986 11.6384 121.41C11.3329 124.834 13.8607 127.857 17.2844 128.162C20.7082 128.468 23.7314 125.94 24.037 122.516Z
            M10.347 112.035C10.5709 109.525 8.71809 107.309 6.2085 107.085C3.69891 106.861 1.48291 108.714 1.25894 111.224C1.03496 113.733 2.88782 115.949 5.39742 116.173C7.90701 116.397 10.123 114.544 10.347 112.035Z
            M47.9993 94.4056C48.2233 91.896 46.3704 89.68 43.8608 89.456C41.3512 89.232 39.1352 91.0849 38.9112 93.5945C38.6873 96.1041 40.5401 98.3201 43.0497 98.544C45.5593 98.768 47.7753 96.9151 47.9993 94.4056Z
            M5.99376 124.733C6.13517 123.149 4.96537 121.75 3.38094 121.608C1.79652 121.467 0.397445 122.637 0.256039 124.221C0.114632 125.806 1.28444 127.205 2.86887 127.346C4.45329 127.488 5.85235 126.318 5.99376 124.733Z
            M100.112 95.2025L83.2422 93.6965L70.2152 99.477C62.8967 102.725 56.5888 108.505 52.0427 115.089C49.7018 118.476 47.4926 121.928 45.2516 125.335C41.1689 131.567 36.9943 137.659 31.829 142.98C29.5192 145.369 27.0049 147.55 24.3147 149.5C23.6862 149.953 23.0524 150.399 22.4132 150.838C21.5663 151.422 19.509 152.808 19.509 152.808L25.2775 153.323L35.1447 154.202C37.373 154.402 39.6185 154.143 41.7429 153.441C43.8673 152.74 45.8255 151.611 47.4966 150.123L59.2454 139.673L53.1972 155.812L61.4865 156.563C63.7148 156.763 65.9603 156.504 68.0847 155.802C70.2091 155.101 72.1673 153.972 73.8385 152.484L85.5832 142.034L79.5351 158.161L97.3839 159.759C97.3839 159.759 111.889 147.203 113.399 130.249C115.476 107.027 100.112 95.2025 100.112 95.2025Z
        """.trimIndent()
        )
        fl_broom.setOnStateChangeListener {
            if (it == State.FINISHED && D.loading.value == false) {
                openApp()
            }
            state = it
        }
        fl_broom.start()
        D.download(applicationContext)
        D.loading.observe(this, {
            if (!it && state == State.FINISHED) {
                openApp()
            }
        })
        GPayClient.getInstance(applicationContext)
                .init()

    }

    private fun openApp() {
        if (!isFinishing) {

            val stringKey = intent.getStringExtra("notkey")
            Log.d("TAG_ERROR", "STRING KEY: " + stringKey)

            val i = Intent(applicationContext, MainActivity::class.java)
            i.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            i.putExtra("keysplash", stringKey)


            applicationContext.startActivity(i)
            finish()
//            startActivity(intentFor<MainActivity>().putExtras(intent))
//            finish()
        }
    }


    override fun onBackPressed() {

    }
}