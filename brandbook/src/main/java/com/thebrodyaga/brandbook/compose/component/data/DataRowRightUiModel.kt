package com.thebrodyaga.brandbook.compose.component.data

import com.thebrodyaga.core.uiUtils.text.TextUiModel

sealed interface DataRowRightUiModel {

    data class TwoLineText(
        val firstLineText: TextUiModel? = null,
        val secondLineText: TextUiModel? = null,
    ) : DataRowRightUiModel

    /*data class TextWithIcon(
        val text: TextViewUiModel? = null,
        val icon: ImageViewUiModel? = null,
    ) : DataRowRightUiModel

    data class PlayIcon(
        val playIcon: PlayButtonUiModel,
    ) : DataRowRightUiModel

    sealed interface Button : DataRowRightUiModel {
        data class Text(
            val text: TextViewUiModel? = null,
        ) : Button
    }*/
}