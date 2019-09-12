package com.kondenko.pocketwaka.utils.diffutil

import androidx.recyclerview.widget.DiffUtil

class SimpleCallback<T>(
        private val oldList: List<T>,
        private val newList: List<T>,
        private val areItemsTheSame: ((oldItem: T, newItem: T) -> Boolean) = { o, n ->
            o == n
        },
        private val areContentsTheSame: ((oldItem: T, newItem: T) -> Boolean) = { o, n ->
            o == n
        },
        private val getChangePayload: ((oldItem: T, newItem: T) -> Any?)? = null
) : DiffUtil.Callback() {

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
            areItemsTheSame(oldList[oldItemPosition], newList[newItemPosition])

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
            areContentsTheSame(oldList[oldItemPosition], newList[newItemPosition])

    override fun getChangePayload(oldItemPosition: Int, newItemPosition: Int): Any? =
            getChangePayload?.invoke(oldList[oldItemPosition], newList[newItemPosition])

    override fun getOldListSize(): Int = oldList.size

    override fun getNewListSize(): Int = newList.size

}