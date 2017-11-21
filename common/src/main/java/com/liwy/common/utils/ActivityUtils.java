package com.liwy.common.utils;

import android.app.Activity;

import java.util.ArrayList;

/**
 * Activity管理类
 * Created by liwy on 2017/8/17.
 */

public class ActivityUtils {
    // Activity集合
    private static ArrayList<Activity> activityList = new ArrayList<>();


    //开启新页面，需添加到集合中，方便退出统一管理
    public static void addActivity(Activity activity){
        activityList.add(activity);
    }
    public static void removeActivity(Activity activity){
        activityList.remove(activity);
    }


    //退出清空所有的activity
    public static  void clearAllActivities(){
        if (activityList.size() > 0){
            for(Activity activity : activityList){
                if (!activity.isFinishing()){
                    activity.finish();
                }
            }
        }
    }
    // 退出程序
    public static void exitApp(){
        clearAllActivities();
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(0);
    }
}
