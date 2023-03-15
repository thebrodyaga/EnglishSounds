package com.thebrodyaga.core.uiUtils.image

import androidx.annotation.DrawableRes
import android.widget.ImageView

sealed interface IconContainer {
    data class Res(@DrawableRes val resId: Int) : IconContainer

    data class Url(val url: String) : IconContainer
}

fun ImageView.bind(iconContainer: IconContainer) {
    when (iconContainer) {
        is IconContainer.Res -> setImageResource(iconContainer.resId)
        is IconContainer.Url -> TODO()
    }
}