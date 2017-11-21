package com.liwy.common.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.provider.Settings;
import android.telephony.TelephonyManager;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.UUID;

/**
 * 获取手机/应用基础信息工具类
 * Created by liwy on 2017/9/11.
 */

public class PhoneUtils {
    public static String TAG = "PhoneUtils";

    /**
     * 获取手机当前系统版本号
     * @return
     */
    public static String getSystemVersion(){
        return android.os.Build.VERSION.RELEASE;
    }


    /**
     * 获取手机型号
     * @return  手机型号
     */
    public static String getPhoneModel() {
        return android.os.Build.MODEL;
    }

    /**
     * 获取手机厂商
     * @return  手机厂商
     */
    public static String getDeviceBrand() {
        return android.os.Build.BRAND;
    }

    /**
     * 获取app版本名称
     * @return 版本名称
     */
    public static String getVersionName() {
        return getPackageInfo().versionName;
    }

    /**
     * 获取app版本号
     * @return
     */
    public static int getVersionCode() {
        return getPackageInfo().versionCode;
    }

    private static UUID uuid;//uuid缓存，deviceId信息

    /**
     *  获取deviceId(每台设备唯一且不变)
     * @param filename 存储deviceId的文件绝对路径名称
     * @return
     */
    public static String getDeviceId(String filename){
        String key = "deviceId";
        if (uuid == null) {
            File file = new File(filename);
            if (!file.exists()){
                FileUtils.createNewFile(file);
            }
            synchronized (PhoneUtils.class) {
                if (uuid == null) {
                    final SharedPreferences prefs = BaseUtils.getContext().getSharedPreferences("DEVICE_UUID", 0);
                    String id = prefs.getString(key, null);
                    if (id != null) {
                        uuid = UUID.fromString(id);
                    } else {
                        String content = FileUtils.readFile(file);
                        if (content != null && !"".equals(content)) {
                            uuid = UUID.fromString(content);
                        } else {
                            final String androidId = Settings.Secure.getString(BaseUtils.getContext().getContentResolver(), Settings.Secure.ANDROID_ID);
                            try {
                                if (!"9774d56d682e549c".equals(androidId)) {
                                    uuid = UUID.nameUUIDFromBytes(androidId.getBytes("utf8"));
                                    try {
                                        FileUtils.write(file,uuid.toString());
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                } else {
                                    final String deviceId = ((TelephonyManager) BaseUtils.getContext().getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId();
                                    uuid = deviceId != null ? UUID.nameUUIDFromBytes(deviceId.getBytes("utf8")) : UUID.randomUUID();
                                    try {
                                        FileUtils.write(file,uuid.toString());
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            } catch (UnsupportedEncodingException e) {
                                throw new RuntimeException(e);
                            }
                        }
                        prefs.edit().putString(key, uuid.toString()).commit();
                    }
                }
            }
        }
        return uuid.toString();
    }
    public static String getIPAddress() {
        Context context = BaseUtils.getContext();
        NetworkInfo info = ((ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
        if (info != null && info.isConnected()) {
            if (info.getType() == ConnectivityManager.TYPE_MOBILE) {//当前使用2G/3G/4G网络
                try {
                    //Enumeration<NetworkInterface> en=NetworkInterface.getNetworkInterfaces();
                    for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
                        NetworkInterface intf = en.nextElement();
                        for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                            InetAddress inetAddress = enumIpAddr.nextElement();
                            if (!inetAddress.isLoopbackAddress() && inetAddress instanceof Inet4Address) {
                                return inetAddress.getHostAddress();
                            }
                        }
                    }
                } catch (SocketException e) {
                    e.printStackTrace();
                }

            } else if (info.getType() == ConnectivityManager.TYPE_WIFI) {//当前使用无线网络
                WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
                WifiInfo wifiInfo = wifiManager.getConnectionInfo();
                String ipAddress = intIP2StringIP(wifiInfo.getIpAddress());//得到IPV4地址
                return ipAddress;
            }
        } else {
            //当前无网络连接,请在设置中打开网络
        }
        return null;
    }

    /**
     * 获取manifests里的meta信息
     * @param key
     * @return
     */
    public static String getMetaData(String key) {
        Context context = BaseUtils.getContext();
        try {
            ApplicationInfo ai = context.getPackageManager().getApplicationInfo(
                    context.getPackageName(), PackageManager.GET_META_DATA);
            Object value = ai.metaData.get(key);
            if (value != null) {
                return value.toString();
            }
        } catch (Exception e) {

        }
        return null;
    }
    /**
     * 将得到的int类型的IP转换为String类型
     *
     * @param ip
     * @return
     */
    private static String intIP2StringIP(int ip) {
        return (ip & 0xFF) + "." +
                ((ip >> 8) & 0xFF) + "." +
                ((ip >> 16) & 0xFF) + "." +
                (ip >> 24 & 0xFF);
    }

    private static PackageInfo getPackageInfo() {
        Context context = BaseUtils.getContext();
        PackageInfo pi = null;
        try {
            PackageManager pm = context.getPackageManager();
            pi = pm.getPackageInfo(context.getPackageName(),
                    PackageManager.GET_CONFIGURATIONS);
            return pi;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return pi;
    }
}
