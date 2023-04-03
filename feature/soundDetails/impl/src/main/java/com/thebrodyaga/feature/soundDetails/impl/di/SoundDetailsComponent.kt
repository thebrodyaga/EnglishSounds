package com.thebrodyaga.feature.soundDetails.impl.di

import com.thebrodyaga.data.sounds.api.SoundsRepository
import com.thebrodyaga.englishsounds.base.di.ActivityDependencies
import com.thebrodyaga.englishsounds.base.di.AppDependencies
import com.thebrodyaga.englishsounds.base.di.FeatureScope
import com.thebrodyaga.englishsounds.base.di.findActivityDependencies
import com.thebrodyaga.englishsounds.base.di.findDependencies
import com.thebrodyaga.feature.audioPlayer.api.AudioPlayer
import com.thebrodyaga.feature.setting.api.SettingManager
import com.thebrodyaga.feature.soundDetails.impl.ui.SoundDetailsFragment
import com.thebrodyaga.feature.youtube.api.YoutubeScreenFactory
import dagger.BindsInstance
import dagger.Component

@[FeatureScope Component(
    dependencies = [
        SoundDetailsDependencies::class,
        SoundDetailsActivityDependencies::class
    ],
    modules = [SoundDetailsModule::class]
)]
interface SoundDetailsComponent {
    @Component.Factory
    interface Factory {
        fun create(
            dependencies: SoundDetailsDependencies,
            activityDependencies: SoundDetailsActivityDependencies,
            @BindsInstance transcription: String
        ): SoundDetailsComponent
    }

    fun inject(fragment: SoundDetailsFragment)

    companion object {

        fun factory(
            fragment: SoundDetailsFragment,
            transcription: String,
        ): SoundDetailsComponent {
            return DaggerSoundDetailsComponent.factory()
                .create(fragment.findDependencies(), fragment.findActivityDependencies(), transcription)
        }
    }
}

interface SoundDetailsDependencies : AppDependencies {
    fun soundsRepository(): SoundsRepository
    fun settingManager(): SettingManager
    fun youtubeScreenFactory(): YoutubeScreenFactory
}

interface SoundDetailsActivityDependencies : ActivityDependencies {
    fun audioPlayer(): AudioPlayer
}