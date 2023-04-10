package com.thebrodyaga.core.uiUtils.recycler.pool

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
import java.lang.reflect.Field
import java.util.concurrent.ConcurrentLinkedDeque
import kotlin.random.Random

open class AsyncViewHolderPool constructor(
    private val activity: AppCompatActivity
) : ViewHolderPool {

    companion object {
        private const val TAG = "AsyncViewHolderPool."
    }

    private val allTrace = "${TAG}_all_inflate" to Random.nextInt()
    private val asyncTrace = "${TAG}_async_inflate" to Random.nextInt()
    private val waitingTrace = "${TAG}_waiting_inflate" to Random.nextInt()

    private val mapOfStack = mutableMapOf<Int, StackValueBox>()

    private val fakeParent: FrameLayout = FrameLayout(activity)
    private val scope: LifecycleCoroutineScope
        get() = activity.lifecycleScope

    override fun pop(viewType: Int): RecyclerView.ViewHolder? {
        return try {
            mapOfStack[viewType]?.stack?.pop()
        } catch (e: NoSuchElementException) {
            null
        }
    }

    override fun push(viewHolder: RecyclerView.ViewHolder?) {
        viewHolder?.let {
            val box = mapOfStack[viewHolder.itemViewType] ?: return
            val stack = box.stack
            val maxCount = box.maxSize
            if (stack.size >= maxCount) return
            stack.push(viewHolder)
        }
    }

    override fun size(viewType: Int): Int? = mapOfStack[viewType]?.stack?.size

    override fun maxSize(viewType: Int): Int? = mapOfStack[viewType]?.maxSize

    override fun create(
        viewType: Int,
        @LayoutRes itemLayout: Int,
        maxSize: Int,
        viewHolderFactory: ViewHolderFactory,
    ) {
        scope.launch { create(viewType, itemLayout, maxSize, viewHolderFactory, 0) }
    }

    override suspend fun create(
        viewType: Int,
        @LayoutRes itemLayout: Int,
        maxSize: Int,
        viewHolderFactory: ViewHolderFactory,
        waitingSize: Int
    ): List<RecyclerView.ViewHolder> {
        mapOfStack[viewType]?.stack?.clear()

        val waitingList = mutableListOf<Deferred<RecyclerView.ViewHolder>>()
        val nonBlockingList = mutableListOf<Deferred<RecyclerView.ViewHolder>>()

        Trace.beginAsyncSection(allTrace.first, allTrace.second)
        Trace.beginAsyncSection(asyncTrace.first, asyncTrace.second)
        Trace.beginAsyncSection(allTrace.first, allTrace.second)

        repeat(maxSize) { times ->
            val layoutInflater = AsyncLayoutInflater(activity)
            val job = scope.async {
                val itemView = layoutInflater.inflate(itemLayout, fakeParent)
                val viewHolder = viewHolderFactory(viewType, itemView)
                setViewType(viewHolder, viewType)
                mapOfStack.getOrPut(viewType) {
                    StackValueBox(
                        maxSize,
                        ConcurrentLinkedDeque()
                    )
                }
                    .stack
                    .push(viewHolder)
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

    private fun setViewType(holder: RecyclerView.ViewHolder, viewType: Int) {
        val viewTypeField: Field = holder::class.java.superclass
            .getDeclaredField("mItemViewType")
        viewTypeField.isAccessible = true
        viewTypeField.setInt(holder, viewType)
    }

    private data class StackValueBox(
        val maxSize: Int = 0,
        val stack: ConcurrentLinkedDeque<RecyclerView.ViewHolder>,
    )
}