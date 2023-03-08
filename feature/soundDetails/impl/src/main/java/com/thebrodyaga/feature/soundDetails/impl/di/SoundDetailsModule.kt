package com.thebrodyaga.feature.soundDetails.impl.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.thebrodyaga.englishsounds.base.app.ViewModelFactory
import com.thebrodyaga.englishsounds.base.app.ViewModelKey
import com.thebrodyaga.feature.soundDetails.impl.ui.SoundViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
interface SoundDetailsModule {

    @Binds
    fun bindViewModelFactory(factory: ViewModelFactory): ViewModelProvider.Factory

    @Binds
    @IntoMap
    @ViewModelKey(SoundViewModel::class)
    fun soundViewModel(viewModel: SoundViewModel): ViewModel
}