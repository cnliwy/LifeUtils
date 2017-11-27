package com.liwy.lifeutils.mvp.qrcode

import android.Manifest
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Environment
import com.liwy.common.utils.*
import com.liwy.library.base.presenter.BaseFragmentPresenter
import com.liwy.lifeutils.R
import com.uuzuche.lib_zxing.activity.CaptureActivity
import com.uuzuche.lib_zxing.activity.CodeUtils
import java.io.File

class QrCodePresenter : BaseFragmentPresenter<QrCodeView>(){
    //扫描跳转Activity RequestCode
    val REQUEST_CODE = 111
    //选择系统图片RequestCode
    val REQUEST_IMAGE = 112
    // 申请权限的RequestCode
    val REQUEST_CODE_PERMISSION = 120
    // 当前生成的bitmap
    var mBitmap:Bitmap? = null

    var permissions:MutableList<String>? = null
    var builder = PermissionUtils.getBuilder()

    fun startScan(){
        var intent = Intent(BaseUtils.getContext(), CaptureActivity::class.java)
        mActivity.startActivityForResult(intent, REQUEST_CODE)
    }

    fun hasPermission():Boolean{
        permissions = mutableListOf(Manifest.permission.CAMERA,Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE)

        builder.setContext(mContext).setRequestCode(REQUEST_CODE_PERMISSION).setPermissionList(permissions).setActivity(mActivity)
                .setRefusedMsg("没有摄像头/文件读写权限将导致功能无法使用")
                .setSettingMsg("请您前往设置页面开启摄像头/文件读写权限").setOnRefusedListener { ActivityUtils.exitApp() }
        if (!PermissionUtils.ifHasPermission(mContext, permissions)) run { PermissionUtils.applyForPermission(builder) }else return true
        return false
    }


    /**
     * 打开相册选择二维码
     */
    fun openPhotoAlbum(){
        val intent = Intent()
        intent.action = Intent.ACTION_PICK
        intent.type = "image/*"
        mActivity.startActivityForResult(intent, REQUEST_IMAGE)
    }

    /**
     * 创建二维码
     */
    fun createQrImage(content:String){
        mBitmap = CodeUtils.createImage(content, 400, 400, BitmapFactory.decodeResource(mContext.getResources(), R.mipmap.ic_launcher))
        mView.updateImageView(mBitmap)
    }

    /**
     * 保存二维码图片至SD卡
     */
    fun saveQrImage(){
        if (mBitmap==null){
            ToastUtils.showShortToast("请先扫码或者生成二维码")
            return
        }
        var name = DateUtils.getCurrDate()
        var filePath = Environment.getExternalStorageDirectory().absolutePath + "/" + name +".jpg"
        var file = File(filePath)
        if (!file.exists())file.createNewFile()
        FileUtils.compressBmpToFile(mBitmap,file)
        mBitmap = null
    }

}
