package com.thebrodyaga.feature.audioPlayer.impl

import android.app.Application
import android.content.Context
import android.media.MediaRecorder
import com.thebrodyaga.core.utils.coroutines.AppScope
import com.thebrodyaga.feature.audioPlayer.api.AudioPlayer
import com.thebrodyaga.feature.audioPlayer.api.RecordState
import com.thebrodyaga.feature.audioPlayer.api.RecordVoice
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import timber.log.Timber
import java.io.File
import java.io.IOException
import javax.inject.Inject

class RecordVoiceImpl @Inject constructor(
    private val audioPlayer: AudioPlayer,
    private val application: Application,
    private val appScope: AppScope,
) : RecordVoice {

    private val context: Context = application
    private var currentState = RecordState.EMPTY
    private var myAudioRecorder: MediaRecorder? = null
    private var outputFile = File(context.filesDir, "recording.m4a")
    override val state: MutableStateFlow<RecordState> = MutableStateFlow(RecordState.EMPTY)

    init {
        state
            .onEach { Timber.i(it.toString()) }
            .onEach { currentState = it }
            .catch { }
            .launchIn(appScope)
        outputFile.createNewFile()
    }

    override fun startRecord() {
        if (myAudioRecorder == null)
            prepareRecord()
        try {
            myAudioRecorder?.start()
            state.value = (RecordState.RECORDING)
        } catch (e: RuntimeException) {
            // RuntimeException is thrown when stop() is called immediately after start().
            // In this case the output file is not properly constructed ans should be deleted.
            Timber.d("RuntimeException: start() is throw exception)")
            releaseMediaRecorder()
            state.value = (RecordState.EMPTY)
        }
    }

    override fun stopRecord() {
        if (currentState == RecordState.RECORDING) {
            try {
                myAudioRecorder?.stop()  // stop the recording
                state.value = (RecordState.AUDIO)
            } catch (e: RuntimeException) {
                // RuntimeException is thrown when stop() is called immediately after start().
                // In this case the output file is not properly constructed ans should be deleted.
                Timber.d("RuntimeException: stop() is called immediately after start()")
                releaseMediaRecorder()
                state.value = (RecordState.EMPTY)
            }
        }
    }

    override fun clearRecord() {
        audioPlayer.stopPlay()
        state.value = (RecordState.EMPTY)
    }

    override fun playRecord() {
        if (outputFile.exists())
            audioPlayer.playAudio(outputFile) /*{ isPlaying ->
                state.value = (if (isPlaying) RecordState.PLAYING_AUDIO else RecordState.AUDIO)
            }*/
    }

    override fun stopPlayRecord() {
        if (currentState == RecordState.PLAYING_AUDIO)
            audioPlayer.stopPlay()
    }

    override fun onAppShow() {
//        prepareRecord()
    }

    override fun onAppHide() {
        if (currentState == RecordState.RECORDING)
            stopRecord()
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
}