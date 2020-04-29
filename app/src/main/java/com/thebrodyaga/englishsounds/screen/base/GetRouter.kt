package com.thebrodyaga.englishsounds.screen.base

import androidx.fragment.app.Fragment
import com.thebrodyaga.englishsounds.app.App
import com.thebrodyaga.englishsounds.navigation.RouterTransition
import com.thebrodyaga.englishsounds.screen.fragments.main.TabContainerFragment

interface GetRouter {

    val fragment: Fragment

    /**
     * Ищет первый роутер FlowFragment
     * Идет по родительским фрагментам пока не дойдет до первого FlowFragment
     * Иначе null значит не вложен во FlowFragment
     */
    fun getFlowRouter(): RouterTransition? {
        var parentFragment: Fragment = fragment.parentFragment ?: return null
        while (true) {
            if (parentFragment is FlowFragment) {
                return parentFragment.localRouter
            }
            parentFragment = parentFragment.parentFragment ?: return null
        }
    }

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
     * Ищет роутер TabContainerFragment
     * Идет по родительским фрагментам пока не дойдет до первого TabContainerFragment
     * Иначе null значит не вложен в TabContainerFragment
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