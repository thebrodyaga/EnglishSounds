package com.thebrodyaga.core.uiUtils.image

import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat.getColorStateList
import androidx.core.view.isVisible
import android.widget.ImageView
import com.google.android.material.shape.ShapeAppearanceModel
import com.thebrodyaga.core.uiUtils.drawable.DrawableUiModel
import com.thebrodyaga.core.uiUtils.drawable.applyBackground
import com.thebrodyaga.core.uiUtils.drawable.applyForeground
import com.thebrodyaga.core.uiUtils.shape.shapeOutline

data class ImageViewUiModel(
    val icon: IconContainer,
    @ColorRes val iconTint: Int? = null,
    val scaleType: ImageView.ScaleType = ImageView.ScaleType.CENTER,
    val background: DrawableUiModel? = null,
    val foreground: DrawableUiModel? = null,
    val clippingShape: ShapeAppearanceModel? = null,
)

fun ImageView.bindOrGone(model: ImageViewUiModel?) {
    this.isVisible = model != null
    if (model != null) bind(model)
}

fun ImageView.bind(model: ImageViewUiModel) {
    bind(model.icon)
    imageTintList = model.iconTint?.let { getColorStateList(context, model.iconTint) }
    scaleType = model.scaleType
    model.background.applyBackground(this)
    model.foreground.applyForeground(this)
    shapeOutline(model.clippingShape)
}
