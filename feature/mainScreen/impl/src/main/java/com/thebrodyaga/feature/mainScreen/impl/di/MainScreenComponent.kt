package com.thebrodyaga.feature.mainScreen.impl.di

import androidx.fragment.app.Fragment
import com.thebrodyaga.englishsounds.base.di.*
import com.thebrodyaga.feature.audioPlayer.api.AudioPlayer
import com.thebrodyaga.feature.audioPlayer.api.RecordVoice
import com.thebrodyaga.feature.mainScreen.api.MainScreenFactory
import com.thebrodyaga.feature.mainScreen.impl.MainFragment
import com.thebrodyaga.feature.mainScreen.impl.TabContainerFragment
import com.thebrodyaga.feature.setting.api.SettingsScreenFactory
import com.thebrodyaga.feature.soundList.api.SoundListScreenFactory
import com.thebrodyaga.feature.training.api.TrainingScreenFactory
import com.thebrodyaga.feature.videoList.api.VideoScreenFactory
import dagger.Component

@[FeatureScope Component(
    dependencies = [
        MainScreenDependencies::class,
        MainScreenActivityDependencies::class,
    ]
)]
interface MainScreenComponent {

    @Component.Factory
    interface Factory {
        fun create(
            dependencies: MainScreenDependencies,
            activityDependencies: MainScreenActivityDependencies,
        ): MainScreenComponent
    }

    fun inject(fragment: MainFragment)
    fun inject(fragment: TabContainerFragment)

    companion object {

        fun factory(
            fragment: Fragment,
        ): MainScreenComponent {
            return DaggerMainScreenComponent.factory()
                .create(fragment.findDependencies(), fragment.findActivityDependencies())
        }
    }
}

interface MainScreenDependencies : AppDependencies {
    fun mainScreenFactory(): MainScreenFactory
    fun soundListScreenFactory(): SoundListScreenFactory
    fun videoScreenFactory(): VideoScreenFactory
    fun trainingScreenFactory(): TrainingScreenFactory
    fun settingsScreenFactory(): SettingsScreenFactory
}

interface MainScreenActivityDependencies : ActivityDependencies {
    fun recordVoice(): RecordVoice
    fun audioPlayer(): AudioPlayer
}