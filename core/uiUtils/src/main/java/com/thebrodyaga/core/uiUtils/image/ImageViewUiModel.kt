package com.thebrodyaga.core.uiUtils.image

import androidx.annotation.AttrRes
import androidx.core.view.isVisible
import android.widget.ImageView
import com.google.android.material.shape.ShapeAppearanceModel
import com.thebrodyaga.core.uiUtils.drawable.DrawableUiModel
import com.thebrodyaga.core.uiUtils.drawable.bindBackground
import com.thebrodyaga.core.uiUtils.drawable.bindForeground
import com.thebrodyaga.core.uiUtils.common.getColorStateList
import com.thebrodyaga.core.uiUtils.shape.shapeOutline

data class ImageViewUiModel(
    val icon: IconContainer,
    @AttrRes val iconTint: Int? = null,
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
    imageTintList = getColorStateList(model.iconTint)
    scaleType = model.scaleType
    bindBackground(model.background)
    bindForeground(model.foreground)
    shapeOutline(model.clippingShape)
}
