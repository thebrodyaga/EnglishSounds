package com.thebrodyaga.core.uiUtils.shape

import androidx.annotation.AttrRes
import androidx.annotation.Px
import android.view.View
import com.google.android.material.shape.CornerFamily
import com.google.android.material.shape.ShapeAppearanceModel
import com.google.android.material.shape.ShapeAppearanceModel.PILL
import com.thebrodyaga.core.uiUtils.outline.ShapeOutlineProvider

fun shapeRectangle(): ShapeAppearanceModel = ShapeAppearanceModel.builder().build()

fun shapeCircle(): ShapeAppearanceModel = ShapeAppearanceModel.builder()
    .setAllCornerSizes(PILL)
    .build()

fun shapeTopRounded(@Px top: Float = 0f): ShapeAppearanceModel = shapeRounded(
    topLeft = top,
    topRight = top,
)

fun shapeBottomRounded(@Px bottom: Float = 0f): ShapeAppearanceModel = shapeRounded(
    bottomRight = bottom,
    bottomLeft = bottom,
)

fun shapeRoundedAll(
    @Px cornerSize: Float = 0f,
): ShapeAppearanceModel = ShapeAppearanceModel.builder()
    .setAllCorners(CornerFamily.ROUNDED, cornerSize)
    .build()

fun shapeRounded(
    @Px topLeft: Float = 0f,
    @Px topRight: Float = 0f,
    @Px bottomRight: Float = 0f,
    @Px bottomLeft: Float = 0f,
): ShapeAppearanceModel = ShapeAppearanceModel.builder()
    .setTopLeftCorner(CornerFamily.ROUNDED, topLeft)
    .setTopRightCorner(CornerFamily.ROUNDED, topRight)
    .setBottomRightCorner(CornerFamily.ROUNDED, bottomRight)
    .setBottomLeftCorner(CornerFamily.ROUNDED, bottomLeft)
    .build()
