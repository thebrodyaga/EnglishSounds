package com.thebrodyaga.feature.mainScreen.impl.di

import com.thebrodyaga.feature.mainScreen.api.MainScreenFactory
import com.thebrodyaga.feature.mainScreen.impl.screen.MainScreenFactoryImpl
import dagger.Binds
import dagger.Module

@Module
interface MainScreenModule {

    @Binds
    fun mainScreenFactory(mainScreenFactory: MainScreenFactoryImpl): MainScreenFactory
}