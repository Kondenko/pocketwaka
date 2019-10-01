package com.kondenko.pocketwaka.screens

import android.view.View
import androidx.core.view.isInvisible
import com.kondenko.pocketwaka.R
import kotlinx.android.synthetic.main.item_status.view.*

interface StatusMarker {
    val status: ScreenStatus
}

sealed class ScreenStatus(val lastUpdated: Long? = null) {
    class Loading(lastUpdated: Long? = null) : ScreenStatus(lastUpdated)
    class Offline(lastUpdated: Long? = null) : ScreenStatus(lastUpdated)
}

fun View.renderStatus(status: ScreenStatus) = with(this)  {
    val isOffline = status is ScreenStatus.Offline
    textview_status_description.setText(if (isOffline) R.string.status_offline else R.string.status_updating)
    imageView_status_offline.isInvisible = !isOffline
    progressbar_status_loading.isInvisible = isOffline
}