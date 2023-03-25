package com.thebrodyaga.core.uiUtils.view.pool

import android.view.View
import android.widget.FrameLayout
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.tracing.Trace
import com.thebrodyaga.core.uiUtils.view.AppAsyncLayoutInflater
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.concurrent.ConcurrentLinkedDeque

class AsyncViewPool constructor(
    private val activity: AppCompatActivity,
) : ViewPool {

    private var maxSize = 0
    private val stack = ConcurrentLinkedDeque<View>()
    private val fakeParent: FrameLayout = FrameLayout(activity)

    override fun <V : View> push(view: V) {
        if (stack.size >= maxSize) return
        stack.push(view)
    }

    override fun <V : View> pop(): V? {
        return stack.pop() as? V
    }

    override suspend fun inflate(
        @LayoutRes resId: Int,
        size: Int,
        waitingSize: Int
    ): List<View> = withContext(Dispatchers.IO) {
        stack.clear()
        maxSize = size
        val waitingList = mutableListOf<Deferred<View>>()
        val nonBlockingList = mutableListOf<Deferred<View>>()

        Trace.beginAsyncSection("AsyncViewPool.all_inflate", 111)
        Trace.beginAsyncSection("AsyncViewPool.async_inflate", 222)
        Trace.beginAsyncSection("AsyncViewPool.waiting_inflate", 333)

        repeat(size) { times ->
            val layoutInflater = AppAsyncLayoutInflater(activity)
            val job = async {
                (layoutInflater.inflate(resId, fakeParent)).also { push(it) }
            }
            if (times < waitingSize) {
                waitingList.add(job)
            } else {
                nonBlockingList.add(job)
            }
        }

        val result = waitingList.awaitAll()
        Trace.endAsyncSection("AsyncViewPool.waiting_inflate", 333)

        launch {
            nonBlockingList.awaitAll()
            Trace.endAsyncSection("AsyncViewPool.async_inflate", 222)
            Trace.endAsyncSection("AsyncViewPool.all_inflate", 111)
        }
        return@withContext result
    }
}