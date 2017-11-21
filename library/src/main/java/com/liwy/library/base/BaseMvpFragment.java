package com.liwy.library.base;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.liwy.library.base.presenter.BaseFragmentPresenter;
import com.liwy.library.base.view.IView;

import butterknife.ButterKnife;

/**
 * Created by liwy on 2016/12/9.
 */

public abstract class BaseMvpFragment<T extends BaseFragmentPresenter> extends BaseFragment implements IView {
    public T mPresenter;
    // 获取presenter
    protected BaseFragmentPresenter getPresenter(){
        return mPresenter;
    }
    //初始化presenter
    protected abstract void  initPresenter();

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        initPresenter();
        mContext = context;
        mActivity = getActivity();
        mPresenter.setContext(context);
        mPresenter.setActivity(mActivity);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(getLayoutResId(), container, false);
        unbinder = ButterKnife.bind(this,view);
        return view;
    }
}
