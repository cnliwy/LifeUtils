package com.liwy.common.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * SharePreferenceUtils
 * Created by liwy on 2017/9/13.
 */

public class SPUtils {
    /**
     * 全程序通用的preference文件
     * 存入key-value
     * @param key
     * @param value
     */
    public static void put(String key,Object value){
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(BaseUtils.getContext());
        SharedPreferences.Editor editor = sp.edit();
        if (value instanceof String) {
            editor.putString(key, (String) value);
        } else if (value instanceof Integer) {
            editor.putInt(key, (Integer) value);
        } else if (value instanceof Boolean) {
            editor.putBoolean(key, (Boolean) value);
        } else if (value instanceof Float) {
            editor.putFloat(key, (Float) value);
        } else if (value instanceof Long) {
            editor.putLong(key, (Long) value);
        }
        editor.commit();
    }

    /**
     * 根据key获取value
     * @param key
     * @param defValue 当key不存在时的默认值
     * @return
     */
    public static Object get(String key,Object defValue){
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(BaseUtils.getContext());
        if (defValue instanceof String) {
            return sp.getString(key, (String) defValue);
        } else if (defValue instanceof Integer) {
            return sp.getInt(key, (Integer) defValue);
        } else if (defValue instanceof Boolean) {
            return sp.getBoolean(key, (Boolean) defValue);
        } else if (defValue instanceof Float) {
            return sp.getFloat(key, (Float) defValue);
        } else if (defValue instanceof Long) {
            return sp.getLong(key, (Long) defValue);
        }
        return null;
    }

    /**
     * 删除key
     * @param key
     */
    public static void remove(String key){
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(BaseUtils.getContext());
        SharedPreferences.Editor editor = sp.edit();
        editor.remove(key);
        editor.commit();
    }
//##################################################自定义fileName####################################################################
    /**
     * 自定义SharePreferences文件名
     * @param fileName
     * @param key
     * @param value
     */
    public static void put(String fileName,String key,Object value){
        if (fileName == null || "".equals(fileName)){
            put(key,value);
        }else{
            SharedPreferences sp = BaseUtils.getContext().getSharedPreferences(fileName, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sp.edit();
            if (value instanceof String) {
                editor.putString(key, (String) value);
            } else if (value instanceof Integer) {
                editor.putInt(key, (Integer) value);
            } else if (value instanceof Boolean) {
                editor.putBoolean(key, (Boolean) value);
            } else if (value instanceof Float) {
                editor.putFloat(key, (Float) value);
            } else if (value instanceof Long) {
                editor.putLong(key, (Long) value);
            }
            editor.commit();
        }
    }
    /**
     * 根据key获取value
     * @param key
     * @param defValue 当key不存在时的默认值
     * @return
     */
    public static Object get(String fileName,String key,Object defValue){
        if (fileName == null || "".equals(fileName)){
            return  get(key,defValue);
        }
        SharedPreferences sp = BaseUtils.getContext().getSharedPreferences(fileName,Context.MODE_PRIVATE);
        if (defValue instanceof String) {
            return sp.getString(key, (String) defValue);
        } else if (defValue instanceof Integer) {
            return sp.getInt(key, (Integer) defValue);
        } else if (defValue instanceof Boolean) {
            return sp.getBoolean(key, (Boolean) defValue);
        } else if (defValue instanceof Float) {
            return sp.getFloat(key, (Float) defValue);
        } else if (defValue instanceof Long) {
            return sp.getLong(key, (Long) defValue);
        }
        return null;
    }

    /**
     * 删除key
     * @param fileName
     * @param key
     */
    public static void remove(String fileName,String key){
        if (fileName == null || "".equals(fileName)) {
            remove(key);
            return;
        }
        SharedPreferences sp = BaseUtils.getContext().getSharedPreferences(fileName,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.remove(key);
        editor.commit();
    }
    //==============================================频繁存储方法=======================================================
    public static SharedPreferences.Editor getEditor(String fileName){
        SharedPreferences sp;
        if (fileName!= null && !"".equals(fileName)){
            sp = BaseUtils.getContext().getSharedPreferences(fileName,Context.MODE_PRIVATE);
        }else{
            sp = PreferenceManager.getDefaultSharedPreferences(BaseUtils.getContext());
        }
        return sp.edit();
    }
}
