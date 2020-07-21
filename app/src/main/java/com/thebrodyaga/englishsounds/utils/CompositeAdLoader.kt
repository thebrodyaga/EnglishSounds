package com.thebrodyaga.englishsounds.utils

import android.content.Context
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import com.thebrodyaga.englishsounds.domine.entities.data.AdTag
import timber.log.Timber

class CompositeAdLoader constructor(
    private val context: Context,
    private val lifecycle: Lifecycle
) : MutableMap<String, NativeAdLoader> by mutableMapOf(), LifecycleObserver {

    init {
        lifecycle.addObserver(this)
    }

    fun getLoader(
        adTag: AdTag,
        adapterPosition: Int? = null,
        customTag: String? = null
    ): NativeAdLoader {
        var key = adTag.name
        if (adapterPosition != null)
            key = key.plus(" $adapterPosition")
        if (customTag != null)
            key = key.plus(" $customTag")
        Timber.i("getLoader for $key")
        return get(key) ?: NativeAdLoader(context, lifecycle, adTag)
            .also {
                Timber.i("getLoader create new for $key")
                put(key, it)
            }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun onDestroy() {
        Timber.i("CompositeAdLoader onDestroy")
        clear()
    }
}