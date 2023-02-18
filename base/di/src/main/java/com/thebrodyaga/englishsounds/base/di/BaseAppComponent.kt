package com.thebrodyaga.englishsounds.base.di

import androidx.fragment.app.Fragment
import com.thebrodyaga.data.sounds.api.SoundsRepository
import com.thebrodyaga.data.sounds.api.SoundsVideoRepository

fun Fragment.findComponent(): BaseAppComponent =
    (requireContext().applicationContext as ComponentHolder).component()

interface ComponentHolder {
    fun component(): BaseAppComponent
}

interface BaseAppComponent {

    fun soundsRepository(): SoundsRepository
    fun soundsVideoRepository(): SoundsVideoRepository
}