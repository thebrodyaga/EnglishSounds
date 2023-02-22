package com.thebrodyaga.feature.appActivity.impl.di

import com.thebrodyaga.englishsounds.base.di.ActivityScope
import com.thebrodyaga.englishsounds.base.di.AppDependencies
import com.thebrodyaga.feature.appActivity.impl.AppActivity
import com.thebrodyaga.feature.audioPlayer.api.AudioPlayer
import com.thebrodyaga.feature.audioPlayer.api.RecordVoice
import com.thebrodyaga.feature.mainScreen.api.MainScreenFactory
import com.thebrodyaga.feature.setting.api.SettingManager
import dagger.Component

@[ActivityScope Component(
    dependencies = [AppActivityDependencies::class]
)]
interface AppActivityComponent {

    @Component.Factory
    interface Factory {
        fun create(dependencies: AppActivityDependencies): AppActivityComponent
    }

    fun inject(appActivity: AppActivity)

    companion object {

        fun factory(
            dependencies: AppActivityDependencies,
        ): AppActivityComponent {
            return DaggerAppActivityComponent.factory()
                .create(dependencies)
        }
    }
}

interface AppActivityDependencies : AppDependencies {
    fun recordVoice(): RecordVoice
    fun audioPlayer(): AudioPlayer
    fun settingManager(): SettingManager
    fun mainScreenFactory(): MainScreenFactory
}