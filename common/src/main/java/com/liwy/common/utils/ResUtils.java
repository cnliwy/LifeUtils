package com.liwy.common.utils;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;

/**
 * Created by liwy on 2017/9/20.
 */

public class ResUtils {
    /**
     * 根据颜色资源id，返回资源值
     * @param context
     * @param rid
     * @return
     */
    public static int getColor(Context context, int rid){
        int colorValue = 0;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            colorValue = context.getResources().getColor(rid,null);
        }else {
            colorValue = context.getResources().getColor(rid);
        }
        return colorValue;
    }


    /**
     * 根据资源id获取drawable
     * @param mContext
     * @param rid
     * @return
     */
    public static Drawable getDrawable(Context mContext, int rid){
        Drawable drawable = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            drawable = mContext.getResources().getDrawable(rid, null);
        }else{
            drawable = mContext.getResources().getDrawable(rid);
        }
        return drawable;
    }
}
