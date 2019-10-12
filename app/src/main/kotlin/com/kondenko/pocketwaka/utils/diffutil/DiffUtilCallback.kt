package com.kondenko.pocketwaka.utils.diffutil

import androidx.recyclerview.widget.DiffUtil

typealias DiffUtilCallback<T> = (List<T>, List<T>) -> DiffUtil.Callback