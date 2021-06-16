package com.ghostcleaner.screen.main

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.ghostcleaner.screen.DoneActivity
import com.ghostcleaner.screen.SplashActivity

class NotifReceiverBooster : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        //open fragment booster
        val i = Intent(context, SplashActivity::class.java)
//        i.setClassName("com.ghostcleaner", "com.ghostcleaner.screen" + DoneActivity::class.java)
        i.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        i.putExtra("notkey", "booster")
        context.startActivity(i)
    }
}