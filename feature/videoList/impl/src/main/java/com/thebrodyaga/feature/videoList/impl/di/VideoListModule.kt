package com.thebrodyaga.feature.videoList.impl.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.thebrodyaga.englishsounds.base.app.ViewModelFactory
import com.thebrodyaga.englishsounds.base.app.ViewModelKey
import com.thebrodyaga.feature.videoList.impl.carousel.VideoCarouselViewModel
import com.thebrodyaga.feature.videoList.impl.page.VideoListViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
internal interface VideoListModule {

    @Binds
    fun bindViewModelFactory(factory: ViewModelFactory): ViewModelProvider.Factory

    @Binds
    @IntoMap
    @ViewModelKey(VideoListViewModel::class)
    fun videoListViewModel(viewModel: VideoListViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(VideoCarouselViewModel::class)
    fun listOfVideoListsViewModel(viewModel: VideoCarouselViewModel): ViewModel
}