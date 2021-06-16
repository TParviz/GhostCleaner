package com.ghostcleaner.screen

import android.os.Bundle
import com.ghostcleaner.R
import com.ghostcleaner.screen.base.BaseActivity
import com.ghostcleaner.screen.main.MainActivity
import com.yandex.metrica.YandexMetrica
import kotlinx.android.synthetic.main.activity_success.*
import org.jetbrains.anko.intentFor

class SuccessActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_success)

        YandexMetrica.reportEvent("af_add_to_card")
        tv_back.setOnClickListener {
//            finish()
            startActivity(intentFor<MainActivity>())


        }
    }
}