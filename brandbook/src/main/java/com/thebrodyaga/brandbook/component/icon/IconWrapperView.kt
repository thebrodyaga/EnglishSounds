package com.thebrodyaga.brandbook.component.icon

import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.view.isVisible
import android.content.Context
import android.util.AttributeSet
import com.thebrodyaga.brandbook.R
import com.thebrodyaga.brandbook.databinding.ViewIconWrapperSingleBinding
import com.thebrodyaga.brandbook.databinding.ViewIconWrapperTwoBinding
import com.thebrodyaga.brandbook.utils.viewPool.ComponentViewPool
import com.thebrodyaga.brandbook.utils.image.bind
import com.thebrodyaga.brandbook.utils.image.bindOrGone
import com.thebrodyaga.core.uiUtils.common.inflater

class IconWrapperView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : ConstraintLayout(context, attrs) {

    private val constraintSet = ConstraintSet()
    private var currentModel: IconWrapperUiModel? = null
    private val viewPool = ComponentViewPool<IconWrapperUiModel>(this) {
        when (it) {
            IconWrapperUiModel.SingleIcon::class -> ViewIconWrapperSingleBinding.inflate(inflater, this)
            IconWrapperUiModel.TwoIcon::class -> ViewIconWrapperTwoBinding.inflate(inflater, this)
            else -> error("No type for viewPool: $this")
        }
    }

    init {
        if (isInEditMode) {
            inflate(context, R.layout.view_icon_wrapper_two, this)
        }
    }

    fun bindOrGone(model: IconWrapperUiModel?) {
        isVisible = model != null
        model?.let { bind(it) }
    }

    fun bind(model: IconWrapperUiModel) {
        val pair = viewPool.updatePoolOfViews(this.currentModel, model)
        when (model) {
            is IconWrapperUiModel.SingleIcon -> (pair.first as ViewIconWrapperSingleBinding).bind(model)
            is IconWrapperUiModel.TwoIcon -> (pair.first as ViewIconWrapperTwoBinding).bind(model)
        }
        this.currentModel = model
    }

    private fun ViewIconWrapperSingleBinding.bind(model: IconWrapperUiModel.SingleIcon) {
        this.iconWrapperSingleIcon.bind(model.icon)
    }

    private fun ViewIconWrapperTwoBinding.bind(model: IconWrapperUiModel.TwoIcon) {
        this.iconWrapperTwoFirstIcon.bindOrGone(model.first)
        this.iconWrapperTwoSecondIcon.bindOrGone(model.second)

        constraintSet.clone(this@IconWrapperView)
        when (model.angleType) {
            TwoIconAngle.Plus180 -> {
                constraintSet.setHorizontalBias(iconWrapperTwoFirstIcon.id, 1.0f)
                constraintSet.setVerticalBias(iconWrapperTwoFirstIcon.id, 0.5f)
                constraintSet.setHorizontalBias(iconWrapperTwoSecondIcon.id, 0.0f)
                constraintSet.setVerticalBias(iconWrapperTwoSecondIcon.id, 0.5f)
            }

            TwoIconAngle.Plus45 -> {
                constraintSet.setHorizontalBias(iconWrapperTwoFirstIcon.id, 0.0f)
                constraintSet.setVerticalBias(iconWrapperTwoFirstIcon.id, 0.0f)
                constraintSet.setHorizontalBias(iconWrapperTwoSecondIcon.id, 1.0f)
                constraintSet.setVerticalBias(iconWrapperTwoSecondIcon.id, 1.0f)
            }

            TwoIconAngle.Zero -> {
                constraintSet.setHorizontalBias(iconWrapperTwoFirstIcon.id, 0.0f)
                constraintSet.setVerticalBias(iconWrapperTwoFirstIcon.id, 0.5f)
                constraintSet.setHorizontalBias(iconWrapperTwoSecondIcon.id, 1.0f)
                constraintSet.setVerticalBias(iconWrapperTwoSecondIcon.id, 0.5f)
            }
        }
        constraintSet.applyTo(this@IconWrapperView)
    }
}