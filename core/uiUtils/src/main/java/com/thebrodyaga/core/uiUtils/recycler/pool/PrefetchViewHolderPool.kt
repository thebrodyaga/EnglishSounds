package com.thebrodyaga.core.uiUtils.recycler.pool

interface PrefetchViewHolderPool {

    suspend fun prefetch()
}