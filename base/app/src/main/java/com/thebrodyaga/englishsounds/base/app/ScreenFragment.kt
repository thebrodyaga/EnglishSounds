package com.thebrodyaga.englishsounds.base.app

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import com.thebrodyaga.base.navigation.api.AppRouter
import com.thebrodyaga.base.navigation.impl.GetRouter
import com.thebrodyaga.core.uiUtils.insets.appleInsetPadding
import com.thebrodyaga.core.uiUtils.insets.consume
import com.thebrodyaga.core.uiUtils.insets.doOnApplyWindowInsets
import com.thebrodyaga.core.uiUtils.insets.systemAndIme
import com.thebrodyaga.englishsounds.base.di.AppDependencies
import com.thebrodyaga.englishsounds.base.di.findDependencies
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import moxy.MvpAppCompatFragment

abstract class ScreenFragment : MvpAppCompatFragment(), GetRouter {

    val appRouter: AppRouter by lazy {
        findDependencies<AppDependencies>().appRouter()
    }
    override val fragment: Fragment
        get() = this

    abstract fun getLayoutId(): Int

    private val compositeDisposable: CompositeDisposable = CompositeDisposable()

    protected fun unSubscribeOnDestroy(disposable: Disposable) {
        compositeDisposable.add(disposable)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        requireActivity().onBackPressedDispatcher.addCallback { onBackPressed() }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(getLayoutId(), container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        applyWindowInsets(view)
    }


    protected open fun applyWindowInsets(rootView: View) {
        rootView.doOnApplyWindowInsets { view, insets, _ ->
            insets.systemAndIme().consume {
                view.appleInsetPadding(left = left, top = top, right = right, bottom = bottom)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        compositeDisposable.clear()
    }

    open fun onBackPressed() {
        parentFragmentManager.popBackStack()
    }
}