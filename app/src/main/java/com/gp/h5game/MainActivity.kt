package com.gp.h5game

import android.annotation.SuppressLint
import android.annotation.TargetApi
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
import android.widget.Toast
import kotlinx.android.synthetic.main.layout_webview.*
import kotlinx.android.synthetic.main.layout_webview.view.*
import android.animation.ValueAnimator
import android.util.Log
import android.view.animation.LinearInterpolator

class MainActivity : AppCompatActivity() {

    lateinit var firstLoadUrl : String
    lateinit var url_load : String
    lateinit var webViewContainer : View
    lateinit var webview: WebView;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        webViewContainer = layoutInflater.inflate(R.layout.layout_webview, root, true)
        webview = webViewContainer.webview
        webview.setBackgroundColor(Color.parseColor("#00000000"));
//        webview.setBackgroundColor(0); // 设置背景色
//        webview.getBackground().setAlpha(0);
    }

    fun initWebView() {
        setWebViewConfig(webview, this)
        webview.webViewClient = object : WebViewClient() {

            override fun onPageStarted(view: WebView, url: String, favicon: Bitmap?) {
                super.onPageStarted(view, url, favicon)
            }

            @TargetApi(LOLLIPOP)
            override fun shouldOverrideUrlLoading(view: WebView, request: WebResourceRequest): Boolean {
                val url = request.url.toString()
                if (url.toLowerCase().startsWith("http") || url.toLowerCase().startsWith("https")) {
                    //兼容8.0以上 点击a标签两次跳转不一致
                    if (TextUtils.isEmpty(firstLoadUrl)) {
                        firstLoadUrl = url
                    } else {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && firstLoadUrl == url) {
                            Log.e("TAG", "  do not load again  ")
                            return false
                        }
                    }
                    url_load = url
                    //                    WebViewUtil.webviewSyncCookie(url_load, webview);
                    view.loadUrl(url_load)
                    return true
                }
                return false
            }


            override fun onPageFinished(view: WebView, url: String) {
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
                //监听url变化
            }

            override fun onReceivedHttpError(view: WebView, request: WebResourceRequest, errorResponse: WebResourceResponse) {
                super.onReceivedHttpError(view, request, errorResponse)
            }

        }

        webview.webChromeClient = object: WebChromeClient() {

            override fun onProgressChanged(view: WebView, newProgress: Int) {
                if (newProgress == 100) {
                    progress.setVisibility(View.GONE);
                } else {
                    progress.setVisibility(View.VISIBLE);
                    progress.setProgress(newProgress);
                }
                super.onProgressChanged(view, newProgress)
            }

            override fun onJsAlert(view: WebView, url: String, message: String, result: JsResult): Boolean {
                if (!isFinishing) {
                    Toast.makeText(this@MainActivity, message, Toast.LENGTH_LONG).show()
                    result.confirm()
                }
                return true
            }

            override fun onJsConfirm(arg0: WebView, arg1: String, arg2: String, arg3: JsResult): Boolean {
                if (!isFinishing) {
                }
                return true
            }
        }
        url_load = "http://fdrs.kele55.com"
        webview.loadUrl(url_load)


        val animator = ValueAnimator.ofFloat(webview.height.toFloat(), webview.height / 3 * 2.toFloat())
        animator.interpolator = LinearInterpolator()
        animator.setDuration(800).start()
        animator.addUpdateListener { animation -> webview.setTranslationY(animation.animatedValue as Float) }

//        val enterAnim = AnimationUtils.loadAnimation(this, R.anim.bottom_enter)
//        enterAnim.setAnimationListener(object : Animation.AnimationListener{
//            override fun onAnimationStart(animation: Animation?) {
//            }
//
//            override fun onAnimationEnd(animation: Animation?) {
//               webview.clearAnimation()
//            }
//
//            override fun onAnimationRepeat(animation: Animation?) {
//            }
//
//        })
//        webview.startAnimation(enterAnim)
    }

    fun enter(view: View){
        initWebView()
    }
    fun exit(view: View){
        exitWebview()
    }

    fun exitWebview(){
        val animator = ValueAnimator.ofFloat(webview.height / 3 * 2.toFloat(), webview.height.toFloat())
        animator.interpolator = LinearInterpolator()
        animator.setDuration(800).start()
        animator.addUpdateListener { animation -> webview.setTranslationY(animation.animatedValue as Float) }

//        val enterAnim = AnimationUtils.loadAnimation(this, R.anim.bottom_out)
//        enterAnim.setAnimationListener(object : Animation.AnimationListener{
//            override fun onAnimationStart(animation: Animation?) {
//
//            }
//
//            override fun onAnimationEnd(animation: Animation?) {
//                webview.clearAnimation()
//                root.removeView(webViewContainer)
//            }
//
//            override fun onAnimationRepeat(animation: Animation?) {
//
//            }
//        })
//        webview.startAnimation(enterAnim)
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
