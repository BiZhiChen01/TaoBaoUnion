package com.chen.taobaounion.presenter;

import com.chen.taobaounion.base.IBasePresenter;
import com.chen.taobaounion.view.ISearchCallback;

public interface ISearchPresenter extends IBasePresenter<ISearchCallback> {

    /**
     * 获取搜索历史内容
     */
    void getHistories();

    /**
     * 删除搜索历史内容
     */
    void delHistories();

    /**
     * 搜索
     * @param keyword
     */
    void doSearch(String keyword);

    /**
     * 重新搜索
     */
    void reSearch();

    /**
     * 获取更多搜索内容
     */
    void loaderMore();

    /**
     * 获取热门推荐词
     */
    void getRecommendWords();

}
