package com.kondenko.pocketwaka.screens.base

import androidx.recyclerview.widget.DiffUtil

open class BaseDiffCallback<T>(
        private val oldList: List<T>,
        private val newList: List<T>,
        private val areItemsTheSame: (T, T) -> Boolean = { a, b -> a === b },
        private val areContentsTheSame: (T, T) -> Boolean = { a, b -> a === b }
) : DiffUtil.Callback() {

    override fun getOldListSize(): Int = oldList.size

    override fun getNewListSize(): Int = newList.size

    override fun areItemsTheSame(oldItemIndex: Int, newItemIndex: Int): Boolean =
            areItemsTheSame.invoke(oldList[oldItemIndex], newList[newItemIndex])

    override fun areContentsTheSame(oldItemIndex: Int, newItemIndex: Int): Boolean =
            areContentsTheSame.invoke(oldList[oldItemIndex], newList[newItemIndex])

}