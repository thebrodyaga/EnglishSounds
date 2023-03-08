package com.thebrodyaga.englishsounds.di.feature

import com.thebrodyaga.feature.youtube.api.YoutubeScreenFactory
import com.thebrodyaga.feature.youtube.impl.screen.YoutubeScreenFactoryImpl
import dagger.Binds
import dagger.Module

@Module
interface YoutubeFeatureModule {

    @Binds
    fun youtubeScreenFactory(screenFactoryImpl: YoutubeScreenFactoryImpl): YoutubeScreenFactory
}