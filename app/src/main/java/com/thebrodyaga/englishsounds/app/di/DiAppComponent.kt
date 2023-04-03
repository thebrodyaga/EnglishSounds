package com.thebrodyaga.englishsounds.app.di

import android.app.Application
import com.google.gson.Gson
import com.thebrodyaga.englishsounds.app.App
import com.thebrodyaga.englishsounds.app.di.data.SoundsDataModule
import com.thebrodyaga.englishsounds.app.di.feature.MainScreenFeatureModule
import com.thebrodyaga.englishsounds.app.di.feature.SettingFeatureModule
import com.thebrodyaga.englishsounds.app.di.feature.SoundDetailsFeatureModule
import com.thebrodyaga.englishsounds.app.di.feature.SoundListFeatureModule
import com.thebrodyaga.englishsounds.app.di.feature.TrainingFeatureModule
import com.thebrodyaga.englishsounds.app.di.feature.VideoListFeatureModule
import com.thebrodyaga.englishsounds.app.di.feature.YoutubeFeatureModule
import com.thebrodyaga.englishsounds.app.di.modules.AppModule
import com.thebrodyaga.englishsounds.app.di.modules.NavigationModule
import com.thebrodyaga.englishsounds.di.AppComponent
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
        YoutubeFeatureModule::class,
        SoundDetailsFeatureModule::class,
        VideoListFeatureModule::class,
        SoundListFeatureModule::class,
        TrainingFeatureModule::class,
        MainScreenFeatureModule::class,
        SettingFeatureModule::class,
    ]
)
interface DiAppComponent : AppComponent {
    fun getGson(): Gson
    fun inject(app: App)

    @Component.Builder
    interface Builder {
        @BindsInstance
        fun application(application: Application): Builder

        fun build(): DiAppComponent
    }
}