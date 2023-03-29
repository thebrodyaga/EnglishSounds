package com.thebrodyaga.feature.videoList.impl.screen

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentFactory
import com.thebrodyaga.core.navigation.api.cicerone.Screen
import com.thebrodyaga.core.navigation.impl.cicerone.FragmentScreen
import com.thebrodyaga.feature.videoList.api.VideoScreenFactory
import com.thebrodyaga.feature.videoList.api.VideoListType
import com.thebrodyaga.feature.videoList.impl.AllVideoFragment
import com.thebrodyaga.feature.videoList.impl.carousel.VideoCarouselFragment
import javax.inject.Inject

class VideoScreenFactoryImpl @Inject constructor() : VideoScreenFactory {

    override fun videoCarouselScreen(): Screen {
        return object : FragmentScreen {
            override fun createFragment(factory: FragmentFactory): Fragment {
                return VideoCarouselFragment()
            }
        }
    }

    override fun allVideoScreen(showPage: VideoListType): Screen {
        return object : FragmentScreen {
            override fun createFragment(factory: FragmentFactory): Fragment {
                return AllVideoFragment.newInstance(showPage)
            }
        }
    }
}