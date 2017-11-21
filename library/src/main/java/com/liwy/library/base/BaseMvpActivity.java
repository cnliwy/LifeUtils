package com.liwy.library.base;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.liwy.library.base.presenter.BasePresenter;
import com.liwy.library.base.view.IView;


/**
 * 抽象基类
 * Created by liwy on 2016/11/16.
 */

public abstract class BaseMvpActivity<T extends BasePresenter> extends BaseActivity implements IView {
    public T mPresenter;

    protected BasePresenter getPresenter(){
        return mPresenter;
    }

    /**
     * 初始化presenters
     */
    protected abstract void  initPresenter();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 初始化presenter
        initPresenter();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mPresenter != null)mPresenter.clearMemory();
    }
}
