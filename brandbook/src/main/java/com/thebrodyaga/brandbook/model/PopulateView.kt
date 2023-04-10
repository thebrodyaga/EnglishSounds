package com.thebrodyaga.brandbook.model

interface PopulateView<in UiModel> {
    fun bind(model: UiModel)
}