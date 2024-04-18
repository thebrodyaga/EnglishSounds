package com.thebrodyaga.brandbook.component.data

import com.hannesdorfmann.adapterdelegates4.AdapterDelegate
import com.hannesdorfmann.adapterdelegates4.dsl.adapterDelegateViewBinding
import com.thebrodyaga.brandbook.component.data.right.DataRightUiModel
import com.thebrodyaga.brandbook.databinding.ItemDataViewBinding
import com.thebrodyaga.brandbook.model.UiModel
import com.thebrodyaga.brandbook.recycler.BindListener
import java.lang.ref.WeakReference

fun dataViewCommonDelegate(
    bindListener: (BindListener<DataView, DataUiModel>)? = null,
): AdapterDelegate<List<UiModel>> =
    adapterDelegateViewBinding<DataUiModel, UiModel, ItemDataViewBinding>(
        viewBinding = { inflater, parent -> ItemDataViewBinding.inflate(inflater, parent, false) }
    ) {

        val weakBindListener = WeakReference(bindListener)

        bind {
            binding.root.bind(item)
            weakBindListener.get()?.onBind(binding.root, item)
        }
    }

fun dataViewOnlyLeftDelegate(): AdapterDelegate<List<UiModel>> =
    adapterDelegateViewBinding<DataUiModel, UiModel, ItemDataViewBinding>(
        viewBinding = { inflater, parent -> ItemDataViewBinding.inflate(inflater, parent, false) },
        on = { item, _, _ ->
            when (item) {
                is DataUiModel -> item.rightSide == null
                else -> false
            }
        }
    ) {

        bind {
            binding.root.bind(item)
        }
    }

fun dataViewRightPlayIconDelegate(
    bindListener: (BindListener<DataView, DataUiModel>)? = null,
): AdapterDelegate<List<UiModel>> =
    adapterDelegateViewBinding<DataUiModel, UiModel, ItemDataViewBinding>(
        viewBinding = { inflater, parent -> ItemDataViewBinding.inflate(inflater, parent, false) },
        on = { item, _, _ -> item is DataUiModel && item.rightSide is DataRightUiModel.PlayIcon }
    ) {

        val weakBindListener = WeakReference(bindListener)
        binding.root.rightSideView.inflateType(DataRightUiModel.PlayIcon::class)

        bind {
            binding.root.bind(item)
            weakBindListener.get()?.onBind(binding.root, item)
        }
    }