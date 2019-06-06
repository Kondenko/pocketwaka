package com.kondenko.pocketwaka.utils.extensions

import retrofit2.Retrofit

inline fun <reified S> Retrofit.create(): S = create(S::class.java)