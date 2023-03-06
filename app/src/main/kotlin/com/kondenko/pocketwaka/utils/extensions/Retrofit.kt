package com.kondenko.pocketwaka.utils.extensions

import retrofit2.Retrofit

@Deprecated(message = "Use the official Retrofit extension")
inline fun <reified S> Retrofit.create(): S = create(S::class.java)