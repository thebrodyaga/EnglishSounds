package com.thebrodyaga.feature.soundDetails.impl.screen

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentFactory
import com.thebrodyaga.core.navigation.api.cicerone.Screen
import com.thebrodyaga.core.navigation.impl.cicerone.FragmentScreen
import com.thebrodyaga.feature.soundDetails.api.SoundDetailsScreenFactory
import com.thebrodyaga.feature.soundDetails.impl.ui.SoundFragment
import javax.inject.Inject

class SoundDetailsScreenFactoryImpl @Inject constructor() : SoundDetailsScreenFactory {
    override fun soundDetailsScreen(transcription: String): Screen {
        return object : FragmentScreen {
            override fun createFragment(factory: FragmentFactory): Fragment {
                return SoundFragment.newInstance(transcription)
            }
        }
    }
}