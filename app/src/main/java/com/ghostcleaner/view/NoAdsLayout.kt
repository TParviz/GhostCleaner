package com.ghostcleaner.view

import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.content.Context
import android.os.Build
import android.util.AttributeSet
import android.view.View
import android.widget.RelativeLayout
import androidx.core.content.ContextCompat
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import com.ghostcleaner.Preferences
import com.ghostcleaner.R
import com.ghostcleaner.service.AdmobClient
import kotlinx.android.synthetic.main.merge_no_ads.view.*
import kotlinx.coroutines.*
import timber.log.Timber

class NoAdsLayout : RelativeLayout, CoroutineScope, Observer<Boolean> {

    private val job = SupervisorJob()

    @JvmOverloads
    constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : super(
            context,
            attrs,
            defStyleAttr
    ) {
        init(attrs)
    }

    @Suppress("unused")
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int, defStyleRes: Int) : super(
            context,
            attrs,
            defStyleAttr,
            defStyleRes
    ) {
        init(attrs)
    }

    @SuppressLint("Recycle")
    private fun init(attrs: AttributeSet?) {
        View.inflate(context, R.layout.merge_no_ads, this)
        val preferences = Preferences(context)
        if (!preferences.enableAds) {
            isInvisible = true
        }
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        AdmobClient.getInstance(context).enableAds.observeForever(this)
        if (!isVisible) {
            return
        }
        val green = ContextCompat.getColor(context, R.color.colorGreen)
        val maxR = 20f
        val frames = 60
        launch {
            while (isAttachedToWindow) {
                delay(1000)
                (1..4).forEach { i ->
                    (0 until frames).forEach { f ->
                        val r =
                                maxR * if (i % 2 != 0) f.toFloat() / frames else 1 - f.toFloat() / frames
                        tv_no.setShadowLayer(r, 0f, 0f, green)
                        tv_ads.setShadowLayer(r, 0f, 0f, green)
                        delay(10)
                    }
                }
                delay(29000)
            }
        }
    }

    override fun onChanged(t: Boolean) {
        if (!t) {
            job.cancelChildren()
            isInvisible = true
        }
    }

    override fun onDetachedFromWindow() {
        job.cancelChildren()
        AdmobClient.getInstance(context).enableAds.removeObserver(this)
        super.onDetachedFromWindow()
    }

    override fun hasOverlappingRendering() = false

    override val coroutineContext = Dispatchers.Main + job + CoroutineExceptionHandler { _, e ->
        Timber.e(e)
    }


}