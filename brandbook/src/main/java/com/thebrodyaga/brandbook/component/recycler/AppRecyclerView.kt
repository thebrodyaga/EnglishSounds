package com.thebrodyaga.brandbook.component.recycler

import android.content.Context
import android.util.AttributeSet
import androidx.recyclerview.widget.RecyclerView

class AppRecyclerView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : RecyclerView(context, attrs) {

    init {
        overScrollMode = OVER_SCROLL_IF_CONTENT_SCROLLS
    }
}