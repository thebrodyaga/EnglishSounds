package com.thebrodyaga.feature.audioPlayer.api

import com.jakewharton.rxrelay2.Relay

interface RecordVoice {
    fun onAppHide()
    fun onAppShow()
    fun stopPlayRecord()
    fun playRecord()
    fun clearRecord()
    fun stopRecord()
    fun startRecord()
    val stateSubject: Relay<RecordState>
}


enum class RecordState {
    EMPTY, RECORDING, AUDIO, PLAYING_AUDIO
}