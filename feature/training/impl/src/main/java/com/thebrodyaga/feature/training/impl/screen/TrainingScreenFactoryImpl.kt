package com.thebrodyaga.feature.training.impl.screen

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentFactory
import com.thebrodyaga.core.navigation.api.cicerone.Screen
import com.thebrodyaga.core.navigation.impl.cicerone.FragmentScreen
import com.thebrodyaga.feature.training.api.TrainingScreenFactory
import com.thebrodyaga.feature.training.impl.SoundsTrainingFragment
import javax.inject.Inject

class TrainingScreenFactoryImpl @Inject constructor() : TrainingScreenFactory {
    override fun trainingScreen(): Screen {
        return object : FragmentScreen {
            override fun createFragment(factory: FragmentFactory): Fragment {
                return SoundsTrainingFragment()
            }
        }
    }
}