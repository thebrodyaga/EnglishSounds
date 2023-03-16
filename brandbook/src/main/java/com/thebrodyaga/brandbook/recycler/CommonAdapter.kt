package com.thebrodyaga.brandbook.recycler

import androidx.recyclerview.widget.DiffUtil
import com.hannesdorfmann.adapterdelegates4.AdapterDelegate
import com.hannesdorfmann.adapterdelegates4.AsyncListDifferDelegationAdapter
import com.thebrodyaga.brandbook.model.UiModel
import java.util.*

class CommonAdapter(
    vararg delegates: AdapterDelegate<List<UiModel>>,
    diffUtilCallback: DiffUtil.ItemCallback<UiModel> = CommonDiffCallback()
) : AsyncListDifferDelegationAdapter<UiModel>(diffUtilCallback, *delegates)

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