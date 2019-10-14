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

    fun show(show: Boolean) = if (show) show() else hide()

    fun show() {
        recyclerView.adapter.let {
            if (it == null || (it as? SkeletonAdapter<*, *>)?.showSkeleton == false) {
                recyclerView.adapter = skeletonAdapter
            }
        }
    }

    fun hide() {
        recyclerView.adapter.let {
            if (it == null || (it as? SkeletonAdapter<*, *>)?.showSkeleton == true) {
                recyclerView.adapter = actualAdapter
            }
        }
    }

}