package com.chen.taobaounion.presenter;

import com.chen.taobaounion.base.IBasePresenter;
import com.chen.taobaounion.model.bean.SelectedCategories;
import com.chen.taobaounion.view.ISelectedCallback;

public interface ISelectedPresenter extends IBasePresenter<ISelectedCallback> {

    /**
     * 获取分类
     */
    void getCategories();

    /**
     * 根据分类获取分类内容
     * @param item
     */
    void getCategoryContent(SelectedCategories.DataBean item);

    /**
     * 重新加载内容
     */
    void reloadContent();
}
