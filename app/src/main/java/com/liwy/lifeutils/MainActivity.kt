package com.liwy.lifeutils

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentTransaction
import android.widget.LinearLayout
import com.liwy.common.utils.ToastUtils
import com.liwy.library.base.BaseActivity
import com.liwy.lifeutils.mvp.qrcode.QrCodeFragment
import com.liwy.lifeutils.mvp.appmanage.AppManageFragment
import com.liwy.lifeutils.mvp.container.ContainerActivity
import com.liwy.lifeutils.mvp.main.MainFragment
import com.liwy.lifeutils.mvp.notebook.NoteBookFragment
import com.liwy.lifeutils.mvp.webview.WebViewFragment
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe


class MainActivity : BaseActivity() {
    var context:Context? = null
    var rightLayout:LinearLayout? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        context = this
        EventBus.getDefault().register(this)
        initToolbarTitle(TOOLBAR_MODE_CENTER,"百宝箱")
        initFragment()
    }

    override fun getLayoutResId(): Int {
        return R.layout.activity_main
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }

    @Subscribe
    fun receiveMessage(title:String){
        // 多屏模式
        if (isMutilScreen){
            toolbarTitle?.setText(title)
            when(title){
                "搜索"-> showWebView("https://www.baidu.com/")
                "奇趣百科"->showWebView("https://www.qiushibaike.com/")
                "快递查询"->showWebView("http://www.kuaidi100.com/?from=openv")
                "技术学习"->showWebView("http://www.runoob.com/")
                else->showFragment(title)
            }
        }else{
            // 单屏模式
            var intent = Intent(this,ContainerActivity::class.java)
            intent.putExtra("title",title)
            startActivity(intent)
        }

    }

    var isMutilScreen = false;
    var fragmentManager:FragmentManager? = null
    var transaction:FragmentTransaction? = null

    var mainFragment:MainFragment? = null
    var webViewFragment:WebViewFragment? = null
    var noteBookFragment:NoteBookFragment? = null
    var qrCodeFragment: QrCodeFragment? = null
    var appManageFragment:AppManageFragment? = null

    fun showFragment(title: String){
        transaction = fragmentManager?.beginTransaction()
        when(title){
            "记事本"->{
                hideFragment(transaction!!)
                if (noteBookFragment == null){
                    noteBookFragment = NoteBookFragment()
                    transaction?.add(R.id.fragment_right,noteBookFragment)
                }else transaction?.show(noteBookFragment)
            }
            "二维码"->{
                hideFragment(transaction!!)
                if (qrCodeFragment == null){
                    qrCodeFragment = QrCodeFragment()
                    transaction?.add(R.id.fragment_right,qrCodeFragment)
                }else transaction?.show(qrCodeFragment)
            }
            "程序管理"->{
                hideFragment(transaction!!)
                if (appManageFragment == null){
                    appManageFragment = AppManageFragment()
                    transaction?.add(R.id.fragment_right,appManageFragment)
                }else transaction?.show(appManageFragment)
            }
            else->ToastUtils.showShortToast("暂未开放")
        }
        transaction?.commit()
    }
    fun  showWebView(url: String){
        transaction = fragmentManager?.beginTransaction()
        hideFragment(transaction!!)
        if (webViewFragment!=null){
            webViewFragment?.loadUrl(url)
            transaction?.show(webViewFragment)
        }else{
            webViewFragment = WebViewFragment()
            webViewFragment?.arguments = Bundle()
            webViewFragment?.arguments?.putString("url",url)
            transaction?.add(R.id.fragment_right,webViewFragment)
        }
        transaction?.commit()
    }

    //隐藏所有的fragment
    fun hideFragment(transaction: FragmentTransaction) {
        if (appManageFragment != null) {
            transaction.hide(appManageFragment)
        }
        if (qrCodeFragment != null) {
            transaction.hide(qrCodeFragment)
        }
        if (webViewFragment != null) {
            transaction.hide(webViewFragment)
        }

        if (noteBookFragment != null) {
            transaction.hide(noteBookFragment)
        }
    }

    fun initFragment(){
        println("加载fragment------------------>")
        //步骤一：添加一个FragmentTransaction的实例
        fragmentManager = supportFragmentManager
        transaction = fragmentManager?.beginTransaction()
        //步骤二：用add(方法加上Fragment的对象rightFragment
        var mainFragment = MainFragment()
        transaction?.add(R.id.fragment_left, mainFragment)
        //步骤三：调用commit()方法使得FragmentTransaction实例的改变生效

        //判断右侧布局是否存在
        rightLayout = findViewById(R.id.fragment_right)
        if (rightLayout != null){
            isMutilScreen = true
            webViewFragment = WebViewFragment()
            webViewFragment?.arguments = Bundle()
            webViewFragment?.arguments?.putString("url","https://www.qiushibaike.com/")
            transaction?.add(R.id.fragment_right,webViewFragment)
        }else{
            println("-------------->right为空")
        }
        transaction?.commit()
    }
}
