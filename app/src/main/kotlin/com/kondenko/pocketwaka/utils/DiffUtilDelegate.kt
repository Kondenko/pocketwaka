package com.kondenko.pocketwaka.utils

import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import kotlin.reflect.KProperty

class DiffUtilDelegate<T, VH : RecyclerView.ViewHolder, A : RecyclerView.Adapter<VH>>(
        initialValue: List<T>,
        private val callbackFactory: (List<T>, List<T>) -> DiffUtil.Callback =
                { old, new -> SimpleCallback(old, new) }
) {

    private var currentValue: List<T> = initialValue

    operator fun setValue(thisRef: A?, property: KProperty<*>, value: List<T>) {
        val callback = callbackFactory(currentValue, value)
        val diff = DiffUtil.calculateDiff(callback)
        thisRef?.let { diff.dispatchUpdatesTo(it) }
        currentValue = value
    }

    operator fun getValue(thisRef: Any?, property: KProperty<*>): List<T> = currentValue

}