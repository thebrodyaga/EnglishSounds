package com.thebrodyaga.feature.soundDetails.impl.di

import com.thebrodyaga.feature.soundDetails.api.SoundDetailsScreenFactory
import com.thebrodyaga.feature.soundDetails.impl.screen.SoundDetailsScreenFactoryImpl
import dagger.Binds
import dagger.Module

@Module
interface SoundDetailsModule {

    @Binds
    fun soundDetailsScreenFactory(SoundDetailsScreenFactory: SoundDetailsScreenFactoryImpl): SoundDetailsScreenFactory
}