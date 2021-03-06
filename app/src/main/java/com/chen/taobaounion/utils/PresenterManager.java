package com.chen.taobaounion.utils;

import com.chen.taobaounion.presenter.ICategoryPagerPresenter;
import com.chen.taobaounion.presenter.IHomePresenter;
import com.chen.taobaounion.presenter.IOnSellPresenter;
import com.chen.taobaounion.presenter.ISearchPresenter;
import com.chen.taobaounion.presenter.ISelectedPresenter;
import com.chen.taobaounion.presenter.ITicketPresenter;
import com.chen.taobaounion.presenter.impl.CategoryPagePresenterImpl;
import com.chen.taobaounion.presenter.impl.HomePresenterImpl;
import com.chen.taobaounion.presenter.impl.OnSellPresenterImpl;
import com.chen.taobaounion.presenter.impl.SearchPresenterImpl;
import com.chen.taobaounion.presenter.impl.SelectedPresenterImpl;
import com.chen.taobaounion.presenter.impl.TicketPresenterImpl;

public class PresenterManager {

    private static final PresenterManager ourInstance = new PresenterManager();
    private final ICategoryPagerPresenter mCategoryPagePresenter;
    private final IHomePresenter mHomePresenter;
    private final ITicketPresenter mTicketPresenter;
    private final ISelectedPresenter mSelectedPresenter;
    private final IOnSellPresenter mOnSellPresenter;
    private final ISearchPresenter mSearchPresenter;

    public static PresenterManager getInstance() {
        return ourInstance;
    }

    private PresenterManager() {
        mCategoryPagePresenter = new CategoryPagePresenterImpl();
        mHomePresenter = new HomePresenterImpl();
        mTicketPresenter = new TicketPresenterImpl();
        mSelectedPresenter = new SelectedPresenterImpl();
        mOnSellPresenter = new OnSellPresenterImpl();
        mSearchPresenter = new SearchPresenterImpl();
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

    public ISelectedPresenter getSelectedPresenter() {
        return mSelectedPresenter;
    }

    public IOnSellPresenter getOnSellPresenter() {
        return mOnSellPresenter;
    }

    public ISearchPresenter getSearchPresenter() {
        return mSearchPresenter;
    }
}
