package com.kondenko.pocketwaka.screens.base

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.CallSuper
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.kondenko.pocketwaka.utils.diffutil.SimpleCallback
import com.kondenko.pocketwaka.utils.diffutil.diffUtil

abstract class BaseAdapter<T, VH : BaseAdapter<T, VH>.BaseViewHolder<T>>(protected val context: Context)
    : RecyclerView.Adapter<VH>() {

    open var items: List<T> by diffUtil(emptyList(), ::getDiffCallback)

    @CallSuper
    override fun onBindViewHolder(holder: VH, position: Int) = holder.bind(items[position])

    override fun getItemCount(): Int = items.size

    protected open fun getDiffCallback(oldList: List<T>, newList: List<T>): DiffUtil.Callback = SimpleCallback(oldList, newList)

    protected fun inflate(layoutId: Int, parent: ViewGroup): View = LayoutInflater.from(context).inflate(layoutId, parent, false)

    abstract inner class BaseViewHolder<in Item : T>(view: View) : RecyclerView.ViewHolder(view) {
        abstract fun bind(item: Item)
    }

}