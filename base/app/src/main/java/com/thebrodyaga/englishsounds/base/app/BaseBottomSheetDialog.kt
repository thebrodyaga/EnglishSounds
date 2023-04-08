package com.thebrodyaga.englishsounds.base.app

import android.app.Dialog
import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import androidx.annotation.LayoutRes
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.thebrodyaga.core.uiUtils.insets.WindowInsetsDelegate

abstract class BaseBottomSheetDialog(
    @LayoutRes contentLayoutId: Int
) : BottomSheetDialogFragment(contentLayoutId) {

    protected val behavior: BottomSheetBehavior<FrameLayout>?
        get() = (dialog as? BottomSheetDialog)?.behavior

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        val window = dialog.window!!
//        WindowInsetsDelegate(window).onCreate()
        return dialog
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupBehavior((requireDialog() as BottomSheetDialog).behavior)
    }

    // default setting
    // https://github.com/material-components/material-components-android/blob/master/docs/components/BottomSheet.md#behavior-attributes
    protected open fun setupBehavior(behavior: BottomSheetBehavior<FrameLayout>) {
        behavior.skipCollapsed = false
        behavior.isFitToContents = true
    }
}