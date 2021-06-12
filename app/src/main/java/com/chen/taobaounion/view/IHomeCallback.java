package com.chen.taobaounion.view;

import com.chen.taobaounion.model.bean.Categories;

public interface IHomeCallback {

    void onCategoriesLoaded(Categories categories);

    void onNetworkError();

    void onLoading();

    void onEmpty();
}
