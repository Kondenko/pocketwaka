package com.kondenko.pocketwaka.screens

import android.support.annotation.StringRes

interface BaseView {

    fun onError(throwable: Throwable?, @StringRes messageStringRes: Int? = null)

}