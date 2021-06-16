@file:Suppress("unused", "DEPRECATION")

package com.ghostcleaner.extension

import android.net.ConnectivityManager

val ConnectivityManager.isConnected: Boolean
    get() = activeNetworkInfo?.isConnected == true