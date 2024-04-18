package com.thebrodyaga.brandbook.recycler

import com.thebrodyaga.brandbook.model.UiModel


interface RecyclableView {
    fun clearListeners()
}

fun interface BindListener<View, Item : UiModel> {
    fun onBind(view: View, item: Item)
}