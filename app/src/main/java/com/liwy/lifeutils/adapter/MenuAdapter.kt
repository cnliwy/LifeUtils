package com.liwy.lifeutils.adapter

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.liwy.lifeutils.R
import com.liwy.lifeutils.entity.Menu

/**
 * Created by liwy on 2017/11/20.
 */
class MenuAdapter : BaseQuickAdapter<Menu,BaseViewHolder> {
    constructor(layoutResId: Int, data: MutableList<Menu>?) : super(layoutResId, data)

    override fun convert(helper: BaseViewHolder?, item: Menu?) {
        helper?.setText(R.id.textView,item?.name)
    }
}
