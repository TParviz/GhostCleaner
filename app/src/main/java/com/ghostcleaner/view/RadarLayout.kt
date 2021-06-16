package com.ghostcleaner.view

import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.os.Build
import android.os.SystemClock
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.ghostcleaner.R
import com.ghostcleaner.extension.use
import kotlinx.android.synthetic.main.merge_radar.view.*
import kotlinx.coroutines.*
import org.jetbrains.anko.dip
import timber.log.Timber
import kotlin.math.sqrt

class RadarLayout : FrameLayout, CoroutineScope {

    private val job = SupervisorJob()

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
    }

    private val colorBackground = ContextCompat.getColor(context, R.color.colorBackground)
    private val colorDarkGray = ContextCompat.getColor(context, R.color.colorDarkGray)
    private var colorGreen = ContextCompat.getColor(context, R.color.colorGreen)

    private val circleDarkW = dip(11).toFloat()
    private val circleGreenW = dip(2).toFloat()
    private val circleMiniW = dip(1).toFloat()

    private val circleGreenR = dip(212).toFloat() / 2
    private val circleMiniDs = arrayOf(dip(170).toFloat(), dip(112).toFloat(), dip(48).toFloat())

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
        setWillNotDraw(false)
        View.inflate(context, R.layout.merge_radar, this)
        attrs?.let { set ->
            context.obtainStyledAttributes(set, R.styleable.RadarLayout).use {
                colorGreen = getInt(R.styleable.RadarLayout_color, colorGreen)
                iv_gradient.setColorFilter(colorGreen)
                iv_dot1.setColorFilter(colorGreen)
                iv_dot2.setColorFilter(colorGreen)
                iv_dot3.setColorFilter(colorGreen)
                iv_dot4.setColorFilter(colorGreen)
                iv_dot5.setColorFilter(colorGreen)
            }
        }
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        val frames = 200
        launch {
            while (isAttachedToWindow) {
                (0 until frames).forEach { f ->
                    val h = iv_gradient.height
                    iv_gradient.translationY = (height + h) * (1 - f.toFloat() / frames) - h
                    if (!iv_gradient.isVisible) {
                        iv_gradient.isVisible = true
                    }
                    val r = (0..1000).random()
                    val now = SystemClock.elapsedRealtime()
                    drawDot(iv_dot1, now, r in 0..20)
                    drawDot(iv_dot2, now, r in 200..220)
                    drawDot(iv_dot3, now, r in 400..420)
                    drawDot(iv_dot4, now, r in 600..620)
                    drawDot(iv_dot5, now, r in 800..820)
                    delay(10)
                }
            }
        }
    }

    private fun drawDot(view: View, now: Long, show: Boolean) {
        if (view.tag == null) {
            if (show) {
                val w = width.toFloat()
                val h = height.toFloat()
                val cX = w / 2
                val cY = h / 2
                val r = circleGreenR - circleGreenW
                view.translationX = cX - r + 2 * r * (0..100).random() / 100
                view.translationY = cY - r + 2 * r * (0..100).random() / 100
                view.tag = now
                view.isVisible = true
            }
        } else if (now - view.tag as Long > 1500L) {
            view.tag = null
            view.isVisible = false
        }
    }

    override fun dispatchDraw(canvas: Canvas) {
        super.dispatchDraw(canvas)
        val w = width.toFloat()
        val h = height.toFloat()
        val cX = w / 2
        val cY = h / 2
        with(canvas) {
            val radarHyp = w * sqrt(2f)
            val circleOuterW = (radarHyp - w) / 2
            paint.strokeWidth = circleOuterW
            paint.color = colorBackground
            drawCircle(cX, cY, radarHyp / 2 - circleOuterW / 2, paint)
            paint.strokeWidth = circleDarkW
            paint.color = colorDarkGray
            drawCircle(cX, cY, w / 2 - circleDarkW / 2, paint)
            paint.strokeWidth = circleGreenW
            paint.color = colorGreen
            drawCircle(cX, cY, circleGreenR - circleGreenW / 2, paint)
            paint.strokeWidth = circleMiniW
            circleMiniDs.forEach {
                drawCircle(cX, cY, it / 2 - circleMiniW / 2, paint)
            }
            paint.strokeWidth = circleMiniW / 2
            drawLine(cX - circleGreenR, cY, cX + circleGreenR, cY, paint)
            drawLine(cX, cY + circleGreenR, cX, cY - circleGreenR, paint)
            val proj = circleGreenR / sqrt(2f)
            drawLine(cX - proj, cY - proj, cX + proj, cY + proj, paint)
            drawLine(cX + proj, cY - proj, cX - proj, cY + proj, paint)
        }
    }

    override fun onDetachedFromWindow() {
        job.cancelChildren()
        super.onDetachedFromWindow()
    }

    override fun hasOverlappingRendering() = false

    override val coroutineContext = Dispatchers.Main + job + CoroutineExceptionHandler { _, e ->
        Timber.e(e)
    }
}