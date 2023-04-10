package com.thebrodyaga.englishsounds.di

import com.thebrodyaga.englishsounds.base.di.ActivityDependencies
import com.thebrodyaga.feature.mainScreen.impl.di.MainScreenActivityDependencies
import com.thebrodyaga.feature.soundDetails.impl.di.SoundDetailsActivityDependencies
import com.thebrodyaga.feature.soundList.impl.di.SoundListActivityDependencies
import com.thebrodyaga.feature.training.impl.di.TrainingActivityDependencies
import com.thebrodyaga.feature.videoList.impl.di.VideoListActivityDependencies

interface ActivityComponent :
    ActivityDependencies,
    SoundListActivityDependencies,
    SoundDetailsActivityDependencies,
    MainScreenActivityDependencies,
    VideoListActivityDependencies,
    TrainingActivityDependencies