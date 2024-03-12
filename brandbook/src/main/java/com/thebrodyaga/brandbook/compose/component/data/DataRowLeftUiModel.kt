package com.thebrodyaga.brandbook.compose.component.data

import com.thebrodyaga.brandbook.compose.component.image.ImageWrapperUiModel
import com.thebrodyaga.core.uiUtils.text.TextUiModel

sealed interface DataRowLeftUiModel {

    data class TwoLineText(
        val firstLineText: TextUiModel? = null,
        val secondLineText: TextUiModel? = null,
    ) : DataRowLeftUiModel

    data class IconWithTwoLineText(
        val icon: ImageWrapperUiModel? = null,
        val firstLineText: TextUiModel? = null,
        val secondLineText: TextUiModel? = null,
    ) : DataRowLeftUiModel
}