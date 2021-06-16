package com.ghostcleaner

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.content.res.Configuration.ORIENTATION_PORTRAIT
import android.os.Bundle
import android.widget.RemoteViews
import androidx.annotation.UiThread
import com.ghostcleaner.extension.appWidgetManager
import com.ghostcleaner.extension.pendingWidgetFor
import com.ghostcleaner.screen.SplashActivity
import org.jetbrains.anko.intentFor
import org.jetbrains.anko.newTask
import timber.log.Timber

class WidgetBar : AppWidgetProvider() {

    @UiThread
    override fun onUpdate(context: Context, manager: AppWidgetManager, ids: IntArray) {
        with(context) {
            for (id in ids) {
                updateWidget(id)
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
            when (val action = intent.action) {
                ACTION_ROCKET, ACTION_BATTERY, ACTION_TEMPERATURE, ACTION_TRASH -> {
                    Timber.d("onReceive action=$action")
                    startActivity(intentFor<SplashActivity>(EXTRA_ACTION to action).newTask())
                }
                else -> Timber.d("onReceive action=$action")
            }
            super.onReceive(context, intent)
        }
    }

    companion object {

        /**
         * 1 | 1 1 0 0 | 1
         * 2 | 1 0 1 0 | 2
         * 3 | 1 0 0 1 | 3
         * 4 | 0 1 1 0 | 4
         * 5 | 0 1 0 1 | 5
         * 6 | 0 0 1 1 | 6
         */
        private fun Context.updateWidget(id: Int) {
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
                    id, RemoteViews(packageName, R.layout.widget_bar).apply {
                val n = (1..6).random()
                setImageViewResource(
                        R.id.ib_rocket,
                        if (n in 1..3) R.drawable.ic_rocket_red else R.drawable.ic_rocket
                )
                setImageViewResource(
                        R.id.ib_battery,
                        if (n == 1 || n in 4..5) R.drawable.ic_battery_red else R.drawable.ic_battery
                )
                setImageViewResource(
                        R.id.ib_temperature,
                        if (n % 2 == 0) R.drawable.ic_temperature_red else R.drawable.ic_temperature
                )
                setImageViewResource(
                        R.id.ib_trash,
                        if (n == 3 || n > 4) R.drawable.ic_trash_red else R.drawable.ic_trash
                )
                setOnClickPendingIntent(
                        R.id.ib_rocket,
                        pendingWidgetFor<WidgetBar>(id, ACTION_ROCKET)
                )
                setOnClickPendingIntent(
                        R.id.ib_battery,
                        pendingWidgetFor<WidgetBar>(id, ACTION_BATTERY)
                )
                setOnClickPendingIntent(
                        R.id.ib_temperature,
                        pendingWidgetFor<WidgetBar>(id, ACTION_TEMPERATURE)
                )
                setOnClickPendingIntent(
                        R.id.ib_trash,
                        pendingWidgetFor<WidgetBar>(id, ACTION_TRASH)
                )
            }
            )
        }
    }
}