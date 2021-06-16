@file:Suppress("unused")

package com.ghostcleaner.extension

import android.app.Activity
import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import org.jetbrains.anko.intentFor

val Context.appWidgetManager: AppWidgetManager
    get() = AppWidgetManager.getInstance(applicationContext)

inline fun <reified T> Context.getComponent() = ComponentName(applicationContext, T::class.java)

fun Context.areGranted(vararg permissions: String): Boolean {
    for (permission in permissions) {
        if (checkCallingOrSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
            return false
        }
    }
    return true
}

inline fun <reified T : AppWidgetProvider> Context.pendingWidgetFor(
    widgetId: Int,
    action: String = AppWidgetManager.ACTION_APPWIDGET_UPDATE
): PendingIntent {
    return pendingReceiverFor(intentFor<T>().also {
        it.action = action
        it.data = Uri.parse(it.toUri(Intent.URI_INTENT_SCHEME))
        it.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId)
    }, widgetId)
}

inline fun <reified T : Activity> Context.pendingActivityFor(
    requestCode: Int = 0,
    flags: Int = PendingIntent.FLAG_UPDATE_CURRENT,
    vararg params: Pair<String, Any?>
): PendingIntent =
    PendingIntent.getActivity(applicationContext, requestCode, intentFor<T>(*params), flags)

inline fun <reified T : BroadcastReceiver> Context.pendingReceiverFor(
    requestCode: Int = 0,
    flags: Int = PendingIntent.FLAG_UPDATE_CURRENT,
    vararg params: Pair<String, Any?>
): PendingIntent =
    PendingIntent.getBroadcast(applicationContext, requestCode, intentFor<T>(*params), flags)

fun Context.pendingReceiverFor(
    action: String,
    requestCode: Int = 0,
    flags: Int = PendingIntent.FLAG_UPDATE_CURRENT
): PendingIntent =
    PendingIntent.getBroadcast(applicationContext, requestCode, Intent(action), flags)

fun Context.pendingReceiverFor(
    intent: Intent,
    requestCode: Int = 0,
    flags: Int = PendingIntent.FLAG_UPDATE_CURRENT
): PendingIntent =
    PendingIntent.getBroadcast(applicationContext, requestCode, intent, flags)