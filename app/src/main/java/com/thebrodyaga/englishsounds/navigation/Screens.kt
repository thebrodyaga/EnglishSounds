package com.thebrodyaga.englishsounds.navigation

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentFactory
import com.thebrodyaga.core.navigation.impl.cicerone.FragmentScreen
import com.thebrodyaga.englishsounds.screen.fragments.main.MainFragment
import com.thebrodyaga.englishsounds.screen.fragments.settings.all.SettingsFragment
import com.thebrodyaga.englishsounds.screen.fragments.sounds.details.SoundFragment
import com.thebrodyaga.englishsounds.screen.fragments.sounds.list.SoundsListFragment
import com.thebrodyaga.englishsounds.screen.fragments.sounds.training.SoundsTrainingFragment
import com.thebrodyaga.englishsounds.screen.fragments.video.AllVideoFragment
import com.thebrodyaga.englishsounds.screen.fragments.video.VideoListType
import com.thebrodyaga.englishsounds.screen.fragments.video.listoflists.ListOfVideoListsFragment

object Screens {

    object MainScreen : FragmentScreen {
        override fun createFragment(factory: FragmentFactory): Fragment {
            return MainFragment()
        }
    }

    object SoundsListScreen : FragmentScreen {
        override fun createFragment(factory: FragmentFactory): Fragment {
            return SoundsListFragment()
        }
    }

    data class SoundsDetailsScreen constructor(val transcription: String) : FragmentScreen {
        override fun createFragment(factory: FragmentFactory): Fragment {
            return SoundFragment.newInstance(transcription)
        }
    }

    object SoundsTrainingScreen : FragmentScreen {
        override fun createFragment(factory: FragmentFactory): Fragment {
            return SoundsTrainingFragment()
        }
    }

    object SettingsScreen : FragmentScreen {
        override fun createFragment(factory: FragmentFactory): Fragment {
            return SettingsFragment()
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