package com.thebrodyaga.englishsounds.di.feature

import com.thebrodyaga.feature.soundDetails.api.SoundDetailsScreenFactory
import com.thebrodyaga.feature.soundDetails.impl.screen.SoundDetailsScreenFactoryImpl
import dagger.Binds
import dagger.Module

@Module
interface SoundDetailsFeatureModule {

    @Binds
    fun soundDetailsScreenFactory(SoundDetailsScreenFactory: SoundDetailsScreenFactoryImpl): SoundDetailsScreenFactory
}