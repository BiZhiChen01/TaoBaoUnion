package com.chen.taobaounion.ui.fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.viewpager.widget.ViewPager;

import com.chen.taobaounion.R;
import com.chen.taobaounion.base.BaseFragment;
import com.chen.taobaounion.model.bean.HomeCategories;
import com.chen.taobaounion.presenter.IHomePresenter;
import com.chen.taobaounion.ui.adapter.HomePagerAdapter;
import com.chen.taobaounion.utils.PresenterManager;
import com.chen.taobaounion.view.IHomeCallback;
import com.google.android.material.tabs.TabLayout;

import butterknife.BindView;

public class HomeFragment extends BaseFragment implements IHomeCallback {

    private IHomePresenter mHomePresenter;

    @BindView(R.id.home_indicator)
    public TabLayout mTabLayout;
    @BindView(R.id.home_pager)
    public ViewPager mHomePager;

    private HomePagerAdapter mHomePagerAdapter;

    @Override
    protected int getRootViewResId() {
        return R.layout.fragment_home;
    }

    @Override
    protected void initView(View rootView) {
        mTabLayout.setupWithViewPager(mHomePager);

        mHomePagerAdapter = new HomePagerAdapter(getChildFragmentManager());
        mHomePager.setAdapter(mHomePagerAdapter);
    }

    @Override
    protected void initPresenter() {
        //创建Presenter
        mHomePresenter = PresenterManager.getInstance().getHomePresenter();
        mHomePresenter.registerViewCallback(this);
    }

    @Override
    protected View loadRootView(LayoutInflater inflater, ViewGroup container) {
        return inflater.inflate(R.layout.base_home_fragment_layout, container, false);
    }

    @Override
    protected void loadData() {
        //加载数据
        mHomePresenter.getCategories();
    }

    @Override
    public void onCategoriesLoaded(HomeCategories homeCategories) {
        //得到分类数据
        setUpState(State.SUCCESS);
        if (mHomePresenter != null) {
            mHomePagerAdapter.setCategories(homeCategories);
        }
    }

    @Override
    public void onError() {
        setUpState(State.ERROR);
    }

    @Override
    public void onLoading() {
        setUpState(State.LOADING);
    }

    @Override
    public void onEmpty() {
        setUpState(State.EMPTY);
    }

    @Override
    protected void onRetryClick() {
        //网络错误，点击重新加载
        if (mHomePresenter != null) {
            mHomePresenter.getCategories();
        }
    }

    @Override
    protected void release() {
        if (mHomePresenter != null) {
            mHomePresenter.unregisterViewCallback(this);
        }
    }
}
