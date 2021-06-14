package com.chen.taobaounion.view;

import com.chen.taobaounion.base.IBaseCallback;
import com.chen.taobaounion.model.bean.SearchRecommend;
import com.chen.taobaounion.model.bean.SearchResult;

import java.util.List;

public interface ISearchCallback extends IBaseCallback {

    void onHistoriesLoaded(List<String> histories);

    void onHistoriesDeleted();

    void onSearchLoaded(SearchResult result);

    void onMoreLoaded(SearchResult result);

    void onMoreLoadedError();

    void onMoreLoadedEmpty();

    void onRecommendWordsLoaded(List<SearchRecommend.DataBean> words);

}
