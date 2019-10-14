package com.kondenko.pocketwaka.utils

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageInfo
import android.net.Uri
import androidx.browser.customtabs.CustomTabsClient
import androidx.browser.customtabs.CustomTabsIntent
import androidx.browser.customtabs.CustomTabsServiceConnection
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.OnLifecycleEvent

class BrowserWindow(private var context: Context? = null, lifecycleOwner: LifecycleOwner) : LifecycleObserver {

    private var connection: CustomTabsServiceConnection? = null

    private var isCustomTabsServiceBound = false

    init {
        lifecycleOwner.lifecycle.addObserver(this)
    }

    fun openUrl(url: String) {
        connection = object : CustomTabsServiceConnection() {
            override fun onCustomTabsServiceConnected(componentName: ComponentName, client: CustomTabsClient) {
                client.warmup(0L) // This prevents backgrounding after redirection
                val customTabsIntent = with(CustomTabsIntent.Builder()) {
                    context?.let { setToolbarColor(ContextCompat.getColor(it, android.R.color.white)) }
                    build()
                }
                customTabsIntent.launchUrl(context, Uri.parse(url))
            }

            override fun onServiceDisconnected(name: ComponentName) {
            }
        }
        getChromePackage()?.let {
            isCustomTabsServiceBound = CustomTabsClient.bindCustomTabsService(context, it, connection)
        } ?: openBrowserActivity(url)
    }

    private fun openBrowserActivity(url: String) {
        context?.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
    }

    private fun getChromePackage(): String? {
        fun Iterable<PackageInfo>.find(packageName: String): String? {
            return find { it.packageName == packageName }?.packageName
        }

        val chrome = "com.chrome"
        val stable = "com.android.chrome"
        val beta = "$chrome.beta"
        val dev = "$chrome.dev"
        val canary = "$chrome.canary"
        val apps = context?.packageManager?.getInstalledPackages(0) ?: emptyList()
        return apps.run {
            find(stable) ?: find(beta) ?: find(dev) ?: find(canary)
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    private fun cleanup() {
        connection?.let {
            if (isCustomTabsServiceBound) context?.unbindService(it)
        }
        connection = null
        context = null
    }

}