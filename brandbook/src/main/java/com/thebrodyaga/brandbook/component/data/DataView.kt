package com.thebrodyaga.brandbook.component.data

import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import androidx.core.view.updatePadding
import android.content.Context
import android.util.AttributeSet
import by.kirich1409.viewbindingdelegate.viewBinding
import com.thebrodyaga.brandbook.component.data.left.DataLeftView
import com.thebrodyaga.brandbook.component.data.right.DataRightView
import com.thebrodyaga.brandbook.databinding.ViewDataBinding
import com.thebrodyaga.core.uiUtils.resources.px
import com.thebrodyaga.core.uiUtils.shape.rippleForeground

class DataView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : ConstraintLayout(context, attrs) {

    private val binding by viewBinding(ViewDataBinding::bind)

    val leftSideView: DataLeftView
        get() = binding.leftSideView
    val rightSideView: DataRightView
        get() = binding.rightSideView

    private var _item: DataUiModel? = null
    val item: DataUiModel
        get() = _item ?: error("Not bind yet")


    init {
        val paddingHorizontal = 16.px
        val paddingVertical = 12.px
        updatePadding(
            left = paddingHorizontal,
            top = paddingVertical,
            right = paddingHorizontal,
            bottom = paddingVertical
        )
        rippleForeground()
    }


    fun setOnClickAction(onItemClickAction: (view: DataView, item: DataUiModel) -> Unit) {
        setOnClickListener {
            onItemClickAction.invoke(this, item)
        }
    }

    fun bind(model: DataUiModel) = with(binding) {
        _item = model
//        isEnabled = model.accessibility.isEnabled
//        isClickable = model.accessibility.isClickable
//        model.background?.applyBackground(this.root)
        leftSideView.isVisible = model.leftSide != null
        model.leftSide?.let(leftSideView::bind)
        rightSideView.isVisible = model.rightSide != null
        model.rightSide?.let(rightSideView::bind)
    }
}