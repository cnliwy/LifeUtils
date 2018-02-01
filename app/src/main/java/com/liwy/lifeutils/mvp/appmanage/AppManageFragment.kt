package com.liwy.lifeutils.mvp.appmanage

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import com.liwy.common.utils.BaseUtils
import com.liwy.common.utils.ToastUtils
import com.liwy.library.base.BaseMvpFragment

import com.liwy.lifeutils.R
import com.liwy.lifeutils.adapter.AppInfoAdapter
import com.liwy.lifeutils.entity.AppInfo
import com.liwy.lifeutils.utils.AppInfoUtils
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers


class AppManageFragment : BaseMvpFragment<AppManagePresenter>(), AppManageView {
    var listView: RecyclerView? = null
    var adapter: AppInfoAdapter? = null
    var datas:MutableList<AppInfo>? = mutableListOf()

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }

    override fun initView() {
        listView = view?.findViewById(R.id.list_view)
        listView?.layoutManager = LinearLayoutManager(context)
        initData()
    }

    override fun initPresenter() {
        mPresenter = AppManagePresenter()
        mPresenter.init(this)
    }

    override fun getLayoutResId(): Int {
        return R.layout.fragment_app_manage
    }

    var observable: Observable<MutableList<AppInfo>>? = null
    /*
     * 初始化数据
     */
    fun initData(){
        observable = Observable.create<MutableList<AppInfo>> {
            datas = AppInfoUtils.getAppInfos(context) as MutableList<AppInfo>
            it.onNext(datas!!)
            it.onComplete()
        }
        observable?.subscribeOn(Schedulers.io())!!.observeOn(AndroidSchedulers.mainThread()).subscribe({
            adapter = AppInfoAdapter(R.layout.item_install_record,datas)
            listView?.adapter = adapter
        })
    }

    /**
     * 卸载全部app（没用）
     */
    fun uninstallAll(){
        ToastUtils.showShortToast("开始卸载应用")
        datas?.forEach {
            value->
            run {
                try {
                    print("正在卸载" + value?.packageName!!)
                    AppInfoUtils.uninstallApplication(BaseUtils.getContext(), value?.packageName!!)
                } catch (e: Exception) {
                }
            }

        }

    }
}
