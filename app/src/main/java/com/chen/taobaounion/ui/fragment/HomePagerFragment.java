package com.chen.taobaounion.ui.fragment;

import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.chen.taobaounion.R;
import com.chen.taobaounion.base.BaseFragment;
import com.chen.taobaounion.model.bean.Categories;
import com.chen.taobaounion.model.bean.HomeCategoryContent;
import com.chen.taobaounion.presenter.ICategoryPagerPresenter;
import com.chen.taobaounion.presenter.impl.CategoryPagePresenterImpl;
import com.chen.taobaounion.ui.adapter.HomePagerContentAdapter;
import com.chen.taobaounion.utils.Constants;
import com.chen.taobaounion.utils.LogUtils;
import com.chen.taobaounion.utils.UrlUtils;
import com.chen.taobaounion.view.ICategoryPagerCallback;
import com.youth.banner.Banner;
import com.youth.banner.adapter.BannerImageAdapter;
import com.youth.banner.config.IndicatorConfig;
import com.youth.banner.holder.BannerImageHolder;
import com.youth.banner.indicator.CircleIndicator;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import butterknife.BindView;

public class HomePagerFragment extends BaseFragment implements ICategoryPagerCallback {

    private ICategoryPagerPresenter mCategoryPagePresenter;
    private int mMaterialId;
    private HomePagerContentAdapter mContentAdapter;

    public static HomePagerFragment newInstance(Categories.DataBean category) {
        HomePagerFragment homePagerFragment = new HomePagerFragment();
        Bundle bundle = new Bundle();
        bundle.putString(Constants.KEY_HOME_PAGER_TITLE, category.getTitle());
        bundle.putInt(Constants.KEY_HOME_PAGER_ID, category.getId());
        homePagerFragment.setArguments(bundle);
        return homePagerFragment;
    }

    @BindView(R.id.home_pager_content_list)
    public RecyclerView mContentList;
    @BindView(R.id.banner)
    public Banner mBanner;
    @BindView(R.id.home_pager_title)
    public TextView currentCategoryTitleTv;

    @Override
    protected int getRootViewResId() {
        return R.layout.fragment_home_pager;
    }

    @Override
    protected void initView(View rootView) {
        mContentList.setLayoutManager(new LinearLayoutManager(getContext()));
        mContentList.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(@NonNull @NotNull Rect outRect, @NonNull @NotNull View view, @NonNull @NotNull RecyclerView parent, @NonNull @NotNull RecyclerView.State state) {
                outRect.top = 8;
                outRect.bottom = 8;
            }
        });
        mContentAdapter = new HomePagerContentAdapter();
        mContentList.setAdapter(mContentAdapter);
    }

    @Override
    protected void initPresenter() {
        mCategoryPagePresenter = CategoryPagePresenterImpl.getInstance();
        mCategoryPagePresenter.registerCallback(this);
    }

    @Override
    protected void loadData() {
        Bundle bundle = getArguments();
        String title = bundle.getString(Constants.KEY_HOME_PAGER_TITLE);
        mMaterialId = bundle.getInt(Constants.KEY_HOME_PAGER_ID);
        LogUtils.d(this, "title === > " + title);
        LogUtils.d(this, "id === > " + mMaterialId);
        if (mCategoryPagePresenter != null) {
            mCategoryPagePresenter.getContentByCategoryId(mMaterialId);
        }
        if (currentCategoryTitleTv != null) {
            currentCategoryTitleTv.setText(title);
        }
    }

    @Override
    public void onContentLoaded(List<HomeCategoryContent.DataBean> contents) {
        mContentAdapter.setData(contents);
        setUpState(State.SUCCESS);
    }

    @Override
    public void onLoading() {
        setUpState(State.LOADING);
    }

    @Override
    public void onError() {
        setUpState(State.ERROR);
    }

    @Override
    public void onEmpty() {
        setUpState(State.EMPTY);
    }

    @Override
    public void onLoaderMoreLoaded(List<HomeCategoryContent.DataBean> contents) {

    }

    @Override
    public void onLoaderMoreError() {

    }

    @Override
    public void onLoaderMoreEmpty() {

    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onLooperListLoaded(List<HomeCategoryContent.DataBean> contents) {
        mBanner.addBannerLifecycleObserver(getViewLifecycleOwner())
                .setIndicator(new CircleIndicator(getContext()))
                .setAdapter(new BannerImageAdapter<HomeCategoryContent.DataBean>(contents) {
                    @Override
                    public void onBindView(BannerImageHolder holder, HomeCategoryContent.DataBean data, int position, int size) {
                        Glide.with(getContext()).load(UrlUtils.getCoverPath(contents.get(position).getPict_url())).into(holder.imageView);
                    }
                })
                .setIndicatorSelectedColor(getContext().getColor(R.color.color_main))
                .setIndicatorNormalColor(getContext().getColor(R.color.white))
                .setIndicatorMargins(new IndicatorConfig.Margins(20))
                .setIndicatorWidth(18, 18);
    }

    @Override
    public int getCategoryId() {
        return mMaterialId;
    }

    @Override
    protected void release() {
        if (mCategoryPagePresenter != null) {
            mCategoryPagePresenter.unregisterCallback(this);
        }
    }
}
