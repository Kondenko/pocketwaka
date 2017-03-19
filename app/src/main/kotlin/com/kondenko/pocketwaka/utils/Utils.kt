package com.kondenko.pocketwaka.utils

import android.content.Context
import android.content.res.Resources
import android.net.ConnectivityManager
import android.os.Build
import android.support.annotation.ColorRes
import com.kondenko.pocketwaka.R
import rx.Subscription


object Utils {

    fun getColor(context: Context, @ColorRes id: Int): Int {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return context.resources.getColor(id, null)
        } else {
            return context.resources.getColor(R.color.color_text_black_pale)
        }
    }

    fun isConnectionAvailable(context: Context): Boolean {
        val service = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        return service.activeNetworkInfo != null && service.activeNetworkInfo.isConnectedOrConnecting
    }

    fun currentTimeSec(): Float = System.currentTimeMillis() / 1000f

    fun unsubscribe(subscription: Subscription?) {
        if (subscription != null && !subscription.isUnsubscribed) {
            subscription.unsubscribe()
        }
    }

}