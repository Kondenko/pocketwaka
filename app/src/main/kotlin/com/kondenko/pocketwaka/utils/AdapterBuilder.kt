package com.kondenko.pocketwaka.utils

import android.content.Context
import android.view.View
import android.view.ViewGroup
import com.kondenko.pocketwaka.ui.skeleton.Skeleton
import com.kondenko.pocketwaka.ui.skeleton.SkeletonAdapter
import com.kondenko.pocketwaka.utils.exceptions.IllegalViewTypeException
import kotlin.reflect.KClass

private typealias Binder<T> = View.(T) -> Unit

private typealias ItemDeclarations<T> = Map<Int, ItemDeclaration<out T>>

private typealias SkeletonCreator = (View) -> Skeleton

data class ItemDeclaration<T : Any>(val itemClass: KClass<T>, val itemLayoutRes: Int, val binder: (View.(T) -> Unit)?)

class GenericAdapter<T : Any>(context: Context, private val skeletonCreator: SkeletonCreator? = null, private val itemDeclarations: ItemDeclarations<T>)
    : SkeletonAdapter<T, GenericAdapter<T>.GenericViewHolder>(context, skeletonCreator != null) {

    private val viewTypes = itemDeclarations.entries.associate { (k, v) -> v.itemClass to k }

    override fun getItemViewType(position: Int): Int = viewTypes[items[position]::class]
            ?: throw IllegalViewTypeException("Couldn't find the type of ${items[position]}")

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GenericViewHolder {
        val declaration = itemDeclarations[viewType]
                ?: throw IllegalViewTypeException("View type $viewType wasn't found in $itemDeclarations")
        val view = inflate(declaration.itemLayoutRes, parent)
        val skeleton = createSkeleton(view)
        return GenericViewHolder(view, viewType, skeleton)
    }

    override fun createSkeleton(view: View): Skeleton? = skeletonCreator?.invoke(view)

    inner class GenericViewHolder(view: View, private val viewType: Int, val skeleton: Skeleton?) : SkeletonViewHolder<T>(view, skeleton) {
        override fun bind(item: T) {
            (itemDeclarations[viewType] as? ItemDeclaration<T>)?.let {
                it.binder?.invoke(itemView, item)
            }
            super.bind(item)
        }
    }

}

class Builder<T : Any> {

    val declarations = mutableListOf<ItemDeclaration<out T>>()

    var items: List<T> = emptyList()

    var skeletonCreator: SkeletonCreator? = null

    inline fun <reified I : T> viewHolder(layoutRes: Int, noinline binder: Binder<I>? = null) {
        declarations.add(ItemDeclaration(I::class, layoutRes, binder))
    }

    fun items(itemsProvider: () -> List<T>) {
        items = itemsProvider()
    }

    fun skeleton(skeletonCreator: SkeletonCreator) {
        this.skeletonCreator = skeletonCreator
    }

}

fun <T : Any> createAdapter(context: Context, binders: Builder<T>.() -> Unit): GenericAdapter<T> {
    val builder = Builder<T>().apply(binders)
    val itemDeclarations = builder.declarations
            .distinctBy { (layoutRes, _) -> layoutRes }
            .mapIndexed { index, declaration ->
                index to declaration
            }
            .associate { (viewType, declaration) ->
                viewType to declaration
            }
    return GenericAdapter(context, builder.skeletonCreator, itemDeclarations).apply {
        items = builder.items
    }
}
