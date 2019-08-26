package com.gp.h5game

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.net.http.SslError
import android.os.Build
import android.os.Build.VERSION_CODES.LOLLIPOP
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.webkit.*
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*
import android.view.animation.AnimationUtils
import android.R.attr.gravity
import android.view.animation.Animation


class MainActivity : AppCompatActivity() {

    private var firstLoadUrl = ""
    private var url_load: String? = null
    var enterAnim: Animation? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setWebViewConfig(webview, this)

        webview.webViewClient = object : WebViewClient() {

            @SuppressLint("NewApi")
            override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
                val url = request?.url.toString()
                if (url.toLowerCase().startsWith("http") || url.toLowerCase().startsWith("https")) {
                    //兼容8.0以上 点击a标签两次跳转不一致
                    if (TextUtils.isEmpty(firstLoadUrl)) {
                        firstLoadUrl = url
                    } else {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && firstLoadUrl == url) {
                            return false
                        }
                    }
                    url_load = url
                    //                    WebViewUtil.webviewSyncCookie(url_load, webview);
                    view?.loadUrl(url_load)
                    return true
                }
                return false
            }


            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                synchronized(this) {
                    if (webview != null) {
                        webview.settings.blockNetworkImage = false
                        val title = view?.title
                        url_load = url
                    }
                }
                firstLoadUrl = ""
            }

            @SuppressLint("NewApi")
            override fun onReceivedError(view: WebView, request: WebResourceRequest, error: WebResourceError) {
                super.onReceivedError(view, request, error)
            }

            override fun onReceivedSslError(arg0: WebView, arg1: SslErrorHandler, arg2: SslError) {
                arg1.proceed()         //忽略证书
            }

            override fun onLoadResource(view: WebView, url: String) {
                super.onLoadResource(view, url)
            }

            override fun onReceivedHttpError(view: WebView, request: WebResourceRequest, errorResponse: WebResourceResponse) {
                super.onReceivedHttpError(view, request, errorResponse)
            }

        }

        webview.webChromeClient = object: WebChromeClient() {

            override fun onProgressChanged(view: WebView, newProgress: Int) {
                if (newProgress == 100) {
                    //                    progress_bar.setVisibility(View.GONE);
                } else {
                    //                    progress_bar.setVisibility(View.VISIBLE);
                    //                    progress_bar.setProgress(newProgress);
                }
                super.onProgressChanged(view, newProgress)
            }
        }

        url_load = "http://fdrs.kele55.com"

        webview.setBackgroundColor(Color.parseColor("#00000000"));//setBackgroundColor(0);
        webview.loadUrl(url_load)


        enterAnim = AnimationUtils.loadAnimation(this, R.anim.bottom_enter)
        webview.postDelayed(Runnable { webview.startAnimation(enterAnim) }, 5000)

    }

    fun onClick(view: View){
        webview.startAnimation(enterAnim)
    }

    fun setWebViewConfig(webview: WebView, mContext: Context): WebView {
        webview.settings.javaScriptEnabled = true
        webview.settings.builtInZoomControls = true
        if (Build.VERSION.SDK_INT >= LOLLIPOP) {
            webview.settings.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
        }
        if (Integer.parseInt(Build.VERSION.SDK) >= 14) {
            webview.settings.displayZoomControls = false
        }
//        webview.settings.userAgentString = DeviceUtil.getAppVersion() + "/" + webview.settings.userAgentString
        webview.settings.setSupportZoom(true)
        webview.settings.setRenderPriority(WebSettings.RenderPriority.HIGH)
        webview.settings.blockNetworkImage = true
        //开启存储
        webview.settings.domStorageEnabled = true
        webview.settings.setAppCacheMaxSize((1024 * 1024 * 8).toLong())
        //设置缓冲路径
        val appCachePath = mContext.cacheDir.absolutePath
        webview.settings.setAppCachePath(appCachePath)
        //开启文件数据缓存
        webview.settings.allowFileAccess = false
        //开启APP缓存
        webview.settings.setAppCacheEnabled(true)

        //根据网络状态加载缓冲，有网：走默认设置；无网络：走加载缓冲
//        if (MainActivity.isConnected(mContext)) {
//            webview.settings.cacheMode = WebSettings.LOAD_DEFAULT
//        } else {
//            webview.settings.cacheMode = WebSettings.LOAD_CACHE_ELSE_NETWORK
//        }
        //支持显示PC宽屏页面的全部内容
        webview.settings.useWideViewPort = true
        webview.settings.loadWithOverviewMode = true
        return webview
    }
}
