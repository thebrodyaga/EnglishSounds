package com.thebrodyaga.feature.training.impl.di

import com.thebrodyaga.feature.training.api.TrainingScreenFactory
import com.thebrodyaga.feature.training.impl.screen.TrainingScreenFactoryImpl
import dagger.Binds
import dagger.Module

@Module
interface TrainingModule {

    @Binds
    fun trainingScreenFactory(trainingScreenFactory: TrainingScreenFactoryImpl): TrainingScreenFactory
}