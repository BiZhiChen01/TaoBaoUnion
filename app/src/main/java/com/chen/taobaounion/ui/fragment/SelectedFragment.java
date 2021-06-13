package com.chen.taobaounion.ui.fragment;

import com.chen.taobaounion.R;
import com.chen.taobaounion.base.BaseFragment;
import com.chen.taobaounion.model.bean.SelectedCategories;
import com.chen.taobaounion.model.bean.SelectedCategoryContent;
import com.chen.taobaounion.presenter.ISelectedPresenter;
import com.chen.taobaounion.utils.PresenterManager;
import com.chen.taobaounion.view.ISelectedCallback;

import java.util.List;

public class SelectedFragment extends BaseFragment implements ISelectedCallback {

    private ISelectedPresenter mSelectedPresenter;

    @Override
    protected void initPresenter() {
        mSelectedPresenter = PresenterManager.getInstance().getSelectedPresenter();
        mSelectedPresenter.registerViewCallback(this);
        mSelectedPresenter.getCategories();
    }

    @Override
    protected int getRootViewResId() {
        return R.layout.fragment_selected;
    }

    @Override
    public void onCategoriesLoaded(SelectedCategories categories) {
        List<SelectedCategories.DataBean> data = categories.getData();
        mSelectedPresenter.getCategoryContent(data.get(0));
    }

    @Override
    public void onContentLoaded(SelectedCategoryContent content) {

    }

    @Override
    public void onError() {

    }

    @Override
    public void onLoading() {

    }

    @Override
    public void onEmpty() {

    }

    @Override
    protected void release() {
        super.release();
        if (mSelectedPresenter != null) {
            mSelectedPresenter.unregisterViewCallback(this);
        }
    }
}
