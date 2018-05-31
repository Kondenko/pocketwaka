package com.kondenko.pocketwaka.screens.base.stateful

import android.os.Parcelable
import android.support.v4.app.Fragment

abstract class ModelFragment<M : Parcelable> : Fragment() {
    protected lateinit var model: M
}
