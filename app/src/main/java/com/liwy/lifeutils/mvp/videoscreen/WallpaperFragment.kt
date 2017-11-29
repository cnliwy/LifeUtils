package com.liwy.lifeutils.mvp.videoscreen

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import com.leon.lfilepickerlibrary.LFilePicker
import com.leon.lfilepickerlibrary.utils.Constant
import com.liwy.common.utils.SPUtils
import com.liwy.common.utils.ToastUtils
import com.liwy.library.base.BaseMvpFragment
import com.liwy.lifeutils.R


class WallpaperFragment : BaseMvpFragment<WallpaperPresenter>(), WallpaperView {
    var videoBtn:Button? = null
    var videoBtn2:Button? = null
    var dynamicBtn:Button? = null
    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mPresenter.applyPermission()
        initView()
    }

    override fun initView() {
        videoBtn = view?.findViewById(R.id.btn_video)
        videoBtn2 = view?.findViewById(R.id.btn_video2)
        dynamicBtn = view?.findViewById(R.id.btn_dynamic)
        videoBtn?.setOnClickListener { setVideoToWallPaper() }
        videoBtn2?.setOnClickListener { customVideoWallpaper() }
        dynamicBtn?.setOnClickListener {setTranspantWallpaper()}

    }

    override fun initPresenter() {
        mPresenter = WallpaperPresenter()
        mPresenter.init(this)
    }

    override fun getLayoutResId(): Int {
        return R.layout.fragment_wallpaper
    }

    // 设置视频为壁纸
    fun setVideoToWallPaper() {
        SPUtils.remove("videoWallpaperFile")
        VideoWallpaper.setToWallPaper(context)
    }
    var wallpaperCode = 233
    //
    fun customVideoWallpaper(){
        var wallpaperFragment = this
        LFilePicker().withSupportFragment(wallpaperFragment).withRequestCode(wallpaperCode).withTitle("选择视频").start()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK){
            if (requestCode == wallpaperCode){
                val list = data?.getStringArrayListExtra(Constant.RESULT_INFO)
                if (list != null && list.size > 0){
                    println("------------》选择的视频路径==" + list)
                    SPUtils.put("videoWallpaperFile",list[0])
                    VideoWallpaper.setToWallPaper(context)
                }else{
                    ToastUtils.showShortToast("请选择要作为壁纸的视频")
                }
            }
        }
    }

    fun setTranspantWallpaper(){
        SPUtils.remove("videoWallpaperFile")
        TranspantWallpaper.setTranspantWallPaper(context)
    }

    // 选择
    fun selectDynimicWallpaper(){
        if (mPresenter.applyPermission()){
            val pickWallpaper = Intent(Intent.ACTION_SET_WALLPAPER)
            val chooser = Intent.createChooser(pickWallpaper, "选择壁纸")
            startActivity(chooser)
        }
    }



}
