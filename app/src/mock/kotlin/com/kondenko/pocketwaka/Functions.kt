package com.kondenko.pocketwaka

import android.content.Context
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