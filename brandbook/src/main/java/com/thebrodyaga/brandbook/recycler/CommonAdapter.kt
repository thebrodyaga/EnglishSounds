package com.thebrodyaga.brandbook.recycler

import androidx.recyclerview.widget.DiffUtil
import com.hannesdorfmann.adapterdelegates4.AdapterDelegate
import com.hannesdorfmann.adapterdelegates4.AsyncListDifferDelegationAdapter
import com.thebrodyaga.brandbook.model.UiModel
import java.util.Objects

class CommonAdapter(
    delegates: List<AdapterDelegate<List<UiModel>>> = listOf(),
    delegatesAndViewType: List<Pair<Int, AdapterDelegate<List<UiModel>>>> = listOf(),
    diffUtilCallback: DiffUtil.ItemCallback<UiModel> = CommonDiffCallback(),
) : AsyncListDifferDelegationAdapter<UiModel>(diffUtilCallback) {

    init {
        delegates.forEach {
            delegatesManager.addDelegate(it)
        }
        delegatesAndViewType.forEach { (viewType, delegate) ->
            delegatesManager.addDelegate(viewType, delegate)
        }

    }
}

class CommonDiffCallback : DiffUtil.ItemCallback<UiModel>() {

    override fun areItemsTheSame(oldItem: UiModel, newItem: UiModel): Boolean {
        return when {
            else -> oldItem::class == newItem::class
        }
    }

    override fun areContentsTheSame(oldItem: UiModel, newItem: UiModel): Boolean {
        return Objects.equals(oldItem, newItem)
    }
}