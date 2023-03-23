package com.thebrodyaga.feature.mainScreen.impl.screen

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentFactory
import com.thebrodyaga.base.navigation.impl.AppFragmentScreen
import com.thebrodyaga.feature.mainScreen.api.MainScreenFactory
import com.thebrodyaga.feature.mainScreen.impl.MainFragment
import javax.inject.Inject

class MainScreenFactoryImpl @Inject constructor() : MainScreenFactory {
    override fun mainScreen(): AppFragmentScreen = MainScreen()
}

internal class MainScreen : AppFragmentScreen {
    override fun createFragment(factory: FragmentFactory): Fragment {
        return MainFragment()
    }
}