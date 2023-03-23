package com.thebrodyaga.englishsounds.base.app

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.core.view.ViewCompat
import androidx.fragment.app.Fragment
import com.thebrodyaga.base.navigation.impl.navigator.AppNavigator.Companion.ARG_TRANSITION_NAME
import com.thebrodyaga.base.navigation.impl.createRouterProvider
import com.thebrodyaga.core.uiUtils.insets.appleInsetPadding
import com.thebrodyaga.core.uiUtils.insets.consume
import com.thebrodyaga.core.uiUtils.insets.doOnApplyWindowInsets
import com.thebrodyaga.core.uiUtils.insets.systemAndIme

abstract class ScreenFragment(layoutId: Int) : Fragment(layoutId) {

    protected val routerProvider by lazy { createRouterProvider() }

    @Deprecated("")
    fun getAnyRouter() = routerProvider.anyRouter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = super.onCreateView(inflater, container, savedInstanceState)
        setTransitionName(view)
        return view
    }

    private fun setTransitionName(rootView: View?) {
        rootView ?: return
        val transitionName = arguments?.getString(ARG_TRANSITION_NAME) ?: return
        ViewCompat.setTransitionName(rootView, transitionName);
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        applyWindowInsets(view)
        registerBackPressedDispatcher()
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        // in case of show/hide on main tab
        if (!hidden) registerBackPressedDispatcher()
    }

    protected open fun applyWindowInsets(rootView: View) {
        rootView.doOnApplyWindowInsets { view, insets, _ ->
            insets.systemAndIme().consume {
                view.appleInsetPadding(left = left, top = top, right = right, bottom = bottom)
            }
        }
    }

    private fun registerBackPressedDispatcher() {
        requireActivity().onBackPressedDispatcher.addCallback { onBackPressed() }
    }

    open fun onBackPressed() {
        routerProvider.anyRouter.exit()
    }
}