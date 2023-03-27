package com.thebrodyaga.core.uiUtils.recycler.pool

import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.RecyclerView

interface ViewHolderPool {

    fun pop(viewType: Int): RecyclerView.ViewHolder?
    fun push(viewHolder: RecyclerView.ViewHolder?)
    fun size(viewType: Int): Int?
    fun maxSize(viewType: Int): Int?

    fun create(
        viewType: Int,
        @LayoutRes itemLayout: Int,
        maxSize: Int,
        viewHolderFactory: ViewHolderFactory,
    )

    suspend fun create(
        viewType: Int,
        @LayoutRes itemLayout: Int,
        maxSize: Int,
        viewHolderFactory: ViewHolderFactory,
        waitingSize: Int = 0,
    ): List<RecyclerView.ViewHolder>
}