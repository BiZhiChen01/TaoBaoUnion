package com.chen.taobaounion.presenter.impl;

import com.chen.taobaounion.model.Api;
import com.chen.taobaounion.model.bean.OnSellContent;
import com.chen.taobaounion.presenter.IOnSellPresenter;
import com.chen.taobaounion.utils.LogUtils;
import com.chen.taobaounion.utils.RetrofitManager;
import com.chen.taobaounion.utils.UrlUtils;
import com.chen.taobaounion.view.IOnSellCallback;

import java.net.HttpURLConnection;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class OnSellPresenterImpl implements IOnSellPresenter {

    private final Api mApi;

    public OnSellPresenterImpl() {
        Retrofit retrofit = RetrofitManager.getInstance().getRetrofit();
        mApi = retrofit.create(Api.class);
    }

    public static final int DEFAULT_PAGE = 1;

    private int mCurrentPage = DEFAULT_PAGE;
    private IOnSellCallback mViewCallback = null;

    private boolean mIsLoading = false;


    @Override
    public void getOnSellContent() {
        if (mIsLoading) {
            return;
        }
        mIsLoading = true;
        if (mViewCallback != null) {
            mViewCallback.onLoading();
        }
        String targetUrl = UrlUtils.getOnSellUrl(mCurrentPage);
        Call<OnSellContent> task = mApi.getOnSellContent(targetUrl);
        task.enqueue(new Callback<OnSellContent>() {
            @Override
            public void onResponse(Call<OnSellContent> call, Response<OnSellContent> response) {
                mIsLoading = false;
                int code = response.code();
                LogUtils.d(OnSellPresenterImpl.this, "getOnSellContent code === > " + code);
                if (code == HttpURLConnection.HTTP_OK) {
                    OnSellContent result = response.body();
                    LogUtils.d(OnSellPresenterImpl.this, "getOnSellContent result === > " + result.toString());
                    onLoadedSuccess(result);
                } else {
                    onLoadedError();
                }
            }

            @Override
            public void onFailure(Call<OnSellContent> call, Throwable t) {
                LogUtils.d(OnSellPresenterImpl.this, "getOnSellContent error === > " + t.toString());
                onLoadedError();
            }
        });
    }

    public void onLoadedSuccess(OnSellContent result) {
        if (mViewCallback != null) {
            try {
                int size = result.getData().getTbk_dg_optimus_material_response().getResult_list().getMap_data().size();
                if (size == 0) {
                    onLoadedEmpty();
                } else {
                    mViewCallback.onOnSellContentLoaded(result);
                }
            } catch (Exception e) {
                e.printStackTrace();
                onLoadedEmpty();
            }
        }
    }

    private void onLoadedError() {
        mIsLoading = false;
        if (mViewCallback != null) {
            mViewCallback.onError();
        }
    }

    private void onLoadedEmpty() {
        if (mViewCallback != null) {
            mViewCallback.onEmpty();
        }
    }

    @Override
    public void reLoad() {
        this.getOnSellContent();
    }

    @Override
    public void loaderMore() {
        if (mIsLoading) {
            return;
        }
        mIsLoading = true;
        mCurrentPage ++;
        String targetUrl = UrlUtils.getOnSellUrl(mCurrentPage);
        Call<OnSellContent> task = mApi.getOnSellContent(targetUrl);
        task.enqueue(new Callback<OnSellContent>() {
            @Override
            public void onResponse(Call<OnSellContent> call, Response<OnSellContent> response) {
                mIsLoading = false;
                int code = response.code();
                LogUtils.d(OnSellPresenterImpl.this, "getOnSellContentMore code === > " + code);
                if (code == HttpURLConnection.HTTP_OK) {
                    OnSellContent result = response.body();
                    LogUtils.d(OnSellPresenterImpl.this, "getOnSellContentMore result === > " + result.toString());
                    onLoadedMoreSuccess(result);
                } else {
                    onLoadedMoreError();
                }
            }

            @Override
            public void onFailure(Call<OnSellContent> call, Throwable t) {
                LogUtils.d(OnSellPresenterImpl.this, "getOnSellContentMore error === > " + t.toString());
                onLoadedMoreError();
            }
        });
    }

    private void onLoadedMoreError() {
        mIsLoading = false;
        mCurrentPage --;
        if (mViewCallback != null) {
            mViewCallback.onMoreLoadedError();
        }
    }

    private void onLoadedMoreSuccess(OnSellContent result) {
        if (mViewCallback != null) {
            try {
                int size = result.getData().getTbk_dg_optimus_material_response().getResult_list().getMap_data().size();
                if (size == 0) {
                    mCurrentPage --;
                    mViewCallback.onMoreLoadedEmpty();
                } else {
                    mViewCallback.onMoreLoaded(result);
                }
            } catch (Exception e) {
                e.printStackTrace();
                mViewCallback.onMoreLoadedEmpty();
            }
        }
    }

    @Override
    public void registerViewCallback(IOnSellCallback callback) {
        this.mViewCallback = callback;
    }

    @Override
    public void unregisterViewCallback(IOnSellCallback callback) {
        if (mViewCallback != null) {
            this.mViewCallback = null;
        }
    }
}
