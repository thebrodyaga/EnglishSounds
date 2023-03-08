package com.thebrodyaga.englishsounds.di.feature

import com.thebrodyaga.feature.setting.api.SettingManager
import com.thebrodyaga.feature.setting.api.SettingsScreenFactory
import com.thebrodyaga.feature.setting.impl.SettingScreenFactoryImpl
import com.thebrodyaga.feature.setting.impl.manager.SettingManagerImpl
import dagger.Binds
import dagger.Module
import javax.inject.Singleton

@Module
interface SettingFeatureModule {

    @Binds
    @Singleton
    fun settingManager(settingManagerImpl: SettingManagerImpl): SettingManager

    @Binds
    fun settingsScreenFactory(settingScreenFactoryImpl: SettingScreenFactoryImpl): SettingsScreenFactory
}