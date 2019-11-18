package com.thebrodyaga.englishsounds.tools

import android.content.Context
import android.net.Uri
import com.google.android.exoplayer2.ExoPlayerFactory
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util.getUserAgent
import com.thebrodyaga.englishsounds.R
import java.io.File


class AudioPlayer constructor(
    context: Context
) {

    private var player = ExoPlayerFactory.newSimpleInstance(context)
    private var currentListener: Player.EventListener? = null
    private val dataSourceFactory = DefaultDataSourceFactory(
        context,
        getUserAgent(context, context.getString(R.string.app_name))
    )
    private val sourceFactory = ProgressiveMediaSource.Factory(dataSourceFactory)

    fun playAudio(audio: File, listener: Player.EventListener) {
        player.stop()
        currentListener?.also { player.removeListener(it) }
        currentListener = listener
        player.addListener(listener)
        val source = sourceFactory.createMediaSource(Uri.fromFile(audio))
        player.prepare(source)
        player.playWhenReady = true
    }

    fun stopPlay() {
        player.stop()
    }

    fun onAppHide() {
        stopPlay()
    }

    fun onAppShow() {

    }
}