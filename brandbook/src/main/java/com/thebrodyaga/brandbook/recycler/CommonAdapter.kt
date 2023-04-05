package com.thebrodyaga.brandbook.recycler

import androidx.recyclerview.widget.DiffUtil
import com.hannesdorfmann.adapterdelegates4.AdapterDelegate
import com.hannesdorfmann.adapterdelegates4.AsyncListDifferDelegationAdapter
import com.thebrodyaga.brandbook.model.UiModel
import java.util.*

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