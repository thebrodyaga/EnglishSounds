package com.thebrodyaga.brandbook.component.appbar

import android.content.Context
import android.util.AttributeSet
import com.google.android.material.appbar.AppBarLayout
import com.thebrodyaga.core.uiUtils.outline.shapeOutline
import com.thebrodyaga.core.uiUtils.resources.px
import com.thebrodyaga.core.uiUtils.shape.shapeBottomRounded

class TopAppBar @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : AppBarLayout(context, attrs) {

    init {
        shapeOutline(shapeBottomRounded(16f.px))
    }
}