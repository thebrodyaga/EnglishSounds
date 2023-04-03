package com.thebrodyaga.feature.audioPlayer.impl

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.thebrodyaga.feature.audioPlayer.api.AudioPlayer
import java.io.File
import javax.inject.Inject

class AudioPlayerImpl @Inject constructor(
    activity: AppCompatActivity
) : AudioPlayer {

    private var player = ExoPlayer.Builder(activity).build()
    private var currentListener: Player.Listener? = null

    override fun playAudio(audio: File, onIsPlayingChanged: (isPlaying: Boolean) -> Unit) {
        player.stop()
        currentListener?.also { player.removeListener(it) }
        val listener = object : Player.Listener {
            override fun onIsPlayingChanged(isPlaying: Boolean) {
                onIsPlayingChanged(isPlaying)
            }
        }
        currentListener = listener
        player.addListener(listener)
        val mediaItem: MediaItem = MediaItem.fromUri(Uri.fromFile(audio))
        player.setMediaItem(mediaItem);
        player.prepare()
        player.playWhenReady = true
    }

    override fun stopPlay() {
        player.stop()
    }

    override fun onAppHide() {
        stopPlay()
    }

    override fun onAppShow() {
    }
}