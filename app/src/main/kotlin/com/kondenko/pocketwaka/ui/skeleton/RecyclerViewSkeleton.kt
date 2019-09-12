package com.kondenko.pocketwaka.ui.skeleton

import com.kondenko.pocketwaka.screens.base.SkeletonAdapter

class RecyclerViewSkeleton<T, ADAPTER : SkeletonAdapter<T, *>>(adapterCreator: (Boolean) -> ADAPTER, private val skeletonItems: List<T>) {

    val adapter: ADAPTER = adapterCreator(true)

    fun show(show: Boolean) {
        adapter.showSkeleton = show
        if (show) adapter.items = skeletonItems
    }

}