package com.kondenko.pocketwaka.ui.skeleton

import androidx.recyclerview.widget.RecyclerView
import com.kondenko.pocketwaka.screens.base.SkeletonAdapter

class RecyclerViewSkeleton<T, ADAPTER : SkeletonAdapter<T, *>>(
        private val recyclerView: RecyclerView,
        val actualAdapter: ADAPTER,
        val skeletonAdapter: ADAPTER,
        private val skeletonItems: List<T>
) {

    init {
        skeletonAdapter.apply {
            items = skeletonItems
        }
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