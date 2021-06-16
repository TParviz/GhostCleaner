package com.ghostcleaner.screen.main

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.media.RingtoneManager
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.util.SparseArray
import android.widget.RemoteViews
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkManager
import com.ghostcleaner.*
import com.ghostcleaner.MainApp.Companion.CHANNEL_ID
import com.ghostcleaner.R
import com.ghostcleaner.extension.pendingActivityFor
import com.ghostcleaner.extension.setBackgroundColor
import com.ghostcleaner.screen.OfferActivity
import com.ghostcleaner.screen.SplashActivity
import com.ghostcleaner.screen.base.BaseActivity
import com.ghostcleaner.screen.main.GlobalClickedVars.Companion.BTN_BATTERY_CLICKED
import com.ghostcleaner.screen.main.GlobalClickedVars.Companion.BTN_BOOST_CLICKED
import com.ghostcleaner.screen.main.GlobalClickedVars.Companion.BTN_COOLER_CLICKED
import com.ghostcleaner.screen.main.GlobalClickedVars.Companion.BTN_JUNK_CLICKED
import com.ghostcleaner.service.AdmobClient
import com.ghostcleaner.service.D
import com.google.android.gms.ads.*
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks
import com.google.firebase.ktx.Firebase
import com.yandex.metrica.YandexMetrica
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.intentFor
import org.jetbrains.anko.notificationManager
import java.lang.Exception
import java.util.concurrent.TimeUnit


class MainActivity : BaseActivity(), ViewPager.OnPageChangeListener {

    private var notificationManager: NotificationManagerCompat? = null
    lateinit var sharedPreferences: SharedPreferences
    var isOpenedNotification = false
    var isOpenInter = false

    lateinit var mAdView : AdView

    val data : Intent = Intent()

    lateinit var alarmManager: AlarmManager
    lateinit var context: Context

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        notificationManager = NotificationManagerCompat.from(this)
        alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager

        sharedPreferences = getSharedPreferences("SHARED_PREF", Context.MODE_PRIVATE)
        isOpenedNotification = sharedPreferences.getBoolean("notifstate", false)


           // show notification widget
//        if(isOpenedNotification){
//           showNotificationWidget()
//        }

        //show subscribeAd after open app
//        if ((BTN_BOOST_CLICKED or BTN_BATTERY_CLICKED or BTN_COOLER_CLICKED or BTN_JUNK_CLICKED)) {
//        } else {
//            showBanner()
//        }

        val clickedWidget = intent.getIntExtra("clicked", 0)
        if(clickedWidget == 1) {
            YandexMetrica.reportEvent("clicked_widget")
        }

//        showGoogleBanner()

        FirebaseDynamicLinks.getInstance()
                .getDynamicLink(intent)
                .addOnSuccessListener(this) { pendingDynamicLinkData ->
                    // Get deep link from result (may be null if no link is found)
                    var deepLink: Uri? = null
                    if (pendingDynamicLinkData != null) {
                        deepLink = pendingDynamicLinkData.link
                    }
                }
                .addOnFailureListener(this) {
                    e -> Log.w("TAG", "getDynamicLink:onFailure", e)
                }

        val adapter = TabsAdapter(supportFragmentManager)
        vp_main.adapter = adapter
        vp_main.offscreenPageLimit = adapter.count
        vp_main.addOnPageChangeListener(this)
        bn_main.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.action_rocket -> vp_main.setCurrentItem(0, true)
                R.id.action_battery -> vp_main.setCurrentItem(1, true)
                R.id.action_temperature -> vp_main.setCurrentItem(2, true)
                R.id.action_trash -> vp_main.setCurrentItem(3, true)
                R.id.action_appmanager -> vp_main.setCurrentItem(4, true)
            }
            true
        }
        notifyBottomNav(intent)

    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        recreate()
        notifyBottomNav(intent)
    }

    private fun notifyBottomNav(intent: Intent) {

        val intentFragment = intent.getIntExtra("fragkey", 0)
        val stringKey = intent.getStringExtra("keysplash")

        if (intentFragment == 0) {//first open
            when (intent.getStringExtra(EXTRA_ACTION)) {
                ACTION_BATTERY -> vp_main.setCurrentItem(1, false)
                ACTION_TEMPERATURE -> vp_main.setCurrentItem(2, false)
                ACTION_TRASH -> vp_main.setCurrentItem(3, false)
                ACTION_APPMANAGER-> vp_main.setCurrentItem(4, false)
                else -> vp_main.setCurrentItem(0, false)
            }
        }
        if ((intentFragment == 1) or (stringKey.equals("booster"))) {//rocket open
            when (intent.getStringExtra(EXTRA_ACTION)) {
                ACTION_BATTERY -> vp_main.setCurrentItem(1, false)
                ACTION_TEMPERATURE -> vp_main.setCurrentItem(2, false)
                ACTION_TRASH -> vp_main.setCurrentItem(3, false)
                ACTION_APPMANAGER-> vp_main.setCurrentItem(4, false)
                else -> vp_main.setCurrentItem(0, false)
            }
        }
        if ((intentFragment == 2) or (stringKey.equals("battery"))) {//battery open
            when (intent.getStringExtra(EXTRA_ACTION)) {
                ACTION_ROCKET-> vp_main.setCurrentItem(0, false)
                ACTION_TEMPERATURE -> vp_main.setCurrentItem(2, false)
                ACTION_TRASH -> vp_main.setCurrentItem(3, false)
                ACTION_APPMANAGER-> vp_main.setCurrentItem(4, false)
                else -> vp_main.setCurrentItem(1, false)
            }
        }
        if ((intentFragment == 3) or (stringKey.equals("cooler"))) {//temperature open
            when (intent.getStringExtra(EXTRA_ACTION)) {
                ACTION_BATTERY -> vp_main.setCurrentItem(1, false)
                ACTION_ROCKET -> vp_main.setCurrentItem(0, false)
                ACTION_TRASH -> vp_main.setCurrentItem(3, false)
                ACTION_APPMANAGER-> vp_main.setCurrentItem(4, false)
                else -> vp_main.setCurrentItem(2, false)
            }
        }
        if ((intentFragment == 4) or (stringKey.equals("clean"))){ //junk open

            val editor : SharedPreferences.Editor = sharedPreferences.edit()
            editor.putBoolean("permissionask", true)
            editor.apply()

            when (intent.getStringExtra(EXTRA_ACTION)) {
                ACTION_BATTERY -> vp_main.setCurrentItem(1, false)
                ACTION_TEMPERATURE -> vp_main.setCurrentItem(2, false)
                ACTION_ROCKET -> vp_main.setCurrentItem(0, false)
                ACTION_APPMANAGER-> vp_main.setCurrentItem(4, false)
                else -> vp_main.setCurrentItem(3, false)
            }
        }
        if (stringKey.equals("manager")) {//manager open
            when (intent.getStringExtra(EXTRA_ACTION)) {
                ACTION_BATTERY -> vp_main.setCurrentItem(1, false)
                ACTION_ROCKET -> vp_main.setCurrentItem(0, false)
                ACTION_TRASH -> vp_main.setCurrentItem(3, false)
                ACTION_TEMPERATURE-> vp_main.setCurrentItem(2, false)
                else -> vp_main.setCurrentItem(4, false)
            }
        }

    }


    //initialize notification widget
    private fun showNotificationWidget(){

        val editor : SharedPreferences.Editor = sharedPreferences.edit()
        editor.putBoolean("notifstate", true)
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
        expandedView.setOnClickPendingIntent(R.id.btn_not_appman, clickPendingIntent4)

        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.broom1)
                .setCustomContentView(expandedView)
                .setOngoing(true)
                .build()

        notificationManager!!.notify(1, notification)
    }

    private fun showBanner(){//show Subscribe Ad(OfferActivity)
        //mb this code is start of falling down retention
        val preffs = Preferences(applicationContext)
        if (preffs.enableAds) {
            val someThread = Runnable {
                startActivity(intentFor<OfferActivity>())
            }
            Handler().postDelayed(someThread, 1000);
        }
    }

    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {

    }
    override fun onPageScrollStateChanged(state: Int) {

    }
    override fun onPageSelected(position: Int) { //selected bottom bar
        val id = when (position) {
            0 -> R.id.action_rocket
            1 -> R.id.action_battery
            2 -> R.id.action_temperature
            3 -> R.id.action_trash
            else -> R.id.action_appmanager
        }
        bn_main.menu.findItem(id).isChecked = true
    }

    private fun showGoogleBanner(){
        val preffs = Preferences(applicationContext)
        if (preffs.enableAds) {
            MobileAds.initialize(this) {}

            mAdView = findViewById(R.id.ads_banner)
            val adRequest = AdRequest.Builder().build()
            mAdView.loadAd(adRequest)

        }

    }

    override fun onDestroy() {
//        lifecycle.removeObserver(AdmobClient.getInstance(applicationContext))
//        AdmobClient.getInstance(applicationContext)
//                .hideBanner(vp_main)
        vp_main.removeOnPageChangeListener(this)
        super.onDestroy()
    }
}