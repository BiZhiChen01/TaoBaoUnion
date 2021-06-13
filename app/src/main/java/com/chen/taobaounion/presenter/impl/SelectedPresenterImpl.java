package com.chen.taobaounion.presenter.impl;

import com.chen.taobaounion.model.Api;
import com.chen.taobaounion.model.bean.SelectedCategories;
import com.chen.taobaounion.model.bean.SelectedCategoryContent;
import com.chen.taobaounion.presenter.ISelectedPresenter;
import com.chen.taobaounion.utils.LogUtils;
import com.chen.taobaounion.utils.RetrofitManager;
import com.chen.taobaounion.utils.UrlUtils;
import com.chen.taobaounion.view.ISelectedCallback;

import java.net.HttpURLConnection;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class SelectedPresenterImpl implements ISelectedPresenter {
    private ISelectedCallback mViewCallback = null;
    private final Api mApi;

    public SelectedPresenterImpl() {
        Retrofit retrofit = RetrofitManager.getInstance().getRetrofit();
        mApi = retrofit.create(Api.class);
    }

    @Override
    public void getCategories() {
        if (mViewCallback != null) {
            mViewCallback.onLoading();
        }
        Call<SelectedCategories> task = mApi.getSelectedCategories();
        task.enqueue(new Callback<SelectedCategories>() {
            @Override
            public void onResponse(Call<SelectedCategories> call, Response<SelectedCategories> response) {
                int code = response.code();
                LogUtils.d(SelectedPresenterImpl.this, "result code === > " + code);
                if (code == HttpURLConnection.HTTP_OK) {
                    SelectedCategories result = response.body();
                    LogUtils.d(SelectedPresenterImpl.this, "result === > " + result.toString());
                    if (mViewCallback != null) {
                        mViewCallback.onCategoriesLoaded(result);
                    }
                } else {
                    onLoadedError();
                }
            }

            @Override
            public void onFailure(Call<SelectedCategories> call, Throwable t) {
                LogUtils.e(SelectedPresenterImpl.this, "error === > " + t.toString());
                onLoadedError();
            }
        });
    }

    public void onLoadedError() {
        if (mViewCallback != null) {
            mViewCallback.onError();
        }
        mViewCallback.onError();
    }

    @Override
    public void getCategoryContent(SelectedCategories.DataBean item) {
        String targetUrl = UrlUtils.getSelectedCategoryContentUrl(item.getFavorites_id());
        Call<SelectedCategoryContent> task = mApi.getSelectedCategoryContent(targetUrl);
        task.enqueue(new Callback<SelectedCategoryContent>() {
            @Override
            public void onResponse(Call<SelectedCategoryContent> call, Response<SelectedCategoryContent> response) {
                int code = response.code();
                LogUtils.d(SelectedPresenterImpl.this, "getCategoryContent code === > " + code);
                if (code == HttpURLConnection.HTTP_OK) {
                    SelectedCategoryContent result = response.body();
                    LogUtils.d(SelectedPresenterImpl.this, "getCategoryContent result === > " + result.toString());
                    if (mViewCallback != null) {
                        mViewCallback.onContentLoaded(result);
                    } else {
                        if (mViewCallback != null) {
                            onLoadedError();
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<SelectedCategoryContent> call, Throwable t) {
                LogUtils.d(SelectedPresenterImpl.this, "getCategoryContent error === > " + t.toString());
                onLoadedError();
            }
        });
    }

    @Override
    public void reloadContent() {
        this.getCategories();
    }

    @Override
    public void registerViewCallback(ISelectedCallback callback) {
        this.mViewCallback = callback;
    }

    @Override
    public void unregisterViewCallback(ISelectedCallback callback) {
        this.mViewCallback = null;
    }
}
