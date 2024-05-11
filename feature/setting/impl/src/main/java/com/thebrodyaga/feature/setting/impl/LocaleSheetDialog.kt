package com.thebrodyaga.feature.setting.impl

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import by.kirich1409.viewbindingdelegate.viewBinding
import com.thebrodyaga.brandbook.component.data.DataUiModel
import com.thebrodyaga.brandbook.component.data.dataViewCommonDelegate
import com.thebrodyaga.brandbook.component.data.left.DataLeftUiModel
import com.thebrodyaga.brandbook.component.data.right.DataRightUiModel
import com.thebrodyaga.brandbook.model.UiModel
import com.thebrodyaga.brandbook.recycler.CommonAdapter
import com.thebrodyaga.core.uiUtils.image.IconContainer
import com.thebrodyaga.core.uiUtils.image.ImageViewUiModel
import com.thebrodyaga.core.uiUtils.insets.appleBottomInsets
import com.thebrodyaga.core.uiUtils.insets.consume
import com.thebrodyaga.core.uiUtils.insets.doOnApplyWindowInsets
import com.thebrodyaga.core.uiUtils.insets.systemAndIme
import com.thebrodyaga.core.uiUtils.text.TextContainer
import com.thebrodyaga.core.uiUtils.text.TextViewUiModel
import com.thebrodyaga.englishsounds.base.app.BaseBottomSheetDialog
import com.thebrodyaga.feature.setting.impl.databinding.DialogLocaleSheetBinding
import java.util.Locale


class LocaleSheetDialog : BaseBottomSheetDialog(R.layout.dialog_locale_sheet) {

    private val binding by viewBinding(DialogLocaleSheetBinding::bind)
    private val adapter = CommonAdapter(
        delegates = listOf(
            dataViewCommonDelegate(
                bindListener = { view, _ ->
                    view.setOnClickAction { _, item ->
                        dismiss()
                        AppCompatDelegate.setApplicationLocales(LocaleListCompat.create(item.payload as Locale))
                    }
                }
            )
        )
    )
    private val appLocale = LocaleListCompat.create(
        Locale.ENGLISH,
        Locale("es"),
        Locale("ru"),
    )


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.doOnApplyWindowInsets { _, insets, _ ->
            insets.systemAndIme().consume {
                binding.localeRecyclerView.appleBottomInsets(this)
            }
        }
        binding.localeRecyclerView.adapter = adapter

        // Fetching the current application locale using the AndroidX support Library
        val currentLocale = if (!AppCompatDelegate.getApplicationLocales().isEmpty) {
            // Fetches the current Application Locale from the list
            AppCompatDelegate.getApplicationLocales()[0]!!
        } else {
            // Fetches the default System Locale
            LocaleListCompat.getDefault().get(0)!!
        }

        // eng
        var checkLocaleIndex = 0
        val newItems = mutableListOf<UiModel>()
        for (i in 0 until appLocale.size()) {
            val locale = appLocale[i]!!
            var checkIc: DataRightUiModel? = null
            if (currentLocale.language == locale.language) {
                checkLocaleIndex = i

            }
            val row = DataUiModel(
                leftSide = DataLeftUiModel.TwoLineText(
                    firstLineText = TextViewUiModel.Raw(text = TextContainer.Raw(locale.displayLanguage))
                ),
                rightSide = checkIc,
                payload = locale
            )
            newItems.add(row)
        }
        val icon = DataRightUiModel.TextWithIcon(
            icon = ImageViewUiModel(
                icon = IconContainer.Res(R.drawable.ic_check),
                iconTint = R.attr.colorPrimary
            )
        )
        newItems.set(checkLocaleIndex, (newItems.get(checkLocaleIndex) as DataUiModel).copy(rightSide = icon))
        adapter.items = newItems
    }
}