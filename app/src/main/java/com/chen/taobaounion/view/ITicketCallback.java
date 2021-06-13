package com.chen.taobaounion.view;

import com.chen.taobaounion.base.IBaseCallback;
import com.chen.taobaounion.model.bean.TicketResult;

public interface ITicketCallback extends IBaseCallback {

    void onTicketLoaded(String cover, TicketResult result);
}
