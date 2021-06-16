package com.ghostcleaner.view

import android.content.Context
import android.util.AttributeSet
import com.ghostcleaner.R
import com.ghostcleaner.extension.use
import com.ghostcleaner.service.D
import com.google.android.material.button.MaterialButton

class DMaterialButton @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = R.attr.materialButtonStyle
) : MaterialButton(context, attrs, defStyleAttr) {

    init {
        if (!isInEditMode) {
            if (attrs != null) {
                context.obtainStyledAttributes(attrs, R.styleable.DMaterialButton).use {
                    getString(R.styleable.DMaterialButton_key)?.let {
                        text = D[it]
                    }
                }
            }
        }
    }

    override fun hasOverlappingRendering() = false
}