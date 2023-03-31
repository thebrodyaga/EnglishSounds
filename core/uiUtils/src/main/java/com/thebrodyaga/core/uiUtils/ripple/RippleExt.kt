package com.thebrodyaga.core.uiUtils.ripple

import android.view.View
import androidx.annotation.AttrRes
import com.google.android.material.shape.ShapeAppearanceModel
import com.thebrodyaga.core.uiUtils.R
import com.thebrodyaga.core.uiUtils.shape.shapeRectangle

fun View.rippleForeground(
    shape: ShapeAppearanceModel? = shapeRectangle(),
    @AttrRes color: Int = R.attr.colorPrimary,
) {
    // todo
    /*foreground = AppCompatResources.getDrawable(context, resFromTheme(color))
    if (shape != null) shapeOutline(shape)*/
}