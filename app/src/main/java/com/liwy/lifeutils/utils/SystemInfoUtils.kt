package com.liwy.lifeutils.utils

import android.app.ActivityManager
import android.content.Context
import android.util.Log
import java.io.BufferedReader
import java.io.FileReader
import java.io.IOException


/**
 * Created by liwy on 2017/11/23.
 */
object SystemInfoUtils {

    /**
     * 获取可用内存
     */
    fun getAvailMemory(context: Context):Long{
        var am = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        var mi = ActivityManager.MemoryInfo()
        am.getMemoryInfo(mi)
        return mi.availMem/(1024*1024)
    }

    /**
     * 获取总内存
     *
     * @param context
     * @return
     */
    fun getTotalMemory(context: Context): Long {
        val str1 = "/proc/meminfo"// 系统内存信息文件
        val str2: String
        val arrayOfString: Array<String>
        var initial_memory: Long = 0
        try {
            val localFileReader = FileReader(str1)
            val localBufferedReader = BufferedReader(
                    localFileReader, 8192)
            str2 = localBufferedReader.readLine()// 读取meminfo第一行，系统总内存大小
            arrayOfString = str2.split("\\s+".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            initial_memory = (Integer.valueOf(arrayOfString[1])!!.toInt() * 1024).toLong()// 获得系统总内存，单位是KB，乘以1024转换为Byte
            localBufferedReader.close()
        } catch (e: IOException) {
        }
        return initial_memory / (1024 * 1024)
    }


    /**
     * 获取每个APP占用的内存（貌似只能查到本应用）
     *
     * @param context
     */
    fun getEveryAppMemory(context: Context) {
        val am = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val list = am.runningAppProcesses
        if (list != null) {
            for (i in list.indices) {
                val appinfo = list[i]
                val myMempid = intArrayOf(appinfo.pid)
                val appMem = am.getProcessMemoryInfo(myMempid)
                val memSize = appMem[0].dalvikPrivateDirty / 1024
                println("当前应用----------->" + appinfo.processName + ":" + memSize)
                Log.e("AppMemory", appinfo.processName + ":" + memSize)
            }
        }else{
            Log.e("AppMemory", "查询结果为空")
        }
    }


}