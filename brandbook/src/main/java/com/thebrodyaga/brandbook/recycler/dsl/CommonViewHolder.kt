package com.thebrodyaga.brandbook.recycler.dsl

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.thebrodyaga.brandbook.model.UiModel

class CommonViewHolder<I, V>(view: View) :
    RecyclerView.ViewHolder(view)
        where I : UiModel,
              V : View {

    private var _item: I? = null

    val view: V
        get() = itemView as V

    val item: I
        get() = _item ?: error("not bind yet")

    fun bind(model: I) {
        _item = model
    }
}