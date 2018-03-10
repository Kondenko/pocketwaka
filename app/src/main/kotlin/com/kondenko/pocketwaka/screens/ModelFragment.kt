package com.kondenko.pocketwaka.screens

import android.os.Parcelable
import android.support.v4.app.Fragment
import com.kondenko.pocketwaka.ui.LogFragment

abstract class ModelFragment<M : Parcelable> : Fragment() {
    protected lateinit var model: M
}
