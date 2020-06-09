package com.thebrodyaga.englishsounds.player

import android.content.Context
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.OnLifecycleEvent
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory
import com.google.android.exoplayer2.util.Util
import com.thebrodyaga.englishsounds.player.exo.ExoPlayerWrapper
import com.thebrodyaga.englishsounds.player.exo.PlayerAudioFocus
import timber.log.Timber


class PlayerHolder constructor(
    context: Context,
    lifecycleOwner: LifecycleOwner
) : LifecycleObserver {

    val exoPlayer: ExoPlayerWrapper

    private val playerState = PlayerState()
    private val dataSourceFactory: DataSource.Factory

    init {
        lifecycleOwner.lifecycle.addObserver(this)

        val player = SimpleExoPlayer.Builder(context).build()
        val playerAudioFocus = PlayerAudioFocus(context, player)

        exoPlayer = ExoPlayerWrapper(player, playerAudioFocus)

        dataSourceFactory = DefaultHttpDataSourceFactory(Util.getUserAgent(context, context.packageName))

        Timber.i("SimpleExoPlayer created")
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    fun test() {
        val mediaSource: MediaSource = ProgressiveMediaSource.Factory(dataSourceFactory)
            .createMediaSource(MediaCatalog().mediaCatalog.first().mediaUri)
        exoPlayer.prepare(mediaSource)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun start() {
        with(playerState) {
            // Start playback when media has buffered enough
            // (whenReady is true by default).
            exoPlayer.playWhenReady = whenReady
            exoPlayer.seekTo(window, position)
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun stop() {
        with(exoPlayer) {
            // Save state
            with(playerState) {
                position = currentPosition
                window = currentWindowIndex
                whenReady = playWhenReady
            }
            playWhenReady = false
        }
    }

    // Destroy the player instance.
    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun release() {
        exoPlayer.release() // player instance can't be used again.
    }
}