package com.ghostcleaner.view

import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import com.mikhaellopez.circularprogressbar.CircularProgressBar
import kotlin.math.max

@Suppress("MemberVisibilityCanBePrivate")
class CircleBar(
        val pbOuter: CircularProgressBar,
        val pbInner: CircularProgressBar
) {

    var progress: Float
        get() = max(progressOuter, progressInner)
        set(value) {
            pbOuter.progress = value
            pbInner.progress = value
        }

    var progressOuter: Float
        get() = pbOuter.progress
        set(value) {
            pbOuter.progress = value
            pbInner.progress = 0f
        }

    var progressInner: Float
        get() = pbInner.progress
        set(value) {
            pbOuter.progress = 0f
            pbInner.progress = value
        }

    var colorOuter: Int
        get() = pbOuter.progressBarColor
        set(@ColorRes value) {
            pbOuter.progressBarColor = ContextCompat.getColor(pbOuter.context, value)
        }

    var colorInner: Int
        get() = pbInner.progressBarColor
        set(@ColorRes value) {
            pbInner.progressBarColor = ContextCompat.getColor(pbInner.context, value)
        }
}