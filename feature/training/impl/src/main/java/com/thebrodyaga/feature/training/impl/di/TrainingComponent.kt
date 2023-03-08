package com.thebrodyaga.feature.training.impl.di

import com.thebrodyaga.data.sounds.api.SoundsRepository
import com.thebrodyaga.englishsounds.base.di.AppDependencies
import com.thebrodyaga.englishsounds.base.di.FeatureScope
import com.thebrodyaga.feature.audioPlayer.api.AudioPlayer
import com.thebrodyaga.feature.soundDetails.api.SoundDetailsScreenFactory
import com.thebrodyaga.feature.training.impl.SoundsTrainingFragment
import com.thebrodyaga.feature.videoList.api.VideoScreenFactory
import dagger.Component

@[FeatureScope Component(
    dependencies = [TrainingDependencies::class],
    modules = [TrainingFeatureModule::class],
)]
interface TrainingComponent {
    @Component.Factory
    interface Factory {
        fun create(dependencies: TrainingDependencies): TrainingComponent
    }

    fun inject(fragment: SoundsTrainingFragment)

    companion object {

        fun factory(
            dependencies: TrainingDependencies,
        ): TrainingComponent {
            return DaggerTrainingComponent.factory()
                .create(dependencies)
        }
    }
}

interface TrainingDependencies : AppDependencies {
    fun audioPlayer(): AudioPlayer
    fun videoScreenFactory(): VideoScreenFactory
    fun soundsRepository(): SoundsRepository
    fun soundDetailsScreenFactory(): SoundDetailsScreenFactory
}