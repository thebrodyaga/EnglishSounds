package com.thebrodyaga.brandbook.compose.component.text

import com.thebrodyaga.core.uiUtils.text.TextUiModel
import com.thebrodyaga.core.uiUtils.text.TextWrap

internal object TextTesting {

    internal fun shortText(
        str: String = getRandomString(6),
    ): TextUiModel = TextUiModel.Raw(
        text = TextWrap.Raw(str),
    )

    internal fun shortText(
        int: Int,
    ): TextUiModel = shortText(int.toString())

    internal fun longText(
        str: String = getRandomString(),
    ): TextUiModel = TextUiModel.Raw(
        text = TextWrap.Raw(str),
    )

    internal fun singleLine(
        str: String = getRandomString(),
    ): TextUiModel = lineText(1, str)


    internal fun twoLine(
        str: String = getRandomString(),
    ): TextUiModel = lineText(2, str)

    internal fun threeLine(
        str: String = getRandomString(),
    ): TextUiModel = lineText(3, str)

    internal fun lineText(
        maxLines: Int = Int.MAX_VALUE,
        str: String = getRandomString(),
    ): TextUiModel = TextUiModel.Raw(
        text = TextWrap.Raw(str),
        maxLines = maxLines,
    )

    internal fun getRandomString(length: Int = 1000): String {
        val allowedChars = ('A'..'Z') + ('a'..'z') + ('0'..'9')
        return (1..length)
            .map {
                if (it % 10 == 0) " " else allowedChars.random()
            }
            .joinToString("")
    }
}