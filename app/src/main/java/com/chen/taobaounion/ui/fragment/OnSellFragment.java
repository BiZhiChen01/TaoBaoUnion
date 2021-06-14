package com.chen.taobaounion.ui.fragment;

import android.graphics.Rect;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chen.taobaounion.R;
import com.chen.taobaounion.base.BaseFragment;
import com.chen.taobaounion.model.bean.IBaseInfo;
import com.chen.taobaounion.model.bean.OnSellContent;
import com.chen.taobaounion.presenter.IOnSellPresenter;
import com.chen.taobaounion.ui.adapter.OnSellContentAdapter;
import com.chen.taobaounion.utils.PresenterManager;
import com.chen.taobaounion.utils.SizeUtils;
import com.chen.taobaounion.utils.TicketUtil;
import com.chen.taobaounion.utils.ToastUtils;
import com.chen.taobaounion.view.IOnSellCallback;
import com.lcodecore.tkrefreshlayout.RefreshListenerAdapter;
import com.lcodecore.tkrefreshlayout.TwinklingRefreshLayout;

import org.jetbrains.annotations.NotNull;

import butterknife.BindView;

public class OnSellFragment extends BaseFragment implements IOnSellCallback, OnSellContentAdapter.OnSellItemClickListener {

    public static final int DEFAULT_SPAN_COUNT = 2;

    private IOnSellPresenter mOnSellPresenter;

    @BindView(R.id.on_sell_content_list)
    public RecyclerView mContentList;
    @BindView(R.id.on_sell_refresh)
    public TwinklingRefreshLayout mRefreshLayout;

    private OnSellContentAdapter mContentAdapter;

    @Override
    protected int getRootViewResId() {
        return R.layout.fragment_red_packet;
    }

    @Override
    protected void initView(View rootView) {
        mContentList.setLayoutManager(new GridLayoutManager(getContext(), DEFAULT_SPAN_COUNT));
        mContentAdapter = new OnSellContentAdapter();
        mContentList.setAdapter(mContentAdapter);
        mContentList.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(@NonNull @NotNull Rect outRect, @NonNull @NotNull View view, @NonNull @NotNull RecyclerView parent, @NonNull @NotNull RecyclerView.State state) {
                int size = SizeUtils.dip2px(getContext(), 2.5f);
                outRect.top = size;
                outRect.bottom = size;
                outRect.left = size;
                outRect.right = size;
            }
        });

        mRefreshLayout.setEnableRefresh(false);
        mRefreshLayout.setEnableLoadmore(true);
        mRefreshLayout.setEnableOverScroll(true);
    }

    @Override
    protected void initPresenter() {
        mOnSellPresenter = PresenterManager.getInstance().getOnSellPresenter();
        mOnSellPresenter.registerViewCallback(this);
        mOnSellPresenter.getOnSellContent();
    }

    @Override
    protected void initListener() {
        mRefreshLayout.setOnRefreshListener(new RefreshListenerAdapter() {
            @Override
            public void onLoadMore(TwinklingRefreshLayout refreshLayout) {
                if (mOnSellPresenter != null) {
                    mOnSellPresenter.loaderMore();
                }
            }
        });

        mContentAdapter.setOnSellItemClickListener(this);
    }

    @Override
    public void onOnSellContentLoaded(OnSellContent content) {
        setUpState(State.SUCCESS);
        mContentAdapter.setData(content);
    }

    @Override
    public void onMoreLoaded(OnSellContent content) {
        mRefreshLayout.finishLoadmore();
        mContentAdapter.setMoreData(content);
    }

    @Override
    public void onMoreLoadedError() {
        mRefreshLayout.finishLoadmore();
        ToastUtils.showToast("网络异常！请稍后重试...");
    }

    @Override
    public void onMoreLoadedEmpty() {
        mRefreshLayout.finishLoadmore();
        ToastUtils.showToast("没有更多的内容...");
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
    protected void release() {
        super.release();
        if (mOnSellPresenter != null) {
            mOnSellPresenter.unregisterViewCallback(this);
        }
    }

    @Override
    public void onSellItemClick(IBaseInfo item) {
        TicketUtil.toTicketPage(getContext(), item);
    }
}
