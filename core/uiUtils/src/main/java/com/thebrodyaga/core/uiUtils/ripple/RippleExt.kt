package com.thebrodyaga.core.uiUtils.ripple

import android.graphics.drawable.RippleDrawable
import android.os.Build
import android.view.View
import androidx.annotation.AttrRes
import com.google.android.material.shape.ShapeAppearanceModel
import com.thebrodyaga.core.uiUtils.common.getColorStateList
import com.thebrodyaga.core.uiUtils.drawable.shapeDrawable
import com.thebrodyaga.core.uiUtils.shape.shapeRectangle

fun View.rippleForeground(
    shape: ShapeAppearanceModel? = shapeRectangle(),
    @AttrRes color: Int = android.R.attr.selectableItemBackground,
) {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) return
    val rippleColor = getColorStateList(color) ?: return
    foreground = RippleDrawable(rippleColor, null, shape?.let { shapeDrawable(shape) })
}