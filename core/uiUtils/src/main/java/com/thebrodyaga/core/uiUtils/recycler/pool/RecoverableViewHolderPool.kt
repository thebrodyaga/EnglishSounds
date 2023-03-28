package com.thebrodyaga.core.uiUtils.recycler.pool

import android.util.Log
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import androidx.tracing.Trace
import com.thebrodyaga.core.uiUtils.view.AsyncLayoutInflater
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.lang.reflect.Field
import javax.inject.Inject

class RecoverableViewHolderPool @Inject constructor(
    private val activity: AppCompatActivity,
    private val recoverableDelayMs: Long = 500L,
    private val delegate: ViewHolderPool = AsyncViewHolderPool(activity),
) : ViewHolderPool {

    companion object {
        private const val TAG = "RecoverableVHPool"
    }

    private val recoverableTag = "${TAG}_recoverable_createAndPushViewHolder"
    private val pendingCreation = mutableMapOf<Int, Job>()
    private val viewHolderFactories = mutableMapOf<Int, () -> Job>()

    private val fakeParent: FrameLayout = FrameLayout(activity)
    private val scope: LifecycleCoroutineScope
        get() = activity.lifecycleScope

    override fun pop(viewType: Int): RecyclerView.ViewHolder? {
        resetPendingCreation(viewType)
        val job = scope.launch {
            try {
                delay(recoverableDelayMs)
                val recoverableSize = ((maxSize(viewType) ?: 0) - (size(viewType) ?: 0))
                Log.d(
                    TAG,
                    "recoverableSize = $recoverableSize " +
                            "size = ${size(viewType)}, maxSize = ${maxSize(viewType)}"
                )
                repeat(recoverableSize) {
                    viewHolderFactories[viewType]?.invoke()
                }
            } catch (e: CancellationException) {
                Log.d(TAG, "CancellationException")
            }
        }
        pendingCreation[viewType] = job
        return delegate.pop(viewType)
    }

    override fun push(viewHolder: RecyclerView.ViewHolder?) {
        delegate.push(viewHolder)
    }

    override fun size(viewType: Int): Int? = delegate.size(viewType)

    override fun maxSize(viewType: Int): Int? {
        return delegate.maxSize(viewType)
    }

    override fun create(viewType: Int, itemLayout: Int, maxSize: Int, viewHolderFactory: ViewHolderFactory) {
        scope.launch { create(viewType, itemLayout, maxSize, viewHolderFactory, 0) }
    }

    override suspend fun create(
        viewType: Int,
        itemLayout: Int,
        maxSize: Int,
        viewHolderFactory: ViewHolderFactory,
        waitingSize: Int
    ): List<RecyclerView.ViewHolder> {

        resetPendingCreation(viewType)
        viewHolderFactories[viewType] =
            { scope.launch { createAndPushViewHolder(viewType, itemLayout, viewHolderFactory) } }

        return delegate.create(viewType, itemLayout, maxSize, viewHolderFactory, waitingSize)
    }

    private fun resetPendingCreation(viewType: Int) {
        pendingCreation.remove(viewType)?.also { it.cancel() }
    }

    private suspend fun createAndPushViewHolder(
        viewType: Int,
        itemLayout: Int,
        viewHolderFactory: ViewHolderFactory,
    ) = withContext(Dispatchers.IO) {
        Trace.beginAsyncSection(recoverableTag, 123454)
        val layoutInflater = AsyncLayoutInflater(activity)
        Log.d(TAG, "create recoverable ViewHolder size = ${size(viewType)}, maxSize = ${maxSize(viewType)}")
        val itemView = layoutInflater.inflate(itemLayout, fakeParent)
        val viewHolder = viewHolderFactory(viewType, itemView)
        setViewType(viewHolder, viewType)
        Log.d(TAG, "push recoverable ViewHolder size = ${size(viewType)}, maxSize = ${maxSize(viewType)}")
        push(viewHolder)
        Trace.endAsyncSection(recoverableTag, 123454)
    }

    private fun setViewType(holder: RecyclerView.ViewHolder, viewType: Int) {
        val viewTypeField: Field = holder::class.java.superclass
            .getDeclaredField("mItemViewType")
        viewTypeField.isAccessible = true
        viewTypeField.setInt(holder, viewType)
    }
}