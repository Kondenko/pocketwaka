package com.kondenko.pocketwaka.ui.skeleton

import android.view.View
import android.view.ViewPropertyAnimator
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import androidx.recyclerview.widget.RecyclerView

class RecyclerViewSkeleton<T, ADAPTER : SkeletonAdapter<T, *>>(
      private val recyclerView: RecyclerView,
      adapterCreator: (Boolean) -> ADAPTER,
      skeletonItems: List<T>,
      private val crossfadeAnimDuration: Int = 100
) {

    val actualAdapter: ADAPTER = adapterCreator(false)

    val skeletonAdapter: ADAPTER = adapterCreator(true)

    private var animator: ViewPropertyAnimator? = null

    init {
        skeletonAdapter.items = skeletonItems
    }

    fun show(show: Boolean) = if (show) show() else hide()

    fun show() {
        recyclerView.adapter.let {
            if (it == null || (it as? SkeletonAdapter<*, *>)?.showSkeleton == false) {
                setAdapter(skeletonAdapter, it != null)
            }
        }
    }

    fun hide() {
        recyclerView.adapter.let {
            if (it == null || (it as? SkeletonAdapter<*, *>)?.showSkeleton == true) {
                setAdapter(actualAdapter, it != null)
            }
        }
    }

    private fun setAdapter(adapter: ADAPTER, animate: Boolean) {
        val setAdapter = { recyclerView.adapter = adapter }
        if (animate) recyclerView.crossfade(setAdapter) else setAdapter()
    }

    private inline fun View.crossfade(crossinline doWhenInvisible: () -> Unit) {
        animator?.cancel()
        val duration = crossfadeAnimDuration / 2L
        animator = animate()
              .alpha(0f)
              .withLayer()
              .setDuration(duration)
              .setInterpolator(DecelerateInterpolator())
              .withEndAction {
                  doWhenInvisible()
                  animate()
                        .alpha(1f)
                        .withLayer()
                        .setDuration(duration)
                        .setInterpolator(AccelerateInterpolator())
                        .start()
              }
              .apply(ViewPropertyAnimator::start)

    }

}