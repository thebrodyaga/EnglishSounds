package com.thebrodyaga.brandbook.component.sound

import com.thebrodyaga.brandbook.model.UiModel
import com.thebrodyaga.brandbook.model.UiModelPayload
import com.thebrodyaga.brandbook.utils.drawable.DrawableUiModel
import com.thebrodyaga.brandbook.utils.text.TextViewUiModel

data class SoundCardUiModel(
    val transcription: TextViewUiModel,
    val transcriptionBg: DrawableUiModel,
    val word: TextViewUiModel,
    override val payload: Any? = null
) : UiModel, UiModelPayload
