package com.thebrodyaga.brandbook.component.data.left

import androidx.constraintlayout.widget.ConstraintLayout
import android.content.Context
import android.util.AttributeSet
import com.thebrodyaga.brandbook.R
import com.thebrodyaga.brandbook.databinding.ViewDataLeftIconTwoLineTextBinding
import com.thebrodyaga.brandbook.databinding.ViewDataLeftTwoLineTextBinding
import com.thebrodyaga.brandbook.utils.viewPool.ComponentViewPool
import com.thebrodyaga.brandbook.utils.text.bindOrGone
import com.thebrodyaga.core.uiUtils.common.inflater

class DataLeftView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : ConstraintLayout(context, attrs) {

    private var currentModel: DataLeftUiModel? = null

    private val viewPool = ComponentViewPool<DataLeftUiModel>(this) {
        when (it) {
            DataLeftUiModel.TwoLineText::class -> ViewDataLeftTwoLineTextBinding.inflate(inflater, this)
            DataLeftUiModel.IconWithTwoLineText::class -> ViewDataLeftIconTwoLineTextBinding.inflate(inflater, this)
            else -> error("No type for viewPool: $this")
        }
    }

    init {
        if (isInEditMode) {
            inflate(context, R.layout.view_data_left_icon_two_line_text, this)
        }
    }

    fun bind(model: DataLeftUiModel) {
        val pair = viewPool.updatePoolOfViews(this.currentModel, model)
        when (model) {
            is DataLeftUiModel.TwoLineText -> (pair.first as ViewDataLeftTwoLineTextBinding).bind(model)
            is DataLeftUiModel.IconWithTwoLineText -> (pair.first as ViewDataLeftIconTwoLineTextBinding).bind(model)
        }
        this.currentModel = model
    }

    private fun ViewDataLeftTwoLineTextBinding.bind(model: DataLeftUiModel.TwoLineText) {
        this.dataLeftTwoLineFirst.bindOrGone(model.firstLineText)
        this.dataLeftTwoLineSecond.bindOrGone(model.secondLineText)
    }

    private fun ViewDataLeftIconTwoLineTextBinding.bind(model: DataLeftUiModel.IconWithTwoLineText) {
        this.dataLeftIconTwoLineFirst.bindOrGone(model.firstLineText)
        this.dataLeftIconTwoLineSecond.bindOrGone(model.secondLineText)
        this.dataLeftIconTwoLineIcon.bindOrGone(model.icon)
    }
}