package com.kondenko.pocketwaka.utils

import android.content.Context
import android.net.ConnectivityManager


object Utils {

    fun isConnectionAvailable(context: Context): Boolean {
        val service = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        return service.activeNetworkInfo != null && service.activeNetworkInfo.isConnectedOrConnecting
    }

}