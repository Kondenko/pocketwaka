package com.kondenko.pocketwaka.screens.base

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.CallSuper
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.kondenko.pocketwaka.utils.SimpleCallback
import com.kondenko.pocketwaka.utils.delegates.DiffUtilDelegate

abstract class BaseAdapter<T, VH : BaseAdapter<T, VH>.BaseViewHolder>(protected val context: Context)
    : RecyclerView.Adapter<VH>() {

    open var items: List<T> by DiffUtilDelegate<T, VH, BaseAdapter<T, VH>>(emptyList()) { old, new ->
        getDiffCallback(old, new)
    }

    @CallSuper
    override fun onBindViewHolder(holder: VH, position: Int) = holder.bind(items[position])

    override fun getItemCount(): Int = items.size

    protected open fun getDiffCallback(oldList: List<T>, newList: List<T>): DiffUtil.Callback = SimpleCallback(oldList, newList)

    protected fun inflate(layoutId: Int, parent: ViewGroup): View = LayoutInflater.from(context).inflate(layoutId, parent, false)

    abstract inner class BaseViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        abstract fun bind(item: T)
    }

}