package com.chen.taobaounion.utils;

import com.chen.taobaounion.presenter.ICategoryPagerPresenter;
import com.chen.taobaounion.presenter.IHomePresenter;
import com.chen.taobaounion.presenter.ITicketPresenter;
import com.chen.taobaounion.presenter.impl.CategoryPagePresenterImpl;
import com.chen.taobaounion.presenter.impl.HomePresenterImpl;
import com.chen.taobaounion.presenter.impl.TicketPresenterImpl;

public class PresenterManager {

    private static final PresenterManager ourInstance = new PresenterManager();
    private final ICategoryPagerPresenter mCategoryPagePresenter;
    private final IHomePresenter mHomePresenter;
    private final ITicketPresenter mTicketPresenter;

    public static PresenterManager getInstance() {
        return ourInstance;
    }

    private PresenterManager() {
        mCategoryPagePresenter = new CategoryPagePresenterImpl();
        mHomePresenter = new HomePresenterImpl();
        mTicketPresenter = new TicketPresenterImpl();
    }

    public IHomePresenter getHomePresenter() {
        return mHomePresenter;
    }

    public ICategoryPagerPresenter getCategoryPagePresenter() {
        return mCategoryPagePresenter;
    }

    public ITicketPresenter getTicketPresenter() {
        return mTicketPresenter;
    }
}
