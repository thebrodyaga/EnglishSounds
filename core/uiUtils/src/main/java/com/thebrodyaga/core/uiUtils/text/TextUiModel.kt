package com.thebrodyaga.core.uiUtils.text

import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle

sealed interface TextUiModel {

    data class Raw(
        val text: TextWrap,
        val maxLines: Int? = null,
        val style: TextStyle? = null,
        val modifier: Modifier = Modifier,
    ) : TextUiModel

    /*data class Skeleton(
        val skeleton: SkeletonUiModel,
    ) : TextUiModel*/
}