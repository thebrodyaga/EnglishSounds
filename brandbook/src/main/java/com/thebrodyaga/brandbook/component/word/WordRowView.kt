package com.thebrodyaga.brandbook.component.word

import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.updatePadding
import android.content.Context
import android.util.AttributeSet
import android.view.View.OnClickListener
import by.kirich1409.viewbindingdelegate.viewBinding
import com.hannesdorfmann.adapterdelegates4.AdapterDelegate
import com.hannesdorfmann.adapterdelegates4.dsl.adapterDelegateViewBinding
import com.thebrodyaga.brandbook.R
import com.thebrodyaga.brandbook.databinding.ItemWordRowBinding
import com.thebrodyaga.brandbook.databinding.ViewWordRowBinding
import com.thebrodyaga.brandbook.recycler.model.UiModel
import com.thebrodyaga.core.uiUtils.resources.px
import com.thebrodyaga.core.uiUtils.text.bind

class WordRowView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : ConstraintLayout(context, attrs) {

    private val binding by viewBinding(ViewWordRowBinding::bind)

    private var _item: WordRowUiModel? = null
    val item: WordRowUiModel
        get() = _item ?: error("Not bind yet")

    init {
        inflate(context, R.layout.view_word_row, this)
        val contentPadding = 12.px
        updatePadding(contentPadding, contentPadding, contentPadding, contentPadding)
    }

    fun setOnClickAction(onItemClickAction: (view: WordRowView, item: WordRowUiModel) -> Unit) {
        val onClick = OnClickListener { onItemClickAction.invoke(this, item) }
        binding.wordRowIcon.setOnClickListener(onClick)
        setOnClickListener(onClick)
    }

    fun bind(model: WordRowUiModel) = with(binding) {
        _item = model
        wordRowText.bind(model.text)
    }
}

fun wordRowDelegate(
    inflateListener: ((view: WordRowView) -> Unit)? = null,
    bindListener: ((view: WordRowView, item: WordRowUiModel) -> Unit)? = null,
): AdapterDelegate<List<UiModel>> =
    adapterDelegateViewBinding<WordRowUiModel, UiModel, ItemWordRowBinding>(
        viewBinding = { inflater, parent -> ItemWordRowBinding.inflate(inflater, parent, false) }
    ) {

        inflateListener?.invoke(binding.root)

        bind {
            binding.root.bind(item)
            bindListener?.invoke(binding.root, item)
        }
    }