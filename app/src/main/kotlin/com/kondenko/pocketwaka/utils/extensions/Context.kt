package com.kondenko.pocketwaka.utils.extensions

import android.app.Activity
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.res.TypedArray
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.TypedValue
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import com.kondenko.pocketwaka.R
import java.util.*


inline fun <reified T : Activity> Context.startActivity(extras: Bundle? = null) {
    startActivity(Intent(this, T::class.java).apply {
        extras?.let(::putExtras)
    })
}

fun Context.getCurrentLocale(): Locale = resources.configuration.run {
    @Suppress("DEPRECATION")
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) locales[0]
    else locale
}

fun Context.dp(number: Number): Float
    = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, number.toFloat(), resources.displayMetrics)

fun Context.getColorCompat(@ColorRes colorRes: Int) = ContextCompat.getColor(this, colorRes)

fun <T> Context.getTypedValue(resId: Int, getFromArray: TypedArray.() -> T): T {
    val array = obtainStyledAttributes(TypedValue().data, intArrayOf(resId))
    val value = array.getFromArray()
    array.recycle()
    return value
}

fun Context.getAccentColor() = getTypedValue(R.attr.colorAccent) { getColor(0, 0) }

/**
 * Source: https://stackoverflow.com/a/28090925/3410790
 */
fun Context.openPlayStore(ifNotFound: (String) -> Unit) {
    // you can also use BuildConfig.APPLICATION_ID
    val appId = packageName
    val marketIntent = Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$appId"))
    var marketFound = false

    // find all applications able to handle our rateIntent
    val marketApps = packageManager.queryIntentActivities(marketIntent, 0)

    for (app in marketApps) {
        // look for Google Play application
        if (app.activityInfo.applicationInfo.packageName == "com.android.vending") {
            val otherAppActivity = app.activityInfo
            val componentName = ComponentName(
                otherAppActivity.applicationInfo.packageName,
                otherAppActivity.name
            )
            // make sure it does NOT open in the stack of your activity
            marketIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            // task reparenting if needed
            marketIntent.addFlags(Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED)
            // if the Google Play was already open in a search result
            //  this make sure it still go to the app page you requested
            marketIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            // this make sure only the Google Play app is allowed to
            // intercept the intent
            marketIntent.component = componentName
            startActivity(marketIntent)
            marketFound = true
            break
        }
    }

    // if GP not present on device, open web browser
    if (!marketFound) ifNotFound("https://play.google.com/store/apps/details?id=$appId")
}