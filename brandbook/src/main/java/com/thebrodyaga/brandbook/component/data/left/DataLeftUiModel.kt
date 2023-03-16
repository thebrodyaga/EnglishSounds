package com.thebrodyaga.brandbook.component.data.left

import com.thebrodyaga.brandbook.component.icon.IconWrapperUiModel
import com.thebrodyaga.brandbook.model.UiModel
import com.thebrodyaga.brandbook.utils.text.TextViewUiModel

sealed interface DataLeftUiModel : UiModel {

    data class TwoLineText(
        val firstLineText: TextViewUiModel? = null,
        val secondLineText: TextViewUiModel? = null,
    ) : DataLeftUiModel

    data class IconWithTwoLineText(
        val icon: IconWrapperUiModel? = null,
        val firstLineText: TextViewUiModel? = null,
        val secondLineText: TextViewUiModel? = null,
    ) : DataLeftUiModel
}