package com.liwy.lifeutils

import android.app.Application
import com.liwy.common.utils.BaseUtils
import com.uuzuche.lib_zxing.activity.ZXingLibrary

/**
 * Created by admin on 2017/11/21.
 */
class LifeApp: Application() {
    override fun onCreate() {
        super.onCreate()
        BaseUtils.init(applicationContext)
        ZXingLibrary.initDisplayOpinion(applicationContext)
    }
}