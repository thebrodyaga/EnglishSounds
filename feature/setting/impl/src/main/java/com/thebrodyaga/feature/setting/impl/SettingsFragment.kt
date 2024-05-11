package com.thebrodyaga.feature.setting.impl

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import by.kirich1409.viewbindingdelegate.viewBinding
import com.thebrodyaga.ad.api.AppAdManager
import com.thebrodyaga.ad.google.GoogleMobileAdsConsentManager
import com.thebrodyaga.brandbook.component.data.DataUiModel
import com.thebrodyaga.brandbook.component.data.left.DataLeftUiModel
import com.thebrodyaga.brandbook.component.data.right.DataRightUiModel
import com.thebrodyaga.core.uiUtils.insets.appleBottomInsets
import com.thebrodyaga.core.uiUtils.insets.appleTopInsets
import com.thebrodyaga.core.uiUtils.insets.consume
import com.thebrodyaga.core.uiUtils.insets.doOnApplyWindowInsets
import com.thebrodyaga.core.uiUtils.insets.systemAndIme
import com.thebrodyaga.core.uiUtils.isSystemDarkMode
import com.thebrodyaga.core.uiUtils.text.TextContainer
import com.thebrodyaga.core.uiUtils.text.TextViewUiModel
import com.thebrodyaga.data.setting.api.CurrentTheme
import com.thebrodyaga.data.setting.api.SettingManager
import com.thebrodyaga.englishsounds.base.app.ScreenFragment
import com.thebrodyaga.englishsounds.base.app.ViewModelFactory
import com.thebrodyaga.englishsounds.base.di.findDependencies
import com.thebrodyaga.feature.setting.impl.databinding.FragmentSettingsBinding
import com.thebrodyaga.feature.setting.impl.di.SettingComponent
import javax.inject.Inject

class SettingsFragment : ScreenFragment(R.layout.fragment_settings) {

    @Inject
    lateinit var settingManager: SettingManager
    private val binding by viewBinding(FragmentSettingsBinding::bind)

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    @Inject
    lateinit var googleMobileAdsConsentManager: GoogleMobileAdsConsentManager

    @Inject
    lateinit var appAdManager: AppAdManager

    private val viewModel: SettingsViewModel by viewModels { viewModelFactory }

    override fun onCreate(savedInstanceState: Bundle?) {
        SettingComponent.factory(findDependencies()).inject(this)
        super.onCreate(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setThemeSetting()
        val isPrivacyOptionsRequired = googleMobileAdsConsentManager.isPrivacyOptionsRequired
        binding.settingsPrivacy.isVisible = isPrivacyOptionsRequired
        binding.settingsPrivacy.setOnClickAction { _, _ ->
            val activity = activity
            if (activity != null) {
                googleMobileAdsConsentManager.showPrivacyOptionsForm(activity) {
                    appAdManager.refreshAds()
                }
            }
        }
        binding.settingsPrivacy.bind(
            DataUiModel(
                leftSide = DataLeftUiModel.TwoLineText(
                    firstLineText = TextViewUiModel.Raw(
                        text = TextContainer.Res(R.string.privacy_settings),
                        textAppearance = R.attr.textAppearanceBodyLarge
                    ),
                )
            )
        )

        binding.locale.setOnClickAction { _, _ ->
            LocaleSheetDialog().show(childFragmentManager, "LocaleSheetDialog")
        }
        val currentLocale = if (!AppCompatDelegate.getApplicationLocales().isEmpty) {
            // Fetches the current Application Locale from the list
            AppCompatDelegate.getApplicationLocales()[0]!!
        } else {
            // Fetches the default System Locale
            LocaleListCompat.getDefault().get(0)!!
        }
        binding.locale.bind(
            DataUiModel(
                leftSide = DataLeftUiModel.TwoLineText(
                    firstLineText = TextViewUiModel.Raw(
                        text = TextContainer.Res(R.string.language),
                        textAppearance = R.attr.textAppearanceBodyLarge
                    ),
                ),
                rightSide = DataRightUiModel.TwoLineText(
                    firstLineText = TextViewUiModel.Raw(
                        text = TextContainer.Raw(currentLocale.displayLanguage),
                        textAppearance = R.attr.textAppearanceBodyLarge
                    ),
                ),
            )
        )
    }

    override fun applyWindowInsets(rootView: View) {
        rootView.doOnApplyWindowInsets { _, insets, _ ->
            insets.systemAndIme().consume {
                binding.settingsAppbar.appleTopInsets(this)
                binding.settingsList.appleBottomInsets(this)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.systemTheme.setOnCheckedChangeListener(null)
        binding.isDarkTheme.setOnCheckedChangeListener(null)
    }

    private fun setThemeSetting() {
        when (settingManager.getCurrentTheme()) {
            CurrentTheme.SYSTEM -> {
                binding.systemTheme.isChecked = true
                binding.isDarkTheme.isGone = (true)
            }

            CurrentTheme.DARK -> {
                binding.systemTheme.isChecked = false
                binding.isDarkTheme.isGone = (false)
                binding.isDarkTheme.isChecked = true
            }

            CurrentTheme.LIGHT -> {
                binding.systemTheme.isChecked = false
                binding.isDarkTheme.isGone = false
                binding.isDarkTheme.isChecked = false
            }
        }

        binding.systemTheme.setOnCheckedChangeListener { _, isChecked ->
            onSystemThemeListener.invoke(isChecked)
        }

        binding.isDarkTheme.setOnCheckedChangeListener { _, isChecked ->
            onIsDarkThemeListener.invoke(isChecked)
        }
    }

    private val onSystemThemeListener = { isChecked: Boolean ->
        if (isChecked) {
            settingManager.setCurrentTheme(CurrentTheme.SYSTEM)
            binding.isDarkTheme.isGone = (true)
        } else {
            val isSystemDark = context?.isSystemDarkMode() ?: false
            binding.isDarkTheme.isChecked = !isSystemDark
            settingManager.setCurrentTheme(
                if (isSystemDark) CurrentTheme.LIGHT
                else CurrentTheme.DARK
            )
            binding.isDarkTheme.isGone = (false)
        }
        settingManager.updateTheme()
    }

    private val onIsDarkThemeListener = { isChecked: Boolean ->
        settingManager.setCurrentTheme(
            if (isChecked) CurrentTheme.DARK
            else CurrentTheme.LIGHT
        )
        settingManager.updateTheme()
    }
}
