package com.chen.taobaounion.view;

import com.chen.taobaounion.base.IBaseCallback;
import com.chen.taobaounion.model.bean.HomeCategoryContent;

import java.util.List;

public interface ICategoryPagerCallback extends IBaseCallback {

    void onContentLoaded(List<HomeCategoryContent.DataBean> contents);

    void onLoaderMoreLoaded(List<HomeCategoryContent.DataBean> contents);

    void onLoaderMoreError();

    void onLoaderMoreEmpty();

    void onLooperListLoaded(List<HomeCategoryContent.DataBean> contents);

    int getCategoryId();
}
