package com.thebrodyaga.brandbook.component.sound

import android.content.Context
import android.util.AttributeSet
import android.view.ViewGroup
import by.kirich1409.viewbindingdelegate.viewBinding
import com.google.android.material.card.MaterialCardView
import com.thebrodyaga.brandbook.R
import com.thebrodyaga.brandbook.databinding.ViewSoundCardBinding
import com.thebrodyaga.brandbook.model.PopulateView
import com.thebrodyaga.brandbook.recycler.dsl.DslRowAdapterDelegate
import com.thebrodyaga.brandbook.recycler.dsl.RowDelegateBlock
import com.thebrodyaga.brandbook.recycler.dsl.rowDelegate
import com.thebrodyaga.core.uiUtils.drawable.bindBackground
import com.thebrodyaga.core.uiUtils.resources.px
import com.thebrodyaga.core.uiUtils.text.bind

class SoundCardView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : MaterialCardView(context, attrs, R.attr.materialCardViewElevatedStyle), PopulateView<SoundCardUiModel> {

    companion object {
        val LAYOUT_ID: Int = R.layout.item_sound_card
        val VIEW_TYPE: Int = LAYOUT_ID
    }

    private val binding by viewBinding(ViewSoundCardBinding::bind)

    private var _item: SoundCardUiModel? = null
    val contentLayout: ViewGroup
        get() = binding.contentLayout
    val item: SoundCardUiModel
        get() = _item ?: error("Not bind yet")

    init {
        inflate(context, R.layout.view_sound_card, this)
        val contentPadding = 16.px
        setContentPadding(contentPadding, contentPadding, contentPadding, contentPadding)
    }

    fun setOnClickAction(onItemClickAction: (view: SoundCardView, item: SoundCardUiModel) -> Unit) {
        setOnClickListener {
            onItemClickAction.invoke(this, item)
        }
    }

    override fun bind(model: SoundCardUiModel) = with(binding) {
        _item = model
        soundCardSound.bind(model.transcription)
        soundCardSound.bindBackground(model.transcriptionBg)
        soundCardWord.bind(model.word)
    }
}

fun soundCardDelegate(
    block: RowDelegateBlock<SoundCardUiModel, SoundCardView>.() -> Unit
): DslRowAdapterDelegate<SoundCardUiModel, SoundCardView> =
    rowDelegate(SoundCardView.LAYOUT_ID, SoundCardView.VIEW_TYPE) {
        block.invoke(this)
    }