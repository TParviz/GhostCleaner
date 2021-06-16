package com.ghostcleaner.extension

import android.graphics.drawable.Drawable
import androidx.annotation.ColorInt
import androidx.core.graphics.drawable.DrawableCompat

fun Drawable.setTintCompat(@ColorInt color: Int) {
    DrawableCompat.setTint(DrawableCompat.wrap(this), color)
}