package com.thebrodyaga.brandbook.component.icon

import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.view.isVisible
import android.content.Context
import android.util.AttributeSet
import by.kirich1409.viewbindingdelegate.viewBinding
import com.thebrodyaga.brandbook.R
import com.thebrodyaga.brandbook.databinding.ViewIconWrapperSingleBinding
import com.thebrodyaga.brandbook.databinding.ViewIconWrapperTwoBinding
import com.thebrodyaga.brandbook.utils.viewPool.ComponentViewPool
import com.thebrodyaga.core.uiUtils.image.bind
import com.thebrodyaga.core.uiUtils.image.bindOrGone

class IconWrapperView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : ConstraintLayout(context, attrs) {

    private val singleIcon by viewBinding(ViewIconWrapperSingleBinding::bind)
    private val twoIcon by viewBinding(ViewIconWrapperTwoBinding::bind)

    private val constraintSet = ConstraintSet()
    private var currentModel: IconWrapperUiModel? = null
    private val viewPool = ComponentViewPool<IconWrapperUiModel>(this) {
        when (this) {
            IconWrapperUiModel.SingleIcon::class -> singleIcon
            IconWrapperUiModel.TwoIcon::class -> twoIcon
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
        this.imageViewIcon.bind(model.icon)
    }

    private fun ViewIconWrapperTwoBinding.bind(model: IconWrapperUiModel.TwoIcon) {
        this.imageViewFirstIcon.bindOrGone(model.first)
        this.imageViewSecondIcon.bindOrGone(model.second)

        constraintSet.clone(this@IconWrapperView)
        when (model.angleType) {
            TwoIconAngle.Plus180 -> {
                constraintSet.setHorizontalBias(imageViewFirstIcon.id, 1.0f)
                constraintSet.setVerticalBias(imageViewFirstIcon.id, 0.5f)
                constraintSet.setHorizontalBias(imageViewSecondIcon.id, 0.0f)
                constraintSet.setVerticalBias(imageViewSecondIcon.id, 0.5f)
            }
            TwoIconAngle.Plus45 -> {
                constraintSet.setHorizontalBias(imageViewFirstIcon.id, 0.0f)
                constraintSet.setVerticalBias(imageViewFirstIcon.id, 0.0f)
                constraintSet.setHorizontalBias(imageViewSecondIcon.id, 1.0f)
                constraintSet.setVerticalBias(imageViewSecondIcon.id, 1.0f)
            }
            TwoIconAngle.Zero -> {
                constraintSet.setHorizontalBias(imageViewFirstIcon.id, 0.0f)
                constraintSet.setVerticalBias(imageViewFirstIcon.id, 0.5f)
                constraintSet.setHorizontalBias(imageViewSecondIcon.id, 1.0f)
                constraintSet.setVerticalBias(imageViewSecondIcon.id, 0.5f)
            }
        }
        constraintSet.applyTo(this@IconWrapperView)
    }
}