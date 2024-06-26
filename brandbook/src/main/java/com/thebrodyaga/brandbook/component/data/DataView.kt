package com.thebrodyaga.brandbook.component.data

import android.content.Context
import android.util.AttributeSet
import android.view.View.OnClickListener
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import androidx.core.view.updatePadding
import by.kirich1409.viewbindingdelegate.viewBinding
import com.thebrodyaga.brandbook.R
import com.thebrodyaga.brandbook.component.data.left.DataLeftView
import com.thebrodyaga.brandbook.component.data.right.DataRightView
import com.thebrodyaga.brandbook.databinding.ViewDataBinding
import com.thebrodyaga.brandbook.recycler.RecyclableView
import com.thebrodyaga.core.uiUtils.drawable.bindBackground
import com.thebrodyaga.core.uiUtils.resources.px
import com.thebrodyaga.core.uiUtils.ripple.rippleForeground

class DataView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : ConstraintLayout(context, attrs), RecyclableView {

    private val binding by viewBinding(ViewDataBinding::bind)

    val leftSideView: DataLeftView
        get() = binding.leftSideView
    val rightSideView: DataRightView
        get() = binding.rightSideView

    private var _item: DataUiModel? = null
    val item: DataUiModel
        get() = _item ?: error("Not bind yet")


    init {
        inflate(context, R.layout.view_data, this)
        val paddingHorizontal = 16.px
        val paddingVertical = 12.px
        minHeight = 72.px
        updatePadding(
            left = paddingHorizontal,
            top = paddingVertical,
            right = paddingHorizontal,
            bottom = paddingVertical
        )
        rippleForeground()
    }


    fun setOnClickAction(onItemClickAction: ((view: DataView, item: DataUiModel) -> Unit)?) {
        val onClick = onItemClickAction?.let { OnClickListener { onItemClickAction(this, item) } }
        setOnClickListener(onClick)
    }

    fun bind(model: DataUiModel) = with(binding) {
        _item = model
//        isEnabled = model.accessibility.isEnabled
//        isClickable = model.accessibility.isClickable
        root.bindBackground(model.background)
        leftSideView.isVisible = model.leftSide != null
        model.leftSide?.let(leftSideView::bind)
        rightSideView.isVisible = model.rightSide != null
        model.rightSide?.let(rightSideView::bind)
    }

    override fun clearListeners() {
        setOnClickAction(null)
        isClickable = false
        leftSideView.setOnClickListener(null)
        leftSideView.isClickable = false
        rightSideView.setOnClickListener(null)
        rightSideView.isClickable = false
        rightSideView.setOnPlayIconClickAction(null)
        rightSideView.setOnTextButtonClickAction(null)
    }
}