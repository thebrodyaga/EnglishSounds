package com.thebrodyaga.feature.setting.api

import com.thebrodyaga.core.navigation.api.cicerone.Screen

interface SettingsScreenFactory {
    fun settingScreen(): Screen
}