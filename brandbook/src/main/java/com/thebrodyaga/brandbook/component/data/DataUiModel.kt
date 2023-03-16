package com.thebrodyaga.brandbook.component.data

import com.thebrodyaga.brandbook.component.data.left.DataLeftUiModel
import com.thebrodyaga.brandbook.component.data.right.DataRightUiModel
import com.thebrodyaga.brandbook.model.UiModel
import com.thebrodyaga.brandbook.model.UiModelPayload

data class DataUiModel(
    val leftSide: DataLeftUiModel? = null,
    val rightSide: DataRightUiModel? = null,
    override val payload: Any? = null
) : UiModel, UiModelPayload
