package com.thebrodyaga.englishsounds.di

import android.app.Application
import com.google.gson.Gson
import com.thebrodyaga.data.sounds.api.SoundsRepository
import com.thebrodyaga.data.sounds.api.SoundsVideoRepository
import com.thebrodyaga.data.sounds.impl.di.SoundsModule
import com.thebrodyaga.englishsounds.app.App
import com.thebrodyaga.englishsounds.app.SplashActivity
import com.thebrodyaga.englishsounds.base.di.AppDependencies
import com.thebrodyaga.englishsounds.di.modules.AppModule
import com.thebrodyaga.englishsounds.di.modules.NavigationModule
import com.thebrodyaga.englishsounds.screen.dialogs.RateAppDialog
import com.thebrodyaga.englishsounds.screen.fragments.main.MainFragment
import com.thebrodyaga.englishsounds.screen.fragments.main.TabContainerFragment
import com.thebrodyaga.englishsounds.screen.fragments.sounds.details.SoundFragment
import com.thebrodyaga.englishsounds.screen.fragments.sounds.list.SoundsListFragment
import com.thebrodyaga.englishsounds.screen.fragments.sounds.training.SoundsTrainingFragment
import com.thebrodyaga.englishsounds.screen.fragments.video.list.VideoListFragment
import com.thebrodyaga.englishsounds.screen.fragments.video.listoflists.ListOfVideoListsFragment
import com.thebrodyaga.feature.appActivity.impl.AppActivity
import com.thebrodyaga.feature.appActivity.impl.di.AppActivityDependencies
import com.thebrodyaga.feature.audioPlayer.impl.di.AudioPlayerModule
import com.thebrodyaga.feature.setting.impl.di.SettingDependencies
import com.thebrodyaga.feature.setting.impl.di.SettingModule
import com.thebrodyaga.feature.youtube.impl.di.YoutubeActivityDependencies
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
        SettingModule::class,
    ]
)
interface AppComponent : AppDependencies,
    AppActivityDependencies,
    SettingDependencies,
    YoutubeActivityDependencies {
    fun soundsRepository(): SoundsRepository
    fun soundsVideoRepository(): SoundsVideoRepository
    fun getGson(): Gson
    fun inject(app: App)
    fun inject(activity: AppActivity)
    fun inject(fragment: TabContainerFragment)
    fun inject(fragment: MainFragment)
    fun inject(fragment: SoundsListFragment)
    fun inject(activity: SplashActivity)
    fun inject(fragment: SoundFragment)
    fun inject(fragment: SoundsTrainingFragment)
    fun inject(dialog: RateAppDialog)
    fun inject(fragment: VideoListFragment)
    fun inject(activity: com.thebrodyaga.feature.youtube.impl.YoutubePlayerActivity)
    fun inject(fragment: ListOfVideoListsFragment)

    @Component.Builder
    interface Builder {
        @BindsInstance
        fun application(application: Application): Builder

        fun build(): AppComponent
    }
}