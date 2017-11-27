package com.liwy.lifeutils.mvp.webview

import android.os.Bundle
import android.view.View
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ProgressBar
import com.liwy.library.base.BaseMvpFragment

import com.liwy.lifeutils.R
import com.orhanobut.logger.Logger


class WebViewFragment : BaseMvpFragment<WebViewPresenter>(), WebViewView {
    var webView:WebView? = null
    var progressBar: ProgressBar? = null

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }

    override fun initView() {
        Logger.d("WebViewFragment-------------->")
        initWebView()
        loadUrl(arguments.getString("url"))
    }

    override fun initPresenter() {
        mPresenter = WebViewPresenter()
        mPresenter.init(this)
    }

    override fun getLayoutResId(): Int {
        return R.layout.fragment_web_view
    }

    fun initWebView() {
        webView = view?.findViewById(R.id.webview)
        progressBar = view?.findViewById(R.id.progressBar)
        //声明WebSettings子类
        val webSettings = webView?.getSettings()
        //如果访问的页面中要与Javascript交互，则webview必须设置支持Javascript
        webSettings?.setJavaScriptEnabled(true)
        //设置自适应屏幕，两者合用
        webSettings?.setUseWideViewPort(true) //将图片调整到适合webview的大小
        webSettings?.setLoadWithOverviewMode(true) // 缩放至屏幕的大小
        //        webSettings.setBlockNetworkImage(true);// 设置图片不加载为true,
        webSettings?.setCacheMode(WebSettings.LOAD_NO_CACHE)
//        webView.addJavascriptInterface(InJavaScriptLocalObj(), "appObj")// 注入js对象
        webView?.setWebChromeClient(object : WebChromeClient() {
            override fun onProgressChanged(view: WebView, newProgress: Int) {
                if (newProgress == 100) {
                    progressBar?.setVisibility(View.INVISIBLE)
                } else {
                    if (View.INVISIBLE == progressBar?.getVisibility()) {
                        progressBar?.setVisibility(View.VISIBLE)
                    }
                    progressBar?.setProgress(newProgress)
                }
                super.onProgressChanged(view, newProgress)
            }
        })
        webView?.setWebViewClient(WebViewClient())
    }

    override fun loadUrl(url: String) {
        webView?.loadUrl(url)
    }
}
