package com.thebrodyaga.englishsounds.activity.di

import androidx.appcompat.app.AppCompatActivity
import com.thebrodyaga.data.sounds.api.SoundsRepository
import com.thebrodyaga.englishsounds.activity.di.feature.ViewPoolModule
import com.thebrodyaga.englishsounds.base.di.ActivityScope
import com.thebrodyaga.englishsounds.base.di.AppDependencies
import com.thebrodyaga.englishsounds.base.di.findDependencies
import com.thebrodyaga.englishsounds.di.ActivityComponent
import com.thebrodyaga.feature.appActivity.impl.AppActivity
import com.thebrodyaga.feature.audioPlayer.api.AudioPlayer
import com.thebrodyaga.feature.audioPlayer.api.RecordVoice
import com.thebrodyaga.feature.mainScreen.api.MainScreenFactory
import com.thebrodyaga.feature.setting.api.SettingManager
import dagger.BindsInstance
import dagger.Component

@[ActivityScope Component(
    dependencies = [AppActivityDependencies::class],
    modules = [
        AppActivityModule::class,
        ViewPoolModule::class,
    ]
)]
interface DiAppActivityComponent : ActivityComponent {

    @Component.Factory
    interface Factory {
        fun create(
            dependencies: AppActivityDependencies,
            @BindsInstance activity: AppCompatActivity,
        ): DiAppActivityComponent
    }

    fun inject(appActivity: AppActivity)

    companion object {

        fun factory(
            activity: AppCompatActivity,
        ): DiAppActivityComponent {
            return DaggerDiAppActivityComponent.factory()
                .create(activity.findDependencies(), activity)
        }
    }
}

interface AppActivityDependencies : AppDependencies {
    fun recordVoice(): RecordVoice
    fun audioPlayer(): AudioPlayer
    fun settingManager(): SettingManager
    fun mainScreenFactory(): MainScreenFactory
    fun soundsRepository(): SoundsRepository
}