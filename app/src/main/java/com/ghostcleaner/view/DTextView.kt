package com.ghostcleaner.view

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView
import com.ghostcleaner.R
import com.ghostcleaner.extension.use
import com.ghostcleaner.service.D

class DTextView @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0
) : AppCompatTextView(context, attrs, defStyleAttr) {

    init {
        if (!isInEditMode) {
            if (attrs != null) {
                context.obtainStyledAttributes(attrs, R.styleable.DTextView).use {
                    getString(R.styleable.DTextView_key)?.let {
                        text = D[it]
                    }
                }
            }
        }
    }

    override fun hasOverlappingRendering() = false
}