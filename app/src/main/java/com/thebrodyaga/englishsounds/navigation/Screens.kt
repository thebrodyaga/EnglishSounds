package com.thebrodyaga.englishsounds.navigation

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentFactory
import com.thebrodyaga.core.navigation.impl.cicerone.FragmentScreen
import com.thebrodyaga.englishsounds.screen.fragments.main.MainFragment
import com.thebrodyaga.feature.soundDetails.impl.ui.SoundFragment
import com.thebrodyaga.feature.training.impl.SoundsTrainingFragment
import com.thebrodyaga.feature.videoList.api.VideoListType
import com.thebrodyaga.feature.videoList.impl.AllVideoFragment
import com.thebrodyaga.feature.videoList.impl.listoflists.ListOfVideoListsFragment

object Screens {

    object MainScreen : FragmentScreen {
        override fun createFragment(factory: FragmentFactory): Fragment {
            return MainFragment()
        }
    }

    object SoundsListScreen : FragmentScreen {
        override fun createFragment(factory: FragmentFactory): Fragment {
            return com.thebrodyaga.feature.soundList.impl.SoundsListFragment()
        }
    }

    data class SoundsDetailsScreen constructor(val transcription: String) : FragmentScreen {
        override fun createFragment(factory: FragmentFactory): Fragment {
            return SoundFragment.newInstance(transcription)
        }
    }

    object SoundsTrainingScreen : FragmentScreen {
        override fun createFragment(factory: FragmentFactory): Fragment {
            return com.thebrodyaga.feature.training.impl.SoundsTrainingFragment()
        }
    }

    data class AllVideoScreen(val showPage: VideoListType = VideoListType.ContrastingSounds) :
        FragmentScreen {
        override fun createFragment(factory: FragmentFactory): Fragment {
            return AllVideoFragment.newInstance(showPage)
        }
    }

    object ListOfVideoListScreen :
        FragmentScreen {
        override fun createFragment(factory: FragmentFactory): Fragment {
            return ListOfVideoListsFragment()
        }
    }
}