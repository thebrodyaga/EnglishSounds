package com.thebrodyaga.englishsounds.activity.di.feature

import androidx.appcompat.app.AppCompatActivity
import com.thebrodyaga.core.uiUtils.recycler.pool.PrefetchRecycledViewPool
import com.thebrodyaga.englishsounds.base.di.ActivityScope
import com.thebrodyaga.feature.soundDetails.impl.ui.SoundsDetailsViewPool
import com.thebrodyaga.feature.soundList.impl.SoundsListViewPool
import com.thebrodyaga.feature.videoList.impl.carousel.VideoCarouselViewPool
import dagger.Module
import dagger.Provides

@Module
object ViewPoolModule {

    @Provides
    @ActivityScope
    fun setOfPrefetchRecycledViewPool(activity: AppCompatActivity): Set<@JvmSuppressWildcards PrefetchRecycledViewPool> {
        return buildSet {
//            add(SoundsListViewPool(activity))
            add(SoundsDetailsViewPool(activity))
//            add(VideoCarouselViewPool(activity))
        }
    }

    @Provides
    @ActivityScope
    fun soundsListViewPool(set: Set<@JvmSuppressWildcards PrefetchRecycledViewPool>): SoundsListViewPool {
        return set.first { it is SoundsListViewPool } as SoundsListViewPool
    }

    @Provides
    @ActivityScope
    fun soundsDetailsViewPool(set: Set<@JvmSuppressWildcards PrefetchRecycledViewPool>): SoundsDetailsViewPool {
        return set.first { it is SoundsDetailsViewPool } as SoundsDetailsViewPool
    }

    @Provides
    @ActivityScope
    fun videoCarouselViewPool(set: Set<@JvmSuppressWildcards PrefetchRecycledViewPool>): VideoCarouselViewPool {
        return set.first { it is VideoCarouselViewPool } as VideoCarouselViewPool
    }
}