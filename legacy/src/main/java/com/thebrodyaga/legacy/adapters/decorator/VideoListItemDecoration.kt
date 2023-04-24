package com.thebrodyaga.legacy.adapters.decorator

import androidx.recyclerview.widget.RecyclerView
import android.graphics.Rect
import android.view.View
import com.hannesdorfmann.adapterdelegates4.dsl.AdapterDelegateViewBindingViewHolder

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
        else super.getItemOffsets(outRect, view, parent, state)
    }
}