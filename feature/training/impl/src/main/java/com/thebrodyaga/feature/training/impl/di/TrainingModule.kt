package com.thebrodyaga.feature.training.impl.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.thebrodyaga.englishsounds.base.app.ViewModelFactory
import com.thebrodyaga.englishsounds.base.app.ViewModelKey
import com.thebrodyaga.feature.training.impl.SoundsTrainingViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
interface TrainingModule {

    @Binds
    fun bindViewModelFactory(factory: ViewModelFactory): ViewModelProvider.Factory

    @Binds
    @IntoMap
    @ViewModelKey(SoundsTrainingViewModel::class)
    fun soundsTrainingViewModel(viewModel: SoundsTrainingViewModel): ViewModel

}