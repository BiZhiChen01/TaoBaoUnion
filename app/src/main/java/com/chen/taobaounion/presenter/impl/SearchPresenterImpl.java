package com.chen.taobaounion.presenter.impl;

import com.chen.taobaounion.model.Api;
import com.chen.taobaounion.model.bean.Histories;
import com.chen.taobaounion.model.bean.SearchRecommend;
import com.chen.taobaounion.model.bean.SearchResult;
import com.chen.taobaounion.presenter.ISearchPresenter;
import com.chen.taobaounion.utils.JsonCacheUtil;
import com.chen.taobaounion.utils.LogUtils;
import com.chen.taobaounion.utils.RetrofitManager;
import com.chen.taobaounion.view.ISearchCallback;

import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class SearchPresenterImpl implements ISearchPresenter {

    private final Api mApi;
    private ISearchCallback mViewCallback = null;
    private final JsonCacheUtil mJsonCacheUtil;

    public SearchPresenterImpl() {
        Retrofit retrofit = RetrofitManager.getInstance().getRetrofit();
        mApi = retrofit.create(Api.class);
        mJsonCacheUtil = JsonCacheUtil.getInstance();
    }

    public static final int DEFAULT_PAGE = 1;
    public static final String KEY_HISTORIES = "key_histories";
    public static final int DEFAULT_HISTORIES_SIZE = 10;

    private int mCurrentPage = DEFAULT_PAGE;

    private String mCurrentKeyword;
    private int mHistoriesMaxSize = DEFAULT_HISTORIES_SIZE;

    @Override
    public void getHistories() {
        Histories histories = mJsonCacheUtil.getValue(KEY_HISTORIES, Histories.class);
        if (mViewCallback != null && histories != null && histories.getHistories() != null && histories.getHistories().size() != 0) {
            mViewCallback.onHistoriesLoaded(histories.getHistories());
        }
    }

    @Override
    public void delHistories() {
        mJsonCacheUtil.delCache(KEY_HISTORIES);
    }

    public void saveHistory(String history) {
        Histories histories = mJsonCacheUtil.getValue(KEY_HISTORIES, Histories.class);
        List<String> historiesList = null;
        if (histories != null && histories.getHistories() != null) {
            historiesList = histories.getHistories();
            if (historiesList.contains(history)) {
                historiesList.remove(history);
            }
        }
        if (historiesList == null) {
            historiesList = new ArrayList<>();
        }
        if (histories == null) {
            histories = new Histories();
        }
        histories.setHistories(historiesList);

        if (historiesList.size() > mHistoriesMaxSize) {
            historiesList = historiesList.subList(0, mHistoriesMaxSize);
        }
        historiesList.add(history);
        mJsonCacheUtil.saveCache(KEY_HISTORIES, histories);
    }

    @Override
    public void doSearch(String keyword) {
        if (mCurrentKeyword == null || mCurrentKeyword.endsWith(keyword)) {
            saveHistory(keyword);
            this.mCurrentKeyword = keyword;
        }
        if (mViewCallback != null) {
            mViewCallback.onLoading();
        }
        Call<SearchResult> task = mApi.getSearchResult(mCurrentPage, keyword);
        task.enqueue(new Callback<SearchResult>() {
            @Override
            public void onResponse(Call<SearchResult> call, Response<SearchResult> response) {
                int code = response.code();
                LogUtils.d(SearchPresenterImpl.this, "doSearch code === > " + code);
                if (code == HttpURLConnection.HTTP_OK) {
                    SearchResult result = response.body();
                    LogUtils.d(SearchPresenterImpl.this, "doSearch result === > " + result.toString());
                    handleSearchResult(result);
                } else {
                    onDoSearchError();
                }
            }

            @Override
            public void onFailure(Call<SearchResult> call, Throwable t) {
                LogUtils.d(SearchPresenterImpl.this, "doSearch error === > " + t.toString());
                onDoSearchError();
            }
        });
    }

    private void onDoSearchError() {
        if (mViewCallback != null) {
            mViewCallback.onError();
        }
    }

    private void handleSearchResult(SearchResult result) {
        if (mViewCallback != null) {
            if (isResultEmpty(result)) {
                mViewCallback.onEmpty();
            } else {
                mViewCallback.onSearchLoaded(result);
            }
        }
    }

    private boolean isResultEmpty(SearchResult result) {
        try {
            return result == null || result.getData().getTbk_dg_material_optional_response().getResult_list().getMap_data().size() == 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public void reSearch() {
        if (mCurrentKeyword == null) {
            if (mViewCallback != null) {
                mViewCallback.onEmpty();
            }
        } else {
            this.doSearch(mCurrentKeyword);
        }
    }

    @Override
    public void loaderMore() {
        mCurrentPage ++;
        doSearchMore();
    }

    private void doSearchMore() {
        Call<SearchResult> task = mApi.getSearchResult(mCurrentPage, mCurrentKeyword);
        task.enqueue(new Callback<SearchResult>() {
            @Override
            public void onResponse(Call<SearchResult> call, Response<SearchResult> response) {
                int code = response.code();
                LogUtils.d(SearchPresenterImpl.this, "doSearchMore code === > " + code);
                if (code == HttpURLConnection.HTTP_OK) {
                    SearchResult result = response.body();
                    LogUtils.d(SearchPresenterImpl.this, "doSearchMore result === > " + result.toString());
                    handleSearchMoreResult(result);
                } else {
                    onSearchMoreError();
                }
            }

            @Override
            public void onFailure(Call<SearchResult> call, Throwable t) {
                LogUtils.d(SearchPresenterImpl.this, "doSearchMore error === > " + t.toString());
                onSearchMoreError();
            }
        });
    }

    private void handleSearchMoreResult(SearchResult result) {
        if (mViewCallback != null) {
            if (isResultEmpty(result)) {
                mViewCallback.onMoreLoadedEmpty();
            } else {
                mViewCallback.onMoreLoaded(result);
            }
        }
    }

    private void onSearchMoreError() {
        mCurrentPage --;
        if (mViewCallback != null) {
            mViewCallback.onMoreLoadedError();
        }
    }

    @Override
    public void getRecommendWords() {
        Call<SearchRecommend> task = mApi.getSearchRecommend();
        task.enqueue(new Callback<SearchRecommend>() {
            @Override
            public void onResponse(Call<SearchRecommend> call, Response<SearchRecommend> response) {
                int code = response.code();
                LogUtils.d(SearchPresenterImpl.this, "getRecommendWords === > " + code);
                if (code == HttpURLConnection.HTTP_OK) {
                    SearchRecommend result = response.body();
                    LogUtils.d(SearchPresenterImpl.this, "getRecommendWords result === > " + result.toString());
                    if (mViewCallback != null) {
                        mViewCallback.onRecommendWordsLoaded(result.getData());
                    }
                }
            }

            @Override
            public void onFailure(Call<SearchRecommend> call, Throwable t) {
                LogUtils.d(SearchPresenterImpl.this, "getRecommendWords error === > " + t.toString());
            }
        });
    }

    @Override
    public void registerViewCallback(ISearchCallback callback) {
        this.mViewCallback = callback;
    }

    @Override
    public void unregisterViewCallback(ISearchCallback callback) {
        this.mViewCallback = null;
    }
}
