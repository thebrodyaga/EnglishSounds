package com.thebrodyaga.brandbook.component.text

import android.content.Context
import android.util.AttributeSet
import com.google.android.material.textview.MaterialTextView

class AppTextView @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null
) : MaterialTextView(context, attrs) {

    init {
        includeFontPadding = false
    }
}