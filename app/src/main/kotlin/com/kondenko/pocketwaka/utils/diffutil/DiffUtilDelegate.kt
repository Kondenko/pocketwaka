package com.kondenko.pocketwaka.utils.diffutil

import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import kotlin.reflect.KProperty

fun <T, A : RecyclerView.Adapter<*>> diffUtil(
        initialValue: List<T> = emptyList(),
        callbackFactory: DiffUtilCallback<T> = { old, new -> SimpleCallback(old, new) }
) = DiffUtilDelegate<T, A>(initialValue, callbackFactory)

class DiffUtilDelegate<T, A : RecyclerView.Adapter<*>>
internal constructor(
        initialValue: List<T>,
        private val callbackFactory: DiffUtilCallback<T>
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