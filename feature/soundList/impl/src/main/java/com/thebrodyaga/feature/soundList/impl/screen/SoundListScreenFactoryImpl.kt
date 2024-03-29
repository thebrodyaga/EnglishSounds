package com.thebrodyaga.feature.soundList.impl.screen

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentFactory
import com.thebrodyaga.core.navigation.api.cicerone.Screen
import com.thebrodyaga.core.navigation.impl.cicerone.FragmentScreen
import com.thebrodyaga.feature.soundList.api.SoundListScreenFactory
import com.thebrodyaga.feature.soundList.impl.SoundsListFragment
import javax.inject.Inject

class SoundListScreenFactoryImpl @Inject constructor() : SoundListScreenFactory {
    override fun soundListFactory(): Screen = object : FragmentScreen {
        override fun createFragment(factory: FragmentFactory): Fragment {
            return SoundsListFragment()
        }
    }
}