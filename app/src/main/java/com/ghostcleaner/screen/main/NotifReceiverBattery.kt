package com.ghostcleaner.screen.main

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Space
import com.ghostcleaner.screen.DoneActivity
import com.ghostcleaner.screen.SplashActivity

class NotifReceiverBattery : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        //open fragment battery
        val i = Intent(context, SplashActivity::class.java)
//        i.setClassName("com.ghostcleaner", "com.ghostcleaner.screen" + SplashActivity::class.java)
        i.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        i.putExtra("notkey", "battery")
        context.startActivity(i)
    }
}