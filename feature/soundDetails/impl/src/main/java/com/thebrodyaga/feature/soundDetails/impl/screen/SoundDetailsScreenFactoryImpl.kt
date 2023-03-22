package com.thebrodyaga.feature.soundDetails.impl.screen

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentFactory
import com.thebrodyaga.base.navigation.api.AppFragmentScreen
import com.thebrodyaga.base.navigation.api.FragmentTransactionBox
import com.thebrodyaga.core.navigation.api.cicerone.Screen
import com.thebrodyaga.feature.soundDetails.api.SoundDetailsScreenFactory
import com.thebrodyaga.feature.soundDetails.impl.ui.SoundFragment
import javax.inject.Inject

class SoundDetailsScreenFactoryImpl @Inject constructor() : SoundDetailsScreenFactory {
    override fun soundDetailsScreen(
        transcription: String,
        sharedElement: FragmentTransactionBox?
    ): Screen = SoundDetailsScreen(transcription, sharedElement)
}

internal class SoundDetailsScreen(
    private val transcription: String,
    override val sharedElement: FragmentTransactionBox?
) : AppFragmentScreen {
    override fun createFragment(factory: FragmentFactory): Fragment {
        return SoundFragment.newInstance(transcription)
    }
}