package com.thebrodyaga.core.uiUtils.recycler

import android.widget.FrameLayout
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import androidx.tracing.Trace
import com.thebrodyaga.core.uiUtils.view.AsyncLayoutInflater
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import kotlin.random.Random

open class AsyncViewHolderPool(
    private val activity: AppCompatActivity
) : ViewHolderPool {

    companion object {
        private const val TAG = "AsyncViewHolderPool"
    }

    private val allTrace = "${TAG}_all_inflate" to Random.nextInt()
    private val asyncTrace = "${TAG}_async_inflate" to Random.nextInt()
    private val waitingTrace = "${TAG}_waiting_inflate" to Random.nextInt()

    private val stack = mutableMapOf<Int, ArrayDeque<RecyclerView.ViewHolder>>()

    private val fakeParent: FrameLayout = FrameLayout(activity)
    private val scope: LifecycleCoroutineScope
        get() = activity.lifecycleScope

    override fun pop(viewType: Int): RecyclerView.ViewHolder? {
        return stack[viewType]?.removeLast()
    }

    override fun create(
        viewType: Int,
        @LayoutRes itemLayout: Int,
        count: Int,
        viewHolderFactory: ViewHolderFactory,
    ) {
        scope.launch { create(viewType, itemLayout, count, viewHolderFactory, 0) }
    }

    override suspend fun create(
        viewType: Int,
        @LayoutRes itemLayout: Int,
        count: Int,
        viewHolderFactory: ViewHolderFactory,
        waitingSize: Int
    ): List<RecyclerView.ViewHolder> {

        val waitingList = mutableListOf<Deferred<RecyclerView.ViewHolder>>()
        val nonBlockingList = mutableListOf<Deferred<RecyclerView.ViewHolder>>()

        Trace.beginAsyncSection(allTrace.first, allTrace.second)
        Trace.beginAsyncSection(asyncTrace.first, asyncTrace.second)
        Trace.beginAsyncSection(allTrace.first, allTrace.second)

        repeat(count) { times ->
            val layoutInflater = AsyncLayoutInflater(activity)
            val job = scope.async {
                val itemView = layoutInflater.inflate(itemLayout, fakeParent)
                val viewHolder = viewHolderFactory(viewType, itemView)
                stack.getOrPut(viewType) { ArrayDeque() }
                    .add(viewHolder)
                viewHolder
            }
            if (times < waitingSize) {
                waitingList.add(job)
            } else {
                nonBlockingList.add(job)
            }
        }
        val result = waitingList.awaitAll()
        Trace.endAsyncSection(waitingTrace.first, waitingTrace.second)

        scope.launch {
            nonBlockingList.awaitAll()
            Trace.endAsyncSection(asyncTrace.first, asyncTrace.second)
            Trace.endAsyncSection(allTrace.first, allTrace.second)
        }
        return result
    }
}