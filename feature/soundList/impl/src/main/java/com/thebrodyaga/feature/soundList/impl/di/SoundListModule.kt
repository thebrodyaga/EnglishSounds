package com.thebrodyaga.feature.soundList.impl.di

import com.thebrodyaga.feature.soundList.api.SoundListScreenFactory
import com.thebrodyaga.feature.soundList.impl.screen.SoundListScreenFactoryImpl
import dagger.Binds
import dagger.Module

@Module
interface SoundListModule {

    @Binds
    fun soundListFactory(SoundListFactory: SoundListScreenFactoryImpl): SoundListScreenFactory
}