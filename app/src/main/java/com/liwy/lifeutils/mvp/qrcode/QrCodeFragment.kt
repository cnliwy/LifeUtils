package com.liwy.lifeutils.mvp.qrcode

import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import com.liwy.common.utils.ImageUtil
import com.liwy.common.utils.ToastUtils
import com.liwy.library.base.BaseMvpFragment

import com.liwy.lifeutils.R
import com.uuzuche.lib_zxing.activity.CodeUtils


class QrCodeFragment : BaseMvpFragment<QrCodePresenter>(), QrCodeView,View.OnClickListener{
    var contentEt:EditText? = null
    var scanBtn:Button? = null
    var photoAlbumBtn:Button? = null
    var createBtn:Button? = null
    var saveBtn:Button? = null
    var qrImageView:ImageView? = null
    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }

    override fun initView() {
        mPresenter.fragment = this
        contentEt = view?.findViewById(R.id.et_content)
        qrImageView = view?.findViewById(R.id.iv_qr)
        scanBtn = view?.findViewById(R.id.btn_scan)
        photoAlbumBtn = view?.findViewById(R.id.btn_photo_album)
        createBtn = view?.findViewById(R.id.btn_create)
        saveBtn = view?.findViewById(R.id.btn_save)
        scanBtn?.setOnClickListener(this)
        photoAlbumBtn?.setOnClickListener(this)
        createBtn?.setOnClickListener(this)
        saveBtn?.setOnClickListener(this)

    }


    override fun initPresenter() {
        mPresenter = QrCodePresenter()
        mPresenter.init(this)
    }

    override fun getLayoutResId(): Int {
        return R.layout.fragment_qr_code
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
                    ToastUtils.showShortToast( "解析结果:" + result!!)
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
                    CodeUtils.analyzeBitmap(ImageUtil.getImageAbsolutePath(context, uri), object : CodeUtils.AnalyzeCallback {
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
