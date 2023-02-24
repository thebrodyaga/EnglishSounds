package com.thebrodyaga.englishsounds.di

import android.app.Application
import com.google.gson.Gson
import com.thebrodyaga.data.sounds.impl.di.SoundsModule
import com.thebrodyaga.englishsounds.app.App
import com.thebrodyaga.englishsounds.app.SplashActivity
import com.thebrodyaga.englishsounds.base.di.AppDependencies
import com.thebrodyaga.englishsounds.di.modules.AppModule
import com.thebrodyaga.englishsounds.di.modules.NavigationModule
import com.thebrodyaga.feature.appActivity.impl.di.AppActivityDependencies
import com.thebrodyaga.feature.audioPlayer.impl.di.AudioPlayerModule
import com.thebrodyaga.feature.mainScreen.impl.MainFragment
import com.thebrodyaga.feature.mainScreen.impl.TabContainerFragment
import com.thebrodyaga.feature.mainScreen.impl.di.MainScreenDependencies
import com.thebrodyaga.feature.mainScreen.impl.di.MainScreenModule
import com.thebrodyaga.feature.setting.impl.di.SettingDependencies
import com.thebrodyaga.feature.setting.impl.di.SettingModule
import com.thebrodyaga.feature.soundDetails.impl.di.SoundDetailsModule
import com.thebrodyaga.feature.soundList.impl.di.SoundListDependencies
import com.thebrodyaga.feature.soundList.impl.di.SoundListModule
import com.thebrodyaga.feature.training.impl.di.TrainingDependencies
import com.thebrodyaga.feature.training.impl.di.TrainingModule
import com.thebrodyaga.feature.videoList.impl.di.VideoListDependencies
import com.thebrodyaga.feature.videoList.impl.di.VideoListModule
import com.thebrodyaga.feature.youtube.impl.di.YoutubeActivityDependencies
import com.thebrodyaga.feature.youtube.impl.di.YoutubeModule
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        AppModule::class,
        NavigationModule::class,
        AudioPlayerModule::class,
        SoundsModule::class,
        YoutubeModule::class,
        SoundDetailsModule::class,
        VideoListModule::class,
        SoundListModule::class,
        TrainingModule::class,
        MainScreenModule::class,
        SettingModule::class,
    ]
)
interface AppComponent : AppDependencies,
    AppActivityDependencies,
    SettingDependencies,
    SoundListDependencies,
    VideoListDependencies,
    TrainingDependencies,
    MainScreenDependencies,
    YoutubeActivityDependencies {
    fun getGson(): Gson
    fun inject(app: App)
    fun inject(fragment: TabContainerFragment)
    fun inject(fragment: MainFragment)
    fun inject(activity: SplashActivity)

    @Component.Builder
    interface Builder {
        @BindsInstance
        fun application(application: Application): Builder

        fun build(): AppComponent
    }
}