package com.thebrodyaga.englishsounds.screen.adapters.decorator

import android.content.Context
import android.graphics.Rect
import android.view.View
import androidx.annotation.DimenRes
import androidx.recyclerview.widget.RecyclerView
import com.hannesdorfmann.adapterdelegates4.dsl.AdapterDelegateLayoutContainerViewHolder
import com.thebrodyaga.englishsounds.domine.entities.ui.AdItem
import timber.log.Timber

class AdItemDecorator constructor(
    context: Context,
    @RecyclerView.Orientation private val orientation: Int,
    @DimenRes horizontalOffsetRes: Int? = null,
    @DimenRes verticalOffsetRes: Int? = null
) : RecyclerView.ItemDecoration() {

    private val horizontalOffset: Int
    private val verticalOffset: Int

    init {
        val resources = context.resources
        horizontalOffset = horizontalOffsetRes?.let { resources.getDimensionPixelOffset(it) } ?: 0
        verticalOffset = verticalOffsetRes?.let { resources.getDimensionPixelOffset(it) } ?: 0
        checkOffsets()
    }

    private fun checkOffsets() {
        if (horizontalOffset <= 0 && verticalOffset <= 0) {
            Timber.w("No offsets applied.")
        }
    }

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
        if (viewHolder !is AdapterDelegateLayoutContainerViewHolder<*> || viewHolder.item !is AdItem)
            return
        val count = state.itemCount
        when (orientation) {
            RecyclerView.HORIZONTAL -> offsetHorizontal(outRect, position, count)
            RecyclerView.VERTICAL -> offsetVertical(outRect, position, count)
        }
    }

    private fun offsetHorizontal(outRect: Rect, position: Int, count: Int) {
        when (position) {
            0 -> outRect.set(horizontalOffset, verticalOffset, horizontalOffset / 2, verticalOffset)
            in 1 until count - 1 -> outRect.set(
                horizontalOffset / 2,
                verticalOffset,
                horizontalOffset / 2,
                verticalOffset
            )
            count - 1 -> outRect.set(
                horizontalOffset / 2,
                verticalOffset,
                horizontalOffset,
                verticalOffset
            )
            else -> outRect.set(0, 0, 0, 0)
        }
    }

    private fun offsetVertical(outRect: Rect, position: Int, count: Int) {
        when (position) {
            0 -> outRect.set(horizontalOffset, verticalOffset, horizontalOffset, verticalOffset / 2)
            in 1 until count - 1 -> outRect.set(
                horizontalOffset,
                verticalOffset / 2,
                horizontalOffset,
                verticalOffset / 2
            )
            count - 1 -> outRect.set(
                horizontalOffset,
                verticalOffset / 2,
                horizontalOffset,
                verticalOffset
            )
            else -> outRect.set(0, 0, 0, 0)
        }
    }
}