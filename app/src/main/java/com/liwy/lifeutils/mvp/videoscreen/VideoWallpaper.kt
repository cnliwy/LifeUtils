package com.liwy.lifeutils.mvp.videoscreen

import android.app.WallpaperManager
import android.content.*
import android.media.MediaPlayer
import android.service.wallpaper.WallpaperService
import android.view.SurfaceHolder
import com.liwy.common.utils.SPUtils
import java.io.IOException

/**
 * Created by liwy on 2017/11/29.
 */
class VideoWallpaper : WallpaperService {
    val VIDEO_PARAMS_CONTROL_ACTION = "com.liwy.lifeutils"
    val KEY_ACTION = "action"
    val ACTION_VOICE_SILENCE = 110
    val ACTION_VOICE_NORMAL = 111
    constructor() : super()

    override fun onCreateEngine(): Engine {
        return VideoEngine()
    }

    fun voiceSilence(context: Context) {
        val intent = Intent(VIDEO_PARAMS_CONTROL_ACTION)
        intent.putExtra(KEY_ACTION, ACTION_VOICE_SILENCE)
        context.sendBroadcast(intent)
    }

    fun voiceNormal(context: Context) {
        val intent = Intent(VIDEO_PARAMS_CONTROL_ACTION)
        intent.putExtra(KEY_ACTION, ACTION_VOICE_NORMAL)
        context.sendBroadcast(intent)
    }

    companion object {
        fun setToWallPaper(context: Context) {
            val intent = Intent(WallpaperManager.ACTION_CHANGE_LIVE_WALLPAPER)
            intent.putExtra(WallpaperManager.EXTRA_LIVE_WALLPAPER_COMPONENT,
                    ComponentName(context, VideoWallpaper::class.java!!))
            context.startActivity(intent)
        }
    }

    internal inner class VideoEngine : WallpaperService.Engine() {

        private var mMediaPlayer: MediaPlayer? = null

        private var mVideoParamsControlReceiver: BroadcastReceiver? = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                val action = intent?.getIntExtra(KEY_ACTION, -1)

                when (action) {
                    ACTION_VOICE_NORMAL -> mMediaPlayer!!.setVolume(1.0f, 1.0f)
                    ACTION_VOICE_SILENCE -> mMediaPlayer!!.setVolume(0f, 0f)
                }
            }
        }


        override fun onCreate(surfaceHolder: SurfaceHolder) {
            super.onCreate(surfaceHolder)

            val intentFilter = IntentFilter(VIDEO_PARAMS_CONTROL_ACTION)
            registerReceiver(mVideoParamsControlReceiver, intentFilter)


        }

        override fun onDestroy() {
            unregisterReceiver(mVideoParamsControlReceiver)
            super.onDestroy()

        }

        override fun onVisibilityChanged(visible: Boolean) {
            if (visible) {
                mMediaPlayer!!.start()
            } else {
                mMediaPlayer!!.pause()
            }
        }


        override fun onSurfaceCreated(holder: SurfaceHolder) {
            super.onSurfaceCreated(holder)
            mMediaPlayer = MediaPlayer()
            mMediaPlayer!!.setSurface(holder.surface)
            try {
                // 如果用户已自定义视频壁纸，则加载自定义的视频，否则加载默认的test1.mp4
                var customerFile = SPUtils.get("videoWallpaperFile","")
                if (!"".equals(customerFile)){
                    mMediaPlayer!!.setDataSource(customerFile as String)
                }else{
                    val assetMg = applicationContext.assets
                    val fileDescriptor = assetMg.openFd("test1.mp4")
                    mMediaPlayer!!.setDataSource(fileDescriptor.fileDescriptor,
                            fileDescriptor.startOffset, fileDescriptor.length)
                }
                mMediaPlayer!!.isLooping = true
                mMediaPlayer!!.setVolume(0f, 0f)
                mMediaPlayer!!.prepare()
                mMediaPlayer!!.start()

            } catch (e: IOException) {
                e.printStackTrace()
            }

        }

        override fun onSurfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
            super.onSurfaceChanged(holder, format, width, height)
        }

        override fun onSurfaceDestroyed(holder: SurfaceHolder) {
            super.onSurfaceDestroyed(holder)
            mMediaPlayer!!.release()
            mMediaPlayer = null

        }
    }
}