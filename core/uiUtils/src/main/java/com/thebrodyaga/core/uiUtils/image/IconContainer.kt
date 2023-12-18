package com.thebrodyaga.core.uiUtils.image

import android.graphics.drawable.Drawable
import android.widget.ImageView
import androidx.annotation.DrawableRes

sealed interface IconContainer {
    data class Res(@DrawableRes val resId: Int) : IconContainer
    data class Draw(val drawable: Drawable) : IconContainer

    data class Url(val url: String) : IconContainer
}

fun ImageView.bind(iconContainer: IconContainer) {
    when (iconContainer) {
        is IconContainer.Res -> setImageResource(iconContainer.resId)
        is IconContainer.Url -> TODO()
        is IconContainer.Draw -> setImageDrawable(iconContainer.drawable)
    }
}