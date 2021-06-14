package com.chen.taobaounion.ui.fragment;

import android.graphics.Rect;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chen.taobaounion.R;
import com.chen.taobaounion.base.BaseFragment;
import com.chen.taobaounion.model.bean.SelectedCategories;
import com.chen.taobaounion.model.bean.SelectedCategoryContent;
import com.chen.taobaounion.presenter.ISelectedPresenter;
import com.chen.taobaounion.ui.adapter.SelectContentAdapter;
import com.chen.taobaounion.ui.adapter.SelectedCategoriesAdapter;
import com.chen.taobaounion.utils.LogUtils;
import com.chen.taobaounion.utils.PresenterManager;
import com.chen.taobaounion.utils.SizeUtils;
import com.chen.taobaounion.utils.TicketUtil;
import com.chen.taobaounion.view.ISelectedCallback;

import org.jetbrains.annotations.NotNull;

import butterknife.BindView;

public class SelectedFragment extends BaseFragment implements ISelectedCallback, SelectedCategoriesAdapter.OnCategoryItemClickListener, SelectContentAdapter.OnSelectedContentItemClickListener {

    private ISelectedPresenter mSelectedPresenter;

    @BindView(R.id.selected_category_list)
    public RecyclerView mCategoryList;
    @BindView(R.id.selected_content_list)
    public RecyclerView mContentList;
    private SelectedCategoriesAdapter mCategoriesAdapter;
    private SelectContentAdapter mContentAdapter;

    @Override
    protected void initView(View rootView) {
        mCategoryList.setLayoutManager(new LinearLayoutManager(getContext()));
        mCategoriesAdapter = new SelectedCategoriesAdapter();
        mCategoryList.setAdapter(mCategoriesAdapter);

        mContentList.setLayoutManager(new LinearLayoutManager(getContext()));
        mContentAdapter = new SelectContentAdapter();
        mContentList.setAdapter(mContentAdapter);
        mContentList.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(@NonNull @NotNull Rect outRect, @NonNull @NotNull View view, @NonNull @NotNull RecyclerView parent, @NonNull @NotNull RecyclerView.State state) {
                int topAndBottom = SizeUtils.dip2px(getContext(), 4);
                int leftAndRight = SizeUtils.dip2px(getContext(), 3);
                outRect.top = topAndBottom;
                outRect.bottom = topAndBottom;
                outRect.left = leftAndRight;
                outRect.right = leftAndRight;
            }
        });
    }

    @Override
    protected void initPresenter() {
        mSelectedPresenter = PresenterManager.getInstance().getSelectedPresenter();
        mSelectedPresenter.registerViewCallback(this);
        mSelectedPresenter.getCategories();
    }

    @Override
    protected void initListener() {
        mCategoriesAdapter.setOnCategoryItemClickListener(this);
        mContentAdapter.setOnSelectedContentItemClickListener(this);
    }

    @Override
    protected int getRootViewResId() {
        return R.layout.fragment_selected;
    }

    @Override
    public void onCategoriesLoaded(SelectedCategories categories) {
        setUpState(State.SUCCESS);
        mCategoriesAdapter.setData(categories);
    }

    @Override
    public void onContentLoaded(SelectedCategoryContent content) {
        mContentAdapter.setData(content);
        mContentList.scrollToPosition(0);
    }

    @Override
    public void onError() {
        setUpState(State.ERROR);
    }

    @Override
    protected void onRetryClick() {
        if (mSelectedPresenter != null) {
            mSelectedPresenter.reloadContent();
        }
    }

    @Override
    public void onLoading() {
        setUpState(State.LOADING);
    }

    @Override
    public void onEmpty() {
        setUpState(State.EMPTY);
    }

    @Override
    protected void release() {
        super.release();
        if (mSelectedPresenter != null) {
            mSelectedPresenter.unregisterViewCallback(this);
        }
    }

    @Override
    public void onCategoryItemClick(SelectedCategories.DataBean item) {
        LogUtils.d(this, "current selected item === > " + item.getFavorites_title());
        mSelectedPresenter.getCategoryContent(item);
    }

    @Override
    public void onContentItemClick(SelectedCategoryContent.DataBean.TbkDgOptimusMaterialResponseBean.ResultListBean.MapDataBean item) {
        TicketUtil.toTicketPage(getContext(), item);
    }
}
