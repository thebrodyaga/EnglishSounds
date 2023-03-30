package com.thebrodyaga.brandbook.component.sound.mini

import android.content.Context
import android.util.AttributeSet
import by.kirich1409.viewbindingdelegate.viewBinding
import com.google.android.material.card.MaterialCardView
import com.thebrodyaga.brandbook.R
import com.thebrodyaga.brandbook.databinding.ViewSoundCardMiniBinding
import com.thebrodyaga.brandbook.utils.drawable.bindBackground
import com.thebrodyaga.brandbook.utils.text.bind
import com.thebrodyaga.core.uiUtils.resources.px

class SoundCardMiniView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : MaterialCardView(context, attrs, R.attr.materialCardViewElevatedStyle) {

    private val binding by viewBinding(ViewSoundCardMiniBinding::bind)

    private var _item: SoundCardMiniUiModel? = null
    val item: SoundCardMiniUiModel
        get() = _item ?: error("Not bind yet")

    init {
        inflate(context, R.layout.view_sound_card_mini, this)
        val contentPadding = 8.px
        setContentPadding(contentPadding, contentPadding, contentPadding, contentPadding)
    }

    fun setOnClickAction(onItemClickAction: (view: SoundCardMiniView, item: SoundCardMiniUiModel) -> Unit) {
        setOnClickListener {
            onItemClickAction.invoke(this, item)
        }
    }

    fun bind(model: SoundCardMiniUiModel) = with(binding) {
        _item = model
        soundCardMiniSound.bind(model.transcription)
        soundCardMiniSound.bindBackground(model.transcriptionBg)
    }
}