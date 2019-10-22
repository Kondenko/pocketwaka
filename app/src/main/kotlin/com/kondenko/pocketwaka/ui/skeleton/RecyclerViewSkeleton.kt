package com.kondenko.pocketwaka.ui.skeleton

import androidx.recyclerview.widget.RecyclerView

class RecyclerViewSkeleton<T, ADAPTER : SkeletonAdapter<T, *>>(adapterCreator: (Boolean) -> ADAPTER, skeletonItems: List<T>) {

    val actualAdapter: ADAPTER = adapterCreator(false)

    val skeletonAdapter: ADAPTER = adapterCreator(true)

    init {
        skeletonAdapter.items = skeletonItems
    }

    fun show(recyclerView: RecyclerView, show: Boolean) =
          if (show) {
              show(recyclerView)
          } else {
              hide(recyclerView)
          }

    fun show(recyclerView: RecyclerView) {
        recyclerView.adapter.let {
            if (it == null || (it as? SkeletonAdapter<*, *>)?.showSkeleton == false) {
                recyclerView.adapter = skeletonAdapter
            }
        }
    }

    fun hide(recyclerView: RecyclerView) {
        recyclerView.adapter.let {
            if (it == null || (it as? SkeletonAdapter<*, *>)?.showSkeleton == true) {
                recyclerView.adapter = actualAdapter
            }
        }
    }

}