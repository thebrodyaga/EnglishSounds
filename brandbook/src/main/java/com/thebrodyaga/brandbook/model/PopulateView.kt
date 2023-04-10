package com.thebrodyaga.brandbook.model

interface PopulateView<I : UiModel> {
    fun bind(model: I)
}