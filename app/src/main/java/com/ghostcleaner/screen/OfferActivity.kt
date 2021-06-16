package com.ghostcleaner.screen

import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.observeFreshly
import com.ghostcleaner.R
import com.ghostcleaner.SKU_ADS
import com.ghostcleaner.SUB_ADS2
import com.ghostcleaner.screen.base.BaseActivity
import com.ghostcleaner.service.GPayClient
import kotlinx.android.synthetic.main.activity_banner.*
import org.jetbrains.anko.intentFor


class OfferActivity : BaseActivity() {

/*    private val min = TimeUnit.MINUTES.toMillis(7)
    private val max = TimeUnit.MINUTES.toMillis(10)


    private val timer = object : CountDownTimer((min..max).random(), 1000) {

        override fun onTick(millis: Long) {
            if (!isFinishing) {
                val seconds = millis / 1000
                tv_time.text = String.format("00:%02d:%02d", seconds / 60, seconds % 60)
            }
        }

        override fun onFinish() {
            if (!isFinishing) {
                finish()
            }
        }
    }*/

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_offer)
        setContentView(R.layout.activity_banner);


        ib_close.setOnClickListener {
            finish()
        }

        btn_disable.setOnClickListener {
            GPayClient.getInstance(applicationContext).subscribe(this, SUB_ADS2)
        }

        GPayClient.getInstance(applicationContext).purchases.observeFreshly(this, {
            if (it == SKU_ADS || it == SUB_ADS2) {
                startActivity(intentFor<SuccessActivity>().putExtras(intent))
                finish()
            }
        })
//        timer.start()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        GPayClient.getInstance(applicationContext).onActivityResult(requestCode, resultCode, data)
    }


    override fun onDestroy() {
//        timer.cancel()
        super.onDestroy()
    }
}