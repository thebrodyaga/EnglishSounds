package com.thebrodyaga.feature.soundDetails.impl.di

import com.thebrodyaga.ad.api.SingleAdLoader
import com.thebrodyaga.base.navigation.api.RouterProvider
import com.thebrodyaga.base.navigation.impl.createRouterProvider
import com.thebrodyaga.data.sounds.api.SoundsRepository
import com.thebrodyaga.englishsounds.base.di.ActivityDependencies
import com.thebrodyaga.englishsounds.base.di.AppDependencies
import com.thebrodyaga.englishsounds.base.di.FeatureScope
import com.thebrodyaga.englishsounds.base.di.findActivityDependencies
import com.thebrodyaga.englishsounds.base.di.findDependencies
import com.thebrodyaga.feature.audioPlayer.api.AudioPlayer
import com.thebrodyaga.data.setting.api.SettingManager
import com.thebrodyaga.feature.soundDetails.impl.ui.SoundDetailsFragment
import com.thebrodyaga.feature.soundDetails.impl.ui.SoundsDetailsViewPool
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
            @BindsInstance routerProvider: RouterProvider,
            @BindsInstance transcription: String,
        ): SoundDetailsComponent
    }

    fun inject(fragment: SoundDetailsFragment)

    companion object {

        fun factory(
            fragment: SoundDetailsFragment,
            transcription: String,
        ): SoundDetailsComponent {
            return DaggerSoundDetailsComponent.factory()
                .create(
                    fragment.findDependencies(),
                    fragment.findActivityDependencies(),
                    fragment.createRouterProvider(),
                    transcription,
                )
        }
    }
}

interface SoundDetailsDependencies : AppDependencies {
    fun soundsRepository(): SoundsRepository
    fun settingManager(): SettingManager
    fun youtubeScreenFactory(): YoutubeScreenFactory
    fun adLoader(): SingleAdLoader
}

interface SoundDetailsActivityDependencies : ActivityDependencies {
    fun audioPlayer(): AudioPlayer
    fun soundsDetailsViewPool(): SoundsDetailsViewPool
}