package com.ghostcleaner.service

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.media.RingtoneManager
import android.net.Uri
import android.os.SystemClock
import android.text.format.DateUtils
import android.util.Log
import android.widget.RemoteViews
import androidx.core.app.AlarmManagerCompat
import androidx.core.app.NotificationCompat
import com.ghostcleaner.MainApp
import com.ghostcleaner.Preferences
import com.ghostcleaner.R
import com.ghostcleaner.extension.pendingActivityFor
import com.ghostcleaner.extension.pendingReceiverFor
import com.ghostcleaner.extension.setBackgroundColor
import com.ghostcleaner.screen.SplashActivity
import com.ghostcleaner.screen.main.*
import org.jetbrains.anko.alarmManager
import org.jetbrains.anko.intentFor
import org.jetbrains.anko.notificationManager
import org.threeten.bp.ZoneOffset
import org.threeten.bp.ZonedDateTime
import timber.log.Timber
import java.sql.Time
import java.util.concurrent.TimeUnit

class RemindReceiver : BroadcastReceiver() {

//    lateinit var sharedPreferences: SharedPreferences

    override fun onReceive(context: Context, intent: Intent?) {
        with(context) {
            setAlarm(applicationContext)
            val builder = NotificationCompat.Builder(applicationContext, "main")
                .setSmallIcon(R.drawable.broom)
                .setColor(Color.WHITE)
                .setContentIntent(pendingActivityFor<SplashActivity>())
                .setContentTitle(D["notText"])
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setAutoCancel(true)
                .setBadgeIconType(NotificationCompat.BADGE_ICON_SMALL)

            notificationManager.notify(99, builder.build())
// initialize
/*            val editor : SharedPreferences.Editor = sharedPreferences.edit()
            editor.putBoolean("notifstateredone", true)
            editor.apply()

            val expandedView = RemoteViews(packageName, R.layout.activity_buttons)

            val clickIntent = Intent(this, NotifReceiverBooster::class.java)
            val clickIntent1 = Intent(this, NotifReceiverBattery::class.java)
            val clickIntent2 = Intent(this, NotifReceiverClean::class.java)
            val clickIntent3 = Intent(this, NotifReceiverCooler::class.java)
            val clickIntent4 = Intent(this, NotifReceiverManager::class.java)

            val clickPendingIntent = PendingIntent.getBroadcast(this, 0, clickIntent, 0)
            val clickPendingIntent1 = PendingIntent.getBroadcast(this, 0, clickIntent1, 0)
            val clickPendingIntent2 = PendingIntent.getBroadcast(this, 0, clickIntent2, 0)
            val clickPendingIntent3 = PendingIntent.getBroadcast(this, 0, clickIntent3, 0)
            val clickPendingIntent4 = PendingIntent.getBroadcast(this, 0, clickIntent4, 0)


            expandedView.setOnClickPendingIntent(R.id.btn_not_rocket, clickPendingIntent)
            expandedView.setOnClickPendingIntent(R.id.btn_not_battery, clickPendingIntent1)
            expandedView.setOnClickPendingIntent(R.id.btn_not_temperature, clickPendingIntent3)
            expandedView.setOnClickPendingIntent(R.id.btn_not_trash, clickPendingIntent2)
            //making color of icon - Red,  for example
            expandedView.setBackgroundColor(R.id.btn_not_trash, Color.RED)
            expandedView.setOnClickPendingIntent(R.id.btn_not_appman, clickPendingIntent4)

            val notification = NotificationCompat.Builder(this, MainApp.CHANNEL_ID)
                .setSmallIcon(R.drawable.broom1)
                .setCustomContentView(expandedView)
                .setAutoCancel(true)
                .build()

            notificationManager.notify(1, notification)*/

        }
    }

    companion object {

        private val maxInterval = TimeUnit.HOURS.toMillis(4)

        fun setAlarm(context: Context) {
            with(context) {
                val now = ZonedDateTime.now(ZoneOffset.UTC).toInstant().toEpochMilli()
                val preferences = Preferences(applicationContext)
                val nextAlarm = preferences.nextAlarm
                val interval = if (nextAlarm <= now) {
                    preferences.nextAlarm = now + maxInterval
                    maxInterval
                } else {
                    nextAlarm - now
                }
                Timber.d("Next alarm ${DateUtils.formatElapsedTime(interval / 1000)}")
                AlarmManagerCompat.setExactAndAllowWhileIdle(
                        alarmManager,
                        AlarmManager.ELAPSED_REALTIME_WAKEUP,
                        SystemClock.elapsedRealtime() + interval,
                        pendingReceiverFor(intentFor<RemindReceiver>().also {
                            it.data = Uri.parse("$packageName://1")
                        }, 1)
                )
            }
        }
    }
}