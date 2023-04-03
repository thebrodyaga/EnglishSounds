package com.thebrodyaga.feature.audioPlayer.api

import java.io.File

sealed interface AudioPlayerState {

    object Idle : AudioPlayerState

    data class Playing(
        val audioFile: File
    ) : AudioPlayerState
}