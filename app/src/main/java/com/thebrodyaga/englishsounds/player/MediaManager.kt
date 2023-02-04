package com.thebrodyaga.englishsounds.player

import android.content.Context
import android.support.v4.media.session.MediaControllerCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.OnLifecycleEvent

class MediaManager constructor(
    private val context: Context,
    private val playerHolder: PlayerHolder,
    lifecycleOwner: LifecycleOwner
) : LifecycleObserver {

    lateinit var mediaController: MediaControllerCompat
        private set

    init {
        lifecycleOwner.lifecycle.addObserver(this)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    fun onCreate() {
    }
}