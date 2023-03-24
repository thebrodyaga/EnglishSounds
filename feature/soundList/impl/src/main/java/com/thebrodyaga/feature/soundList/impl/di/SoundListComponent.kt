package com.thebrodyaga.feature.soundList.impl.di

import com.thebrodyaga.base.navigation.api.RouterProvider
import com.thebrodyaga.base.navigation.impl.createRouterProvider
import com.thebrodyaga.core.uiUtils.view.pool.ViewPoolHolder
import com.thebrodyaga.data.sounds.api.SoundsRepository
import com.thebrodyaga.data.sounds.api.SoundsVideoRepository
import com.thebrodyaga.englishsounds.base.app.ScreenFragment
import com.thebrodyaga.englishsounds.base.di.ActivityDependencies
import com.thebrodyaga.englishsounds.base.di.AppDependencies
import com.thebrodyaga.englishsounds.base.di.FeatureScope
import com.thebrodyaga.englishsounds.base.di.findActivityDependencies
import com.thebrodyaga.englishsounds.base.di.findDependencies
import com.thebrodyaga.feature.soundDetails.api.SoundDetailsScreenFactory
import com.thebrodyaga.feature.soundList.impl.SoundsListFragment
import com.thebrodyaga.feature.youtube.api.YoutubeScreenFactory
import dagger.BindsInstance
import dagger.Component

@[FeatureScope Component(
    dependencies = [
        SoundListDependencies::class,
        SoundListActivityDependencies::class
    ],
    modules = [SoundListModule::class]
)]
interface SoundListComponent {
    @Component.Factory
    interface Factory {
        fun create(
            dependencies: SoundListDependencies,
            activityDependencies: SoundListActivityDependencies,
            @BindsInstance routerProvider: RouterProvider,
        ): SoundListComponent
    }

    fun inject(fragment: SoundsListFragment)

    companion object {

        fun factory(
            fragment: ScreenFragment,
        ): SoundListComponent {
            return DaggerSoundListComponent.factory()
                .create(
                    dependencies = fragment.findDependencies(),
                    activityDependencies = fragment.findActivityDependencies(),
                    routerProvider = fragment.createRouterProvider()
                )
        }
    }
}

interface SoundListDependencies : AppDependencies {
    fun soundsRepository(): SoundsRepository
    fun soundsVideoRepository(): SoundsVideoRepository
    fun youtubeScreenFactory(): YoutubeScreenFactory
    fun soundDetailsScreenFactory(): SoundDetailsScreenFactory
}

interface SoundListActivityDependencies : ActivityDependencies {
    fun viewPoolHolder(): ViewPoolHolder
}