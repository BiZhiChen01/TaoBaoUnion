package com.chen.taobaounion.presenter;

import com.chen.taobaounion.base.IBasePresenter;
import com.chen.taobaounion.view.ITicketCallback;

public interface ITicketPresenter extends IBasePresenter<ITicketCallback> {

    /**
     * 生成淘口令
     * @param title
     * @param url
     * @param cover
     */
    void getTicket(String title, String url, String cover);
}
