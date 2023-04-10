package com.thebrodyaga.brandbook.recycler.dsl

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.RecyclerView
import com.hannesdorfmann.adapterdelegates4.AbsListItemAdapterDelegate
import com.thebrodyaga.brandbook.model.PopulateView
import com.thebrodyaga.brandbook.model.UiModel

inline fun <reified I, reified V> rowDelegate(
    @LayoutRes
    layout: Int,
    viewType: Int,
    noinline on: (item: UiModel, items: List<UiModel>, position: Int) -> Boolean = { item, _, _ -> item is I },
    noinline block: RowDelegateBlock<I, V>.() -> Unit,
): DslRowAdapterDelegate<I, V> where I : UiModel,
                                     V : PopulateView<I>,
                                     V : View {
    return DslRowAdapterDelegate(layout, viewType, on, block)
}

class DslRowAdapterDelegate<I, V>(
    @LayoutRes
    val layout: Int,
    val viewType: Int,
    private val on: (item: UiModel, items: List<UiModel>, position: Int) -> Boolean,
    private val initializeBlock: RowDelegateBlock<I, V>.() -> Unit,
) : AbsListItemAdapterDelegate<I, UiModel, CommonViewHolder<I, V>>()
        where I : UiModel,
              V : PopulateView<I>,
              V : View {

    private val delegateBlock = RowDelegateBlock<I, V>()

    init {
        initializeBlock.invoke(delegateBlock)
    }

    override fun isForViewType(item: UiModel, items: MutableList<UiModel>, position: Int): Boolean =
        on(item, items, position)


    override fun onCreateViewHolder(parent: ViewGroup): CommonViewHolder<I, V> {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(layout, parent, false)
        return CommonViewHolder<I, V>(itemView).also {
            delegateBlock._onInflate?.invoke(it.view)
        }
    }

    override fun onBindViewHolder(item: I, holder: CommonViewHolder<I, V>, payloads: MutableList<Any>) {
        holder.bind(item)
        holder.view.bind(item)
        delegateBlock._onBind?.invoke(holder, payloads)
    }

    override fun onViewRecycled(holder: RecyclerView.ViewHolder) {
        @Suppress("UNCHECKED_CAST")
        val vh = holder as CommonViewHolder<I, V>
        delegateBlock._onViewRecycled?.invoke(vh)
    }

    override fun onFailedToRecycleView(holder: RecyclerView.ViewHolder): Boolean {
        @Suppress("UNCHECKED_CAST")
        val vh = holder as CommonViewHolder<I, V>
        val block = delegateBlock._onFailedToRecycleView
        return if (block == null) {
            super.onFailedToRecycleView(holder)
        } else {
            block(vh)
        }
    }

    override fun onViewAttachedToWindow(holder: RecyclerView.ViewHolder) {
        @Suppress("UNCHECKED_CAST")
        val vh = holder as CommonViewHolder<I, V>
        delegateBlock._onViewAttachedToWindow?.invoke(vh)
    }

    override fun onViewDetachedFromWindow(holder: RecyclerView.ViewHolder) {
        @Suppress("UNCHECKED_CAST")
        val vh = holder as CommonViewHolder<I, V>
        delegateBlock._onViewDetachedFromWindow?.invoke(vh)
    }
}