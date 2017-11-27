package com.liwy.lifeutils.mvp.webview

import com.liwy.library.base.view.IView

interface WebViewView : IView{
    fun loadUrl(url:String)
}
