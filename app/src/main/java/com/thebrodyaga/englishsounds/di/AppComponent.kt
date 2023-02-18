package com.thebrodyaga.englishsounds.di

import android.app.Application
import com.google.gson.Gson
import com.thebrodyaga.core.navigation.api.cicerone.NavigatorHolder
import com.thebrodyaga.englishsounds.app.AppActivity
import com.thebrodyaga.englishsounds.app.SplashActivity
import com.thebrodyaga.englishsounds.base.di.BaseAppComponent
import com.thebrodyaga.englishsounds.di.modules.AppModule
import com.thebrodyaga.englishsounds.di.modules.NavigationModule
import com.thebrodyaga.englishsounds.screen.dialogs.RateAppDialog
import com.thebrodyaga.englishsounds.screen.fragments.main.MainFragment
import com.thebrodyaga.englishsounds.screen.fragments.main.TabContainerFragment
import com.thebrodyaga.englishsounds.screen.fragments.settings.all.SettingsFragment
import com.thebrodyaga.englishsounds.screen.fragments.sounds.list.SoundsListFragment
import com.thebrodyaga.englishsounds.screen.fragments.sounds.training.SoundsTrainingFragment
import com.thebrodyaga.englishsounds.screen.fragments.video.list.VideoListFragment
import com.thebrodyaga.englishsounds.screen.fragments.video.listoflists.ListOfVideoListsFragment
import com.thebrodyaga.data.sounds.impl.setting.SettingManager
import com.thebrodyaga.englishsounds.screen.fragments.sounds.details.SoundFragment
import com.thebrodyaga.englishsounds.youtube.YoutubePlayerActivity
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [AppModule::class, NavigationModule::class])
interface AppComponent : BaseAppComponent {
    fun getNavigatorHolder(): NavigatorHolder
    fun getSettingManager(): SettingManager

    fun getGson(): Gson
    fun inject(activity: AppActivity)
    fun inject(fragment: TabContainerFragment)
    fun inject(fragment: MainFragment)
    fun inject(fragment: SoundsListFragment)
    fun inject(activity: SplashActivity)
    fun inject(fragment: SoundFragment)
    fun inject(fragment: SettingsFragment)
    fun inject(fragment: SoundsTrainingFragment)
    fun inject(dialog: RateAppDialog)
    fun inject(fragment: VideoListFragment)
    fun inject(activity: YoutubePlayerActivity)
    fun inject(fragment: ListOfVideoListsFragment)

    @Component.Builder
    interface Builder {
        @BindsInstance
        fun application(application: Application): Builder

        fun build(): AppComponent
    }
}