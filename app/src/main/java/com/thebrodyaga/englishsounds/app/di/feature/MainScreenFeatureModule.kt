package com.thebrodyaga.englishsounds.app.di.feature

import com.thebrodyaga.feature.mainScreen.api.MainScreenFactory
import com.thebrodyaga.feature.mainScreen.impl.screen.MainScreenFactoryImpl
import dagger.Binds
import dagger.Module

@Module
interface MainScreenFeatureModule {

    @Binds
    fun mainScreenFactory(mainScreenFactory: MainScreenFactoryImpl): MainScreenFactory
}