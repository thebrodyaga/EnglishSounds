package com.thebrodyaga.feature.videoList.impl.di

import com.thebrodyaga.data.sounds.api.SoundsRepository
import com.thebrodyaga.data.sounds.api.SoundsVideoRepository
import com.thebrodyaga.englishsounds.base.di.AppDependencies
import com.thebrodyaga.englishsounds.base.di.FeatureScope
import com.thebrodyaga.feature.soundDetails.api.SoundDetailsScreenFactory
import com.thebrodyaga.feature.videoList.api.VideoListScreenFactory
import com.thebrodyaga.feature.videoList.impl.interactor.AllVideoInteractor
import com.thebrodyaga.feature.videoList.impl.list.VideoListFragment
import com.thebrodyaga.feature.videoList.impl.listoflists.ListOfVideoListsFragment
import com.thebrodyaga.feature.videoList.impl.screen.VideoListScreenFactoryImpl
import com.thebrodyaga.feature.youtube.api.YoutubeScreenFactory
import dagger.Binds
import dagger.Component
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
interface VideoListModule {

    @Binds
    fun videoListScreenFactory(videoListScreenFactory: VideoListScreenFactoryImpl): VideoListScreenFactory

    companion object {
        @Provides
        @Singleton
        fun provideAllVideoInteractor(
            soundsRepository: SoundsRepository,
            soundsVideoRepository: SoundsVideoRepository
        ) = AllVideoInteractor(soundsRepository, soundsVideoRepository)
    }
}