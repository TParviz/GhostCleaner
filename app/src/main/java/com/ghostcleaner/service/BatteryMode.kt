package com.ghostcleaner.service

enum class BatteryMode(
        val brightness: Int,
        val toggleSync: Boolean,
        val toggleRotate: Int,
        val disableBle: Boolean,
        val disableWifi: Boolean
) {
    NORMAL(153/* 60% */, true, 1, false, false),
    ULTRA(102/* 40% */, false, 0, true, false),
    EXTREME(51/* 20% */, false, 0, true, true);
}