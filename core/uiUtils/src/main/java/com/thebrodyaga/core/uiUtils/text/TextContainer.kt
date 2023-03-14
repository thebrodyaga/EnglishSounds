package com.thebrodyaga.core.uiUtils.text

import androidx.annotation.StringRes
import android.widget.TextView

sealed interface TextContainer {
    data class Resource(@StringRes val resId: Int) : TextContainer

    data class ResourceParams(
        @StringRes val value: Int,
        val args: List<Any>
    ) : TextContainer

    data class Simple(val text: CharSequence) : TextContainer
}

fun TextView.bind(textContainer: TextContainer) {
    when (textContainer) {
        is TextContainer.Resource -> setText(textContainer.resId)
        is TextContainer.ResourceParams ->
            text = context.getString(textContainer.value, *textContainer.args.toTypedArray())
        is TextContainer.Simple -> text = textContainer.text
    }
}
