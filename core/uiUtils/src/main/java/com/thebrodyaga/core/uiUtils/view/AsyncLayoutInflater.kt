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
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Runnable
import kotlinx.coroutines.withContext
import java.util.concurrent.Executors
import java.util.concurrent.ThreadFactory
import java.util.concurrent.atomic.AtomicInteger
import kotlin.coroutines.CoroutineContext

class AsyncLayoutInflater(
    context: Context,
) {

    companion object {
        private const val TAG: String = "AppAsyncLayoutInflater"
    }

    private val inflater = BasicInflater(context)
    private val dispatcher = AsyncInflaterDispatcher()

    suspend fun inflate(
        @LayoutRes resId: Int,
        parent: ViewGroup?,
    ): View = withContext(dispatcher) {
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

    private class AsyncInflaterDispatcher : CoroutineDispatcher() {
        private val executor = Executors.newCachedThreadPool(MinPriorityThreadFactory())

        override fun dispatch(context: CoroutineContext, block: Runnable) {
            executor.execute(block)
        }
    }

    private class MinPriorityThreadFactory : ThreadFactory {
        private val group: ThreadGroup
        private val threadNumber = AtomicInteger(1)
        private val namePrefix: String

        init {
            val s = System.getSecurityManager()
            group = if (s != null) s.threadGroup else Thread.currentThread().threadGroup
            namePrefix = "InflaterPool-" + poolNumber.getAndIncrement() + "-thread-"
        }

        override fun newThread(r: java.lang.Runnable): Thread {
            val t = Thread(
                group, r,
                namePrefix + threadNumber.getAndIncrement(),
                0
            )
            if (t.isDaemon) t.isDaemon = false
            if (t.priority != Thread.MIN_PRIORITY) t.priority = Thread.MIN_PRIORITY
            return t
        }

        companion object {
            private val poolNumber = AtomicInteger(1)
        }
    }
}