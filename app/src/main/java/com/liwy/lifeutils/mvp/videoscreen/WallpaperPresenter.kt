package com.liwy.lifeutils.mvp.videoscreen

import android.Manifest
import com.liwy.common.utils.ActivityUtils
import com.liwy.common.utils.PermissionUtils
import com.liwy.library.base.presenter.BaseFragmentPresenter


class WallpaperPresenter : BaseFragmentPresenter<WallpaperView>(){
    var camera = Manifest.permission.CAMERA
    var permisions = mutableListOf(camera,Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE)
    var builder = PermissionUtils.getBuilder()

    /**
     * 申请摄像头权限
     * 先检测是否具有摄像头权限
     * 已有权限返回true
     * 不具备权限返回false
     */
    fun applyPermission(): Boolean{
        if (builder == null)
            builder.setContext(mContext).setRequestCode(120).setPermissionList(permisions).setActivity(mActivity)
                .setRefusedMsg("没有摄像头/文件存储权限将无法使用动态壁纸")
                .setSettingMsg("请您前往设置页面开启摄像头/文件存储权限").setOnRefusedListener { ActivityUtils.exitApp() }
        if (!PermissionUtils.ifHasPermission(mContext, permisions)) {
            PermissionUtils.applyForPermission(builder)
        } else {
            return true
        }
        return false
    }


}
