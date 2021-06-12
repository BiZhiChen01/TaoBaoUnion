package com.chen.taobaounion.presenter;

import com.chen.taobaounion.base.IBasePresenter;
import com.chen.taobaounion.view.IHomeCallback;

public interface IHomePresenter extends IBasePresenter<IHomeCallback> {

    /**
     * 获取商品分类
     */
    void getCategories();

}
