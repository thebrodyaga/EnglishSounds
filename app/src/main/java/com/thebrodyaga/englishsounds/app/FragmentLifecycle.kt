package com.thebrodyaga.englishsounds.app

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager

class FragmentLifecycle : FragmentManager.FragmentLifecycleCallbacks() {
    override fun onFragmentPreAttached(fm: FragmentManager, f: Fragment, context: Context) {
        super.onFragmentPreAttached(fm, f, context)
        fragmentLifecycleLog(f)
    }

    override fun onFragmentAttached(fm: FragmentManager, f: Fragment, context: Context) {
        super.onFragmentAttached(fm, f, context)
        fragmentLifecycleLog(f)
    }

    override fun onFragmentCreated(
        fm: FragmentManager,
        f: Fragment,
        savedInstanceState: Bundle?
    ) {
        super.onFragmentCreated(fm, f, savedInstanceState)
        fragmentLifecycleLog(
            f,
            savedInstanceState
        )

    }

    override fun onFragmentActivityCreated(
        fm: FragmentManager,
        f: Fragment,
        savedInstanceState: Bundle?
    ) {
        super.onFragmentActivityCreated(fm, f, savedInstanceState)
        fragmentLifecycleLog(
            f,
            savedInstanceState
        )
    }

    override fun onFragmentViewCreated(
        fm: FragmentManager,
        f: Fragment,
        v: View,
        savedInstanceState: Bundle?
    ) {
        super.onFragmentViewCreated(fm, f, v, savedInstanceState)
        fragmentLifecycleLog(
            f,
            savedInstanceState
        )
    }

    override fun onFragmentStarted(fm: FragmentManager, f: Fragment) {
        super.onFragmentStarted(fm, f)
        fragmentLifecycleLog(f)
    }

    override fun onFragmentResumed(fm: FragmentManager, f: Fragment) {
        super.onFragmentResumed(fm, f)
        fragmentLifecycleLog(f)
    }

    override fun onFragmentPaused(fm: FragmentManager, f: Fragment) {
        super.onFragmentPaused(fm, f)
        fragmentLifecycleLog(f)
    }

    override fun onFragmentStopped(fm: FragmentManager, f: Fragment) {
        super.onFragmentStopped(fm, f)
        fragmentLifecycleLog(f)
    }

    override fun onFragmentSaveInstanceState(
        fm: FragmentManager,
        f: Fragment,
        outState: Bundle
    ) {
        super.onFragmentSaveInstanceState(fm, f, outState)
        fragmentLifecycleLog(
            f,
            outState
        )
    }

    override fun onFragmentViewDestroyed(fm: FragmentManager, f: Fragment) {
        super.onFragmentViewDestroyed(fm, f)
        fragmentLifecycleLog(f)
    }

    override fun onFragmentDestroyed(fm: FragmentManager, f: Fragment) {
        super.onFragmentDestroyed(fm, f)
        fragmentLifecycleLog(f)
    }

    override fun onFragmentDetached(fm: FragmentManager, f: Fragment) {
        super.onFragmentDetached(fm, f)
        fragmentLifecycleLog(f)
    }

    companion object {
        private const val FRAGMENT_LIVECYCLE_CALLBACKS = "FragmentLifecycle"
        private fun String.trace(e: Array<StackTraceElement>): String {
            var doNext = false
            for (s in e) {
                if (doNext) return s.methodName
                doNext = s.methodName.contains(this)
            }
            return "null"
        }

        @SuppressLint("LogNotTimber")
        private fun fragmentLifecycleLog(fragment: Fragment, bundle: Bundle?) {
            Log.d(
                FRAGMENT_LIVECYCLE_CALLBACKS, "Fragment: ${fragment.javaClass.simpleName +
                        ", Id: " + System.identityHashCode(fragment)}" +
                        ", MethodName: ${"access\$fragmentLifecycleLog".trace(
                            Thread.currentThread().stackTrace
                        )}, " +
                        "Bundle: ${if (bundle != null) bundle.javaClass.simpleName +
                                ", Id: " + System.identityHashCode(bundle) else "null"}"
            )
        }

        @SuppressLint("LogNotTimber")
        private fun fragmentLifecycleLog(fragment: Fragment) {
            Log.d(
                FRAGMENT_LIVECYCLE_CALLBACKS, ("Fragment:  ${fragment.javaClass.simpleName +
                        ", Id: " + System.identityHashCode(fragment)}" +
                        ", MethodName: " + "access\$fragmentLifecycleLog".trace(
                    Thread.currentThread().stackTrace
                ))
            )
        }
    }
}
