package com.liwy.lifeutils

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import com.liwy.library.base.BaseActivity
import com.liwy.lifeutils.adapter.AppInfoAdapter
import com.liwy.lifeutils.entity.AppInfo
import com.liwy.lifeutils.utils.AppInfoUtils
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

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
        listView?.layoutManager = LinearLayoutManager(this)
    }

    override fun onResume() {
        super.onResume()
        initData()
    }

    var observable:Observable<MutableList<AppInfo>>? = null
    /*
     * 初始化数据
     */
    fun initData(){
        observable = Observable.create<MutableList<AppInfo>> {
            datas = AppInfoUtils.getAppInfos(this) as MutableList<AppInfo>
            it.onNext(datas!!)
            it.onComplete()
        }
        observable?.subscribeOn(Schedulers.io())!!.observeOn(AndroidSchedulers.mainThread()).subscribe({
            adapter = AppInfoAdapter(R.layout.item_install_record,datas)
            listView?.adapter = adapter
        })
    }
}
