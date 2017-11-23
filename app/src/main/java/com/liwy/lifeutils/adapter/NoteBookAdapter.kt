package com.liwy.lifeutils.adapter

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.liwy.lifeutils.R
import com.liwy.lifeutils.entity.Menu
import com.liwy.lifeutils.entity.NoteBook

/**
 * Created by liwy on 2017/11/20.
 */
class NoteBookAdapter : BaseQuickAdapter<NoteBook,BaseViewHolder> {
    constructor(layoutResId: Int, data: MutableList<NoteBook>?) : super(layoutResId, data)

    override fun convert(helper: BaseViewHolder?, item: NoteBook?) {
        helper?.setText(R.id.tv_content,item?.content)
    }
}
