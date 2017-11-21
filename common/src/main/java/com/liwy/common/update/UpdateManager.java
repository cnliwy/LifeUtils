package com.liwy.common.update;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.TextUtils;
import android.util.Log;


/**
 * Created by Teprinciple on 2016/11/15.
 */
public class UpdateManager {

    private final String TAG = "UpdateManager";
    public static final int CHECK_BY_VERSION_NAME = 1001;
    public static final int CHECK_BY_VERSION_CODE = 1002;
    public static final int DOWNLOAD_BY_APP = 1003;
    public static final int DOWNLOAD_BY_SYSTEM = 1005;
    public static final int DOWNLOAD_BY_BROWSER = 1004;

    private Activity activity;
    private int checkBy = CHECK_BY_VERSION_CODE;
    private int downloadBy = DOWNLOAD_BY_SYSTEM;
    private int serverVersionCode = 0;
    private String downloadUrl="";//下载url
    private static String downloadPath = "";//下载路径
    private String fileName = "";//自定义文件名称，可选
    private String serverVersionName="";
    private boolean isForce = false; //是否强制更新
    private int localVersionCode = 0;
    private String localVersionName="";
    public static boolean showNotification = true;
    private String updateInfo = "";// 更新内容文本
    private static String authorities = "";//FileProvider authoritier


    private UpdateManager(Activity activity) {
        this.activity = activity;
        getAPPLocalVersion(activity);
    }

    public static UpdateManager from(Activity activity){
        return new UpdateManager(activity);
    }

    public UpdateManager checkBy(int checkBy){
        this.checkBy = checkBy;
        return this;
    }

    public UpdateManager downloadUrl(String downloadUrl){
        this.downloadUrl = downloadUrl;
        return this;
    }

    // 用于android 7.0 FileProvider适配
    public UpdateManager authorities(String authorities){
        this.authorities = authorities;
        return this;
    }

    public UpdateManager downloadBy(int downloadBy){
        this.downloadBy = downloadBy;
        return this;
    }
    public UpdateManager downloadPath(String savePath){
        this.downloadPath = savePath;
        return this;
    }

    public UpdateManager showNotification(boolean showNotification){
        this.showNotification = showNotification;
        return this;
    }

    public UpdateManager updateInfo(String updateInfo){
        this.updateInfo = updateInfo;
        return this;
    }



    public UpdateManager serverVersionCode(int serverVersionCode){
        this.serverVersionCode = serverVersionCode;
        return this;
    }

    public UpdateManager serverVersionName(String  serverVersionName){
        this.serverVersionName = serverVersionName;
        return this;
    }

    public UpdateManager isForce(boolean  isForce){
        this.isForce = isForce;
        return this;
    }

    public UpdateManager fileName(String fileName){
        this.fileName = fileName;
        return this;
    }

    public static String authorities(){
        return authorities;
    }

    public static String downloadPath(){
        return downloadPath;
    }

    //获取apk的版本号 currentVersionCode
    private  void getAPPLocalVersion(Context ctx) {
        PackageManager manager = ctx.getPackageManager();
        try {
            PackageInfo info = manager.getPackageInfo(ctx.getPackageName(), 0);
            localVersionName = info.versionName; // 版本名
            localVersionCode = info.versionCode; // 版本号
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void update(){

        switch (checkBy){
            case CHECK_BY_VERSION_CODE:
                if (serverVersionCode >localVersionCode){
                    toUpdate();
                }else {
                    Log.i(TAG,"当前版本是最新版本"+serverVersionCode+"/"+serverVersionName);
                }
                break;

            case CHECK_BY_VERSION_NAME:
                if (!serverVersionName.equals(localVersionName)){
                    toUpdate();
                }else {
                    Log.i(TAG,"当前版本是最新版本"+serverVersionCode+"/"+serverVersionName);
                }
                break;
        }

    }

    private void toUpdate() {
        realUpdate();
    }

    private void realUpdate() {
        if (fileName == null || "".equals(fileName))fileName = "cache.apk";
        UpdateConfirmDialog dialog = new UpdateConfirmDialog(activity, new Callback() {
            @Override
            public void callback(int position) {
                switch (position){
                    case 0:  //cancle
                        if (isForce)System.exit(0);
                        break;

                    case 1:  //sure
                        if (downloadBy == DOWNLOAD_BY_SYSTEM) {
                            // 系统自带的下载服务下载
                            if (isWifiConnected(activity)){
                                DownloadAppUtils.downloadForAutoInstall(activity, downloadUrl, fileName, serverVersionName);
                            }else {
                                new UpdateConfirmDialog(activity, new Callback() {
                                    @Override
                                    public void callback(int position) {
                                        if (position==1){
                                            DownloadAppUtils.downloadForAutoInstall(activity, downloadUrl, fileName, serverVersionName);
                                        }else {
                                            if (isForce)activity.finish();
                                        }
                                    }
                                }).setContent("目前手机不是WiFi状态\n确认是否继续下载更新？").show();
                            }

                        }else if (downloadBy == DOWNLOAD_BY_BROWSER){
                            // 浏览器下载
                            DownloadAppUtils.downloadForWebView(activity,downloadUrl);
                        }else if (downloadBy == DOWNLOAD_BY_APP){
                            // 应用内下载
                            if (isWifiConnected(activity)){
                                DownloadAppUtils.downLoadByApp(activity,downloadUrl,fileName);
                            }else {
                                new UpdateConfirmDialog(activity, new Callback() {
                                    @Override
                                    public void callback(int position) {
                                        if (position==1){
                                            DownloadAppUtils.downLoadByApp(activity,downloadUrl,fileName);
                                        }else {
                                            if (isForce)activity.finish();
                                        }
                                    }
                                }).setContent("目前手机不是WiFi状态\n确认是否继续下载更新？").show();
                            }
                        }
                        break;
                }
            }
        });

        String content = "发现新版本:"+serverVersionName+"\n是否下载更新?";
        if (!TextUtils.isEmpty(updateInfo)){
            content = "发现新版本:"+serverVersionName+"是否下载更新?\n\n"+updateInfo;
        }
        if (isForce){
            dialog.setButtonTitle("退出应用","立即更新");
        }else{
            dialog.setButtonTitle("稍后更新","立即更新");
        }
        dialog .setContent(content);
        dialog.setCancelable(false);
        dialog.show();
    }


    /**
     * 检测wifi是否连接
     */
    public static boolean isWifiConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm != null) {
            NetworkInfo networkInfo = cm.getActiveNetworkInfo();
            if (networkInfo != null && networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                return true;
            }
        }
        return false;
    }

}
