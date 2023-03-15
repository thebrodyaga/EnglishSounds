package com.thebrodyaga.core.uiUtils.shape

import androidx.annotation.AttrRes
import androidx.annotation.Px
import android.view.View
import com.google.android.material.shape.CornerFamily
import com.google.android.material.shape.ShapeAppearanceModel
import com.google.android.material.shape.ShapeAppearanceModel.PILL
import com.thebrodyaga.core.uiUtils.outline.ShapeOutlineProvider

fun View.rippleForeground(
    shape: ShapeAppearanceModel? = shapeRectangle(),
    @AttrRes color: Int = android.R.attr.selectableItemBackground,
) {
    // todo
    /*foreground = AppCompatResources.getDrawable(context, resFromTheme(color))
    if (shape != null) shapeOutline(shape)*/
}

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
