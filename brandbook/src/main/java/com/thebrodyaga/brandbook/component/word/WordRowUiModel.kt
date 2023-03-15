package com.thebrodyaga.brandbook.component.word

import com.thebrodyaga.brandbook.recycler.model.UiModel
import com.thebrodyaga.brandbook.recycler.model.UiModelPayload
import com.thebrodyaga.core.uiUtils.text.TextViewUiModel

data class WordRowUiModel(
    val text: TextViewUiModel,
    val isPlaying: Boolean,
    override val payload: Any? = null
) : UiModel, UiModelPayload
