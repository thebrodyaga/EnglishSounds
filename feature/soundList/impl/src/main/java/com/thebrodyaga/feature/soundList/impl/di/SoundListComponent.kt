package com.thebrodyaga.feature.soundList.impl.di

import com.thebrodyaga.data.sounds.api.SoundsRepository
import com.thebrodyaga.data.sounds.api.SoundsVideoRepository
import com.thebrodyaga.englishsounds.base.di.AppDependencies
import com.thebrodyaga.englishsounds.base.di.FeatureScope
import com.thebrodyaga.feature.soundDetails.api.SoundDetailsScreenFactory
import com.thebrodyaga.feature.soundList.impl.SoundsListFragment
import com.thebrodyaga.feature.youtube.api.YoutubeScreenFactory
import dagger.Component

@[FeatureScope Component(
    dependencies = [SoundListDependencies::class],
    modules = [SoundListModule::class]
)]
interface SoundListComponent {
    @Component.Factory
    interface Factory {
        fun create(dependencies: SoundListDependencies): SoundListComponent
    }

    fun inject(fragment: SoundsListFragment)

    companion object {

        fun factory(
            dependencies: SoundListDependencies,
        ): SoundListComponent {
            return DaggerSoundListComponent.factory()
                .create(dependencies)
        }
    }
}

interface SoundListDependencies : AppDependencies {
    fun soundsRepository(): SoundsRepository
    fun soundsVideoRepository(): SoundsVideoRepository
    fun youtubeScreenFactory(): YoutubeScreenFactory
    fun soundDetailsScreenFactory(): SoundDetailsScreenFactory
}