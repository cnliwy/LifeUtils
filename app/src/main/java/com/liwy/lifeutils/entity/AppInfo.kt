package com.liwy.lifeutils.entity

import android.graphics.drawable.Drawable

/**
 * 跳转安装应用 用到的实体类
 *
 * @author xzhang
 */

class AppInfo(val name: String, val packageName: String, val icon: Drawable, val firstInstallTime: Long, val versionName: String)
