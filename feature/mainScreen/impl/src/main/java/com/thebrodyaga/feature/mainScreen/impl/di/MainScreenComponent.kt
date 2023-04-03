package com.thebrodyaga.feature.mainScreen.impl.di

import com.thebrodyaga.englishsounds.base.di.AppDependencies
import com.thebrodyaga.englishsounds.base.di.FeatureScope
import com.thebrodyaga.feature.mainScreen.api.MainScreenFactory
import com.thebrodyaga.feature.mainScreen.impl.MainFragment
import com.thebrodyaga.feature.mainScreen.impl.TabContainerFragment
import com.thebrodyaga.feature.soundList.api.SoundListScreenFactory
import com.thebrodyaga.feature.training.api.TrainingScreenFactory
import com.thebrodyaga.feature.videoList.api.VideoScreenFactory
import dagger.Component

@[FeatureScope Component(
    dependencies = [MainScreenDependencies::class]
)]
interface MainScreenComponent {

    @Component.Factory
    interface Factory {
        fun create(dependencies: MainScreenDependencies): MainScreenComponent
    }

    fun inject(fragment: MainFragment)
    fun inject(fragment: TabContainerFragment)

    companion object {

        fun factory(
            dependencies: MainScreenDependencies,
        ): MainScreenComponent {
            return DaggerMainScreenComponent.factory()
                .create(dependencies)
        }
    }
}

interface MainScreenDependencies : AppDependencies {
    fun mainScreenFactory(): MainScreenFactory
    fun soundListScreenFactory(): SoundListScreenFactory
    fun videoScreenFactory(): VideoScreenFactory
    fun trainingScreenFactory(): TrainingScreenFactory
}