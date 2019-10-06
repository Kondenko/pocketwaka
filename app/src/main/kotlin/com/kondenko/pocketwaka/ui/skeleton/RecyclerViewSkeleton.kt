package com.kondenko.pocketwaka.ui.skeleton

import androidx.recyclerview.widget.RecyclerView

class RecyclerViewSkeleton<T, ADAPTER : SkeletonAdapter<T, *>>(
        private val recyclerView: RecyclerView,
        adapterCreator: (Boolean) -> ADAPTER,
        skeletonItems: List<T>
) {

    val actualAdapter: ADAPTER = adapterCreator(false)

    val skeletonAdapter: ADAPTER = adapterCreator(true)

    init {
        skeletonAdapter.items = skeletonItems
    }

    var isShown = false
        private set

    fun show(show: Boolean) = if (show) show() else hide()

    fun show() {
        if (!isShown) {
            recyclerView.adapter = skeletonAdapter
            isShown = true
        }
    }

    fun hide() {
        if (isShown) {
            recyclerView.adapter = actualAdapter
            isShown = false
        }
    }

}