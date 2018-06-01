package com.kondenko.pocketwaka.screens.base.stateful

import android.os.Parcelable
import android.support.v4.app.Fragment

const val ARG_MODEL = "model"

abstract class ModelFragment<M : Parcelable> : Fragment() {

    companion object {
        val TAG = "fragment_model"
    }

    lateinit var model: M

    protected abstract fun displayModel(model: M)

}
