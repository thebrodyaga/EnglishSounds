package com.thebrodyaga.englishsounds.app.di.feature

import com.thebrodyaga.data.sounds.api.SoundsRepository
import com.thebrodyaga.data.sounds.api.SoundsVideoRepository
import com.thebrodyaga.feature.videoList.api.VideoScreenFactory
import com.thebrodyaga.feature.videoList.impl.interactor.AllVideoInteractor
import com.thebrodyaga.feature.videoList.impl.screen.VideoScreenFactoryImpl
import dagger.Binds
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
interface VideoListFeatureModule {

    @Binds
    fun videoListScreenFactory(videoListScreenFactory: VideoScreenFactoryImpl): VideoScreenFactory

    companion object {
        @Provides
        @Singleton
        fun provideAllVideoInteractor(
            soundsRepository: SoundsRepository,
            soundsVideoRepository: SoundsVideoRepository
        ) = AllVideoInteractor(soundsRepository, soundsVideoRepository)
    }
}