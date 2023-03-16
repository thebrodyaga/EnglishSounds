package com.thebrodyaga.brandbook.utils.skeleton

import androidx.annotation.Px
import android.view.Gravity
import com.thebrodyaga.brandbook.utils.text.TextViewUiModel

fun textViewSkeleton(
    @Px height: Int,
    @Px width: Int,
    @Px radius: Float = 0f,
    gravity: Int = Gravity.CENTER,
): TextViewUiModel.Skeleton = TextViewUiModel.Skeleton(
    roundedSkeleton(
        height = height,
        width = width,
        radius = radius,
        gravity = gravity,
    )
)

fun roundedSkeleton(
    @Px height: Int,
    @Px width: Int,
    @Px radius: Float = 0f,
    gravity: Int = Gravity.CENTER,
): SkeletonUiModel = SkeletonUiModel(
    height = height,
    width = width,
    radius = radius,
    gravity = gravity,
)
