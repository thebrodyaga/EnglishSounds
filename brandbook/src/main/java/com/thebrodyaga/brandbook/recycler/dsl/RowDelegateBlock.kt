package com.thebrodyaga.brandbook.recycler.dsl

import android.view.View
import com.thebrodyaga.brandbook.model.PopulateView
import com.thebrodyaga.brandbook.model.UiModel

class RowDelegateBlock<I, V>
        where I : UiModel,
              V : PopulateView<I>,
              V : View {

    internal var _onInflate: ((view: V) -> Unit)? = null
        private set

    internal var _onBind: ((holder: CommonViewHolder<I, V>, payloads: List<Any>) -> Unit)? = null
        private set

    internal var _onViewRecycled: ((holder: CommonViewHolder<I, V>) -> Unit)? = null
        private set

    internal var _onFailedToRecycleView: ((holder: CommonViewHolder<I, V>) -> Boolean)? = null
        private set

    internal var _onViewAttachedToWindow: ((holder: CommonViewHolder<I, V>) -> Unit)? = null
        private set

    internal var _onViewDetachedFromWindow: ((holder: CommonViewHolder<I, V>) -> Unit)? = null
        private set

    fun onInflate(inflateBlock: (view: V) -> Unit) {
        this._onInflate = inflateBlock
    }

    fun onBind(bindingBlock: (holder: CommonViewHolder<I, V>, payloads: List<Any>) -> Unit) {
        this._onBind = bindingBlock
    }

    fun onViewRecycled(block: (holder: CommonViewHolder<I, V>) -> Unit) {
        _onViewRecycled = block
    }

    fun onFailedToRecycleView(block: (holder: CommonViewHolder<I, V>) -> Boolean) {
        _onFailedToRecycleView = block
    }

    fun onViewAttachedToWindow(block: (holder: CommonViewHolder<I, V>) -> Unit) {
        _onViewAttachedToWindow = block
    }

    fun onViewDetachedFromWindow(block: (holder: CommonViewHolder<I, V>) -> Unit) {
        _onViewDetachedFromWindow = block
    }
}