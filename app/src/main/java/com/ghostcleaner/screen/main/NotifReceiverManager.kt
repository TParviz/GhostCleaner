package com.ghostcleaner.screen.main

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.ghostcleaner.screen.SplashActivity

class NotifReceiverManager : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {

        //open fragment App Manager
        val i = Intent(context, SplashActivity::class.java)

        i.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        i.putExtra("notkey", "manager")

        context.startActivity(i)
    }
}