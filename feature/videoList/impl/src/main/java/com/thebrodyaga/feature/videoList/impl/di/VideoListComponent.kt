package com.thebrodyaga.feature.videoList.impl.di

import androidx.fragment.app.Fragment
import com.thebrodyaga.data.sounds.api.SoundsRepository
import com.thebrodyaga.data.sounds.api.SoundsVideoRepository
import com.thebrodyaga.englishsounds.base.di.AppDependencies
import com.thebrodyaga.englishsounds.base.di.FeatureScope
import com.thebrodyaga.englishsounds.base.di.findDependencies
import com.thebrodyaga.feature.soundDetails.api.SoundDetailsScreenFactory
import com.thebrodyaga.feature.videoList.api.VideoListType
import com.thebrodyaga.feature.videoList.api.VideoScreenFactory
import com.thebrodyaga.feature.videoList.impl.interactor.AllVideoInteractor
import com.thebrodyaga.feature.videoList.impl.page.VideoListPageFragment
import com.thebrodyaga.feature.videoList.impl.carousel.VideoCarouselFragment
import com.thebrodyaga.feature.youtube.api.YoutubeScreenFactory
import dagger.BindsInstance
import dagger.Component

@[FeatureScope Component(
    dependencies = [VideoListDependencies::class],
    modules = [VideoListModule::class]
)]
interface VideoListComponent {
    @Component.Factory
    interface Factory {
        fun create(
            dependencies: VideoListDependencies,
            @BindsInstance listType: VideoListType?
        ): VideoListComponent
    }

    fun inject(fragment: VideoCarouselFragment)
    fun inject(fragment: VideoListPageFragment)

    companion object {

        fun factory(
            fragment: Fragment,
            listType: VideoListType?,
        ): VideoListComponent {
            return DaggerVideoListComponent.factory()
                .create(fragment.findDependencies(), listType)
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