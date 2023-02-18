package com.thebrodyaga.englishsounds.base.di

import androidx.fragment.app.Fragment
import android.app.Activity
import com.google.gson.Gson
import com.thebrodyaga.base.navigation.api.LocalCiceroneHolder
import com.thebrodyaga.core.navigation.api.cicerone.NavigatorHolder
import com.thebrodyaga.core.navigation.api.cicerone.Router
import com.thebrodyaga.data.sounds.api.SettingManager
import com.thebrodyaga.data.sounds.api.SoundsRepository
import com.thebrodyaga.data.sounds.api.SoundsVideoRepository
import com.thebrodyaga.feature.audioPlayer.api.AudioPlayer
import com.thebrodyaga.feature.audioPlayer.api.RecordVoice
import com.thebrodyaga.feature.mainScreen.api.MainScreenFactory

fun Fragment.findComponent(): BaseAppComponent =
    (requireContext().applicationContext as ComponentHolder).component()

fun Activity.findComponent(): BaseAppComponent =
    (applicationContext as ComponentHolder).component()

interface ComponentHolder {
    fun component(): BaseAppComponent
}

interface BaseAppComponent {

    fun soundsRepository(): SoundsRepository
    fun soundsVideoRepository(): SoundsVideoRepository
    fun getRouter(): Router
    fun getLocalCiceroneHolder(): LocalCiceroneHolder
    fun recordVoice(): RecordVoice
    fun audioPlayer(): AudioPlayer
    fun settingManager(): SettingManager
    fun mainScreenFactory(): MainScreenFactory
    fun getNavigatorHolder(): NavigatorHolder
}