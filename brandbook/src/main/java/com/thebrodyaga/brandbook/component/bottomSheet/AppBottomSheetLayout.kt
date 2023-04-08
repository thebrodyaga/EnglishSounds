package com.thebrodyaga.brandbook.component.bottomSheet

import android.content.Context
import android.util.AttributeSet
import android.widget.LinearLayout
import com.google.android.material.bottomsheet.BottomSheetDragHandleView

class AppBottomSheetLayout @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : LinearLayout(context, attrs) {

    init {
        orientation = VERTICAL
        val dragHandleView = BottomSheetDragHandleView(context)
        dragHandleView.layoutParams = LayoutParams(
            LayoutParams.MATCH_PARENT,
            LayoutParams.WRAP_CONTENT,
        )
        addView(dragHandleView)
    }
}