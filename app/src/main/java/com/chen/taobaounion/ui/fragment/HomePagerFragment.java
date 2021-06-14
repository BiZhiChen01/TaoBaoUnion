package com.chen.taobaounion.ui.fragment;

import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.chen.taobaounion.R;
import com.chen.taobaounion.base.BaseFragment;
import com.chen.taobaounion.model.bean.HomeCategories;
import com.chen.taobaounion.model.bean.HomeCategoryContent;
import com.chen.taobaounion.model.bean.IBaseInfo;
import com.chen.taobaounion.presenter.ICategoryPagerPresenter;
import com.chen.taobaounion.ui.adapter.HomePagerContentAdapter;
import com.chen.taobaounion.utils.Constants;
import com.chen.taobaounion.utils.LogUtils;
import com.chen.taobaounion.utils.PresenterManager;
import com.chen.taobaounion.utils.SizeUtils;
import com.chen.taobaounion.utils.TicketUtil;
import com.chen.taobaounion.utils.ToastUtils;
import com.chen.taobaounion.utils.UrlUtils;
import com.chen.taobaounion.view.ICategoryPagerCallback;
import com.lcodecore.tkrefreshlayout.RefreshListenerAdapter;
import com.lcodecore.tkrefreshlayout.TwinklingRefreshLayout;
import com.lcodecore.tkrefreshlayout.footer.LoadingView;
import com.lcodecore.tkrefreshlayout.header.progresslayout.ProgressLayout;
import com.lcodecore.tkrefreshlayout.views.TbNestedScrollView;
import com.youth.banner.Banner;
import com.youth.banner.adapter.BannerImageAdapter;
import com.youth.banner.config.IndicatorConfig;
import com.youth.banner.holder.BannerImageHolder;
import com.youth.banner.indicator.CircleIndicator;
import com.youth.banner.listener.OnBannerListener;
import com.youth.banner.transformer.DepthPageTransformer;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import butterknife.BindView;

public class HomePagerFragment extends BaseFragment implements ICategoryPagerCallback, HomePagerContentAdapter.OnListItemClickListener {

    private ICategoryPagerPresenter mCategoryPagePresenter;
    private int mMaterialId;
    private HomePagerContentAdapter mContentAdapter;

    public static HomePagerFragment newInstance(HomeCategories.DataBean category) {
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
    @BindView(R.id.home_pager_refresh)
    public TwinklingRefreshLayout mTwinklingRefreshLayout;
    @BindView(R.id.home_pager_parent)
    public LinearLayout mHomePagerParent;
    @BindView(R.id.home_pager_nested_scroller)
    public TbNestedScrollView mHomePagerNestedScroller;
    @BindView(R.id.home_pager_header_container)
    public LinearLayout mHomePagerHeaderContainer;

    private Boolean loaderMore = false;
    private Boolean refreshMore = false;

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
                int topAndBottom = SizeUtils.dip2px(getContext(), 4);
                outRect.top = topAndBottom;
                outRect.bottom = topAndBottom;
            }
        });
        mContentAdapter = new HomePagerContentAdapter();
        mContentList.setAdapter(mContentAdapter);
        mTwinklingRefreshLayout.setEnableRefresh(true);
        mTwinklingRefreshLayout.setEnableLoadmore(true);
        mTwinklingRefreshLayout.setHeaderView(new ProgressLayout(getContext()));
        mTwinklingRefreshLayout.setFloatRefresh(true);
        mTwinklingRefreshLayout.setBottomView(new LoadingView(getContext()));
    }

    @Override
    protected void initPresenter() {
        mCategoryPagePresenter = PresenterManager.getInstance().getCategoryPagePresenter();
        mCategoryPagePresenter.registerViewCallback(this);
    }

    @Override
    protected void initListener() {
        mTwinklingRefreshLayout.setOnRefreshListener(new RefreshListenerAdapter() {
            @Override
            public void onLoadMore(TwinklingRefreshLayout refreshLayout) {
                if (mCategoryPagePresenter != null) {
                    loaderMore = true;
                    mCategoryPagePresenter.loaderMore(mMaterialId);
                }
            }

            @Override
            public void onRefresh(TwinklingRefreshLayout refreshLayout) {
                if (mCategoryPagePresenter != null) {
                    refreshMore = true;
                    mCategoryPagePresenter.loaderMore(mMaterialId);
                }
            }
        });

        mHomePagerParent.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (mHomePagerHeaderContainer == null) {
                    return;
                }
                int headerHeight = mHomePagerHeaderContainer.getMeasuredHeight();
                mHomePagerNestedScroller.setHeaderHeight(headerHeight);

                int measuredHeight = mHomePagerParent.getMeasuredHeight();
                LinearLayout.LayoutParams contentListLayoutParams = (LinearLayout.LayoutParams) mContentList.getLayoutParams();
                contentListLayoutParams.height = measuredHeight;
                mContentList.setLayoutParams(contentListLayoutParams);
                if (measuredHeight != 0) {
                    mHomePagerParent.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                }
            }
        });

        mContentAdapter.setOnListItemClickListener(this);
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
        if (mTwinklingRefreshLayout != null) {
            if (loaderMore) {
                mContentAdapter.addLoadedData(contents);
                mTwinklingRefreshLayout.finishLoadmore();
                loaderMore = false;
            } else if (refreshMore) {
                mContentAdapter.addRefreshData(contents);
                mTwinklingRefreshLayout.finishRefreshing();
                refreshMore = false;
            }
        }
    }

    @Override
    public void onLoaderMoreError() {
        ToastUtils.showToast("网络异常，请稍后重试...");
    }

    @Override
    public void onLoaderMoreEmpty() {
        ToastUtils.showToast("没有更多的商品了...");
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onLooperListLoaded(List<HomeCategoryContent.DataBean> contents) {
        mBanner.addBannerLifecycleObserver(getViewLifecycleOwner())
                .setIndicator(new CircleIndicator(getContext()))
                .setAdapter(new BannerImageAdapter<HomeCategoryContent.DataBean>(contents) {
                    @Override
                    public void onBindView(BannerImageHolder holder, HomeCategoryContent.DataBean data, int position, int size) {
                        int measuredHeight = holder.imageView.getMeasuredHeight();
                        int measuredWidth = holder.imageView.getMeasuredWidth();
                        int coverSize = (measuredWidth > measuredHeight ? measuredWidth : measuredHeight) / 2;
                        Glide.with(getContext()).load(UrlUtils.getCoverPath(contents.get(position).getPict_url(), coverSize)).into(holder.imageView);
                    }
                })
                .setIndicatorSelectedColor(getContext().getColor(R.color.color_main))
                .setIndicatorNormalColor(getContext().getColor(R.color.white))
                .setIndicatorMargins(new IndicatorConfig.Margins(20))
                .setIndicatorWidth(18, 18)
                .setPageTransformer(new DepthPageTransformer())
                .setOnBannerListener(new OnBannerListener() {
                    @Override
                    public void OnBannerClick(Object data, int position) {
                        handleItemClick(contents.get(position));
                    }
                });
    }

    @Override
    public int getCategoryId() {
        return mMaterialId;
    }

    @Override
    protected void release() {
        if (mCategoryPagePresenter != null) {
            mCategoryPagePresenter.unregisterViewCallback(this);
        }
    }

    @Override
    public void onItemClick(HomeCategoryContent.DataBean item) {
        handleItemClick(item);
    }

    private void handleItemClick(IBaseInfo item) {
        TicketUtil.toTicketPage(getContext(), item);
    }

}
