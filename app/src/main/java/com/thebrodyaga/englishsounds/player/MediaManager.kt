package com.thebrodyaga.englishsounds.player

import android.content.Context
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.MediaSessionCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.OnLifecycleEvent
import com.google.android.exoplayer2.ext.mediasession.MediaSessionConnector

class MediaManager constructor(
    private val context: Context,
    private val playerHolder: PlayerHolder,
    lifecycleOwner: LifecycleOwner
) : LifecycleObserver {

    lateinit var mediaController: MediaControllerCompat
        private set
    lateinit var mediaSession: MediaSessionConnector
        private set

    init {
        lifecycleOwner.lifecycle.addObserver(this)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    fun onCreate() {
        mediaSession = createMediaSession()
        mediaController = createMediaController(mediaSession)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun onStart() {
        activateMediaSession()
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun onStop() {
        deactivateMediaSession()
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun onDestroy() {
        mediaSession.mediaSession.release()
    }

    private fun activateMediaSession() {
        mediaSession.setPlayer(playerHolder.exoPlayer)
        mediaSession.mediaSession.isActive = true
    }

    private fun deactivateMediaSession() {
        mediaSession.setPlayer(null)
        mediaSession.mediaSession.isActive = false
    }

    private fun createMediaSession(): MediaSessionConnector {
        val session = MediaSessionCompat(context, context.packageName)
        return MediaSessionConnector(session)
    }

    private fun createMediaController(mediaSession: MediaSessionConnector): MediaControllerCompat {
        return MediaControllerCompat(context, mediaSession.mediaSession)
    }
}