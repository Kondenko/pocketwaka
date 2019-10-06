package com.kondenko.pocketwaka

import android.content.Context
import com.google.gson.Gson
import com.kondenko.pocketwaka.utils.extensions.singleOrErrorIfNull
import io.reactivex.Single
import timber.log.Timber
import java.io.IOException
import java.nio.charset.Charset

fun getJsonAsset(context: Context, fileName: String): String? {
    return try {
        val inputStream = context.assets.open(fileName)
        val size = inputStream.available()
        val buffer = ByteArray(size)
        inputStream.read(buffer)
        inputStream.close()
        String(buffer, Charset.forName("UTF-8"))
    } catch (ex: IOException) {
        Timber.e(ex)
        null
    }
}

fun getMockJsonName(jsonName: String) = "mocks/$jsonName.json"

inline fun <reified T> Gson.jsonToServiceModel(context: Context, jsonName: String): Single<T> {
    return fromJson(getJsonAsset(context, getMockJsonName(jsonName)), T::class.java).singleOrErrorIfNull()
}