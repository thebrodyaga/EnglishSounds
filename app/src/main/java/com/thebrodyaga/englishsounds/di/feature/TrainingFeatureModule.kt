package com.thebrodyaga.englishsounds.di.feature

import com.thebrodyaga.feature.training.api.TrainingScreenFactory
import com.thebrodyaga.feature.training.impl.screen.TrainingScreenFactoryImpl
import dagger.Binds
import dagger.Module

@Module
interface TrainingFeatureModule {

    @Binds
    fun trainingScreenFactory(trainingScreenFactory: TrainingScreenFactoryImpl): TrainingScreenFactory
}