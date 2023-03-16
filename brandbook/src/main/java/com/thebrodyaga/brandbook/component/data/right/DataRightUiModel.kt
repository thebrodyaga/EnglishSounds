package com.thebrodyaga.brandbook.component.data.right

import com.thebrodyaga.brandbook.model.UiModel
import com.thebrodyaga.brandbook.utils.image.ImageViewUiModel
import com.thebrodyaga.brandbook.utils.text.TextViewUiModel

sealed interface DataRightUiModel : UiModel {

    data class TwoLineText(
        val firstLineText: TextViewUiModel? = null,
        val secondLineText: TextViewUiModel? = null,
    ) : DataRightUiModel

    data class TextWithIcon(
        val text: TextViewUiModel? = null,
        val icon: ImageViewUiModel? = null,
    ) : DataRightUiModel
}