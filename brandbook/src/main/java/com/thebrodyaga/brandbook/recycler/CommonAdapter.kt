package com.thebrodyaga.brandbook.recycler

import android.view.View
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.hannesdorfmann.adapterdelegates4.AdapterDelegate
import com.hannesdorfmann.adapterdelegates4.AsyncListDifferDelegationAdapter
import com.thebrodyaga.brandbook.model.PopulateView
import com.thebrodyaga.brandbook.model.UiModel
import com.thebrodyaga.brandbook.recycler.dsl.DslRowAdapterDelegate
import com.thebrodyaga.brandbook.recycler.dsl.RowDelegateBlock
import com.thebrodyaga.brandbook.recycler.dsl.rowDelegate
import java.util.Objects

class CommonAdapter(
    delegates: List<AdapterDelegate<List<UiModel>>> = listOf(),
    diffUtilCallback: DiffUtil.ItemCallback<UiModel> = CommonDiffCallback(),
    block: CommonAdapterBuilder.() -> Unit = {}
) : AsyncListDifferDelegationAdapter<UiModel>(diffUtilCallback) {

    private val builder: CommonAdapterBuilder = CommonAdapterBuilder()

    init {
        delegates.forEach {
            delegatesManager.addDelegate(it)
        }
        block.invoke(builder)
        builder.rowDelegates.forEach {
            delegatesManager.addDelegate(it.viewType, it)
        }
    }

    override fun onViewRecycled(holder: RecyclerView.ViewHolder) {
        super.onViewRecycled(holder)
        holder.itemView.setOnClickListener(null)
        (holder.itemView as? RecyclableView)?.clearListeners()
    }
}

class CommonAdapterBuilder {
    val rowDelegates = mutableListOf<DslRowAdapterDelegate<*, *>>()

    fun row(delegate: DslRowAdapterDelegate<*, *>) {
        rowDelegates.add(delegate)
    }

    inline fun <reified I, reified V> row(
        @LayoutRes layout: Int,
        viewType: Int,
        noinline block: RowDelegateBlock<I, V>.() -> Unit,
    ) where I : UiModel, V : PopulateView<I>, V : View {
        rowDelegates.add(rowDelegate(layout, viewType) {
            block.invoke(this)
        })
    }
}

const val SAME_UI_MODEL_TYPE_CHANGE_PAYLOAD = "SAME_UI_MODEL_TYPE_CHANGE_PAYLOAD"

open class CommonDiffCallback : DiffUtil.ItemCallback<UiModel>() {

    override fun areItemsTheSame(oldItem: UiModel, newItem: UiModel): Boolean {
        return when {
            else -> oldItem::class == newItem::class
        }
    }

    override fun areContentsTheSame(oldItem: UiModel, newItem: UiModel): Boolean {
        return Objects.equals(oldItem, newItem)
    }

    override fun getChangePayload(oldItem: UiModel, newItem: UiModel): Any? {
        return when {
            // disable default blink onChange animation
            oldItem::class == newItem::class -> SAME_UI_MODEL_TYPE_CHANGE_PAYLOAD
            else -> super.getChangePayload(oldItem, newItem)
        }
    }
}