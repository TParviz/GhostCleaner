package com.ghostcleaner

import android.app.AlarmManager
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.content.res.Configuration.ORIENTATION_PORTRAIT
import android.net.Uri
import android.os.Bundle
import android.os.SystemClock
import android.widget.RemoteViews
import androidx.annotation.UiThread
import androidx.core.app.AlarmManagerCompat
import com.ghostcleaner.extension.appWidgetManager
import com.ghostcleaner.extension.getComponent
import com.ghostcleaner.extension.pendingReceiverFor
import com.ghostcleaner.extension.pendingWidgetFor
import com.ghostcleaner.screen.SplashActivity
import com.ghostcleaner.service.D
import com.ghostcleaner.service.EnergyManager
import org.jetbrains.anko.alarmManager
import org.jetbrains.anko.intentFor
import org.jetbrains.anko.newTask
import org.threeten.bp.Duration
import org.threeten.bp.LocalTime
import org.threeten.bp.temporal.ChronoUnit
import timber.log.Timber
import kotlin.math.max

class WidgetClock : AppWidgetProvider() {

    @UiThread
    override fun onUpdate(context: Context, manager: AppWidgetManager, ids: IntArray) {
        with(context) {
            val clock = LocalTime.now().format(timeFormatter)
            val energyManager = EnergyManager.getInstance(applicationContext)
            val level = energyManager.batteryLevel
            val time = energyManager.getBatteryTime()
            for (id in ids) {
                updateWidget(id, clock, time, level)
            }
        }
    }

    @UiThread
    override fun onAppWidgetOptionsChanged(
            context: Context,
            manager: AppWidgetManager,
            id: Int,
            newOptions: Bundle?
    ) {
        onUpdate(context, manager, intArrayOf(id))
    }

    @UiThread
    override fun onReceive(context: Context, intent: Intent) {
        with(context) {
            val ids = appWidgetManager.getAppWidgetIds(getComponent<WidgetClock>())
            when (val action = intent.action) {
                ACTION_OPEN -> {
                    Timber.d("onReceive action=$action")
                    startActivity(intentFor<SplashActivity>().newTask())
                }
                "$packageName.ALARM", Intent.ACTION_TIME_TICK, Intent.ACTION_TIME_CHANGED,
                Intent.ACTION_TIMEZONE_CHANGED, Intent.ACTION_LOCALE_CHANGED, Intent.ACTION_BATTERY_CHANGED,
                Intent.ACTION_BATTERY_LOW, Intent.ACTION_BATTERY_OKAY, Intent.ACTION_SCREEN_ON -> {
                    Timber.d("onReceive action=$action")
                    if (ids.isNotEmpty()) {
                        onUpdate(applicationContext, appWidgetManager, ids)
                    }
                }
                else -> Timber.d("onReceive action=$action")
            }
            super.onReceive(context, intent)
            if (ids.isNotEmpty()) {
                val start = LocalTime.now()
                val end = start.plusMinutes(1).truncatedTo(ChronoUnit.MINUTES)
                val millis = Duration.between(start, end).toMillis()
                Timber.d("Alarm millis=$millis")
                AlarmManagerCompat.setExact(
                        alarmManager,
                        AlarmManager.ELAPSED_REALTIME,
                        SystemClock.elapsedRealtime() + max(0, millis),
                        pendingReceiverFor(intentFor<WidgetClock>().also {
                            it.data = Uri.parse("$packageName://0")
                            it.action = "$packageName.ALARM"
                        }, 0)
                )
            }
        }
    }

    companion object {

        private fun Context.updateWidget(id: Int, clock: String, time: String, level: Int) {
            val options = appWidgetManager.getAppWidgetOptions(id)
            val isPortrait = resources.configuration.orientation == ORIENTATION_PORTRAIT
            val width = options.getInt(
                    if (isPortrait) {
                        AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH
                    } else {
                        AppWidgetManager.OPTION_APPWIDGET_MAX_WIDTH
                    }
            )
            Timber.d("updateWidget isPortrait=$isPortrait width=$width")
            appWidgetManager.updateAppWidget(
                    id, RemoteViews(packageName, R.layout.widget_clock).apply {
                setTextViewText(R.id.tv_clock, clock)
                setTextViewText(R.id.tv_time, D["clockRemaining", time])
                setTextViewText(R.id.tv_battery, "$level%")
                setOnClickPendingIntent(
                        R.id.ll_clock,
                        pendingWidgetFor<WidgetClock>(id, ACTION_OPEN)
                )
            }
            )
        }
    }
}