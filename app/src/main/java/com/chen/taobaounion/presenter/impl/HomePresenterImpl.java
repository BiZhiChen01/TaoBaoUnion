package com.chen.taobaounion.presenter.impl;

import com.chen.taobaounion.model.Api;
import com.chen.taobaounion.model.bean.HomeCategories;
import com.chen.taobaounion.presenter.IHomePresenter;
import com.chen.taobaounion.utils.LogUtils;
import com.chen.taobaounion.utils.RetrofitManager;
import com.chen.taobaounion.view.IHomeCallback;

import java.net.HttpURLConnection;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class HomePresenterImpl implements IHomePresenter {

    private IHomeCallback mCallback = null;

    @Override
    public void getCategories() {
        if (mCallback != null) {
            mCallback.onLoading();
        }
        Retrofit retrofit = RetrofitManager.getInstance().getRetrofit();
        Api api = retrofit.create(Api.class);
        Call<HomeCategories> task = api.getCategories();
        task.enqueue(new Callback<HomeCategories>() {
            @Override
            public void onResponse(Call<HomeCategories> call, Response<HomeCategories> response) {
                int code = response.code();
                LogUtils.d(HomePresenterImpl.this, "result code is ===> " + code);
                if (code == HttpURLConnection.HTTP_OK) {
                    HomeCategories homeCategories = response.body();
                    LogUtils.d(HomePresenterImpl.this, homeCategories.toString());
                    if (mCallback != null) {
                        if (homeCategories == null || homeCategories.getData().size() == 0) {
                            mCallback.onEmpty();
                        } else {
                            mCallback.onCategoriesLoaded(homeCategories);
                        }
                    }
                } else {
                    if (mCallback != null) {
                        mCallback.onError();
                    }
                    LogUtils.i(HomePresenterImpl.this, "请求失败...");
                }
            }

            @Override
            public void onFailure(Call<HomeCategories> call, Throwable t) {
                if (mCallback != null) {
                    mCallback.onError();
                }
                LogUtils.e(HomePresenterImpl.this, "请求错误..." + t);
            }
        });
    }

    @Override
    public void registerViewCallback(IHomeCallback callback) {
        this.mCallback = callback;
    }

    @Override
    public void unregisterViewCallback(IHomeCallback callback) {
        mCallback = null;
    }
}
