package com.kondenko.pocketwaka.screens.base

import androidx.annotation.StringRes

interface BaseView {

    fun showError(throwable: Throwable?, @StringRes messageStringRes: Int? = null)

}