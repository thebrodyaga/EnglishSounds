package com.thebrodyaga.core.uiUtils.skeleton

import androidx.annotation.Px
import android.view.Gravity
import android.view.View

data class SkeletonUiModel(
    @Px val height: Int,
    @Px val width: Int,
    @Px val radius: Float = 0f,
    val gravity: Int = Gravity.CENTER,
)

fun View.bindSkeleton(model: SkeletonUiModel) {

    // todo
    /*fun setupSkeleton(drawable: PaintDrawable): Drawable = drawable.apply {
        setCornerRadius(model.radius)
        intrinsicWidth = model.width
        intrinsicHeight = model.height
    }

    val shimmer = Shimmer.ColorHighlightBuilder()
        .setHighlightAlpha(0f)
        .setHighlightColor(getColor(R.color.bg_snow))
        .setBaseAlpha(1f)
        .setBaseColor(getColor(R.color.bg_rain))
        .setAutoStart(true)
        .build()
    val skeletonDrawable = SkeletonDrawable()
    skeletonDrawable.setShimmer(shimmer)

    val shadowBackground = setupSkeleton(PaintDrawable())
    val skeleton = setupSkeleton(skeletonDrawable)

    background = shadowBackground
    backgroundTintList = getColorStateList(context, android.R.color.transparent)
    foreground = skeleton
    foregroundGravity = model.gravity*/
}
