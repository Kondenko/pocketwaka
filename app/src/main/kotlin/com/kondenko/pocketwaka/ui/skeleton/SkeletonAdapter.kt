package com.kondenko.pocketwaka.ui.skeleton

import android.content.Context
import android.view.View
import androidx.core.view.doOnLayout
import com.kondenko.pocketwaka.screens.base.BaseAdapter
import com.kondenko.pocketwaka.utils.extensions.dp
import com.kondenko.pocketwaka.utils.extensions.negateIfTrue

abstract class SkeletonAdapter<T, VH : SkeletonAdapter<T, VH>.SkeletonViewHolder<T>>(context: Context, val showSkeleton: Boolean = false)
    : BaseAdapter<T, VH>(context) {

    protected abstract fun createSkeleton(view: View): Skeleton?

    protected fun Number.adjustValue(isSkeleton: Boolean) = (context.dp(this)).negateIfTrue(!isSkeleton)

    abstract inner class SkeletonViewHolder<in Item : T>(view: View, private val skeleton: Skeleton?) : BaseViewHolder<Item>(view) {

        override fun bind(item: Item) {
            itemView.doOnLayout {
                if (showSkeleton) skeleton?.show()
            }
        }

    }

}