package com.thebrodyaga.englishsounds.navigation

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import ru.terrakok.cicerone.android.support.SupportAppNavigator
import ru.terrakok.cicerone.commands.Command


open class TransitionNavigator : SupportAppNavigator {
    private val fragmentManager: FragmentManager
    private val containerId: Int

    constructor(activity: FragmentActivity, containerId: Int) : super(activity, containerId) {
        this.fragmentManager = activity.supportFragmentManager
        this.containerId = containerId
    }

    constructor(activity: FragmentActivity, fragmentManager: FragmentManager, containerId: Int)
            : super(activity, fragmentManager, containerId) {
        this.fragmentManager = fragmentManager
        this.containerId = containerId
    }

    override fun applyCommand(command: Command?) {
        when (command) {
            is ForwardWithTransition -> activityForward(command)
            is ReplaceWithTransition -> activityReplace(command)
            else -> super.applyCommand(command)
        }
    }

    override fun setupFragmentTransaction(
            command: Command,
            currentFragment: Fragment?,
            nextFragment: Fragment,
            fragmentTransaction: FragmentTransaction
    ) {
        if (command !is TransitionCommand)
            return
        command.transitionBox?.apply {
            nextFragment.sharedElementEnterTransition = sharedElementEnterTransition
            nextFragment.enterTransition = enterTransition
            nextFragment.exitTransition = exitTransition
            nextFragment.sharedElementReturnTransition = sharedElementReturnTransition
        }
        command.sharedElement
                .forEach { fragmentTransaction.addSharedElement(it.first, it.second) }
        fragmentTransaction.setReorderingAllowed(true)
    }
}