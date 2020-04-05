package com.kondenko.pocketwaka.utils

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.ResolveInfo
import android.net.Uri
import android.widget.Toast
import androidx.browser.customtabs.CustomTabsClient
import androidx.browser.customtabs.CustomTabsIntent
import androidx.browser.customtabs.CustomTabsServiceConnection
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.OnLifecycleEvent
import com.kondenko.pocketwaka.R
import com.kondenko.pocketwaka.utils.extensions.isValidUrl


class BrowserWindow(private var context: Context? = null, lifecycleOwner: LifecycleOwner) : LifecycleObserver {

    companion object {

        private const val ACTION_CUSTOM_TABS_CONNECTION = "android.support.customtabs.action.CustomTabsService"

        private val BROWSER_BLACKLIST = arrayOf("com.UCMobile.intl")

    }

    private var connection: CustomTabsServiceConnection? = null

    private var isCustomTabsServiceBound = false

    private var onWebViewRequired: (() -> Unit)? = null

    init {
        lifecycleOwner.lifecycle.addObserver(this)
    }

    fun openUrl(url: String) {
        val url = url.trim()
        val context = context
        if (url.isValidUrl() && context != null) {
            connection = object : CustomTabsServiceConnection() {

                override fun onCustomTabsServiceConnected(componentName: ComponentName, client: CustomTabsClient) {
                    client.warmup(0L) // This prevents backgrounding after redirection
                    val customTabsIntent = with(CustomTabsIntent.Builder()) {
                        setToolbarColor(ContextCompat.getColor(context, android.R.color.white))
                        build()
                    }
                    customTabsIntent.intent.`package` = componentName.packageName
                    customTabsIntent.launchUrl(context, Uri.parse(url))
                }

                override fun onServiceDisconnected(name: ComponentName) {}

            }.also { connection ->
                getCustomTabsPackages(context, url)
                      .map { it.activityInfo.packageName }
                      .filterNot { it in BROWSER_BLACKLIST }
                      .firstOrNull()
                      ?.let { pkg ->
                          isCustomTabsServiceBound = CustomTabsClient.bindCustomTabsService(context, pkg, connection)
                      } ?: onWebViewRequired?.invoke()
            }
        } else {
            Toast.makeText(context, R.string.all_error_invalid_url, Toast.LENGTH_LONG).show()
        }
    }

    fun onWebViewRequired(action: () -> Unit) {
        onWebViewRequired = action
    }

    private fun getCustomTabsPackages(context: Context, url: String): List<ResolveInfo> {
        // Get default VIEW intent handler.
        val activityIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        // Get all apps that can handle VIEW intents.
        val resolvedActivityList = context.packageManager.queryIntentActivities(activityIntent, 0)
        val packagesSupportingCustomTabs = mutableListOf<ResolveInfo>()
        for (info in resolvedActivityList) {
            val serviceIntent = Intent()
            serviceIntent.action = ACTION_CUSTOM_TABS_CONNECTION
            serviceIntent.setPackage(info.activityInfo.packageName)
            if (context.packageManager.resolveService(serviceIntent, 0) != null) {
                packagesSupportingCustomTabs += info
            }
        }
        return packagesSupportingCustomTabs
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    private fun cleanup() {
        connection?.let {
            if (isCustomTabsServiceBound) context?.unbindService(it)
        }
        connection = null
        context = null
        onWebViewRequired = null
    }

}