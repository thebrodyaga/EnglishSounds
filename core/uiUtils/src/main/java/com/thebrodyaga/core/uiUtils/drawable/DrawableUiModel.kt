package com.thebrodyaga.core.uiUtils.drawable

import androidx.annotation.ColorRes
import androidx.annotation.Px
import androidx.core.content.ContextCompat.getColorStateList
import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Build
import android.view.View
import com.google.android.material.shape.MaterialShapeDrawable
import com.google.android.material.shape.ShapeAppearanceModel
import com.thebrodyaga.core.uiUtils.shape.shapeRectangle

data class DrawableUiModel(
    val drawable: Drawable = shapeDrawable(),
    @ColorRes val tint: Int = android.R.color.transparent,
    // only MaterialShapeDrawable support
    @Px val strokeWidth: Float = 0f,
    // only MaterialShapeDrawable support
    @ColorRes val strokeColor: Int = android.R.color.transparent,
)

fun shapeDrawable(
    shape: ShapeAppearanceModel = shapeRectangle(),
): MaterialShapeDrawable = MaterialShapeDrawable(shape)

fun DrawableUiModel?.applyBackground(view: View) {
    val model = this
    if (model == null) {
        view.background = null
        view.backgroundTintList = null
        return
    }
    val drawable = model.mutateDrawable(view.context)
    view.background = drawable
    view.backgroundTintList = getColorStateList(view.context, model.tint)
}

fun DrawableUiModel?.applyForeground(view: View) {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) return
    val model = this
    if (model == null) {
        view.foreground = null
        view.foregroundTintList = null
        return
    }
    val drawable = model.mutateDrawable(view.context)
    view.foreground = drawable
    view.foregroundTintList = view.context.getColorStateList(model.tint)
}

private fun DrawableUiModel.mutateDrawable(context: Context): Drawable? {
    val drawable = this.drawable.mutate().constantState?.newDrawable()
    if (drawable is MaterialShapeDrawable) {
        drawable.strokeColor = getColorStateList(context, this.strokeColor)
        drawable.strokeWidth = this.strokeWidth
    }
    return drawable
}
