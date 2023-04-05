package com.thebrodyaga.brandbook.component.play

import androidx.annotation.AttrRes
import com.thebrodyaga.brandbook.R
import com.thebrodyaga.brandbook.model.UiModel

data class PlayButtonUiModel(
    val state: PlayButtonBindingState,
    @AttrRes val iconTint: Int? = R.attr.colorPrimary,
) : UiModel

sealed interface PlayButtonBindingState {
    val force: Boolean

    data class PlayToPause(
        override val force: Boolean = false
    ) : PlayButtonBindingState

    data class PauseToPlay(
        override val force: Boolean = false
    ) : PlayButtonBindingState
}