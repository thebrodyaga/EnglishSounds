package com.thebrodyaga.feature.audioPlayer.api

import java.io.File

sealed interface AudioPlayerState {

    data class Idle(
        val audioFile: File,
    ) : AudioPlayerState

    data class Playing(
        val audioFile: File
    ) : AudioPlayerState
}