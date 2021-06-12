package com.chen.taobaounion.presenter;

import com.chen.taobaounion.base.IBasePresenter;
import com.chen.taobaounion.view.ICategoryPagerCallback;

public interface ICategoryPagerPresenter extends IBasePresenter<ICategoryPagerCallback> {

    /**
     * 根据分类id去获取内容
     * @param id
     */
    void getContentByCategoryId(int id);

    /**
     * 加载更多
     * @param id
     */
    void loaderMore(int id);

    /**
     * 重新加载
     * @param id
     */
    void reload(int id);

}
