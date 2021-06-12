package com.chen.taobaounion.base;

public interface IBasePresenter<T> {

    /**
     * 注册UI通知接口
     * @param callback
     */
    void registerCallback(T callback);

    /**
     * 取消UI通知接口
     * @param callback
     */
    void unregisterCallback(T callback);

}
