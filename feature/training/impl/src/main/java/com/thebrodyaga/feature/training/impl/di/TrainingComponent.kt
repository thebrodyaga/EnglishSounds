package com.thebrodyaga.feature.training.impl.di

import androidx.fragment.app.Fragment
import com.thebrodyaga.ad.api.AppAdLoader
import com.thebrodyaga.data.sounds.api.SoundsRepository
import com.thebrodyaga.englishsounds.base.di.ActivityDependencies
import com.thebrodyaga.englishsounds.base.di.AppDependencies
import com.thebrodyaga.englishsounds.base.di.FeatureScope
import com.thebrodyaga.englishsounds.base.di.findActivityDependencies
import com.thebrodyaga.englishsounds.base.di.findDependencies
import com.thebrodyaga.feature.audioPlayer.api.AudioPlayer
import com.thebrodyaga.feature.soundDetails.api.SoundDetailsScreenFactory
import com.thebrodyaga.feature.training.impl.SoundsTrainingFragment
import com.thebrodyaga.feature.videoList.api.VideoScreenFactory
import dagger.Component

@[FeatureScope Component(
    dependencies = [
        TrainingDependencies::class,
        TrainingActivityDependencies::class,
    ],
    modules = [TrainingModule::class],
)]
interface TrainingComponent {
    @Component.Factory
    interface Factory {
        fun create(
            dependencies: TrainingDependencies,
            activityDependencies: TrainingActivityDependencies,
        ): TrainingComponent
    }

    fun inject(fragment: SoundsTrainingFragment)

    companion object {

        fun factory(
            fragment: Fragment,
        ): TrainingComponent {
            return DaggerTrainingComponent.factory()
                .create(fragment.findDependencies(), fragment.findActivityDependencies())
        }
    }
}

interface TrainingDependencies : AppDependencies {
    fun videoScreenFactory(): VideoScreenFactory
    fun soundsRepository(): SoundsRepository
    fun soundDetailsScreenFactory(): SoundDetailsScreenFactory
    fun adLoader(): AppAdLoader
}

interface TrainingActivityDependencies : ActivityDependencies {
    fun audioPlayer(): AudioPlayer
}