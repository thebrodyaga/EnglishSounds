package com.thebrodyaga.brandbook.component.data.right

import androidx.constraintlayout.widget.ConstraintLayout
import android.content.Context
import android.util.AttributeSet
import com.thebrodyaga.brandbook.R
import com.thebrodyaga.brandbook.databinding.ViewDataRightTextButtonBinding
import com.thebrodyaga.brandbook.databinding.ViewDataRightTextWithIconBinding
import com.thebrodyaga.brandbook.databinding.ViewDataRightTwoLineTextBinding
import com.thebrodyaga.core.uiUtils.image.bindOrGone
import com.thebrodyaga.core.uiUtils.text.bindOrGone
import com.thebrodyaga.brandbook.utils.viewPool.ComponentViewPool
import com.thebrodyaga.core.uiUtils.common.inflater

class DataRightView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : ConstraintLayout(context, attrs) {

    private var currentModel: DataRightUiModel? = null

    private val viewPool = ComponentViewPool<DataRightUiModel>(this) {
        when (it) {
            DataRightUiModel.TwoLineText::class -> ViewDataRightTwoLineTextBinding.inflate(inflater, this)
            DataRightUiModel.TextWithIcon::class -> ViewDataRightTextWithIconBinding.inflate(inflater, this)
            DataRightUiModel.Button.Text::class -> ViewDataRightTextButtonBinding.inflate(inflater, this)
            else -> error("No type for viewPool: $this")
        }
    }

    init {
        if (isInEditMode) {
            inflate(context, R.layout.view_data_right_two_line_text, this)
        }
    }

    fun bind(model: DataRightUiModel) {
        val pair = viewPool.updatePoolOfViews(this.currentModel, model)
        when (model) {
            is DataRightUiModel.TwoLineText -> (pair.first as ViewDataRightTwoLineTextBinding).bind(model)
            is DataRightUiModel.TextWithIcon -> (pair.first as ViewDataRightTextWithIconBinding).bind(model)
            is DataRightUiModel.Button.Text -> (pair.first as ViewDataRightTextButtonBinding).bind(model)
        }
        this.currentModel = model
    }

    private fun ViewDataRightTwoLineTextBinding.bind(model: DataRightUiModel.TwoLineText) {
        this.dataRightTwoLineTextFirst.bindOrGone(model.firstLineText)
        this.dataRightTwoLineTextSecond.bindOrGone(model.secondLineText)
    }

    private fun ViewDataRightTextWithIconBinding.bind(model: DataRightUiModel.TextWithIcon) {
        this.dataRightTextWithIconText.bindOrGone(model.text)
        this.dataRightTextWithIconIcon.bindOrGone(model.icon)
    }

    private fun ViewDataRightTextButtonBinding.bind(model: DataRightUiModel.Button.Text) {
        this.dataRightTextButton.bindOrGone(model.text)
    }
}