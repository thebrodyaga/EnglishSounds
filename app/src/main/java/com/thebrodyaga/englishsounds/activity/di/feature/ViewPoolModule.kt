package com.thebrodyaga.englishsounds.activity.di.feature

import com.thebrodyaga.core.uiUtils.recycler.AsyncViewHolderPool
import com.thebrodyaga.core.uiUtils.recycler.ViewHolderPool
import com.thebrodyaga.englishsounds.base.di.ActivityScope
import dagger.Binds
import dagger.Module

@Module
interface ViewPoolModule {

//    @Binds
//    @ActivityScope
//    fun viewPoolHolder(viewPoolHolder: ViewPoolHolderImpl): ViewPoolHolder

    @Binds
    @ActivityScope
    fun asyncViewHolderPool(viewPoolHolder: AsyncViewHolderPool): ViewHolderPool
}