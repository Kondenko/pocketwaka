package com.kondenko.pocketwaka.screens

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.core.net.toUri
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import com.kondenko.pocketwaka.R
import com.kondenko.pocketwaka.screens.main.MainViewModel
import com.kondenko.pocketwaka.screens.main.OnLogIn
import kotlinx.android.synthetic.main.fragment_web_view.*
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class FragmentOauthWebView : Fragment() {

    companion object {

        private const val ARG_URL = "url"

        private const val ARG_REDIRECT_URL = "redirect_url"

        fun openUrl(url: String, redirectUrl: String) = FragmentOauthWebView().apply {
            arguments = bundleOf(ARG_URL to url, ARG_REDIRECT_URL to redirectUrl)
        }

    }

    private val onLogIn: OnLogIn by sharedViewModel<MainViewModel>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
          inflater.inflate(R.layout.fragment_web_view, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val url = arguments?.getString(ARG_URL, null)
        val redirectUrl = arguments?.getString(ARG_REDIRECT_URL, null)

        webview_fragmentwebview.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                if (redirectUrl != null && url != null && url.contains(redirectUrl)) {
                    activity?.intent = Intent(activity?.intent).apply {
                        data = url.toUri()
                    }
                    onLogIn.closeWebView()
                    return true
                }
                return false
            }
        }
        webview_fragmentwebview.loadUrl(url)

        with(toolbar_fragmentwebview) {
            setTitle(R.string.loginactivity_title_webview)
            setNavigationIcon(R.drawable.ic_close)
            setNavigationOnClickListener {
                onLogIn.closeWebView()
            }
        }
    }

}
