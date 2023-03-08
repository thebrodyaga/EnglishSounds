package com.thebrodyaga.feature.youtube.impl.di

import com.thebrodyaga.englishsounds.base.di.ActivityScope
import com.thebrodyaga.englishsounds.base.di.AppDependencies
import com.thebrodyaga.feature.youtube.impl.YoutubePlayerActivity
import dagger.Component

@[ActivityScope Component(
    dependencies = [YoutubeActivityDependencies::class],
    modules = [YoutubeActivityModule::class],
)]
interface YoutubeActivityComponent {

    @Component.Factory
    interface Factory {
        fun create(dependencies: YoutubeActivityDependencies): YoutubeActivityComponent
    }

    fun inject(activity: YoutubePlayerActivity)

    companion object {

        fun factory(
            dependencies: YoutubeActivityDependencies,
        ): YoutubeActivityComponent {
            return DaggerYoutubeActivityComponent.factory()
                .create(dependencies)
        }
    }
}

interface YoutubeActivityDependencies : AppDependencies {
}