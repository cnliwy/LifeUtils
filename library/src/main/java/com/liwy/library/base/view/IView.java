package com.liwy.library.base.view;

import android.content.Intent;
import android.os.Bundle;

/**
 * Created by liwy on 2016/11/16.
 */

public interface IView {
    /**
     * 初始化view
     */
    public void initView();

    public void showToast(String msg);

    // 跳转至下个页面
    public void turnToActivity(Class className);
    public void turnToActivity(Class className, Bundle bundle);
    public void turnToActivity(Intent intent);

    // 跳转至下个页面并销毁
    public void turnToActivityWithFinish(Class className);
    public void turnToActivityWithFinish(Class className, Bundle bundle);
    public void turnToActivityWithFinish(Intent intent);



}
