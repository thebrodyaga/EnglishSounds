package com.thebrodyaga.core.uiUtils.skeleton

import android.graphics.drawable.Drawable
import android.graphics.drawable.PaintDrawable
import android.os.Build
import android.view.Gravity
import android.view.View
import androidx.annotation.Px
import androidx.core.content.ContextCompat.getColorStateList
import com.thebrodyaga.core.uiUtils.R
import com.thebrodyaga.core.uiUtils.common.getAttrColor
import com.thebrodyaga.core.uiUtils.common.getColorStateListCompat

data class SkeletonUiModel(
    @Px val height: Int,
    @Px val width: Int,
    @Px val radius: Float = 0f,
    val gravity: Int = Gravity.CENTER,
)

fun View.bindSkeleton(model: SkeletonUiModel) {

    // todo
    fun setupSkeleton(drawable: PaintDrawable): Drawable = drawable.apply {
        setCornerRadius(model.radius)
        intrinsicWidth = model.width
        intrinsicHeight = model.height
    }

    val baseSkeletonColorAttr = R.attr.colorOutline

    val shimmer = Shimmer.ColorHighlightBuilder()
        .setHighlightAlpha(0f)
        .setHighlightColor(getAttrColor(R.attr.colorOutlineVariant))
        .setBaseAlpha(1f)
        .setBaseColor(getAttrColor(baseSkeletonColorAttr))
        .setAutoStart(true)
        .build()
    val skeletonDrawable = SkeletonDrawable()
    skeletonDrawable.setShimmer(shimmer)

    val shadowBackground = setupSkeleton(PaintDrawable())
    val skeleton = setupSkeleton(skeletonDrawable)

    background = shadowBackground

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        backgroundTintList = getColorStateList(context, android.R.color.transparent)
        foreground = skeleton
        foregroundGravity = model.gravity
    } else {
        backgroundTintList = context.getColorStateListCompat(baseSkeletonColorAttr)
    }
}
