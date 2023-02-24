package com.thebrodyaga.feature.videoList.impl.di

import com.thebrodyaga.data.sounds.api.SoundsRepository
import com.thebrodyaga.data.sounds.api.SoundsVideoRepository
import com.thebrodyaga.englishsounds.base.di.AppDependencies
import com.thebrodyaga.englishsounds.base.di.FeatureScope
import com.thebrodyaga.feature.soundDetails.api.SoundDetailsScreenFactory
import com.thebrodyaga.feature.videoList.api.VideoScreenFactory
import com.thebrodyaga.feature.videoList.impl.interactor.AllVideoInteractor
import com.thebrodyaga.feature.videoList.impl.list.VideoListFragment
import com.thebrodyaga.feature.videoList.impl.listoflists.ListOfVideoListsFragment
import com.thebrodyaga.feature.youtube.api.YoutubeScreenFactory
import dagger.Component

@[FeatureScope Component(
    dependencies = [VideoListDependencies::class]
)]
interface VideoListComponent {
    @Component.Factory
    interface Factory {
        fun create(dependencies: VideoListDependencies): VideoListComponent
    }

    fun inject(fragment: ListOfVideoListsFragment)
    fun inject(fragment: VideoListFragment)

    companion object {

        fun factory(
            dependencies: VideoListDependencies,
        ): VideoListComponent {
            return DaggerVideoListComponent.factory()
                .create(dependencies)
        }
    }
}

interface VideoListDependencies : AppDependencies {
    fun soundsRepository(): SoundsRepository
    fun soundsVideoRepository(): SoundsVideoRepository
    fun soundDetailsScreenFactory(): SoundDetailsScreenFactory
    fun youtubeScreenFactory(): YoutubeScreenFactory
    fun videoListScreenFactory(): VideoScreenFactory
    fun allVideoInteractor(): AllVideoInteractor
}