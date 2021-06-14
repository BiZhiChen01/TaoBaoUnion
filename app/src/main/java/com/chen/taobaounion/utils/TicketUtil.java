package com.chen.taobaounion.utils;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.chen.taobaounion.model.bean.IBaseInfo;
import com.chen.taobaounion.presenter.ITicketPresenter;
import com.chen.taobaounion.ui.activity.TicketActivity;

public class TicketUtil {

    public static void toTicketPage(Context context, IBaseInfo info) {
        String title = info.getTitle();
        String url = info.getCoupon_click_url();
        if (TextUtils.isEmpty(url)) {
            url = info.getClick_url();
        }
        String cover = info.getPict_url();
        ITicketPresenter ticketPresenter = PresenterManager.getInstance().getTicketPresenter();
        ticketPresenter.getTicket(title, url, cover);
        context.startActivity(new Intent(context, TicketActivity.class));
    }
}
