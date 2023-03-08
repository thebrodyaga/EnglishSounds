package com.thebrodyaga.feature.soundDetails.impl.di

import com.thebrodyaga.data.sounds.api.SoundsRepository
import com.thebrodyaga.englishsounds.base.di.ActivityScope
import com.thebrodyaga.englishsounds.base.di.AppDependencies
import com.thebrodyaga.englishsounds.base.di.FeatureScope
import com.thebrodyaga.feature.audioPlayer.api.AudioPlayer
import com.thebrodyaga.feature.setting.api.SettingManager
import com.thebrodyaga.feature.soundDetails.impl.ui.SoundFragment
import com.thebrodyaga.feature.youtube.api.YoutubeScreenFactory
import dagger.Component

@[FeatureScope Component(
    dependencies = [SoundDetailsDependencies::class],
    modules = [SoundDetailsFeatureModule::class]
)]
interface SoundDetailsComponent {
    @Component.Factory
    interface Factory {
        fun create(dependencies: SoundDetailsDependencies): SoundDetailsComponent
    }

    fun inject(fragment: SoundFragment)

    companion object {

        fun factory(
            dependencies: SoundDetailsDependencies,
        ): SoundDetailsComponent {
            return DaggerSoundDetailsComponent.factory()
                .create(dependencies)
        }
    }
}

interface SoundDetailsDependencies : AppDependencies {
    fun soundsRepository(): SoundsRepository
    fun audioPlayer(): AudioPlayer
    fun settingManager(): SettingManager
    fun youtubeScreenFactory(): YoutubeScreenFactory
}