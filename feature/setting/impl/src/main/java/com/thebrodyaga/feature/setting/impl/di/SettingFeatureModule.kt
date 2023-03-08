package com.thebrodyaga.feature.setting.impl.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.thebrodyaga.englishsounds.base.app.ViewModelFactory
import com.thebrodyaga.englishsounds.base.app.ViewModelKey
import com.thebrodyaga.feature.setting.api.SettingManager
import com.thebrodyaga.feature.setting.api.SettingsScreenFactory
import com.thebrodyaga.feature.setting.impl.SettingScreenFactoryImpl
import com.thebrodyaga.feature.setting.impl.SettingsViewModel
import com.thebrodyaga.feature.setting.impl.manager.SettingManagerImpl
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import javax.inject.Singleton

@Module
interface SettingFeatureModule {

    @Binds
    fun bindViewModelFactory(factory: ViewModelFactory): ViewModelProvider.Factory

    @Binds
    @IntoMap
    @ViewModelKey(SettingsViewModel::class)
    fun settingsViewModel(viewModel: SettingsViewModel): ViewModel
}