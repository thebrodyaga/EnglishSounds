package com.thebrodyaga.feature.setting.impl

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentFactory
import com.thebrodyaga.core.navigation.api.cicerone.Screen
import com.thebrodyaga.core.navigation.impl.cicerone.FragmentScreen
import com.thebrodyaga.feature.setting.api.SettingsScreenFactory

class SettingScreenFactoryImpl : SettingsScreenFactory {
    override fun settingScreen(): Screen = object : FragmentScreen {
        override fun createFragment(factory: FragmentFactory): Fragment {
            return SettingsFragment()
        }
    }
}