package com.kondenko.pocketwaka.utils.diffutil

import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import kotlin.reflect.KProperty

fun <T, VH : RecyclerView.ViewHolder, A : RecyclerView.Adapter<VH>> diffUtil(
        initialValue: List<T>,
        callbackFactory: (List<T>, List<T>) -> DiffUtil.Callback = { old, new -> SimpleCallback(old, new) }
) = DiffUtilDelegate<T, VH, A>(initialValue, callbackFactory)

class DiffUtilDelegate<T, VH : RecyclerView.ViewHolder, A : RecyclerView.Adapter<VH>>
internal constructor(
        initialValue: List<T>,
        private val callbackFactory: (List<T>, List<T>) -> DiffUtil.Callback
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