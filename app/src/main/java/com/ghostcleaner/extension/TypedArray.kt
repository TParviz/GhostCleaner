package com.ghostcleaner.extension

import android.content.res.TypedArray
import timber.log.Timber

inline fun <T> TypedArray.use(block: TypedArray.() -> T): T {
    try {
        return block()
    } finally {
        try {
            recycle()
        } catch (e: Throwable) {
            Timber.e(e)
        }
    }
}