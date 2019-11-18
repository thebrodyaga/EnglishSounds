package com.thebrodyaga.englishsounds.tools

import android.content.Context
import android.media.MediaRecorder
import com.google.android.exoplayer2.Player
import com.jakewharton.rxrelay2.BehaviorRelay
import com.jakewharton.rxrelay2.Relay
import timber.log.Timber
import java.io.File
import java.io.IOException


class RecordVoice constructor(
    private val audioPlayer: AudioPlayer,
    private val context: Context
) : Player.EventListener {

    private var currentState = RecordState.EMPTY
    private var myAudioRecorder: MediaRecorder? = null
    private var outputFile = File(context.filesDir, "recording.m4a")
    val stateSubject: Relay<RecordState> = BehaviorRelay.createDefault(RecordState.EMPTY)

    init {
        val state = stateSubject
            .doOnNext { Timber.i(it.toString()) }
            .subscribe { currentState = it }
        outputFile.createNewFile()
    }

    fun startRecord() {
        if (myAudioRecorder == null)
            prepareRecord()
        try {
            myAudioRecorder?.start()
            stateSubject.accept(RecordState.RECORDING)
        } catch (e: RuntimeException) {
            // RuntimeException is thrown when stop() is called immediately after start().
            // In this case the output file is not properly constructed ans should be deleted.
            Timber.d("RuntimeException: start() is throw exception)")
            releaseMediaRecorder()
            stateSubject.accept(RecordState.EMPTY)
        }
    }

    fun stopRecord() {
        if (currentState == RecordState.RECORDING) {
            try {
                myAudioRecorder?.stop()  // stop the recording
                stateSubject.accept(RecordState.AUDIO)
            } catch (e: RuntimeException) {
                // RuntimeException is thrown when stop() is called immediately after start().
                // In this case the output file is not properly constructed ans should be deleted.
                Timber.d("RuntimeException: stop() is called immediately after start()")
                releaseMediaRecorder()
                stateSubject.accept(RecordState.EMPTY)
            }

        }
    }

    fun clearRecord() {
        audioPlayer.stopPlay()
        stateSubject.accept(RecordState.EMPTY)
    }

    fun playRecord() {
        if (outputFile.exists())
            audioPlayer.playAudio(outputFile, this)
    }

    fun stopPlayRecord() {
        if (currentState == RecordState.PLAYING_AUDIO)
            audioPlayer.stopPlay()
    }

    fun onAppShow() {
//        prepareRecord()
    }

    fun onAppHide() {
        if (currentState == RecordState.RECORDING)
            stopRecord()
    }

    override fun onIsPlayingChanged(isPlaying: Boolean) {
        stateSubject.accept(if (isPlaying) RecordState.PLAYING_AUDIO else RecordState.AUDIO)
    }

    private fun prepareRecord() {
        MediaRecorder().also { myAudioRecorder ->
            myAudioRecorder.setAudioSource(MediaRecorder.AudioSource.MIC)
            myAudioRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
            myAudioRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB)
            myAudioRecorder.setAudioEncodingBitRate(16 * 44100)
            myAudioRecorder.setAudioSamplingRate(44100)

            myAudioRecorder.setOutputFile(outputFile.absolutePath)
            try {
                myAudioRecorder.prepare()
                this.myAudioRecorder = myAudioRecorder
            } catch (e: IllegalStateException) {
                Timber.d("IllegalStateException preparing MediaRecorder: %s", e.message)
                releaseMediaRecorder()
            } catch (e: IOException) {
                Timber.d("IOException preparing MediaRecorder: %s", e.message)
                releaseMediaRecorder()
            }
        }
    }

    private fun releaseMediaRecorder() {
        myAudioRecorder?.also { myAudioRecorder ->
            // clear recorder configuration
            myAudioRecorder.reset()
            // release the recorder object
            myAudioRecorder.release()
            // Lock camera for later use i.e taking it back from MediaRecorder.
            // MediaRecorder doesn't need it anymore and we will release it if the activity pauses.
            this.myAudioRecorder = null
        }
    }

    enum class RecordState {
        EMPTY, RECORDING, AUDIO, PLAYING_AUDIO
    }
}