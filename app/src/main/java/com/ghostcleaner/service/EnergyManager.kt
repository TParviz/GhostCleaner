package com.ghostcleaner.service

import android.bluetooth.BluetoothAdapter
import android.content.*
import android.net.Uri
import android.os.BatteryManager
import android.provider.Settings
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.OnLifecycleEvent
import com.ghostcleaner.Preferences
import com.ghostcleaner.REQUEST_SETTINGS
import com.ghostcleaner.extension.isMarshmallowPlus
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.jetbrains.anko.batteryManager
import org.jetbrains.anko.wifiManager
import kotlin.math.roundToInt

@Suppress("unused")
class EnergyManager(context: Context) : BaseManager<Int>(context), LifecycleObserver {

    private val preferences = Preferences(context)

    private val wifiManager = context.wifiManager

    private val batteryManager = context.batteryManager

    private val contentResolver = context.contentResolver

    private val receiver = object : BroadcastReceiver() {

        override fun onReceive(context: Context, intent: Intent?) {
            when (intent?.action) {
                Intent.ACTION_BATTERY_CHANGED, Intent.ACTION_BATTERY_LOW, Intent.ACTION_BATTERY_OKAY -> {
                    val level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0)
                    batteryChanges.value = level
                }
            }
        }
    }

    val batteryChanges = MutableLiveData<Int>()

    val batteryLevel: Int
        get() = batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY)

    fun checkPermission(context: Context, fragment: Fragment): Boolean {
        if (isMarshmallowPlus()) {
            if (!Settings.System.canWrite(context)) {
                fragment.startActivityForResult(Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS).apply {
                    data = Uri.parse("package:$packageName")
                }, REQUEST_SETTINGS)
                return false
            }
        }
        return true
    }

    override fun optimize(vararg args: Any) {
        super.optimize(args)
        val mode = args[0] as BatteryMode
        job.cancelChildren()
        launch {
            val interval = 500L
            Settings.System.putInt(
                    contentResolver,
                    Settings.System.SCREEN_BRIGHTNESS_MODE,
                    Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL
            )
            Settings.System.putInt(
                    contentResolver,
                    Settings.System.SCREEN_BRIGHTNESS,
                    mode.brightness
            )
            delay(interval)
            optimization.postValue(1)
            Settings.System.putInt(
                    contentResolver,
                    Settings.System.ACCELEROMETER_ROTATION,
                    mode.toggleRotate
            )
            delay(interval)
            optimization.postValue(2)
            killProcesses({
            }, 0)
            delay(interval)
            optimization.postValue(3)
            ContentResolver.setMasterSyncAutomatically(mode.toggleSync)
            if (mode.disableBle) {
                BluetoothAdapter.getDefaultAdapter().disable()
            }
            if (mode.disableWifi) {
                @Suppress("DEPRECATION")
                wifiManager.isWifiEnabled = false
            }
            delay(interval)
            optimization.postValue(4)
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun onStart() {
        reference.get()?.registerReceiver(receiver, IntentFilter().apply {
            addAction(Intent.ACTION_BATTERY_CHANGED)
            addAction(Intent.ACTION_BATTERY_LOW)
            addAction(Intent.ACTION_BATTERY_OKAY)
        })
    }

    fun getBatteryTime(mode: BatteryMode = BatteryMode.valueOf(preferences.batteryMode)): String {
        val level = batteryLevel
        return when {
            level <= 5 -> formatTime(mode, 15, 145, 235)
            level <= 10 -> formatTime(mode, 30, 185, 360)
            level <= 15 -> formatTime(mode, 45, 230, 505)
            level <= 25 -> formatTime(mode, 90, 285, 775)
            level <= 35 -> formatTime(mode, 140, 380, 1142)
            level <= 50 -> formatTime(mode, 320, 565, 1320)
            level <= 65 -> formatTime(mode, 450, 661, 1695)
            level <= 75 -> formatTime(mode, 550, 865, 1855)
            level <= 85 -> formatTime(mode, 855, 1030, 2285)
            else -> formatTime(mode, 1245, 1800, 3895)
        }
    }

    fun getDescList(mode: BatteryMode): List<CharSequence> {
        val services = when (mode) {
            BatteryMode.ULTRA -> "Bluetooth, Sync"
            BatteryMode.EXTREME -> "Wifi, Bluetooth, Sync"
            else -> "Sync"
        }
        return listOf(
                D["powOpt1", (100f * mode.brightness / 255).roundToInt()],
                D["powOpt2"],
                D["powOpt3"],
                D["powOpt4", services]
        )
    }

    private fun formatTime(mode: BatteryMode, nTime: Int, uTime: Int, eTime: Int): String {
        val minutes = when (mode) {
            BatteryMode.ULTRA -> uTime
            BatteryMode.EXTREME -> eTime
            else -> nTime
        }
        return "${minutes / 60}${D["hours"]} ${minutes % 60}${D["minutes"]}"
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun onStop() {
        reference.get()?.unregisterReceiver(receiver)
    }

    // for widget only
    companion object : Singleton<EnergyManager, Context>(::EnergyManager)
}