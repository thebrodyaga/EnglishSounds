package com.thebrodyaga.englishsounds.screen.view

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.annotation.IntRange
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import com.thebrodyaga.englishsounds.R
import kotlinx.android.synthetic.main.view_layout_app_rating_bar.view.*

class AppRatingBar @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr), View.OnClickListener {

    private val bgRateError =
        ContextCompat.getDrawable(context, R.drawable.shape_rounded_stroke_error_15dp)
    private val bg = null
//            ContextCompat.getDrawable(context, R.drawable.bg_app_rating_bar)
    private var isErrorState = false

    @IntRange(from = 1, to = 5)
    var selectedStar: Int? = null
        private set

    init {
        val padding = context.resources.getDimensionPixelSize(R.dimen.base_offset_small)
        setPaddingRelative(padding, padding, padding, padding)
        background = bg

        inflate(context, R.layout.view_layout_app_rating_bar, this)

        rating_one.setOnClickListener(this)
        rating_two.setOnClickListener(this)
        rating_three.setOnClickListener(this)
        rating_four.setOnClickListener(this)
        rating_five.setOnClickListener(this)
    }

    override fun onClick(v: View) {
        if (isErrorState) {
            background = bg
            isErrorState = false
        }

        isSelected = true

        rating_one.isSelected = v.id == rating_one.id ||
                v.id == rating_two.id ||
                v.id == rating_three.id ||
                v.id == rating_four.id ||
                v.id == rating_five.id

        rating_two.isSelected = v.id == rating_two.id ||
                v.id == rating_three.id ||
                v.id == rating_four.id ||
                v.id == rating_five.id

        rating_three.isSelected = v.id == rating_three.id ||
                v.id == rating_four.id ||
                v.id == rating_five.id

        rating_four.isSelected = v.id == rating_four.id ||
                v.id == rating_five.id

        rating_five.isSelected = v.id == rating_five.id

        selectedStar = when (v.id) {
            rating_one.id -> 1
            rating_two.id -> 2
            rating_three.id -> 3
            rating_four.id -> 4
            rating_five.id -> 5
            else -> null
        }
    }

    fun setOnError() {
        background = bgRateError
        isErrorState = true
    }
}