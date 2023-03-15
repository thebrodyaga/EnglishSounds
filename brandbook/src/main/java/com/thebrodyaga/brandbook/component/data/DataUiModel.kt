package com.thebrodyaga.brandbook.component.data

import com.thebrodyaga.brandbook.recycler.model.UiModel
import com.thebrodyaga.brandbook.recycler.model.UiModelPayload

data class DataUiModel(
    override val payload: Any? = null
) : UiModel, UiModelPayload
