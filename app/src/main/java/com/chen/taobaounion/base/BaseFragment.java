package com.chen.taobaounion.base;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.chen.taobaounion.R;
import com.chen.taobaounion.utils.LogUtils;

import org.jetbrains.annotations.NotNull;

import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public abstract class BaseFragment extends Fragment {

    public enum State { NONE, LOADING, SUCCESS, ERROR, EMPTY }

    private View mLoadingView;
    private View mSuccessView;
    private View mErrorView;
    private View mEmptyView;
    private State currentState = State.NONE;

    private Unbinder mBind;
    private FrameLayout mBaseContainer;

    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        View rootView = loadRootView(inflater, container);
        mBaseContainer = rootView.findViewById(R.id.base_content);
        loadStatesView(inflater, container);
        mBind = ButterKnife.bind(this, rootView);
        initView(rootView);
        initPresenter();
        initListener();
        loadData();
        return rootView;
    }

    protected void initListener() {
    }

    protected View loadRootView(LayoutInflater inflater, ViewGroup container) {
        return inflater.inflate(R.layout.base_fragment_layout, container, false);
    }

    /**
     * 加载各种状态的view
     * @param inflater
     * @param container
     */
    private void loadStatesView(LayoutInflater inflater, ViewGroup container) {
        //成功的view
        mSuccessView = loadSuccessView(inflater, container);
        mBaseContainer.addView(mSuccessView);
        //Loading的view
        mLoadingView = loadingView(inflater, container);
        mBaseContainer.addView(mLoadingView);
        //错误的view
        mErrorView = loadErrorView(inflater, container);
        mBaseContainer.addView(mErrorView);
        //内容为空的View
        mEmptyView = loadEmptyView(inflater, container);
        mBaseContainer.addView(mEmptyView);

        setUpState(State.NONE);
    }

    /**
     * 子类通过这个方法来切换状态页面
     * @param state
     */
    public void setUpState(State state) {
        this.currentState = state;
        mSuccessView.setVisibility(currentState == State.SUCCESS ? View.VISIBLE : View.GONE);
        mLoadingView.setVisibility(currentState == State.LOADING ? View.VISIBLE : View.GONE);
        mErrorView.setVisibility(currentState == State.ERROR ? View.VISIBLE : View.GONE);
        mEmptyView.setVisibility(currentState == State.EMPTY ? View.VISIBLE : View.GONE);
    }

    /**
     * 加载Loading界面
     * @param inflater
     * @param container
     * @return
     */
    protected View loadingView(LayoutInflater inflater, ViewGroup container) {
        return inflater.inflate(R.layout.fragment_loading, container, false);
    }

    /**
     * 错误Error的页面
     * @param inflater
     * @param container
     * @return
     */
    protected View loadErrorView(LayoutInflater inflater, ViewGroup container) {
        return inflater.inflate(R.layout.fragment_error, container, false);
    }

    @OnClick(R.id.network_error_tips)
    public void retry() {
        //点击了重新加载内容
        LogUtils.d(this, "on retry ...");
        onRetryClick();
    }
    protected void onRetryClick() {
    }

    /**
     * 为空Empty的页面
     * @param inflater
     * @param container
     * @return
     */
    protected View loadEmptyView(LayoutInflater inflater, ViewGroup container) {
        return inflater.inflate(R.layout.fragment_empty, container, false);
    }

    protected void initView(View rootView) {
    }

    protected void loadData() {
        //加载数据
    }

    protected void initPresenter() {
        //创建Presenter
    }

    protected View loadSuccessView(LayoutInflater inflater, ViewGroup container) {
        int resId = getRootViewResId();
        return inflater.inflate(resId, container, false);
    }

    protected abstract int getRootViewResId();

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mBind != null) {
            mBind.unbind();
        }
        release();
    }

    protected void release() {
        //释放资源
    }
}
