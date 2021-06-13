package com.chen.taobaounion.presenter.impl;

import com.chen.taobaounion.model.Api;
import com.chen.taobaounion.model.bean.HomeCategoryContent;
import com.chen.taobaounion.presenter.ICategoryPagerPresenter;
import com.chen.taobaounion.utils.LogUtils;
import com.chen.taobaounion.utils.RetrofitManager;
import com.chen.taobaounion.utils.UrlUtils;
import com.chen.taobaounion.view.ICategoryPagerCallback;

import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class CategoryPagePresenterImpl implements ICategoryPagerPresenter {

    private Integer mCurrentPage;

    private Map<Integer, Integer> pagesInfo = new HashMap<>();
    public static final int DEFAULT_PAGE = 1;

    @Override
    public void getContentByCategoryId(int id) {
        for (ICategoryPagerCallback callback : mCallbacks) {
            if (callback.getCategoryId() == id) {
                callback.onLoading();
            }
        }
        Integer targetPage = pagesInfo.get(id);
        if (targetPage == null) {
            targetPage = DEFAULT_PAGE;
            pagesInfo.put(id, DEFAULT_PAGE);
        }
        Call<HomeCategoryContent> task = createTask(id, targetPage);
        task.enqueue(new Callback<HomeCategoryContent>() {
            @Override
            public void onResponse(Call<HomeCategoryContent> call, Response<HomeCategoryContent> response) {
                int code = response.code();
                LogUtils.d(CategoryPagePresenterImpl.this, "code === > " + code);
                if (code == HttpURLConnection.HTTP_OK) {
                    HomeCategoryContent categoryContent = response.body();
                    LogUtils.d(CategoryPagePresenterImpl.this, "pageContent === > " + categoryContent);
                    handleHomeCategoryContentResult(categoryContent, id);
                } else {
                    handleNetworError(id);
                }
            }

            @Override
            public void onFailure(Call<HomeCategoryContent> call, Throwable t) {
                LogUtils.d(CategoryPagePresenterImpl.this, "onFailure === > " + t.toString());
                handleNetworError(id);
            }
        });
    }

    private Call<HomeCategoryContent> createTask(int id, Integer targetPage) {
        Retrofit retrofit = RetrofitManager.getInstance().getRetrofit();
        Api api = retrofit.create(Api.class);
        String homeCategoryContentUrl = UrlUtils.createHomeCategoryContentUrl(id, targetPage);
        Call<HomeCategoryContent> task = api.getHomeCategoryContent(homeCategoryContentUrl);
        return task;
    }

    private void handleNetworError(int id) {
        for (ICategoryPagerCallback callback : mCallbacks) {
            if (callback.getCategoryId() == id) {
                callback.onError();
            }
        }
    }

    private void handleHomeCategoryContentResult(HomeCategoryContent categoryContent, int id) {
        List<HomeCategoryContent.DataBean> data = categoryContent.getData();
        for (ICategoryPagerCallback callback : mCallbacks) {
            if (callback.getCategoryId() == id) {
                if (categoryContent == null || categoryContent.getData().size() == 0) {
                    callback.onEmpty();
                } else {
                    List<HomeCategoryContent.DataBean> looperData = data.subList(data.size() - 5, data.size());
                    callback.onContentLoaded(data);
                    callback.onLooperListLoaded(looperData);
                }
            }
        }
    }

    @Override
    public void loaderMore(int id) {
        mCurrentPage = pagesInfo.get(id);
        if (pagesInfo == null) {
            mCurrentPage = 1;
        }
        mCurrentPage ++;
        pagesInfo.put(id, mCurrentPage);
        Call<HomeCategoryContent> task = createTask(id, mCurrentPage);
        task.enqueue(new Callback<HomeCategoryContent>() {
            @Override
            public void onResponse(Call<HomeCategoryContent> call, Response<HomeCategoryContent> response) {
                int code = response.code();
                LogUtils.d(CategoryPagePresenterImpl.this, "result code === > " + code);
                if (code == HttpURLConnection.HTTP_OK) {
                    HomeCategoryContent result = response.body();
                    LogUtils.d(CategoryPagePresenterImpl.this, "result === > " + result.toString());
                    handleLoaderResult(result, id);
                } else {
                    handleLoaderMoreError(id);
                }
            }

            @Override
            public void onFailure(Call<HomeCategoryContent> call, Throwable t) {
                LogUtils.d(CategoryPagePresenterImpl.this, "onFailure === > " + t.toString());
                handleLoaderMoreError(id);
            }
        });
    }

    private void handleLoaderResult(HomeCategoryContent result, int id) {
        for (ICategoryPagerCallback callback : mCallbacks) {
            if (result == null || result.getData().size() == 0) {
                callback.onLoaderMoreEmpty();
            } else {
                callback.onLoaderMoreLoaded(result.getData());
            }
        }
    }

    private void handleLoaderMoreError(int id) {
        mCurrentPage --;
        pagesInfo.put(id, mCurrentPage);
        for (ICategoryPagerCallback callback : mCallbacks) {
            if (callback.getCategoryId() == id) {
                callback.onLoaderMoreError();
            }
        }
    }

    @Override
    public void reload(int id) {

    }

    private List<ICategoryPagerCallback> mCallbacks = new ArrayList<>();

    @Override
    public void registerViewCallback(ICategoryPagerCallback callback) {
        if (!mCallbacks.contains(callback)) {
            mCallbacks.add(callback);
        }
    }

    @Override
    public void unregisterViewCallback(ICategoryPagerCallback callback) {
        mCallbacks.remove(callback);
    }
}
