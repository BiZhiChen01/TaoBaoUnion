package com.chen.taobaounion.presenter;

import com.chen.taobaounion.view.IHomeCallback;

public interface IHomePresenter {

    /**
     * 获取商品分类
     */
    void getCategories();

    /**
     * 注册UI通知接口
     * @param callback
     */
    void registerCallback(IHomeCallback callback);

    /**
     * 取消UI通知接口
     * @param callback
     */
    void unregisterCallback(IHomeCallback callback);
}
