package com.thebrodyaga.core.uiUtils.insets

import android.view.View
import androidx.core.graphics.Insets
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import com.thebrodyaga.core.uiUtils.R

val systemAndImeInsetType: Int
    get() = WindowInsetsCompat.Type.systemBars() or WindowInsetsCompat.Type.ime()

val imeInsetType: Int
    get() = WindowInsetsCompat.Type.ime()

val navigationInsetType: Int
    get() = WindowInsetsCompat.Type.navigationBars()

fun WindowInsetsCompat.systemAndIme(): Insets = getInsets(systemAndImeInsetType)
fun WindowInsetsCompat.ime(): Insets = getInsets(WindowInsetsCompat.Type.ime())
fun WindowInsetsCompat.systemBars(): Insets = getInsets(WindowInsetsCompat.Type.systemBars())
fun WindowInsetsCompat.navigationBars(): Insets = getInsets(WindowInsetsCompat.Type.navigationBars())

inline fun Insets.consume(block: Insets.() -> Unit = {}): WindowInsetsCompat {
    block(this)
    return WindowInsetsCompat.CONSUMED
}

fun WindowInsetsCompat.updateInsets(block: WindowInsetsCompat.Builder.() -> Unit = {}): WindowInsetsCompat {
    val builder = WindowInsetsCompat.Builder(this)
    block(builder)
    return builder.build()
}

fun View.appleTopInsets(insets: Insets): Insets =
    appleInsetPadding(oldInsets = insets, top = insets.top, left = insets.left, right = insets.right)

fun View.appleBottomInsets(insets: Insets): Insets =
    appleInsetPadding(oldInsets = insets, bottom = insets.bottom, left = insets.left, right = insets.right)

/**
 * appleInsetPadding and return not consumed Insets
 */
fun View.appleInsetPadding(oldInsets: Insets, left: Int = 0, top: Int = 0, right: Int = 0, bottom: Int = 0): Insets {
    appleInsetPadding(left, top, right, bottom)
    return Insets.of(
        oldInsets.left - left,
        oldInsets.top - top,
        oldInsets.right - right,
        oldInsets.bottom - bottom,

        )
}

private fun View.appleInsetPadding(left: Int = 0, top: Int = 0, right: Int = 0, bottom: Int = 0) {
    val tagKey = R.id.initial_view_padding_tag_id
    val initialPadding = getTag(tagKey) as? InitialViewPadding ?: let {
        val paddings = recordInitialPaddingForView(this)
        setTag(tagKey, paddings)
        paddings
    }
    updatePadding(
        initialPadding.left + left,
        initialPadding.top + top,
        initialPadding.right + right,
        initialPadding.bottom + bottom
    )
}

fun View.doOnApplyWindowInsets(
    f: (view: View, insets: WindowInsetsCompat, initialPadding: InitialViewPadding) -> WindowInsetsCompat
) {
    // Create a snapshot of the view's padding state
    val initialPadding = recordInitialPaddingForView(this)
    // Set an actual OnApplyWindowInsetsListener which proxies to the given
    // lambda, also passing in the original padding state
    ViewCompat.setOnApplyWindowInsetsListener(this) { v, insets ->
        f(v, insets, initialPadding)
    }
    // request some insets
    requestApplyInsetsWhenAttached()
}

fun View.requestApplyInsetsWhenAttached() {
    if (isAttachedToWindow) {
        // We're already attached, just request as normal
        requestApplyInsets()
    } else {
        // We're not attached to the hierarchy, add a listener to
        // request when we are
        addOnAttachStateChangeListener(object : View.OnAttachStateChangeListener {
            override fun onViewAttachedToWindow(v: View) {
                v.removeOnAttachStateChangeListener(this)
                v.requestApplyInsets()
            }

            override fun onViewDetachedFromWindow(v: View) = Unit
        })
    }
}

data class InitialViewPadding(val left: Int, val top: Int, val right: Int, val bottom: Int)

fun recordInitialPaddingForView(view: View): InitialViewPadding = InitialViewPadding(
    view.paddingStart, view.paddingTop, view.paddingEnd, view.paddingBottom
)
