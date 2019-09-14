package com.kondenko.pocketwaka.utils

import android.content.Context
import android.view.View
import android.view.ViewGroup
import com.kondenko.pocketwaka.screens.base.BaseAdapter
import com.kondenko.pocketwaka.utils.exceptions.IllegalViewTypeException
import com.kondenko.pocketwaka.utils.extensions.findKey
import kotlin.reflect.KClass

private typealias Binder<T> = View.(T) -> Unit

private typealias ItemDeclarations<T> = Map<Int, ItemDeclaration<out T>>

data class ItemDeclaration<T : Any>(val itemClass: KClass<T>, val itemLayoutRes: Int, val binder: View.(T) -> Unit)

class GenericAdapter<T : Any>(context: Context, private val itemDeclarations: ItemDeclarations<T>)
    : BaseAdapter<T, GenericAdapter<T>.GenericViewHolder>(context) {

    override fun getItemViewType(position: Int): Int =
            itemDeclarations.findKey { items[position]::class == it.itemClass }
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
                it.binder(itemView, item)
            }
        }
    }

}

class Builder<T : Any> {

    val declarations = mutableListOf<ItemDeclaration<out T>>()

    inline fun <reified I : T> bindItem(layoutRes: Int, noinline binder: Binder<I>) {
        declarations.add(ItemDeclaration(I::class, layoutRes, binder))
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
    return GenericAdapter(context, itemDeclarations)
}
