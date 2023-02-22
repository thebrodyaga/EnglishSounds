package com.thebrodyaga.feature.setting.impl

import androidx.core.view.isGone
import android.os.Bundle
import android.view.View
import com.jakewharton.rxbinding3.widget.checkedChanges
import com.thebrodyaga.core.uiUtils.isSystemDarkMode
import com.thebrodyaga.englishsounds.base.app.BaseFragment
import com.thebrodyaga.englishsounds.base.di.findDependencies
import com.thebrodyaga.feature.setting.api.CurrentTheme
import com.thebrodyaga.feature.setting.api.SettingManager
import com.thebrodyaga.feature.setting.impl.di.SettingComponent
import kotlinx.android.synthetic.main.fragment_settings.*
import javax.inject.Inject

class SettingsFragment : BaseFragment() {

    @Inject
    lateinit var settingManager: SettingManager

    override fun getLayoutId(): Int = R.layout.fragment_settings

    override fun onCreate(savedInstanceState: Bundle?) {
        SettingComponent.factory(findDependencies()).inject(this)
        super.onCreate(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        toolbar.setNavigationOnClickListener { getAnyRouter().exit() }
        setThemeSetting()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        system_theme.setOnCheckedChangeListener(null)
        is_dark_theme.setOnCheckedChangeListener(null)
    }

    private fun setThemeSetting() {
        when (settingManager.getCurrentTheme()) {
            CurrentTheme.SYSTEM -> {
                system_theme.isChecked = true
                is_dark_theme.isGone = (true)
            }
            CurrentTheme.DARK -> {
                system_theme.isChecked = false
                is_dark_theme.isGone = (false)
                is_dark_theme.isChecked = true
            }
            CurrentTheme.LIGHT -> {
                system_theme.isChecked = false
                is_dark_theme.isGone = false
                is_dark_theme.isChecked = false
            }
        }

        unSubscribeOnDestroy(
            system_theme.checkedChanges()
                .skipInitialValue()
                .subscribe { onSystemThemeListener.invoke(it) }
        )

        unSubscribeOnDestroy(
            is_dark_theme.checkedChanges()
                .skipInitialValue()
                .subscribe { onIsDarkThemeListener.invoke(it) }
        )
    }

    private val onSystemThemeListener = { isChecked: Boolean ->
        if (isChecked) {
            settingManager.setCurrentTheme(CurrentTheme.SYSTEM)
            is_dark_theme.isGone = (true)
        } else {
            val isSystemDark = context?.isSystemDarkMode() ?: false
            is_dark_theme.isChecked = !isSystemDark
            settingManager.setCurrentTheme(
                if (isSystemDark)
                    CurrentTheme.LIGHT
                else CurrentTheme.DARK
            )
            is_dark_theme.isGone = (false)
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
