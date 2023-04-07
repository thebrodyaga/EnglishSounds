package com.thebrodyaga.feature.audioPlayer.api

import java.io.File

sealed interface AudioPlayerState {
    val audioFile: File

    data class Idle(
        override val audioFile: File,
    ) : AudioPlayerState

    data class Playing(
        override val audioFile: File
    ) : AudioPlayerState
}