package com.thebrodyaga.brandbook.utils.drawable

import androidx.annotation.AttrRes
import androidx.annotation.Px
import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Build
import android.view.View
import com.google.android.material.color.MaterialColors
import com.google.android.material.shape.MaterialShapeDrawable
import com.google.android.material.shape.ShapeAppearanceModel
import com.thebrodyaga.brandbook.R
import com.thebrodyaga.core.uiUtils.common.getColorStateList
import com.thebrodyaga.core.uiUtils.shape.shapeRectangle

data class DrawableUiModel(
    val drawable: Drawable = shapeDrawable(),
    @AttrRes val tint: Int = R.attr.colorTransparent,
    // only MaterialShapeDrawable support
    @Px val strokeWidth: Float = 0f,
    // only MaterialShapeDrawable support
    @AttrRes val strokeColor: Int = R.attr.colorTransparent,
)

fun shapeDrawable(
    shape: ShapeAppearanceModel = shapeRectangle(),
): MaterialShapeDrawable = MaterialShapeDrawable(shape)

fun View.bindBackground(model: DrawableUiModel?) {
    if (model == null) {
        background = null
        backgroundTintList = null
        return
    }
    R.attr.colorAccent
    val drawable = model.mutateDrawable(context)
    background = drawable
    backgroundTintList = getColorStateList(model.tint)
}

fun View.bindForeground(model: DrawableUiModel?) {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) return
    if (model == null) {
        foreground = null
        foregroundTintList = null
        return
    }
    val drawable = model.mutateDrawable(context)
    foreground = drawable
    foregroundTintList = getColorStateList(model.tint)
}

private fun DrawableUiModel.mutateDrawable(context: Context): Drawable? {
    val drawable = this.drawable.mutate().constantState?.newDrawable()
    if (drawable is MaterialShapeDrawable) {
        drawable.strokeColor = MaterialColors.getColorStateListOrNull(context, tint)
        drawable.strokeWidth = this.strokeWidth
    }
    return drawable
}
