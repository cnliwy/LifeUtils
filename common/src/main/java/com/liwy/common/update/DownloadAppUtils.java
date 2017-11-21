package com.liwy.common.update;

import android.app.AlertDialog;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;


/**
 *Created by Teprinciple on 2016/12/13.
 */
public class DownloadAppUtils {
    private static final String TAG = DownloadAppUtils.class.getSimpleName();
    public static long downloadUpdateApkId = -1;//下载更新Apk 下载任务对应的Id
    public static String downloadUpdateApkFilePath;//下载更新Apk 文件路径

    /**
     * 通过浏览器下载APK包
     * @param context
     * @param url
     */
    public static void downloadForWebView(Context context, String url) {
        Uri uri = Uri.parse(url);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }


    /**
     * 下载更新apk包
     * 权限:1,<uses-permission android:name="android.permission.DOWNLOAD_WITHOUT_NOTIFICATION" />
     * @param context
     * @param url
     */
    public static void downloadForAutoInstall(final Context context, String url, String fileName, String title) {
        if (TextUtils.isEmpty(url)) {
            return;
        }

        final String packageName = "com.android.providers.downloads";
        int state = context.getPackageManager().getApplicationEnabledSetting(packageName);
        //检测下载管理器是否被禁用
        if (state == PackageManager.COMPONENT_ENABLED_STATE_DISABLED
                || state == PackageManager.COMPONENT_ENABLED_STATE_DISABLED_USER
                || state == PackageManager.COMPONENT_ENABLED_STATE_DISABLED_UNTIL_USED) {
            AlertDialog.Builder builder = new AlertDialog.Builder(context).setTitle("温馨提示").setMessage
                    ("系统下载管理器被禁止，需手动打开").setPositiveButton("确定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    try {
                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        intent.setData(Uri.parse("package:" + packageName));
                        context.startActivity(intent);
                    } catch (ActivityNotFoundException e) {
                        Intent intent = new Intent(Settings.ACTION_MANAGE_APPLICATIONS_SETTINGS);
                        context.startActivity(intent);
                    }
                }
            }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            builder.create().show();
        }else {



            try {
                Uri uri = Uri.parse(url);
                DownloadManager downloadManager = (DownloadManager) context
                        .getSystemService(Context.DOWNLOAD_SERVICE);
                DownloadManager.Request request = new DownloadManager.Request(uri);
                //在通知栏中显示
                request.setVisibleInDownloadsUi(true);
                request.setTitle(title);

                // VISIBILITY_VISIBLE:                   下载过程中可见, 下载完后自动消失 (默认)
                // VISIBILITY_VISIBLE_NOTIFY_COMPLETED:  下载过程中和下载完成后均可见
                // VISIBILITY_HIDDEN:                    始终不显示通知
                if (!UpdateManager.showNotification)
                    request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_HIDDEN);

                Long mobile = downloadManager.getMaxBytesOverMobile(context);
                String filePath = getDefaultPath(context);
                downloadUpdateApkFilePath = filePath + File.separator + fileName;
                deleteFile(downloadUpdateApkFilePath);// 若存在，则删除
                Uri fileUri = Uri.fromFile(new File(downloadUpdateApkFilePath));
                request.setDestinationUri(fileUri);
                downloadUpdateApkId = downloadManager.enqueue(request);

            } catch (Exception e) {
                e.printStackTrace();
                downloadForWebView(context, url);
            }finally {
//            registerReceiver(receiver, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
            }
        }
    }
    public static String getDefaultPath(Context context){
        String filePath = null;
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {//外部存储卡
//            filePath = Environment.getExternalStorageDirectory().getAbsolutePath();
            filePath = context.getExternalCacheDir().getAbsolutePath();
        } else {
//            Log.i(TAG,"没有SD卡");
            filePath = context.getCacheDir().getPath();
        }
        return filePath;
    }

    private static boolean deleteFile(String fileStr) {
        File file = new File(fileStr);
        return file.delete();
    }

//==========================================APP内部下载======================================================

    static ProgressDialog pd; // 进度条对话框
    /**
     * 从服务器中下载APK
     */
    @SuppressWarnings("unused")
    public static void downLoadByApp(final Context context,final String downURL,final String appName ) {
        pd = new ProgressDialog(context);
        pd.setProgressNumberFormat("%1d/%2d KB");
//        pd.setProgressNumberFormat("%2d ");
        pd.setCancelable(false);// 必须一直下载完，不可取消
        pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        pd.setMessage("正在下载安装包，请稍后");
        pd.setTitle("版本升级");
        pd.show();
        new Thread() {
            @Override
            public void run() {
                try {
                    File file = downloadFile(context,downURL,appName);
                    installApk(context, file);
                    // 结束掉进度条对话框
                    sendMessage(0,null);//关闭对话框
                } catch (Exception e) {
                    sendMessage(0,null);//关闭对话框
                }
            }
        }.start();
    }

    /**
     * 从服务器下载最新更新文件
     *
     * @param path
     *            下载路径
     * @return
     * @throws Exception
     */
    private static File downloadFile(Context context,String path,String appName) throws Exception {
        // 如果相等的话表示当前的sdcard挂载在手机上并且是可用的
        if (Environment.MEDIA_MOUNTED.equals(Environment
                .getExternalStorageState())) {
            URL url = new URL(path);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(5000);
            // 获取到文件的大小
//            pd.setMax(conn.getContentLength());
            int contentLength = conn.getContentLength();
            sendMessage(1,bytes2kbNum(contentLength));//1 设置文件大小
            InputStream is = conn.getInputStream();
            String filePath = UpdateManager.downloadPath();
            if (filePath == null || "".equals(filePath)){
               filePath = getDefaultPath(context);
            }
            String fileName = filePath + File.separator + appName;
            downloadUpdateApkFilePath = filePath + File.separator + fileName;
            Log.d(TAG,fileName);
            File file = new File(fileName);
            // 目录不存在创建目录
            if (!file.getParentFile().exists())
                file.getParentFile().mkdirs();

            if (!file.exists()){
                file.createNewFile();
            }else{
                Log.e(TAG, "downloadFile: 已存在文件");
                deleteFile(downloadUpdateApkFilePath);// 若存在，则删除
//                return file;
            }
            FileOutputStream fos = new FileOutputStream(file);
            BufferedInputStream bis = new BufferedInputStream(is);
            byte[] buffer = new byte[1024];
            int len;
            int total = 0;
            while ((len = bis.read(buffer)) != -1) {
                fos.write(buffer, 0, len);
                total += len;
                // 获取当前下载量
                sendMessage(2,bytes2kbNum(total));//2 设置下载进度
            }
            Log.d(TAG,"下载完成");
            fos.close();
            bis.close();
            is.close();
            return file;
        } else {
            throw new IOException("未发现有SD卡");
        }
    }
    private static void sendMessage(int what,Object obj){
        Message message = new Message();
        message.what = what;
        message.obj = obj;
        handler.sendMessage(message);
    }
    private static Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 0:
                    pd.dismiss();
                    break;
                case 1:
                    pd.setMax(Math.round((float)msg.obj));
                    break;
                case 2:
                    pd.setProgress(Math.round((float)msg.obj));
                    break;
            }
        }
    };
    /*
    * *
     * 安装apk
     */
    private static void installApk(Context mContext, File file) {
        Intent it = getInstallIntent(mContext,file);
        mContext.startActivity(it);
    }

    /**
     *   method describe:得到安装的intent
     *
     */
    private static Intent getInstallIntent(Context context,File apkFile){
        Intent intent=new Intent();
        Uri uri;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent.setAction(Intent.ACTION_VIEW);
            System.out.println(apkFile.getPath());
            uri = FileProvider.getUriForFile(context, UpdateManager.authorities(), apkFile);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        }else {
            intent.setAction(Intent.ACTION_VIEW);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            uri=Uri.fromFile(new File(apkFile.getAbsolutePath()));
        }
        Log.e(TAG, "getInstallIntent: " + uri.getPath() );
        intent.setDataAndType(uri,
                "application/vnd.android.package-archive");
        return intent;
    }


    /**
     * 获取应用程序版本（versionName）
     *
     * @return 当前应用的版本号
     */

    private static double getLocalVersion(Context context) {
        PackageManager manager = context.getPackageManager();
        PackageInfo info = null;
        try {
            info = manager.getPackageInfo(context.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(TAG, "获取应用程序版本失败，原因：" + e.getMessage());
            return 0.0;
        }

        return Double.valueOf(info.versionName);
    }
    /**
     * byte(字节)根据长度转成kb(千字节)和mb(兆字节)
     *
     * @param bytes
     * @return
     */
    public static String bytes2kb(long bytes) {
        BigDecimal filesize = new BigDecimal(bytes);
        BigDecimal megabyte = new BigDecimal(1024 * 1024);
        float returnValue = filesize.divide(megabyte, 2, BigDecimal.ROUND_UP)
                .floatValue();
        if (returnValue > 1)
            return (returnValue + "MB");
        BigDecimal kilobyte = new BigDecimal(1024);
        returnValue = filesize.divide(kilobyte, 2, BigDecimal.ROUND_UP)
                .floatValue();
        return (returnValue + "KB");
    }

    public static float bytes2kbNum(long bytes) {
        BigDecimal filesize = new BigDecimal(bytes);
//        BigDecimal megabyte = new BigDecimal(1024 * 1024);
//        float returnValue = filesize.divide(megabyte, 2, BigDecimal.ROUND_UP)
//                .floatValue();
//        if (returnValue > 1)
//            return returnValue;

        // kb
        BigDecimal kilobyte = new BigDecimal(1024);
        float returnValue = filesize.divide(kilobyte, 2, BigDecimal.ROUND_UP)
                .floatValue();
        return returnValue;
    }

}
