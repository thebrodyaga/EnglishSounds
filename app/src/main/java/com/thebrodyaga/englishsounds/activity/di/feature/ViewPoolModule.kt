package com.thebrodyaga.englishsounds.activity.di.feature

import androidx.appcompat.app.AppCompatActivity
import com.thebrodyaga.englishsounds.base.di.ActivityScope
import com.thebrodyaga.feature.soundList.impl.SoundsListViewPool
import dagger.Module
import dagger.Provides

@Module
interface ViewPoolModule {

//    @Binds
//    @ActivityScope
//    fun viewPoolHolder(viewPoolHolder: ViewPoolHolderImpl): ViewPoolHolder

    companion object {

        @Provides
        @ActivityScope
        fun prefetchRecycledViewPool(
            activity: AppCompatActivity,
        ) = SoundsListViewPool(activity)
    }
}