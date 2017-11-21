package com.liwy.lifeutils

import android.content.Context
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.widget.Toast
import com.chad.library.adapter.base.BaseQuickAdapter
import com.liwy.library.base.BaseActivity
import com.liwy.lifeutils.adapter.MenuAdapter
import com.liwy.lifeutils.entity.Menu

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
            println(menu.name)
            Toast.makeText(context,menu.name,Toast.LENGTH_SHORT).show()
            if (menu.name.equals("程序管理"))turnToActivity(AppManageActivity::class.java)
        }
        listView?.adapter = adapter
    }

    fun  getMenus():MutableList<Menu>{
        var menus  = mutableListOf<Menu>()
        menus.add(Menu("二维码",R.mipmap.ic_launcher))
        menus.add(Menu("程序管理",R.mipmap.ic_launcher))
        menus.add(Menu("测试3",R.mipmap.ic_launcher))
        return menus
    }
}
