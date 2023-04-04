package com.thebrodyaga.brandbook.component.data.right

import com.thebrodyaga.brandbook.component.play.PlayButtonUiModel
import com.thebrodyaga.brandbook.model.UiModel
import com.thebrodyaga.core.uiUtils.image.ImageViewUiModel
import com.thebrodyaga.core.uiUtils.text.TextViewUiModel

sealed interface DataRightUiModel : UiModel {

    data class TwoLineText(
        val firstLineText: TextViewUiModel? = null,
        val secondLineText: TextViewUiModel? = null,
    ) : DataRightUiModel

    data class TextWithIcon(
        val text: TextViewUiModel? = null,
        val icon: ImageViewUiModel? = null,
    ) : DataRightUiModel

    data class PlayIcon(
        val playIcon: PlayButtonUiModel,
    ) : DataRightUiModel

    sealed interface Button : DataRightUiModel {
        data class Text(
            val text: TextViewUiModel? = null,
        ) : Button
    }
}