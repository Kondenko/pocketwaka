package com.kondenko.pocketwaka.ui

import android.content.Context
import android.support.v7.widget.LinearLayoutManager

/**
 * Disables scrolling for nested RecyclerView
 */
class NonScrollableLinearLayoutManager(context: Context) : LinearLayoutManager(context) {

    override fun canScrollVertically(): Boolean {
        return false
    }

}