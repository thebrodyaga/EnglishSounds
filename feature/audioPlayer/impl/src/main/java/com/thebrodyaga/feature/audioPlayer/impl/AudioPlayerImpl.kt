package com.thebrodyaga.feature.audioPlayer.impl

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.thebrodyaga.feature.audioPlayer.api.AudioPlayer
import com.thebrodyaga.feature.audioPlayer.api.AudioPlayerState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.io.File
import javax.inject.Inject

class AudioPlayerImpl @Inject constructor(
    activity: AppCompatActivity
) : AudioPlayer, DefaultLifecycleObserver {

    private var player = ExoPlayer.Builder(activity).build()
    private var currentAudio: File? = null
    private val state = MutableStateFlow<AudioPlayerState>(AudioPlayerState.Idle(File("")))
    private var listener: Player.Listener? = null

    init {
        activity.lifecycle.addObserver(this)
    }

    override fun playAudio(audio: File): StateFlow<AudioPlayerState> {
        player.stop()
        state.value = AudioPlayerState.Idle(audio)
        currentAudio = audio
        this.listener?.let { player.removeListener(it) }
        val listener = object : Player.Listener {
            override fun onIsPlayingChanged(isPlaying: Boolean) {
                if (isPlaying) state.value = AudioPlayerState.Playing(audio)
                else state.value = AudioPlayerState.Idle(audio)
            }
        }
        this.listener = listener
        player.addListener(listener)
        val mediaItem: MediaItem = MediaItem.fromUri(Uri.fromFile(audio))
        player.setMediaItem(mediaItem);
        player.prepare()
        player.play()
        return state()
    }

    override fun state(): StateFlow<AudioPlayerState> = state.asStateFlow()

    override fun stopPlay() {
        player.stop()
    }

    override fun onPause(owner: LifecycleOwner) {
        stopPlay()
    }
}