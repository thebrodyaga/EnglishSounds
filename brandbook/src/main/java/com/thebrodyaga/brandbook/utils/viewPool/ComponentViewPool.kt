package com.thebrodyaga.brandbook.utils.viewPool

import androidx.core.view.forEach
import androidx.viewbinding.ViewBinding
import android.view.View
import android.view.ViewGroup
import com.thebrodyaga.brandbook.recycler.model.UiModel
import kotlin.reflect.KClass

typealias BindingWithViews = Pair<ViewBinding, List<View>>

class ComponentViewPool<Model : UiModel>(
    private val viewGroup: ViewGroup,
    private val inflater: (type: KClass<out Model>) -> ViewBinding
) {

    private val viewPool = mutableMapOf<KClass<out Model>, BindingWithViews>()

    fun getViewPool(): Map<KClass<out Model>, BindingWithViews> = viewPool

    fun findPoolOfViews(type: KClass<out Model>, removeIfInflate: Boolean): BindingWithViews {
        return viewPool[type] ?: inflateAndSave(type)
            .also { if (removeIfInflate) viewGroup.removeAllViews() }
    }

    fun updatePoolOfViews(oldModel: Model?, newModel: Model): BindingWithViews {
        return when {
            oldModel == null -> inflateAndSave(newModel::class)
            oldModel != newModel -> updatePoolOfChildViews(newModel::class)
            else -> viewPool[newModel::class]!!
        }
    }

    private fun updatePoolOfChildViews(newModel: KClass<out Model>): BindingWithViews {
        val pair = viewPool[newModel]
        viewGroup.removeAllViews()
        return pair
            ?.also { it.second.forEach { view -> viewGroup.addView(view) } }
            ?: inflateAndSave(newModel)
    }

    private fun inflateAndSave(type: KClass<out Model>): BindingWithViews {
        val binding = inflater(type)
        val newView = mutableListOf<View>()
        viewGroup.forEach { newView.add(it) }
        val result = binding to newView
        viewPool[type] = result
        return result
    }
}
