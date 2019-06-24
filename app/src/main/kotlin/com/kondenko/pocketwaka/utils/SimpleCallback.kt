package com.kondenko.pocketwaka.utils

import androidx.recyclerview.widget.DiffUtil

class SimpleCallback<T>(
    private val oldList: List<T>,
    private val newList: List<T>,
    private val areItemsTheSame: ((oldItem: T, newItem: T) -> Boolean)? = null,
    private val areContentsTheSame: ((oldItem: T, newItem: T) -> Boolean)? = null,
    private val getChangePayload: ((oldItem: T, newItem: T) -> Any?)? = null
) : DiffUtil.Callback() {

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return areItemsTheSame?.invoke(
            oldList[oldItemPosition],
            newList[newItemPosition]
        ) ?: oldList[oldItemPosition] == newList[newItemPosition]
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return areContentsTheSame?.invoke(
            oldList[oldItemPosition],
            newList[newItemPosition]
        ) ?: oldList[oldItemPosition] == newList[newItemPosition]
    }

    override fun getOldListSize(): Int = oldList.size

    override fun getNewListSize(): Int = newList.size

    override fun getChangePayload(oldItemPosition: Int, newItemPosition: Int): Any? {
        return getChangePayload?.invoke(oldList[oldItemPosition], newList[newItemPosition])
    }

}