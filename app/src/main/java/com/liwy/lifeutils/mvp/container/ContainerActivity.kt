package com.liwy.lifeutils.mvp.container

import android.os.Bundle
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentTransaction
import com.liwy.common.utils.ToastUtils
import com.liwy.library.base.BaseActivity
import com.liwy.library.base.BaseMvpActivity

import com.liwy.lifeutils.R
import com.liwy.lifeutils.mvp.qrcode.QrCodeFragment
import com.liwy.lifeutils.mvp.appmanage.AppManageFragment
import com.liwy.lifeutils.mvp.notebook.NoteBookFragment
import com.liwy.lifeutils.mvp.webview.WebViewFragment


class ContainerActivity : BaseMvpActivity<ContainerPresenter>(), ContainerView {
    var fragmentManager: FragmentManager? = null
    var transaction: FragmentTransaction? = null

    override fun initView() {
        fragmentManager = supportFragmentManager
        transaction = fragmentManager?.beginTransaction()
        initToolbarWithBack(BaseActivity.TOOLBAR_MODE_CENTER,"",0,null)
        showFragment(intent.getStringExtra("title"))
    }


    override fun initPresenter() {
        mPresenter = ContainerPresenter()
        mPresenter.init(this, this)
    }

    override fun getLayoutResId(): Int {
        return R.layout.activity_container
    }
    fun showFragment(title:String){
        transaction = fragmentManager?.beginTransaction()
        toolbarTitle?.setText(title)
        when(title) {
            "记事本" -> {
                    var noteBookFragment = NoteBookFragment()
                    transaction?.add(R.id.layout_container, noteBookFragment)
            }
            "搜索"->{
                var web = WebViewFragment()
                web?.arguments = Bundle()
                web?.arguments?.putString("url","https://www.baidu.com")
                transaction?.add(R.id.layout_container, web)
            }
            "二维码"->{
                var qrfrag = QrCodeFragment()
                transaction?.add(R.id.layout_container, qrfrag)
            }
            "奇趣百科"->{
                var web = WebViewFragment()
                web?.arguments = Bundle()
                web?.arguments?.putString("url","https://www.qiushibaike.com/")
                transaction?.add(R.id.layout_container, web)
            }
            "快递查询"->{
                var web = WebViewFragment()
                web?.arguments = Bundle()
                web?.arguments?.putString("url","http://www.kuaidi100.com/?from=openv")
                transaction?.add(R.id.layout_container, web)
            }
            "程序管理"->{
                var app = AppManageFragment()
                transaction?.add(R.id.layout_container, app)
            }
            else->ToastUtils.showShortToast("此功能尚未开开放")
        }
        transaction?.commit()
    }
}
