package com.ghostcleaner.screen.main

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.ghostcleaner.screen.SplashActivity

class NotifReceiverClean : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        //open fragment Clean
        val i = Intent(context, SplashActivity::class.java)
        i.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        i.putExtra("notkey", "clean")

        context.startActivity(i)
    }
}