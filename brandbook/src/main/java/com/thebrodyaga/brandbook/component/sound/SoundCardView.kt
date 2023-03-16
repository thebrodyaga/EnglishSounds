package com.thebrodyaga.brandbook.component.sound

import androidx.core.content.ContextCompat
import android.content.Context
import android.util.AttributeSet
import by.kirich1409.viewbindingdelegate.viewBinding
import com.google.android.material.card.MaterialCardView
import com.hannesdorfmann.adapterdelegates4.AdapterDelegate
import com.hannesdorfmann.adapterdelegates4.dsl.adapterDelegateViewBinding
import com.thebrodyaga.brandbook.R
import com.thebrodyaga.brandbook.databinding.ItemSoundCardBinding
import com.thebrodyaga.brandbook.databinding.ViewSoundCardBinding
import com.thebrodyaga.brandbook.model.UiModel
import com.thebrodyaga.core.uiUtils.resources.px
import com.thebrodyaga.brandbook.utils.text.bind

class SoundCardView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : MaterialCardView(context, attrs) {

    private val binding by viewBinding(ViewSoundCardBinding::bind)

    private var _item: SoundCardUiModel? = null
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

    fun bind(model: SoundCardUiModel) = with(binding) {
        _item = model
        soundCardSound.bind(model.transcription)
        soundCardWord.bind(model.word)
        soundCardSound.backgroundTintList = ContextCompat.getColorStateList(context, model.transcriptionTint)
    }
}

fun soundCardDelegate(
    inflateListener: ((view: SoundCardView) -> Unit)? = null,
    bindListener: ((view: SoundCardView, item: SoundCardUiModel) -> Unit)? = null,
): AdapterDelegate<List<UiModel>> =
    adapterDelegateViewBinding<SoundCardUiModel, UiModel, ItemSoundCardBinding>(
        viewBinding = { inflater, parent -> ItemSoundCardBinding.inflate(inflater, parent, false) }
    ) {

        inflateListener?.invoke(binding.root)

        bind {
            binding.root.bind(item)
            bindListener?.invoke(binding.root, item)
        }
    }