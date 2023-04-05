package com.thebrodyaga.feature.audioPlayer.api

import kotlinx.coroutines.flow.StateFlow
import java.io.File

interface AudioPlayer {
    fun state(): StateFlow<AudioPlayerState>
    fun stopPlay()
    fun playAudio(audio: File): StateFlow<AudioPlayerState>
}