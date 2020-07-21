package com.thebrodyaga.englishsounds.screen.adapters.decorator

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.hannesdorfmann.adapterdelegates4.dsl.AdapterDelegateLayoutContainerViewHolder
import com.thebrodyaga.englishsounds.domine.entities.ui.AdItem

class VideoListItemDecoration constructor(offset: Int, offsetLast: Boolean = true) :
    GridOffsetItemDecoration(offset, offsetLast) {

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        val position = parent.getChildAdapterPosition(view)
        val viewHolder = parent.getChildViewHolder(view)
        if (position == RecyclerView.NO_POSITION)
            return
        if (viewHolder is AdapterDelegateLayoutContainerViewHolder<*> && viewHolder.item is AdItem)
            return
        else super.getItemOffsets(outRect, view, parent, state)
    }
}