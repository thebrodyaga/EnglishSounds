package com.thebrodyaga.core.uiUtils.outline

import android.view.View
import com.google.android.material.shape.ShapeAppearanceModel

fun View.shapeOutline(
    shape: ShapeAppearanceModel?
) {
    if (shape == null) {
        this.outlineProvider = null
        invalidateOutline()
        return
    }
    val outlineProvider = this.outlineProvider
    if (outlineProvider is ShapeOutlineProvider) {
        outlineProvider.update(shape)
        invalidateOutline()
    } else {
        this.outlineProvider = ShapeOutlineProvider().also {
            it.update(shape)
            clipToOutline = true
        }
    }
}