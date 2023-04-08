package com.thebrodyaga.feature.mainScreen.impl

import android.os.Bundle
import android.view.View
import androidx.lifecycle.lifecycleScope
import by.kirich1409.viewbindingdelegate.viewBinding
import com.thebrodyaga.brandbook.component.data.DataUiModel
import com.thebrodyaga.brandbook.component.data.dataViewCommonDelegate
import com.thebrodyaga.brandbook.component.data.left.DataLeftUiModel
import com.thebrodyaga.brandbook.recycler.CommonAdapter
import com.thebrodyaga.core.uiUtils.insets.appleBottomInsets
import com.thebrodyaga.core.uiUtils.insets.consume
import com.thebrodyaga.core.uiUtils.insets.doOnApplyWindowInsets
import com.thebrodyaga.core.uiUtils.insets.systemAndIme
import com.thebrodyaga.core.uiUtils.text.TextContainer
import com.thebrodyaga.core.uiUtils.text.TextViewUiModel
import com.thebrodyaga.englishsounds.base.app.BaseBottomSheetDialog
import com.thebrodyaga.feature.mainScreen.impl.databinding.DialogTestBottomSheetBinding
import kotlinx.coroutines.delay

class TestBottomSheetDialog : BaseBottomSheetDialog(R.layout.dialog_test_bottom_sheet) {


    private val binding by viewBinding(DialogTestBottomSheetBinding::bind)
    private val adapter = CommonAdapter(
        delegates = listOf(
            dataViewCommonDelegate(
                inflateListener = {view -> view.setOnClickAction { _, _ ->  } }
            )
        )
    )

    private val list = buildList {
        repeat(55) {
            add(
                DataUiModel(
                    leftSide = DataLeftUiModel.TwoLineText(
                        firstLineText = TextViewUiModel.Raw(text = TextContainer.Raw(it.toString()))
                    )
                )
            )
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.doOnApplyWindowInsets { _, insets, _ ->
            insets.systemAndIme().consume {
                binding.recyclerView.appleBottomInsets(this)
            }
        }
        binding.recyclerView.adapter = adapter
        lifecycleScope.launchWhenStarted {
            delay(500L)
            adapter.items = list
        }
    }
}