package com.thebrodyaga.feature.mainScreen.impl.di

import androidx.fragment.app.Fragment
import com.thebrodyaga.englishsounds.base.di.ActivityDependencies
import com.thebrodyaga.englishsounds.base.di.AppDependencies
import com.thebrodyaga.englishsounds.base.di.FeatureScope
import com.thebrodyaga.englishsounds.base.di.findActivityDependencies
import com.thebrodyaga.englishsounds.base.di.findDependencies
import com.thebrodyaga.feature.audioPlayer.api.AudioPlayer
import com.thebrodyaga.feature.audioPlayer.api.RecordVoice
import com.thebrodyaga.feature.mainScreen.api.MainScreenFactory
import com.thebrodyaga.feature.mainScreen.impl.MainFragment
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
}

interface MainScreenActivityDependencies : ActivityDependencies {
    fun recordVoice(): RecordVoice
    fun audioPlayer(): AudioPlayer
}