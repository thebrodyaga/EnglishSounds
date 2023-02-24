package com.thebrodyaga.feature.soundList.impl.di

import com.thebrodyaga.feature.soundList.api.SoundListFactory
import com.thebrodyaga.feature.soundList.impl.screen.SoundListFactoryImpl
import dagger.Binds
import dagger.Module

@Module
interface SoundListModule {

    @Binds
    fun soundListFactory(SoundListFactory: SoundListFactoryImpl): SoundListFactory
}