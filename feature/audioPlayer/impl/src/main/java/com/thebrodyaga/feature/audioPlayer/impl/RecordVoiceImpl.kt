package com.thebrodyaga.feature.audioPlayer.impl

import android.content.Context
import android.media.MediaRecorder
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.thebrodyaga.feature.audioPlayer.api.AudioPlayer
import com.thebrodyaga.feature.audioPlayer.api.AudioPlayerState
import com.thebrodyaga.feature.audioPlayer.api.RecordState
import com.thebrodyaga.feature.audioPlayer.api.RecordVoice
import kotlinx.coroutines.flow.*
import timber.log.Timber
import java.io.File
import java.io.IOException
import javax.inject.Inject

class RecordVoiceImpl @Inject constructor(
    private val audioPlayer: AudioPlayer,
    private val activity: AppCompatActivity,
) : RecordVoice, DefaultLifecycleObserver {

    private val context: Context = activity
    private val currentState
        get() = state.value
    private var myAudioRecorder: MediaRecorder? = null
    private var outputFile = File(context.filesDir, "recording.m4a")
    override val state: MutableStateFlow<RecordState> = MutableStateFlow(RecordState.ReadyToRecord)

    init {
        activity.lifecycle.addObserver(this)
        state
            .onEach { Timber.i(it.toString()) }
            .catch { }
            .flowWithLifecycle(activity.lifecycle)
            .launchIn(activity.lifecycleScope)
        outputFile.createNewFile()
        audioPlayer.state()
            .filter {
                state.value is RecordState.Audio || state.value is RecordState.PlayingAudio
            }
            .onEach {
                when (it) {
                    is AudioPlayerState.Idle -> {
                        val isPlayingRecordNow = state.value is RecordState.PlayingAudio
                        when {
                            isPlayingRecordNow -> state.value = RecordState.Audio()
                        }
                    }
                    is AudioPlayerState.Playing -> {
                        val isRecordingFile = it.audioFile.path.contains("recording")
                        val isRecordIdleNow = state.value is RecordState.Audio
                        when {
                            isRecordingFile && isRecordIdleNow -> state.value = RecordState.PlayingAudio()
                        }
                    }
                }
            }
            .flowWithLifecycle(activity.lifecycle)
            .launchIn(activity.lifecycleScope)
    }

    override fun startRecord() {
        if (myAudioRecorder == null)
            prepareRecord()
        try {
            myAudioRecorder?.start()
            state.value = (RecordState.Recording)
        } catch (e: RuntimeException) {
            // RuntimeException is thrown when stop() is called immediately after start().
            // In this case the output file is not properly constructed ans should be deleted.
            Timber.d("RuntimeException: start() is throw exception)")
            releaseMediaRecorder()
            state.value = (RecordState.ReadyToRecord)
        }
    }

    override fun stopRecord() {
        if (currentState == RecordState.Recording) {
            try {
                myAudioRecorder?.stop()  // stop the recording
                state.value = (RecordState.Audio(isWhenPlayingChanged = false))
            } catch (e: RuntimeException) {
                // RuntimeException is thrown when stop() is called immediately after start().
                // In this case the output file is not properly constructed ans should be deleted.
                Timber.d("RuntimeException: stop() is called immediately after start()")
                releaseMediaRecorder()
                state.value = (RecordState.ReadyToRecord)
            }
        }
    }

    override fun clearRecord() {
        audioPlayer.stopPlay()
        releaseMediaRecorder()
        state.value = (RecordState.ReadyToRecord)
    }

    override fun playRecord() {
        if (outputFile.exists()) {
            audioPlayer.playAudio(outputFile)
        }
    }

    override fun stopPlayRecord() {
        if (currentState is RecordState.PlayingAudio)
            audioPlayer.stopPlay()
    }

    override fun onPause(owner: LifecycleOwner) {
        super.onPause(owner)
        if (currentState == RecordState.Recording)
            stopRecord()
    }

    private fun prepareRecord() {
        val mediaRecorder =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S)
                MediaRecorder(activity) else MediaRecorder()
        mediaRecorder.apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
            setAudioEncoder(MediaRecorder.AudioEncoder.AMR_WB)
            setAudioSamplingRate(44100)

            setOutputFile(outputFile.absolutePath)
            try {
                prepare()
                myAudioRecorder = this
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