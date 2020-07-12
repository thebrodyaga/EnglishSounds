package com.thebrodyaga.englishsounds.screen.view

import android.content.Context
import android.content.res.ColorStateList
import android.util.AttributeSet
import android.view.View
import com.google.android.material.card.MaterialCardView
import com.thebrodyaga.englishsounds.R
import kotlinx.android.synthetic.main.item_sound.view.*

class ItemSound @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : MaterialCardView(context, attrs, defStyleAttr) {

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
        sound.backgroundTintList = soundColor
    }
}