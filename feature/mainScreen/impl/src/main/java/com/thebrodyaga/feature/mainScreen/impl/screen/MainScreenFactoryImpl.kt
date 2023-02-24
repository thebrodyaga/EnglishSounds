package com.thebrodyaga.feature.mainScreen.impl.screen

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentFactory
import com.thebrodyaga.core.navigation.api.cicerone.Screen
import com.thebrodyaga.core.navigation.impl.cicerone.FragmentScreen
import com.thebrodyaga.feature.mainScreen.api.MainScreenFactory
import com.thebrodyaga.feature.mainScreen.impl.MainFragment
import javax.inject.Inject

class MainScreenFactoryImpl @Inject constructor() : MainScreenFactory {
    override fun mainScreen(): Screen {
        return object : FragmentScreen{
            override fun createFragment(factory: FragmentFactory): Fragment {
                return MainFragment()
            }
        }
    }
}