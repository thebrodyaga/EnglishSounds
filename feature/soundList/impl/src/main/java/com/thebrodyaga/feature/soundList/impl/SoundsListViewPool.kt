package com.thebrodyaga.feature.soundList.impl

import androidx.appcompat.app.AppCompatActivity
import com.thebrodyaga.core.uiUtils.recycler.pool.PrefetchRecycledViewPool
import com.thebrodyaga.core.uiUtils.recycler.pool.RecoverableViewHolderPool

class SoundsListViewPool(
    private val activity: AppCompatActivity,
) : PrefetchRecycledViewPool(
    activity, RecoverableViewHolderPool(activity)
)