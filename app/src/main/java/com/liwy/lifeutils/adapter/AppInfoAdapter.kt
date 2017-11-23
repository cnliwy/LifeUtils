package com.liwy.lifeutils.adapter

import android.view.View
import android.widget.LinearLayout
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.liwy.common.utils.BaseUtils
import com.liwy.lifeutils.R
import com.liwy.lifeutils.entity.AppInfo
import com.liwy.lifeutils.utils.AppInfoUtils
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by admin on 2017/11/21.
 */
class AppInfoAdapter : BaseQuickAdapter<AppInfo,BaseViewHolder> {
    constructor(layoutResId: Int, data: MutableList<AppInfo>?) : super(layoutResId, data)
    override fun convert(holder: BaseViewHolder?, appInfo: AppInfo?) {
        holder?.setImageDrawable(R.id.localpackage_item_icon_view, appInfo?.icon)
        holder?.setText(R.id.localpackage_item_name_view, appInfo?.name)
        holder?.setText(R.id.localpackage_item_version_view, appInfo?.versionName)
        holder?.setText(R.id.localpackage_item_date_view, getDate(appInfo!!.firstInstallTime))

        holder?.setOnClickListener(R.id.ll_info, {
            val ll = holder?.getView<LinearLayout>(R.id.uninstall_list_actions_layout)
            ll.visibility = if (ll.isShown) View.GONE else View.VISIBLE
        })
        holder?.setOnClickListener(R.id.localpackage_option_button, {
            //卸载应用
            AppInfoUtils.uninstallApplication(BaseUtils.getContext(), appInfo?.packageName!!)
        })
        holder?.setOnClickListener(R.id.app_management_button, {
            //应用管理
            AppInfoUtils.showInstalledAppDetails(BaseUtils.getContext(), appInfo?.packageName!!)
        })
    }

    fun getDate(): String {
        val dateFormater = SimpleDateFormat("yyyy/MM/dd")
        val date = Date()
        return dateFormater.format(date)
    }

    fun getDate(time: Long): String {
        val dateFormater = SimpleDateFormat("yyyy/MM/dd")
        val date = Date(time)
        return dateFormater.format(date)
    }
}