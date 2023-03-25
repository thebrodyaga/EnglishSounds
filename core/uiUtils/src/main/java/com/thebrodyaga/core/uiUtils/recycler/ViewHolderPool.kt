package com.thebrodyaga.core.uiUtils.recycler

import android.view.View
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.RecyclerView

typealias ViewHolderFactory = (viewType: Int, itemView: View) -> RecyclerView.ViewHolder

interface ViewHolderPool {

    fun pop(viewType: Int): RecyclerView.ViewHolder?

    fun create(
        viewType: Int,
        @LayoutRes itemLayout: Int,
        count: Int,
        viewHolderFactory: ViewHolderFactory,
    )

    suspend fun create(
        viewType: Int,
        @LayoutRes itemLayout: Int,
        count: Int,
        viewHolderFactory: ViewHolderFactory,
        waitingSize: Int = 0,
    ): List<RecyclerView.ViewHolder>
}