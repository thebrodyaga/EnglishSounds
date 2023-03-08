package com.thebrodyaga.feature.videoList.impl.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.thebrodyaga.data.sounds.api.SoundsRepository
import com.thebrodyaga.data.sounds.api.SoundsVideoRepository
import com.thebrodyaga.englishsounds.base.app.ViewModelFactory
import com.thebrodyaga.englishsounds.base.app.ViewModelKey
import com.thebrodyaga.feature.videoList.api.VideoScreenFactory
import com.thebrodyaga.feature.videoList.impl.interactor.AllVideoInteractor
import com.thebrodyaga.feature.videoList.impl.list.VideoListViewModel
import com.thebrodyaga.feature.videoList.impl.listoflists.ListOfVideoListsViewModel
import com.thebrodyaga.feature.videoList.impl.screen.VideoScreenFactoryImpl
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoMap
import javax.inject.Singleton

@Module
internal interface VideoListFeatureModule {

    @Binds
    fun bindViewModelFactory(factory: ViewModelFactory): ViewModelProvider.Factory

    @Binds
    @IntoMap
    @ViewModelKey(VideoListViewModel::class)
    fun videoListViewModel(viewModel: VideoListViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(ListOfVideoListsViewModel::class)
    fun listOfVideoListsViewModel(viewModel: ListOfVideoListsViewModel): ViewModel
}