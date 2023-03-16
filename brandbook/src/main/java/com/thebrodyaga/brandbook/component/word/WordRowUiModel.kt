package com.thebrodyaga.brandbook.component.word

import com.thebrodyaga.brandbook.model.UiModel
import com.thebrodyaga.brandbook.model.UiModelPayload
import com.thebrodyaga.brandbook.utils.text.TextViewUiModel

data class WordRowUiModel(
    val text: TextViewUiModel,
    val isPlaying: Boolean,
    override val payload: Any? = null
) : UiModel, UiModelPayload
