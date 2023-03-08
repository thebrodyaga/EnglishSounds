package com.thebrodyaga.englishsounds.di

import android.app.Application
import com.google.gson.Gson
import com.thebrodyaga.englishsounds.di.data.SoundsDataModule
import com.thebrodyaga.englishsounds.app.App
import com.thebrodyaga.englishsounds.app.SplashActivity
import com.thebrodyaga.englishsounds.base.di.AppDependencies
import com.thebrodyaga.englishsounds.di.modules.AppModule
import com.thebrodyaga.englishsounds.di.modules.NavigationModule
import com.thebrodyaga.feature.appActivity.impl.di.AppActivityDependencies
import com.thebrodyaga.englishsounds.di.feature.AudioPlayerFeatureModule
import com.thebrodyaga.feature.mainScreen.impl.MainFragment
import com.thebrodyaga.feature.mainScreen.impl.TabContainerFragment
import com.thebrodyaga.feature.mainScreen.impl.di.MainScreenDependencies
import com.thebrodyaga.englishsounds.di.feature.MainScreenFeatureModule
import com.thebrodyaga.feature.setting.impl.di.SettingDependencies
import com.thebrodyaga.englishsounds.di.feature.SettingFeatureModule
import com.thebrodyaga.feature.soundDetails.impl.di.SoundDetailsDependencies
import com.thebrodyaga.englishsounds.di.feature.SoundDetailsFeatureModule
import com.thebrodyaga.feature.soundList.impl.di.SoundListDependencies
import com.thebrodyaga.englishsounds.di.feature.SoundListFeatureModule
import com.thebrodyaga.feature.training.impl.di.TrainingDependencies
import com.thebrodyaga.englishsounds.di.feature.TrainingFeatureModule
import com.thebrodyaga.feature.videoList.impl.di.VideoListDependencies
import com.thebrodyaga.englishsounds.di.feature.VideoListFeatureModule
import com.thebrodyaga.feature.youtube.impl.di.YoutubeActivityDependencies
import com.thebrodyaga.englishsounds.di.feature.YoutubeFeatureModule
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        AppModule::class,
        NavigationModule::class,
        //data
        SoundsDataModule::class,
        //feature
        AudioPlayerFeatureModule::class,
        YoutubeFeatureModule::class,
        SoundDetailsFeatureModule::class,
        VideoListFeatureModule::class,
        SoundListFeatureModule::class,
        TrainingFeatureModule::class,
        MainScreenFeatureModule::class,
        SettingFeatureModule::class,
    ]
)
interface AppComponent : AppDependencies,
    AppActivityDependencies,
    SettingDependencies,
    SoundListDependencies,
    VideoListDependencies,
    SoundDetailsDependencies,
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