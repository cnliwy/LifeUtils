package com.liwy.lifeutils

import android.app.ActivityManager
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.liwy.common.utils.BaseUtils
import com.liwy.common.utils.ToastUtils
import com.liwy.library.base.BaseActivity
import com.liwy.lifeutils.adapter.MenuAdapter
import com.liwy.lifeutils.entity.Menu
import com.liwy.lifeutils.mvp.QrCodeActivity
import com.liwy.lifeutils.mvp.notebook.NoteBookActivity
import com.liwy.lifeutils.utils.SystemInfoUtils


class MainActivity : BaseActivity() {
    var datas = mutableListOf<Menu>()
    var listView:RecyclerView? = null
    var adapter:MenuAdapter? = null
    var context:Context? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        context = this
        initView()
    }

    override fun getLayoutResId(): Int {
        return R.layout.activity_main
    }

    fun initView(){
        initToolbarTitle(TOOLBAR_MODE_CENTER,"百宝箱")
        listView = findViewById(R.id.list_menu)
        datas = getMenus()
        adapter = MenuAdapter(R.layout.item_menu,datas)
        listView?.layoutManager = LinearLayoutManager(this)
        adapter?.onItemClickListener = BaseQuickAdapter.OnItemClickListener{
            adapter, view, position ->
            var menu = datas.get(position)
            when(menu.name){
                "清理内存" -> clearMemory()
                else -> turnToActivity(menu.desActivity)
            }
        }
        listView?.adapter = adapter
    }

    fun  getMenus():MutableList<Menu>{
        var menus  = mutableListOf<Menu>()
        menus.add(Menu("二维码",R.mipmap.ic_launcher,QrCodeActivity::class.java as Class<Any>))
        menus.add(Menu("程序管理",R.mipmap.ic_launcher,AppManageActivity::class.java as Class<Any>))
        menus.add(Menu("记事本",R.mipmap.ic_launcher, NoteBookActivity::class.java as Class<Any>))
        menus.add(Menu("清理内存",R.mipmap.ic_launcher, NoteBookActivity::class.java as Class<Any>))
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
