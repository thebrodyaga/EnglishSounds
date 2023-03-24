package com.thebrodyaga.englishsounds.app.di.feature

import com.thebrodyaga.feature.soundList.api.SoundListScreenFactory
import com.thebrodyaga.feature.soundList.impl.screen.SoundListScreenFactoryImpl
import dagger.Binds
import dagger.Module

@Module
interface SoundListFeatureModule {

    @Binds
    fun soundListFactory(SoundListFactory: SoundListScreenFactoryImpl): SoundListScreenFactory
}