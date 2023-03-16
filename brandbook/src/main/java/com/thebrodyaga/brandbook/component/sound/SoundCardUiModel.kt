package com.thebrodyaga.brandbook.component.sound

import androidx.annotation.ColorRes
import com.thebrodyaga.brandbook.model.UiModel
import com.thebrodyaga.brandbook.model.UiModelPayload
import com.thebrodyaga.core.uiUtils.text.TextViewUiModel

data class SoundCardUiModel(
    val transcription: TextViewUiModel,
    val word: TextViewUiModel,
    @ColorRes val transcriptionTint: Int,
    override val payload: Any? = null
) : UiModel, UiModelPayload
