package com.thebrodyaga.feature.youtube.impl.di

import com.thebrodyaga.feature.youtube.api.YoutubeScreenFactory
import com.thebrodyaga.feature.youtube.impl.screen.YoutubeScreenFactoryImpl
import dagger.Binds
import dagger.Module

@Module
interface YoutubeModule {

    @Binds
    fun youtubeScreenFactory(screenFactoryImpl: YoutubeScreenFactoryImpl): YoutubeScreenFactory
}