package com.thebrodyaga.feature.audioPlayer.impl

import android.content.Context
import android.net.Uri
import com.google.android.exoplayer2.ExoPlayerFactory
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util.getUserAgent
import com.thebrodyaga.feature.audioPlayer.api.AudioPlayer
import java.io.File

class AudioPlayerImpl constructor(
    context: Context
) : AudioPlayer {

    private var player = ExoPlayerFactory.newSimpleInstance(context)
    private var currentListener: Player.EventListener? = null
    private val dataSourceFactory = DefaultDataSourceFactory(
        context,
        getUserAgent(context, "English Sounds")
    )
    private val sourceFactory = ProgressiveMediaSource.Factory(dataSourceFactory)

    override fun playAudio(audio: File, onIsPlayingChanged: (isPlaying: Boolean) -> Unit) {
        player.stop()
        currentListener?.also { player.removeListener(it) }
        val listener = object :Player.EventListener{
            override fun onIsPlayingChanged(isPlaying: Boolean) {
                onIsPlayingChanged(isPlaying)
            }
        }
        currentListener = listener
        player.addListener(listener)
        val source = sourceFactory.createMediaSource(Uri.fromFile(audio))
        player.prepare(source)
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