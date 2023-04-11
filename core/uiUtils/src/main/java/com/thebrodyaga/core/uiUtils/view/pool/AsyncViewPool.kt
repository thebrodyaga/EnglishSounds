package com.thebrodyaga.core.uiUtils.view.pool

import android.view.View
import android.widget.FrameLayout
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.tracing.Trace
import com.thebrodyaga.core.uiUtils.view.AsyncLayoutInflater
import kotlinx.coroutines.*
import java.util.concurrent.ConcurrentLinkedDeque
import kotlin.random.Random

class AsyncViewPool constructor(
    private val activity: AppCompatActivity,
) : ViewPool {

    companion object {
        private const val TAG = "AsyncViewPool."
    }

    private val allTrace = "${TAG}_all_inflate" to Random.nextInt()
    private val asyncTrace = "${TAG}_async_inflate" to Random.nextInt()
    private val waitingTrace = "${TAG}_waiting_inflate" to Random.nextInt()

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
    ): List<View> = coroutineScope {
        stack.clear()
        maxSize = size
        val waitingList = mutableListOf<Deferred<View>>()
        val nonBlockingList = mutableListOf<Deferred<View>>()

        Trace.beginAsyncSection(allTrace.first, allTrace.second)
        Trace.beginAsyncSection(asyncTrace.first, asyncTrace.second)
        Trace.beginAsyncSection(allTrace.first, allTrace.second)

        repeat(size) { times ->
            val layoutInflater = AsyncLayoutInflater(activity)
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
        Trace.endAsyncSection(waitingTrace.first, waitingTrace.second)

        launch {
            nonBlockingList.awaitAll()
            Trace.endAsyncSection(asyncTrace.first, asyncTrace.second)
            Trace.endAsyncSection(allTrace.first, allTrace.second)
        }
        result
    }
}