package com.thebrodyaga.feature.soundDetails.impl.screen

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentFactory
import com.thebrodyaga.base.navigation.api.TransitionInfo
import com.thebrodyaga.base.navigation.impl.AppFragmentScreen
import com.thebrodyaga.core.navigation.api.cicerone.Screen
import com.thebrodyaga.feature.soundDetails.api.SoundDetailsScreenFactory
import com.thebrodyaga.feature.soundDetails.impl.ui.SoundDetailsFragment
import javax.inject.Inject

class SoundDetailsScreenFactoryImpl @Inject constructor() : SoundDetailsScreenFactory {
    override fun soundDetailsScreen(
        transcription: String,
        transitionInfo: TransitionInfo?
    ): Screen = SoundDetailsScreen(transcription, transitionInfo)
}

internal class SoundDetailsScreen(
    private val transcription: String,
    override val transitionInfo: TransitionInfo?
) : AppFragmentScreen {
    override fun createFragment(factory: FragmentFactory): Fragment {
        return SoundDetailsFragment.newInstance(transcription)
    }
}