package com.thebrodyaga.core.uiUtils.view

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.LayoutInflaterCompat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AsyncLayoutInflater(
    context: Context,
) {

    companion object {
        private const val TAG: String = "AppAsyncLayoutInflater"
    }

    private val inflater = BasicInflater(context)

    suspend fun inflate(
        @LayoutRes resId: Int,
        parent: ViewGroup?,
    ): View = withContext(Dispatchers.IO) {
        try {
            inflater.inflate(resId, parent, false)
        } catch (ex: RuntimeException) {
            val msg = "Background inflate is failed. Try use main thread. Error message=${ex.message}"
            Log.e(TAG, msg)
            // Some views need to be inflation-only in the main thread,
            // fall back to inflation in the main thread if there is an exception
            withContext(Dispatchers.Main) { inflater.inflate(resId, parent, false) }
        }
    }

    private class BasicInflater constructor(context: Context) : LayoutInflater(context) {

        init {
            if (context is AppCompatActivity) {
                val appCompatDelegate = context.delegate
                if (appCompatDelegate is Factory2) {
                    LayoutInflaterCompat.setFactory2(this, appCompatDelegate)
                }
            }
        }

        override fun cloneInContext(newContext: Context): LayoutInflater = BasicInflater(newContext)

        override fun onCreateView(name: String, attrs: AttributeSet): View {
            for (prefix in sClassPrefixList) {
                try {
                    val view = createView(name, prefix, attrs)
                    if (view != null) return view
                } catch (_: ClassNotFoundException) {
                }
            }
            return super.onCreateView(name, attrs)
        }

        companion object {
            private val sClassPrefixList = arrayOf(
                "android.widget.", "android.webkit.", "android.app."
            )
        }
    }
}