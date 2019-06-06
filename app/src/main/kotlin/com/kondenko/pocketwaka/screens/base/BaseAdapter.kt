package com.kondenko.pocketwaka.screens.base

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.CallSuper
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView

abstract class BaseAdapter<T, VH : BaseAdapter<T, VH>.BaseViewHolder>(
        protected val context: Context,
        items: List<T> = emptyList()
) : RecyclerView.Adapter<VH>() {

    open var items: List<T> = items
        set(value) {
            val result = DiffUtil.calculateDiff(getDiffCallback(field, value), false)
            field = value
            result.dispatchUpdatesTo(this)
        }

    @CallSuper
    override fun onBindViewHolder(holder: VH, position: Int) = holder.bind(items[position])

    override fun getItemCount(): Int = items.size

    protected open fun getDiffCallback(oldList: List<T>, newList: List<T>): BaseDiffCallback<T> =
            BaseDiffCallback(oldList, newList)

    protected fun inflate(layoutId: Int, parent: ViewGroup): View = LayoutInflater.from(context).inflate(layoutId, parent, false)

    abstract inner class BaseViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        abstract fun bind(item: T)
    }

}