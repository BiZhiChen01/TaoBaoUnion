package com.chen.taobaounion.view;

import com.chen.taobaounion.base.IBaseCallback;
import com.chen.taobaounion.model.bean.Categories;

public interface IHomeCallback extends IBaseCallback {

    void onCategoriesLoaded(Categories categories);

}
