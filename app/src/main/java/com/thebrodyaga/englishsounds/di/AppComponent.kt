package com.thebrodyaga.englishsounds.di

import com.thebrodyaga.englishsounds.base.di.AppDependencies
import com.thebrodyaga.englishsounds.activity.di.AppActivityDependencies
import com.thebrodyaga.feature.mainScreen.impl.di.MainScreenDependencies
import com.thebrodyaga.feature.setting.impl.di.SettingDependencies
import com.thebrodyaga.feature.soundDetails.impl.di.SoundDetailsDependencies
import com.thebrodyaga.feature.soundList.impl.di.SoundListDependencies
import com.thebrodyaga.feature.training.impl.di.TrainingDependencies
import com.thebrodyaga.feature.videoList.impl.di.VideoListDependencies
import com.thebrodyaga.feature.youtube.impl.di.YoutubeActivityDependencies

interface AppComponent :
    AppDependencies,
    AppActivityDependencies,
    SettingDependencies,
    SoundListDependencies,
    VideoListDependencies,
    SoundDetailsDependencies,
    TrainingDependencies,
    MainScreenDependencies,
    YoutubeActivityDependencies