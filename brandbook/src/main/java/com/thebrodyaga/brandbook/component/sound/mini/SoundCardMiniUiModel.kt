package com.thebrodyaga.brandbook.component.sound.mini

import com.thebrodyaga.brandbook.model.UiModel
import com.thebrodyaga.brandbook.model.UiModelPayload
import com.thebrodyaga.brandbook.utils.drawable.DrawableUiModel
import com.thebrodyaga.brandbook.utils.text.TextViewUiModel

data class SoundCardMiniUiModel(
    val transcription: TextViewUiModel,
    val transcriptionBg: DrawableUiModel,
    override val payload: Any? = null
) : UiModel, UiModelPayload
