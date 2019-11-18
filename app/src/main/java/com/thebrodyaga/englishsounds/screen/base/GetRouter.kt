package com.thebrodyaga.englishsounds.screen.base

import androidx.annotation.Nullable
import androidx.fragment.app.Fragment
import com.thebrodyaga.englishsounds.app.App
import com.thebrodyaga.englishsounds.navigation.RouterTransition
import com.thebrodyaga.englishsounds.screen.fragments.main.TabContainerFragment
import ru.terrakok.cicerone.Router

interface GetRouter {

    val fragment: Fragment

    /**
     * Локальный роутер родительского фрагмента, если null то либо не вложен во FlowFragment
     * либо вложен в активити
     */
    fun getFlowRouter(): RouterTransition? {
        val parentFragment = fragment.parentFragment
        return if (parentFragment is FlowFragment)
            parentFragment.localRouter
        else null
    }

    /*  */
    /**
     * Идет по родительским фрагментам пока не дойдет до TabContainerFragment
     * Иначе null значит в активити находиться
     *//*

    fun getTabRouter(): Router? {
        var parentFragment: Fragment = fragment.parentFragment ?: return null
        var result: Router? = null
        while (true) {
            if (parentFragment is TabContainerFragment) {
                result = parentFragment.localRouter
                break
            }
            if (parentFragment.parentFragment == null)
                break
            else parentFragment = parentFragment.parentFragment
        }
        return result
    }*/

    /**
     * Ищет первый роутер и так до глобального
     */
    fun getAnyRouter(): RouterTransition {
        return getFlowRouter() ?: getTabRouter() ?: App.appComponent.getRouter()
    }

    /**
     * Глобальный роутер, активити роутер
     */
    fun getGlobalRouter(): RouterTransition {
        return App.appComponent.getRouter()
    }

    /**
     * Идет по родительским фрагментам пока не дойдет до TabContainerFragment
     * Иначе null значит в активити находиться
     */
    fun getTabRouter(): RouterTransition? {
        var parentFragment: Fragment = fragment.parentFragment ?: return null
        while (true) {
            if (parentFragment is TabContainerFragment) {
                return parentFragment.localRouter
            }
            parentFragment = parentFragment.parentFragment ?: return null
        }
    }
}