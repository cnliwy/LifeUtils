package com.liwy.lifeutils

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import com.liwy.library.base.BaseActivity
import com.liwy.lifeutils.adapter.AppInfoAdapter
import com.liwy.lifeutils.entity.AppInfo
import com.liwy.lifeutils.utils.AppInfoUtils

class AppManageActivity : BaseActivity() {
    var listView:RecyclerView? = null
    var adapter:AppInfoAdapter? = null
    var datas:MutableList<AppInfo>? = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initView()
    }

    override fun getLayoutResId(): Int {
        return R.layout.activity_app_manage
    }

    fun initView(){
        initToolbarWithBack(TOOLBAR_MODE_CENTER,"应用管理",0,null)
        listView = findViewById(R.id.list_view)
        datas = AppInfoUtils.getAppInfos(this) as MutableList<AppInfo>
        adapter = AppInfoAdapter(R.layout.install_record_item,datas)
        listView?.adapter = adapter
        listView?.layoutManager = LinearLayoutManager(this)
    }
}
