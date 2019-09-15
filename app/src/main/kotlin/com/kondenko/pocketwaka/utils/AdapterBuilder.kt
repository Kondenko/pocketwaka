package com.kondenko.pocketwaka.utils

import android.content.Context
import android.view.View
import android.view.ViewGroup
import com.kondenko.pocketwaka.screens.base.BaseAdapter
import com.kondenko.pocketwaka.utils.exceptions.IllegalViewTypeException
import kotlin.reflect.KClass

private typealias Binder<T> = View.(T) -> Unit

private typealias ItemDeclarations<T> = Map<Int, ItemDeclaration<out T>>

data class ItemDeclaration<T : Any>(val itemClass: KClass<T>, val itemLayoutRes: Int, val binder: (View.(T) -> Unit)?)

class GenericAdapter<T : Any>(context: Context, private val itemDeclarations: ItemDeclarations<T>)
    : BaseAdapter<T, GenericAdapter<T>.GenericViewHolder>(context) {

    private val viewTypes = itemDeclarations.entries.associate { (k, v) -> v.itemClass to k }

    override fun getItemViewType(position: Int): Int = viewTypes[items[position]::class]
            ?: throw IllegalViewTypeException("Couldn't find the type of ${items[position]}")

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GenericViewHolder {
        val declaration = itemDeclarations[viewType]
                ?: throw IllegalViewTypeException("View type $viewType wasn't found in $itemDeclarations")
        val view = inflate(declaration.itemLayoutRes, parent)
        return GenericViewHolder(view, viewType)
    }

    inner class GenericViewHolder(view: View, private val viewType: Int) : BaseViewHolder<T>(view) {
        override fun bind(item: T) {
            (itemDeclarations[viewType] as? ItemDeclaration<T>)?.let {
                it.binder?.invoke(itemView, item)
            }
        }
    }

}

class Builder<T : Any> {

    val declarations = mutableListOf<ItemDeclaration<out T>>()

    var items: List<T>? = null

    inline fun <reified I : T> bindItem(layoutRes: Int, noinline binder: Binder<I>? = null) {
        declarations.add(ItemDeclaration(I::class, layoutRes, binder))
    }

    fun items(itemsProvider: () -> List<T>) {
        items = itemsProvider()
    }

}

fun <T : Any> createAdapter(context: Context, binders: Builder<T>.() -> Unit): GenericAdapter<T> {
    val builder = Builder<T>()
    builder.binders()
    val itemDeclarations = builder.declarations
            .distinctBy { (layoutRes, _) -> layoutRes }
            .mapIndexed { index, declaration ->
                index to declaration
            }
            .associate { (viewType, declaration) ->
                viewType to declaration
            }
    val adapter =  GenericAdapter(context, itemDeclarations)
    builder.items?.let { adapter.items = it }
    return adapter
}
