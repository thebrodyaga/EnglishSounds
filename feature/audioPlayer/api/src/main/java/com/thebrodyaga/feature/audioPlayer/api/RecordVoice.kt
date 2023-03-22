package com.thebrodyaga.feature.audioPlayer.api

import kotlinx.coroutines.flow.StateFlow

interface RecordVoice {
    fun onAppHide()
    fun onAppShow()
    fun stopPlayRecord()
    fun playRecord()
    fun clearRecord()
    fun stopRecord()
    fun startRecord()
    val state: StateFlow<RecordState>
}


enum class RecordState {
    EMPTY, RECORDING, AUDIO, PLAYING_AUDIO
}