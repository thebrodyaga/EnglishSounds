package com.thebrodyaga.englishsounds.activity.di.feature

import com.thebrodyaga.core.uiUtils.view.pool.ViewPoolHolder
import com.thebrodyaga.core.uiUtils.view.pool.ViewPoolHolderImpl
import com.thebrodyaga.englishsounds.base.di.ActivityScope
import dagger.Binds
import dagger.Module

@Module
interface ViewPoolModule {

    @Binds
    @ActivityScope
    fun viewPoolHolder(viewPoolHolder: ViewPoolHolderImpl): ViewPoolHolder
}