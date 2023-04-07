package com.thebrodyaga.feature.audioPlayer.api

import kotlinx.coroutines.flow.StateFlow

interface RecordVoice {
    fun stopPlayRecord()
    fun playRecord()
    fun clearRecord()
    fun stopRecord()
    fun startRecord()
    val state: StateFlow<RecordState>
}


sealed interface RecordState {
    object ReadyToRecord : RecordState
    object Recording : RecordState
    data class Audio(
        val isWhenPlayingChanged: Boolean = true
    ) : RecordState

    data class PlayingAudio(
        val isPlayingChanged: Boolean = true
    ) : RecordState
}