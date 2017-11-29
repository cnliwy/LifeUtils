package com.liwy.lifeutils.mvp.videoscreen

import android.app.WallpaperManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.hardware.Camera
import android.service.wallpaper.WallpaperService
import android.view.MotionEvent
import android.view.SurfaceHolder
import java.io.IOException


/**
 * Created by liwy on 2017/11/29.
 */
class TranspantWallpaper : WallpaperService {

    constructor() : super()


    companion object {
        fun setTranspantWallPaper(context: Context) {
            val intent = Intent(WallpaperManager.ACTION_CHANGE_LIVE_WALLPAPER)
            intent.putExtra(WallpaperManager.EXTRA_LIVE_WALLPAPER_COMPONENT,
                    ComponentName(context, TranspantWallpaper::class.java!!))
            context.startActivity(intent)
        }
    }

    // 实现WallpaperService必须实现的抽象方法
    override fun onCreateEngine(): WallpaperService.Engine {
        // 返回自定义的CameraEngine
        return CameraEngine()
    }


    internal inner class CameraEngine : WallpaperService.Engine(), Camera.PreviewCallback {
        private var camera: Camera? = null

        override fun onCreate(surfaceHolder: SurfaceHolder) {
            super.onCreate(surfaceHolder)

            startPreview()
            // 设置处理触摸事件
            setTouchEventsEnabled(true)

        }

        override fun onTouchEvent(event: MotionEvent) {
            super.onTouchEvent(event)
            // 时间处理:点击拍照,长按拍照
        }

        override fun onDestroy() {
            super.onDestroy()
            stopPreview()
        }

        override fun onVisibilityChanged(visible: Boolean) {
            if (visible) {
                startPreview()
            } else {
                stopPreview()
            }
        }

        /**
         * 开始预览
         */
        fun startPreview() {
            camera = Camera.open()
            camera!!.setDisplayOrientation(90)

            try {
                camera!!.setPreviewDisplay(surfaceHolder)
            } catch (e: IOException) {
                e.printStackTrace()
            }

            camera!!.startPreview()

        }

        /**
         * 停止预览
         */
        fun stopPreview() {
            if (camera != null) {
                try {
                    camera!!.stopPreview()
                    camera!!.setPreviewCallback(null)
                    // camera.lock();
                    camera!!.release()
                } catch (e: Exception) {
                    e.printStackTrace()
                }

                camera = null
            }
        }

        override fun onPreviewFrame(bytes: ByteArray, camera: Camera) {
            camera.addCallbackBuffer(bytes)
        }
    }
}