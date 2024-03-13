package com.thebrodyaga.feature.mainScreen.impl.di

import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.thebrodyaga.englishsounds.base.app.ViewModelFactory
import com.thebrodyaga.englishsounds.base.app.ViewModelKey
import com.thebrodyaga.englishsounds.base.di.*
import com.thebrodyaga.feature.mainScreen.impl.TabContainerFragment
import com.thebrodyaga.feature.mainScreen.impl.TabContainerViewModel
import com.thebrodyaga.feature.setting.api.SettingsScreenFactory
import com.thebrodyaga.feature.soundList.api.SoundListScreenFactory
import com.thebrodyaga.feature.training.api.TrainingScreenFactory
import com.thebrodyaga.feature.videoList.api.VideoScreenFactory
import dagger.Binds
import dagger.BindsInstance
import dagger.Component
import dagger.Module
import dagger.multibindings.IntoMap

@[FeatureScope Component(
    modules = [
        TabContainerModule::class
    ],
    dependencies = [
        TabContainerDependencies::class,
    ]
)]
interface TabContainerComponent {

    @Component.Factory
    interface Factory {
        fun create(
            dependencies: TabContainerDependencies,
            @BindsInstance containerName: String,
        ): TabContainerComponent
    }

    fun inject(fragment: TabContainerFragment)

    companion object {

        fun factory(
            fragment: Fragment,
            containerName: String,
        ): TabContainerComponent {
            return DaggerTabContainerComponent.factory()
                .create(fragment.findDependencies(), containerName)
        }
    }
}

interface TabContainerDependencies : AppDependencies {
    fun soundListScreenFactory(): SoundListScreenFactory
    fun videoScreenFactory(): VideoScreenFactory
    fun trainingScreenFactory(): TrainingScreenFactory
    fun settingsScreenFactory(): SettingsScreenFactory
}

@Module
interface TabContainerModule {

    @Binds
    fun bindViewModelFactory(factory: ViewModelFactory): ViewModelProvider.Factory

    @Binds
    @IntoMap
    @ViewModelKey(TabContainerViewModel::class)
    fun settingsViewModel(viewModel: TabContainerViewModel): ViewModel
}