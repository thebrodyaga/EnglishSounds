package com.thebrodyaga.feature.setting.impl.di

import com.thebrodyaga.englishsounds.base.di.AppDependencies
import com.thebrodyaga.englishsounds.base.di.FeatureScope
import com.thebrodyaga.data.setting.api.SettingManager
import com.thebrodyaga.feature.setting.impl.SettingsFragment
import dagger.Component

@[FeatureScope Component(
    dependencies = [SettingDependencies::class],
    modules = [SettingModule::class],
)]
interface SettingComponent {

    @Component.Factory
    interface Factory {
        fun create(dependencies: SettingDependencies): SettingComponent
    }

    fun inject(fragment: SettingsFragment)

    companion object {

        fun factory(
            dependencies: SettingDependencies,
        ): SettingComponent {
            return DaggerSettingComponent.factory()
                .create(dependencies)
        }
    }
}

interface SettingDependencies : AppDependencies {
    fun settingManager(): SettingManager
}