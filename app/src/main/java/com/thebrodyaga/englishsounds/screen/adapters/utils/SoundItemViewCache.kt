package com.thebrodyaga.englishsounds.screen.adapters.utils

import android.content.Context
import android.view.LayoutInflater
import androidx.cardview.widget.CardView
import com.thebrodyaga.englishsounds.R
import java.util.concurrent.atomic.AtomicInteger

class SoundItemViewCache constructor(private val context: Context) {

    private val cache: MutableMap<Int, CardView> = mutableMapOf()
    private var freeIdForView = AtomicInteger(1)

    fun setToCache(views: Sequence<CardView>) {
        views.forEach { setToCache(it) }
    }

    fun setToCache(view: CardView) {
        cache[view.id] = view
    }

    fun popViewFromCache(): CardView {
        val idKey = cache.keys.firstOrNull() ?: return createView()
        return cache[idKey]?.also {
            cache.remove(idKey)
        } ?: createView()
    }

    private fun createView(): CardView {
        val view =
            LayoutInflater.from(context).inflate(R.layout.item_sound_min, null) as CardView
        view.id = freeIdForView.getAndIncrement()
        return view
    }
}