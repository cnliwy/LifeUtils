package com.liwy.lifeutils.mvp.main

import android.app.ActivityManager
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import com.chad.library.adapter.base.BaseQuickAdapter
import com.liwy.common.utils.ToastUtils
import com.liwy.library.base.BaseMvpFragment
import com.liwy.lifeutils.R
import com.liwy.lifeutils.adapter.MenuAdapter
import com.liwy.lifeutils.entity.Menu
import com.liwy.lifeutils.mvp.appmanage.AppManageFragment
import com.liwy.lifeutils.mvp.qrcode.QrCodeFragment
import com.liwy.lifeutils.mvp.notebook.NoteBookFragment
import com.orhanobut.logger.Logger
import org.greenrobot.eventbus.EventBus


class MainFragment : BaseMvpFragment<MainPresenter>(), MainView {
    var datas = mutableListOf<Menu>()
    var listView: RecyclerView? = null
    var adapter: MenuAdapter? = null

    override fun initPresenter() {
        println("加载MainFragment的presenter")
        mPresenter = MainPresenter()
        mPresenter.init(this)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }

    override fun getLayoutResId(): Int {
        return R.layout.fragment_main
    }

    override fun initView(){
        println("初始化MainFragment-------------->")
        Logger.d("初始化MainFragment-------------->")
        listView = view?.findViewById(R.id.list_menu)
        datas = getMenus()
        adapter = MenuAdapter(R.layout.item_menu,datas)
        listView?.layoutManager = LinearLayoutManager(context)
        adapter?.onItemClickListener = BaseQuickAdapter.OnItemClickListener{
            adapter, view, position ->
            var menu = datas.get(position)
            when(menu.name){
                "清理内存" -> clearMemory()
                else -> EventBus.getDefault().post(menu.name)
            }
        }
        listView?.adapter = adapter
    }

    fun  getMenus():MutableList<Menu>{
        var menus  = mutableListOf<Menu>()
        menus.add(Menu("二维码",R.mipmap.ic_launcher, QrCodeFragment::class.java as Class<Any>))
        menus.add(Menu("程序管理",R.mipmap.ic_launcher, AppManageFragment::class.java as Class<Any>))
        menus.add(Menu("记事本",R.mipmap.ic_launcher, NoteBookFragment::class.java as Class<Any>))
        menus.add(Menu("清理内存",R.mipmap.ic_launcher, NoteBookFragment::class.java as Class<Any>))
        menus.add(Menu("奇趣百科",R.mipmap.ic_launcher, NoteBookFragment::class.java as Class<Any>))
        menus.add(Menu("搜索",R.mipmap.ic_launcher, NoteBookFragment::class.java as Class<Any>))
        return menus
    }


    fun clearMemory(){
        val activityManger = context?.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val list = activityManger
                .runningAppProcesses
        if (list != null)
            for (i in list.indices) {
                val apinfo = list[i]
                val pkgList = apinfo.pkgList
                if (apinfo.importance >= ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                    for (j in pkgList.indices) {
                        if (pkgList[j] == context?.getPackageName()) {
                            continue
                        }
                        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.FROYO) {
                            activityManger.restartPackage(pkgList[j])
                        } else {
                            activityManger.killBackgroundProcesses(pkgList[j])
                        }
                    }
                }
            }
        ToastUtils.showShortToast("清理完成")
    }
}
