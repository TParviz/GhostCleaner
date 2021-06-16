package com.ghostcleaner.screen.main

class AppManager {
    var userApps: MutableList<AppInfo> = ArrayList()
    var systemApps: MutableList<AppInfo> = ArrayList()
    var userAppSize: Int = 0
    var systemAppSize: Int = 0
}