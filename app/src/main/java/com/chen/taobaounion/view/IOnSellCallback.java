package com.chen.taobaounion.view;

import com.chen.taobaounion.base.IBaseCallback;
import com.chen.taobaounion.model.bean.OnSellContent;

public interface IOnSellCallback extends IBaseCallback {

    void onOnSellContentLoaded(OnSellContent content);

    void onMoreLoaded(OnSellContent content);

    void onMoreLoadedError();

    void onMoreLoadedEmpty();

}
