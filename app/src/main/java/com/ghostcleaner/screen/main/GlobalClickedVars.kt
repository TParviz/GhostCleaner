package com.ghostcleaner.screen.main

import android.app.Application

class GlobalClickedVars : Application(){

    companion object {
        var BTN_BOOST_CLICKED = false
        var BTN_BATTERY_CLICKED = false
        var BTN_COOLER_CLICKED = false
        var BTN_JUNK_CLICKED = false
    }
}