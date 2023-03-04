package com.thebrodyaga.legacy

import android.content.Context
import android.content.res.ColorStateList
import android.util.AttributeSet
import android.view.View
import by.kirich1409.viewbindingdelegate.viewBinding
import com.google.android.material.card.MaterialCardView
import com.thebrodyaga.legacy.databinding.ItemSoundBinding

class ItemSound @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : MaterialCardView(context, attrs, defStyleAttr) {

    val binding by viewBinding(ItemSoundBinding::bind)

    init {
        View.inflate(context, R.layout.item_sound, this)
        applyAttrs(context, attrs)
    }

    private fun applyAttrs(context: Context, attrs: AttributeSet?) {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.ItemSound, 0, 0)

        val soundColor = typedArray.getColorStateList(R.styleable.ItemSound_soundColor)
        setSoundColor(soundColor)

        typedArray.recycle()
    }

    fun setSoundColor(soundColor: ColorStateList?) {
        binding.sound.backgroundTintList = soundColor
    }
}