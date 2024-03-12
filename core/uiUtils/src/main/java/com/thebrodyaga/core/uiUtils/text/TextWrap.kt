package com.thebrodyaga.core.uiUtils.text

import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString

sealed interface TextWrap {
    data class Res(@StringRes val resId: Int) : TextWrap

    data class ResParams(
        @StringRes val value: Int,
        val args: List<Any>,
    ) : TextWrap

    data class Raw(val text: AnnotatedString) : TextWrap {
        constructor(string: String) : this(text = AnnotatedString(string))
    }

    val annotated: AnnotatedString
        @Composable
        get() = when (this) {
            is Raw -> text
            is Res -> AnnotatedString(stringResource(this.resId))
            is ResParams -> AnnotatedString(stringResource(value, args))
        }
}
