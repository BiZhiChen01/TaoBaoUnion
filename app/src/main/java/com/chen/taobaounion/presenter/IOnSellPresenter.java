package com.chen.taobaounion.presenter;

import com.chen.taobaounion.base.IBasePresenter;
import com.chen.taobaounion.view.IOnSellCallback;

public interface IOnSellPresenter extends IBasePresenter<IOnSellCallback> {

    void getOnSellContent();

    void reLoad();

    void loaderMore();
}
