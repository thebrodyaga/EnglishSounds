package com.thebrodyaga.englishsounds.di

import com.thebrodyaga.englishsounds.base.di.ActivityDependencies
import com.thebrodyaga.feature.soundList.impl.di.SoundListActivityDependencies

interface ActivityComponent :
    ActivityDependencies,
    SoundListActivityDependencies