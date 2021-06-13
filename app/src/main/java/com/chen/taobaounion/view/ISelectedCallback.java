package com.chen.taobaounion.view;

import com.chen.taobaounion.base.IBaseCallback;
import com.chen.taobaounion.model.bean.SelectedCategories;
import com.chen.taobaounion.model.bean.SelectedCategoryContent;

public interface ISelectedCallback extends IBaseCallback {

    void onCategoriesLoaded(SelectedCategories categories);

    void onContentLoaded(SelectedCategoryContent content);
}
