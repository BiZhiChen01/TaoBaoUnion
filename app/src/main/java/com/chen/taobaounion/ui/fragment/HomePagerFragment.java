package com.chen.taobaounion.ui.fragment;

import android.view.View;

import com.chen.taobaounion.R;
import com.chen.taobaounion.base.BaseFragment;

public class HomePagerFragment extends BaseFragment {
    @Override
    protected int getRootViewResId() {
        return R.layout.fragment_home_pager;
    }

    @Override
    protected void initView(View rootView) {
        setUpState(State.SUCCESS);
    }
}
