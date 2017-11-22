package com.liwy.lifeutils.mvp

import android.content.Intent
import android.graphics.Bitmap
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import com.liwy.common.utils.ImageUtil
import com.liwy.common.utils.ToastUtils
import com.liwy.library.base.BaseActivity
import com.liwy.library.base.BaseMvpActivity

import com.liwy.lifeutils.R
import com.uuzuche.lib_zxing.activity.CodeUtils


class QrCodeActivity : BaseMvpActivity<QrCodePresenter>(), QrCodeView ,View.OnClickListener{
    var contentEt:EditText? = null
    var scanBtn:Button? = null
    var photoAlbumBtn:Button? = null
    var createBtn:Button? = null
    var saveBtn:Button? = null
    var qrImageView:ImageView? = null

    override fun initView() {
        initToolbarWithBack(BaseActivity.TOOLBAR_MODE_CENTER,"二维码",0,null)
        contentEt = findViewById(R.id.et_content)
        qrImageView = findViewById(R.id.iv_qr)
        scanBtn = findViewById(R.id.btn_scan)
        photoAlbumBtn = findViewById(R.id.btn_photo_album)
        createBtn = findViewById(R.id.btn_create)
        saveBtn = findViewById(R.id.btn_save)
        scanBtn?.setOnClickListener(this)
        photoAlbumBtn?.setOnClickListener(this)
        createBtn?.setOnClickListener(this)
        saveBtn?.setOnClickListener(this)

    }


    override fun initPresenter() {
        mPresenter = QrCodePresenter()
        mPresenter.init(this, this,this)
    }

    override fun getLayoutResId(): Int {
        return R.layout.activity_qr_code
    }

    override fun updateImageView(bitmap: Bitmap?) {
        qrImageView?.setImageBitmap(bitmap)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        /**
         * 处理二维码扫描结果
         */
        if (requestCode == mPresenter.REQUEST_CODE){
            //处理扫描结果（在界面上显示）
            if (null != data) {
                val bundle = data!!.getExtras() ?: return
                if (bundle.getInt(CodeUtils.RESULT_TYPE) == CodeUtils.RESULT_SUCCESS) {
                    val result = bundle.getString(CodeUtils.RESULT_STRING)
                    Toast.makeText(this, "解析结果:" + result!!, Toast.LENGTH_LONG).show()
                    contentEt?.setText(result)
                } else if (bundle.getInt(CodeUtils.RESULT_TYPE) == CodeUtils.RESULT_FAILED) {
                    ToastUtils.showShortToast("解析二维码失败")
                }
            }
        }else if (requestCode == mPresenter.REQUEST_CODE_PERMISSION){
                // 这个120就是前往设置界面所传入的数字。
            if (mPresenter.hasPermission())mPresenter.startScan()

        }else if (requestCode == mPresenter.REQUEST_IMAGE){
            if (data != null) {
                val uri = data.data
                try {
                    CodeUtils.analyzeBitmap(ImageUtil.getImageAbsolutePath(this, uri), object : CodeUtils.AnalyzeCallback {
                        override fun onAnalyzeSuccess(mBitmap: Bitmap, result: String) {
                            ToastUtils.showShortToast("解析结果:" + result)
                            qrImageView?.setImageBitmap(mBitmap)
                            contentEt?.setText(result)
                        }
                        override fun onAnalyzeFailed() {
                            ToastUtils.showShortToast("解析二维码失败")
                        }
                    })
                } catch (e: Exception) {
                    e.printStackTrace()
                }

            }
        }
    }


    override fun onClick(v: View?) {
        when(v?.id){
            R.id.btn_scan ->  if (mPresenter.hasPermission())mPresenter.startScan()
            R.id.btn_save -> mPresenter.saveQrImage()
            R.id.btn_create -> {
                var content = contentEt?.text.toString()
                if(content==null || "".equals(content)){
                    ToastUtils.showShortToast("请先输入要生成二维码的内容")
                    return
                }
                mPresenter.createQrImage(content)
            }
            R.id.btn_photo_album -> mPresenter.openPhotoAlbum()
        }
    }
}
