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

    fun getByTag(adTag: AdTag): NativeAdLoader {
        Timber.i("getByTag for adTag = $adTag")
        val key = adTag.name
        return get(key) ?: NativeAdLoader(context, lifecycle, adTag)
            .also {
                Timber.i("getByTag create new for adTag = $adTag")
                put(key, it)
            }
    }

    fun getByTagAndAdapterPosition(adTag: AdTag, adapterPosition: Int): NativeAdLoader {
        Timber.i("getByTagAndAdapterPosition for adTag = $adTag adapterPosition = $adapterPosition")
        val key = "${adTag.name} $adapterPosition"
        return get(key) ?: NativeAdLoader(context, lifecycle, adTag)
            .also {
                Timber.i("getByTagAndAdapterPosition create new for adTag = $adTag adapterPosition = $adapterPosition")
                put(key, it)
            }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun onDestroy() {
        Timber.i("CompositeAdLoader onDestroy")
        clear()
    }
}