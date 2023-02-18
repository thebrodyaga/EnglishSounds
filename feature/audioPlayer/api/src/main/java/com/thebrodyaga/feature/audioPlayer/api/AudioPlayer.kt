package com.thebrodyaga.feature.audioPlayer.api

import java.io.File

interface AudioPlayer {
    fun onAppHide()
    fun onAppShow()
    fun stopPlay()
    fun playAudio(audio: File, onIsPlayingChanged: (isPlaying: Boolean) -> Unit)
}