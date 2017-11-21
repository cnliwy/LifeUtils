package com.liwy.library.base.presenter;

import android.app.Activity;
import android.content.Context;

import com.liwy.library.base.view.IView;


/**
 * Created by liwy on 2017/3/14.
 */

public class BasePresenter<V extends IView> implements IPresenter {
    public V mView;
    public Context mContext;
    public Activity mActivity;

    // 初始化方法
    public void init(V view) {
        this.mView = view;
        this.mView.initView();
    }

    public void init(V view,Context context){
        this.mView = view;
        this.mContext = context;
        this.mView.initView();
    }

    public void init(V view, Context context, Activity activity){
        this.mView = view;
        this.mContext = context;
        this.mActivity = activity;
        this.mView.initView();
    }

    public Context getContext() {
        return mContext;
    }

    public void setContext(Context context) {
        this.mContext = context;
    }

    public Activity getActivity() {
        return mActivity;
    }

    public void setActivity(Activity mActivity) {
        this.mActivity = mActivity;
    }

    /**
     * 为防止内存泄漏,需在页面销毁的时候清除对context实例的引用
     */
    @Override
    public void clearMemory(){
        this.mView = null;
        this.mContext = null;
        this.mActivity = null;
    }
}
