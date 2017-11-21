package com.liwy.common.utils;

import android.content.Context;

/**
 * Created by liwy on 2017/8/17.
 */

public class BaseUtils {
    private static Context context;

    private BaseUtils() {
        throw new UnsupportedOperationException("BaseUtils can't instantiate with none parameter...");
    }

    /**
     * 初始化工具类
     *
     * @param context 上下文
     */
    public static void init(Context context) {
        BaseUtils.context = context.getApplicationContext();
    }

    /**
     * 获取ApplicationContext
     *
     * @return ApplicationContext
     */
    public static Context getContext() {
        if (context != null) return context;
        throw new NullPointerException("BaseUtils should init first");
    }
}
