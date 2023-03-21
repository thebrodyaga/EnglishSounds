package com.thebrodyaga.feature.setting.impl

import androidx.core.view.isGone
import androidx.fragment.app.viewModels
import android.os.Bundle
import android.view.View
import by.kirich1409.viewbindingdelegate.viewBinding
import com.jakewharton.rxbinding3.widget.checkedChanges
import com.thebrodyaga.core.uiUtils.isSystemDarkMode
import com.thebrodyaga.englishsounds.base.app.ScreenFragment
import com.thebrodyaga.englishsounds.base.app.ViewModelFactory
import com.thebrodyaga.englishsounds.base.di.findDependencies
import com.thebrodyaga.feature.setting.api.CurrentTheme
import com.thebrodyaga.feature.setting.api.SettingManager
import com.thebrodyaga.feature.setting.impl.databinding.FragmentSettingsBinding
import com.thebrodyaga.feature.setting.impl.di.SettingComponent
import javax.inject.Inject

class SettingsFragment : ScreenFragment() {

    @Inject
    lateinit var settingManager: SettingManager
    private val binding by viewBinding(FragmentSettingsBinding::bind)

    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    private val viewModel: SettingsViewModel by viewModels { viewModelFactory }

    override fun getLayoutId(): Int = R.layout.fragment_settings

    override fun onCreate(savedInstanceState: Bundle?) {
        SettingComponent.factory(findDependencies()).inject(this)
        super.onCreate(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.toolbar.setNavigationOnClickListener { getAnyRouter().exit() }
        setThemeSetting()
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

        unSubscribeOnDestroy(
            binding.systemTheme.checkedChanges()
                .skipInitialValue()
                .subscribe { onSystemThemeListener.invoke(it) }
        )

        unSubscribeOnDestroy(
            binding.isDarkTheme.checkedChanges()
                .skipInitialValue()
                .subscribe { onIsDarkThemeListener.invoke(it) }
        )
    }

    private val onSystemThemeListener = { isChecked: Boolean ->
        if (isChecked) {
            settingManager.setCurrentTheme(CurrentTheme.SYSTEM)
            binding.isDarkTheme.isGone = (true)
        } else {
            val isSystemDark = context?.isSystemDarkMode() ?: false
            binding.isDarkTheme.isChecked = !isSystemDark
            settingManager.setCurrentTheme(
                if (isSystemDark)
                    CurrentTheme.LIGHT
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
