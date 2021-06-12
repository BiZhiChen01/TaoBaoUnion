package com.chen.taobaounion.presenter.impl;

import com.chen.taobaounion.model.Api;
import com.chen.taobaounion.model.bean.Categories;
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
        Call<Categories> task = api.getCategories();
        task.enqueue(new Callback<Categories>() {
            @Override
            public void onResponse(Call<Categories> call, Response<Categories> response) {
                int code = response.code();
                LogUtils.d(HomePresenterImpl.this, "result code is ===> " + code);
                if (code == HttpURLConnection.HTTP_OK) {
                    Categories categories = response.body();
                    LogUtils.d(HomePresenterImpl.this, categories.toString());
                    if (mCallback != null) {
                        if (categories == null || categories.getData().size() == 0) {
                            mCallback.onEmpty();
                        } else {
                            mCallback.onCategoriesLoaded(categories);
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
            public void onFailure(Call<Categories> call, Throwable t) {
                if (mCallback != null) {
                    mCallback.onError();
                }
                LogUtils.e(HomePresenterImpl.this, "请求错误..." + t);
            }
        });
    }

    @Override
    public void registerCallback(IHomeCallback callback) {
        this.mCallback = callback;
    }

    @Override
    public void unregisterCallback(IHomeCallback callback) {
        mCallback = null;
    }
}
