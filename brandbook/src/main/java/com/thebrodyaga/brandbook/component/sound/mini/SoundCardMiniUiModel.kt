package com.thebrodyaga.brandbook.component.sound.mini

import com.thebrodyaga.brandbook.model.UiModel
import com.thebrodyaga.brandbook.model.UiModelPayload
import com.thebrodyaga.core.uiUtils.drawable.DrawableUiModel
import com.thebrodyaga.core.uiUtils.text.TextViewUiModel

data class SoundCardMiniUiModel(
    val transcription: TextViewUiModel,
    val transcriptionBg: DrawableUiModel,
    override val payload: Any? = null
) : UiModel, UiModelPayload
