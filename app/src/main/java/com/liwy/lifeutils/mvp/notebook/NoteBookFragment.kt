package com.liwy.lifeutils.mvp.notebook

import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import com.liwy.common.utils.ToastUtils
import com.liwy.library.base.BaseMvpFragment
import com.liwy.lifeutils.R
import com.liwy.lifeutils.adapter.NoteBookAdapter
import com.liwy.lifeutils.entity.NoteBook
import com.liwy.lifeutils.widget.NoteAddAlert
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe


class NoteBookFragment : BaseMvpFragment<NoteBookPresenter>(), NoteBookView, View.OnClickListener {
    var floatBtn:FloatingActionButton? = null
    var adapter:NoteBookAdapter? = null
    var listView:RecyclerView? = null
    var data:MutableList<NoteBook>?= mutableListOf()

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }

    override fun initView() {
        EventBus.getDefault().register(this)
        floatBtn = view?.findViewById(R.id.btn_float)
        floatBtn?.setOnClickListener(this)
        listView = view?.findViewById(R.id.list_view)
        initData()
    }

    @Subscribe
    fun addNoteBook(noteBook:NoteBook){
        data?.add(noteBook)
        adapter?.notifyDataSetChanged()
    }

    override fun initPresenter() {
        mPresenter = NoteBookPresenter()
        mPresenter.init(this)
    }

    override fun getLayoutResId(): Int {
        return R.layout.fragment_note_book
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.btn_float -> {
                showAddView()
            }
            else -> ToastUtils.showShortToast("瞎点啥呢？")
        }
    }
    fun initData(){
        adapter = NoteBookAdapter(R.layout.item_notebook,data)
        listView?.adapter = adapter
        listView?.layoutManager = LinearLayoutManager(context)
    }

    fun showAddView(){
        var alert = NoteAddAlert(context)
        alert.show()
    }
}
