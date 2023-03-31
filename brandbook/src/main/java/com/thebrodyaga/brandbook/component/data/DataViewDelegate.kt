package com.thebrodyaga.brandbook.component.data

import com.hannesdorfmann.adapterdelegates4.AdapterDelegate
import com.hannesdorfmann.adapterdelegates4.dsl.adapterDelegateViewBinding
import com.thebrodyaga.brandbook.databinding.ItemDataViewBinding
import com.thebrodyaga.brandbook.model.UiModel

fun dataViewCommonDelegate(
    inflateListener: ((view: DataView) -> Unit)? = null,
    bindListener: ((view: DataView, item: DataUiModel) -> Unit)? = null,
): AdapterDelegate<List<UiModel>> =
    adapterDelegateViewBinding<DataUiModel, UiModel, ItemDataViewBinding>(
        viewBinding = { inflater, parent -> ItemDataViewBinding.inflate(inflater, parent, false) }
    ) {

        inflateListener?.invoke(binding.root)

        bind {
            binding.root.bind(item)
            bindListener?.invoke(binding.root, item)
        }
    }

fun dataViewOnlyLeftDelegate(
    inflateListener: ((view: DataView) -> Unit)? = null,
    bindListener: ((view: DataView, item: DataUiModel) -> Unit)? = null,
): AdapterDelegate<List<UiModel>> =
    adapterDelegateViewBinding<DataUiModel, UiModel, ItemDataViewBinding>(
        viewBinding = { inflater, parent -> ItemDataViewBinding.inflate(inflater, parent, false) },
        on = { item, _, _ ->
            when (item) {
                is DataUiModel -> item.rightSide == null
                else -> false
            }
        }
    ) {

        inflateListener?.invoke(binding.root)

        bind {
            binding.root.bind(item)
            bindListener?.invoke(binding.root, item)
        }
    }