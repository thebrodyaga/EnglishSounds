package com.thebrodyaga.feature.setting.impl.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.thebrodyaga.englishsounds.base.app.ViewModelFactory
import com.thebrodyaga.englishsounds.base.app.ViewModelKey
import com.thebrodyaga.feature.setting.impl.SettingsViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
interface SettingModule {

    @Binds
    fun bindViewModelFactory(factory: ViewModelFactory): ViewModelProvider.Factory

    @Binds
    @IntoMap
    @ViewModelKey(SettingsViewModel::class)
    fun settingsViewModel(viewModel: SettingsViewModel): ViewModel
}