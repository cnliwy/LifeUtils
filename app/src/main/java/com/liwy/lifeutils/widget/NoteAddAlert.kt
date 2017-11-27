package com.liwy.lifeutils.widget

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import butterknife.BindView
import butterknife.ButterKnife
import com.liwy.common.utils.ToastUtils
import com.liwy.lifeutils.R
import com.liwy.lifeutils.entity.NoteBook
import kotlinx.android.synthetic.main.fragment_qr_code.*
import org.greenrobot.eventbus.EventBus
import android.view.WindowManager



/**
 * Created by liwy on 2017/11/23.
 */
class NoteAddAlert:AlertDialog {
    var noteBook:NoteBook? = null
    var contentEt:EditText? = null
    var cancelBtn:Button? = null
    var addBtn:Button? = null


    constructor(context: Context?) : super(context)
    constructor(context: Context?, noteBook: NoteBook?) : super(context) {
        this.noteBook = noteBook
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ButterKnife.bind(this@NoteAddAlert)
        setContentView(R.layout.alert_note_add)
        initView()
    }

    fun initView(){
        contentEt = findViewById(R.id.et_content)
        cancelBtn = findViewById(R.id.btn_cancel)
        addBtn = findViewById(R.id.btn_add)
        cancelBtn?.setOnClickListener { dismiss() }
        addBtn?.setOnClickListener{
            var content = contentEt?.text.toString()
            if (content != null && !"".equals(content)){
                EventBus.getDefault().post(NoteBook(content))
                dismiss()
            }else{
                ToastUtils.showShortToast("内容不能为空!")
            }
        }
        if (noteBook!=null)contentEt?.setText(noteBook?.content)
    }

    override fun show() {
        super.show()
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM)
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE)
    }
}