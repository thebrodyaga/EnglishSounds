package com.thebrodyaga.englishsounds.screen.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import androidx.core.view.ViewCompat
import androidx.fragment.app.Fragment
import com.thebrodyaga.englishsounds.R
import com.thebrodyaga.englishsounds.navigation.Screens
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import moxy.MvpAppCompatFragment

abstract class BaseFragment : MvpAppCompatFragment(), GetRouter, Toolbar.OnMenuItemClickListener {
    override val fragment: Fragment
        get() = this

    abstract fun getLayoutId(): Int

    private val compositeDisposable: CompositeDisposable = CompositeDisposable()

    protected fun unSubscribeOnDestroy(disposable: Disposable) {
        compositeDisposable.add(disposable)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(getLayoutId(), container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        ViewCompat.requestApplyInsets(view)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        compositeDisposable.clear()
    }

    /**
     * закрыть фрагмент
     */
    open fun onBackPressed() {
        getAnyRouter().exit()
    }

    override fun onMenuItemClick(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.settings -> {
                getGlobalRouter().navigateTo(Screens.SettingsScreen)
                true
            }
            else -> false
        }
    }
}