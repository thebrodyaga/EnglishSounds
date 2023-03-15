package com.thebrodyaga.core.uiUtils.text

import androidx.annotation.StringRes
import android.widget.TextView

sealed interface TextContainer {
    data class Res(@StringRes val resId: Int) : TextContainer

    data class ResParams(
        @StringRes val value: Int,
        val args: List<Any>
    ) : TextContainer

    data class Raw(val text: CharSequence) : TextContainer
}

fun TextView.bind(textContainer: TextContainer) {
    when (textContainer) {
        is TextContainer.Res -> setText(textContainer.resId)
        is TextContainer.ResParams ->
            text = context.getString(textContainer.value, *textContainer.args.toTypedArray())
        is TextContainer.Raw -> text = textContainer.text
    }
}
