package com.thebrodyaga.englishsounds.navigation

import androidx.fragment.app.Fragment
import com.thebrodyaga.englishsounds.screen.fragments.main.MainFragment
import com.thebrodyaga.englishsounds.screen.fragments.settings.all.SettingsFragment
import com.thebrodyaga.englishsounds.screen.fragments.sounds.details.SoundFragment
import com.thebrodyaga.englishsounds.screen.fragments.sounds.list.SoundsListFragment
import com.thebrodyaga.englishsounds.screen.fragments.sounds.training.SoundsTrainingFragment
import com.thebrodyaga.englishsounds.screen.fragments.video.AllVideoFragment
import com.thebrodyaga.englishsounds.screen.fragments.video.VideoListType
import com.thebrodyaga.englishsounds.screen.fragments.video.listoflists.ListOfVideoListsFragment
import ru.terrakok.cicerone.android.support.SupportAppScreen

object Screens {

    object MainScreen : SupportAppScreen() {
        override fun getFragment(): Fragment {
            return MainFragment()
        }
    }

    object SoundsListScreen : SupportAppScreen() {
        override fun getFragment(): Fragment {
            return SoundsListFragment()
        }
    }

    data class SoundsDetailsScreen constructor(val transcription: String) : SupportAppScreen() {
        override fun getFragment(): Fragment {
            return SoundFragment.newInstance(transcription)
        }
    }

    object SoundsTrainingScreen : SupportAppScreen() {
        override fun getFragment(): Fragment {
            return SoundsTrainingFragment()
        }
    }

    object SettingsScreen : SupportAppScreen() {
        override fun getFragment(): Fragment {
            return SettingsFragment()
        }
    }

    data class AllVideoScreen(val showPage: VideoListType = VideoListType.ContrastingSounds) :
        SupportAppScreen() {
        override fun getFragment(): Fragment {
            return AllVideoFragment.newInstance(showPage)
        }
    }

    object ListOfVideoListScreen :
        SupportAppScreen() {
        override fun getFragment(): Fragment {
            return ListOfVideoListsFragment()
        }
    }
}