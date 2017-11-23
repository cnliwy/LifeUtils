package com.liwy.lifeutils.utils

import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.support.v4.content.FileProvider

import com.liwy.lifeutils.entity.AppInfo
import com.liwy.common.utils.BaseUtils

import java.io.File
import java.util.ArrayList

/**
 * Created by admin on 2017/11/21.
 */

object AppInfoUtils {
    private val apkFile: File? = null

    private val SCHEME = "package"
    /**
     * 调用系统InstalledAppDetails界面所需的Extra名称(用于Android 2.1及之前版本)
     */
    private val APP_PKG_NAME_21 = "com.android.settings.ApplicationPkgName"
    /**
     * 调统InstalledAppDetails界面所需的Ext用系ra名称(用于Android 2.2)
     */
    private val APP_PKG_NAME_22 = "pkg"
    /**
     * InstalledAppDetails所在包名
     */
    private val APP_DETAILS_PACKAGE_NAME = "com.android.settings"
    /**
     * InstalledAppDetails类名
     */
    private val APP_DETAILS_CLASS_NAME = "com.android.settings.InstalledAppDetails"


    fun getAppInfos(context: Context): List<AppInfo> {
        val appInfoList = ArrayList<AppInfo>()

        //获取包管理器
        val pm = context.packageManager
        //获取已安装的包信息
        val packageInfos = pm.getInstalledPackages(0)

        for (packageInfo in packageInfos) {
            //获取包名
            val packageName = packageInfo.packageName
            //获取应用图标
            val icon = packageInfo.applicationInfo.loadIcon(pm)
            //获取应用的名称
            val name = packageInfo.applicationInfo.loadLabel(pm).toString()
            //获取第一次安装的时间
            val firstInstallTime = packageInfo.firstInstallTime
            //获取版本号
            val versionCode = packageInfo.versionCode
            //获取版本名称
            val versionName = packageInfo.versionName

            val appInfo = AppInfo(name, packageName, icon, firstInstallTime, versionName)
            appInfoList.add(appInfo)
        }
        return appInfoList
    }

    fun uninstallApplication(context: Context, packageName: String) {
        val intent = Intent()
        intent.action = "android.intent.action.DELETE"
        intent.addCategory("android.intent.category.DEFAULT")
        intent.data = Uri.parse("package:" + packageName)
        context.startActivity(intent)
    }

    fun openApplication(context: Context, packageName: String) {
        val intent = isexit(context, packageName)
        if (intent == null) {
            println("APP not found!....:" + packageName)
        }
        context.startActivity(intent)
    }

    /**
     * 通过packagename判断应用是否安装
     *
     * @param context
     * @return 跳转的应用主activity Intent
     */

    fun isexit(context: Context, pk_name: String): Intent? {
        //获取包管理器
        val packageManager = context.packageManager
        //通过包名获取Intent
        return packageManager.getLaunchIntentForPackage(pk_name)
    }

    fun showInstalledAppDetails(context: Context, packageName: String) {
        val intent = Intent()
        val apiLevel = Build.VERSION.SDK_INT
        if (apiLevel >= 9) { // 2.3（ApiLevel 9）以上，使用SDK提供的接口
            intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
            val uri = Uri.fromParts(SCHEME, packageName, null)
            intent.data = uri
        } else { // 2.3以下，使用非公开的接口（查看InstalledAppDetails源码）
            // 2.2和2.1中，InstalledAppDetails使用的APP_PKG_NAME不同。
            val appPkgName = if (apiLevel == 8)
                APP_PKG_NAME_22
            else
                APP_PKG_NAME_21
            intent.action = Intent.ACTION_VIEW
            intent.setClassName(APP_DETAILS_PACKAGE_NAME,
                    APP_DETAILS_CLASS_NAME)
            intent.putExtra(appPkgName, packageName)
        }
        context.startActivity(intent)
    }

    fun install(path: String, apkFile: File) {


        val installIntent = Intent(Intent.ACTION_VIEW)
        //判断是否是AndroidN以及更高的版本
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            installIntent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
            val contentUri = FileProvider.getUriForFile(BaseUtils.getContext(), "com.liwy.lifeutils.fileProvider", apkFile)
            installIntent.setDataAndType(contentUri, "application/vnd.android.package-archive")
        } else {
            installIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            installIntent.setDataAndType(Uri.parse("file://" + path),
                    "application/vnd.android.package-archive")
        }

        BaseUtils.getContext().startActivity(installIntent)
    }

    /*得到未安装apk的图标：*/
    fun getAppIcon(context: Context, apkFilepath: String): Drawable? {
        val pm = context.packageManager
        val pkgInfo = getPackageInfo(context, apkFilepath) ?: return null

        val appInfo = pkgInfo.applicationInfo
        if (Build.VERSION.SDK_INT >= 8) {
            appInfo.sourceDir = apkFilepath
            appInfo.publicSourceDir = apkFilepath
        }
        return pm.getApplicationIcon(appInfo)
    }

    //得到PackageInfo对象，其中包含了该apk包含的activity和service
    fun getPackageInfo(context: Context, apkFilepath: String): PackageInfo? {
        val pm = context.packageManager
        var pkgInfo: PackageInfo? = null
        try {
            pkgInfo = pm.getPackageArchiveInfo(apkFilepath, PackageManager.GET_ACTIVITIES or PackageManager.GET_SERVICES)
        } catch (e: Exception) {
            // should be something wrong with parse
            e.printStackTrace()
        }

        return pkgInfo
    }

    /*得到未安装apk的名称：*/
    fun getAppLabel(context: Context, apkFilepath: String): CharSequence? {
        val pm = context.packageManager
        val pkgInfo = getPackageInfo(context, apkFilepath) ?: return null
        val appInfo = pkgInfo.applicationInfo
        if (Build.VERSION.SDK_INT >= 8) {
            appInfo.sourceDir = apkFilepath
            appInfo.publicSourceDir = apkFilepath
        }

        return pm.getApplicationLabel(appInfo)
    }
}
