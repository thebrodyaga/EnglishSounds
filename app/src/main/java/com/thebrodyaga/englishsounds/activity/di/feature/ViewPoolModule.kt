package com.thebrodyaga.englishsounds.activity.di.feature

import androidx.appcompat.app.AppCompatActivity
import com.thebrodyaga.core.uiUtils.recycler.pool.PrefetchRecycledViewPool
import com.thebrodyaga.englishsounds.base.di.ActivityScope
import com.thebrodyaga.feature.soundList.impl.SoundsListViewPool
import dagger.Module
import dagger.Provides

@Module
object ViewPoolModule {

    @Provides
    @ActivityScope
    fun setOfPrefetchRecycledViewPool(activity: AppCompatActivity): Set<@JvmSuppressWildcards PrefetchRecycledViewPool> {
        return buildSet {
            add(SoundsListViewPool(activity))
        }
    }

    @Provides
    @ActivityScope
    fun soundsListViewPool(set: Set<@JvmSuppressWildcards PrefetchRecycledViewPool>): SoundsListViewPool {
        return set.first { it is SoundsListViewPool } as SoundsListViewPool
    }
}