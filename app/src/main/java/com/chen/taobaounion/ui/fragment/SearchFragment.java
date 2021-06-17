package com.chen.taobaounion.ui.fragment;

import android.graphics.Rect;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chen.taobaounion.R;
import com.chen.taobaounion.base.BaseFragment;
import com.chen.taobaounion.model.bean.Histories;
import com.chen.taobaounion.model.bean.SearchRecommend;
import com.chen.taobaounion.model.bean.SearchResult;
import com.chen.taobaounion.presenter.ISearchPresenter;
import com.chen.taobaounion.ui.adapter.HomeAndSearchContentAdapter;
import com.chen.taobaounion.ui.adapter.IHomeAndSearchGoodsItemInfo;
import com.chen.taobaounion.ui.custom.FlowTextLayout;
import com.chen.taobaounion.utils.KeyboardUtil;
import com.chen.taobaounion.utils.LogUtils;
import com.chen.taobaounion.utils.PresenterManager;
import com.chen.taobaounion.utils.SizeUtils;
import com.chen.taobaounion.utils.TicketUtil;
import com.chen.taobaounion.utils.ToastUtils;
import com.chen.taobaounion.view.ISearchCallback;
import com.lcodecore.tkrefreshlayout.RefreshListenerAdapter;
import com.lcodecore.tkrefreshlayout.TwinklingRefreshLayout;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

public class SearchFragment extends BaseFragment implements ISearchCallback, HomeAndSearchContentAdapter.OnListItemClickListener, FlowTextLayout.OnFlowTextItemClickListener {

    private ISearchPresenter mSearchPresenter;

    @BindView(R.id.search_histories)
    public FlowTextLayout mHistoriesView;
    @BindView(R.id.search_recommend)
    public FlowTextLayout mRecommendView;
    @BindView(R.id.search_history_container)
    public LinearLayout mHistoryContainer;
    @BindView(R.id.search_recommend_container)
    public LinearLayout mRecommendContainer;
    @BindView(R.id.search_history_delete)
    public ImageView mHistoryDelete;
    @BindView(R.id.search_result_list)
    public RecyclerView mSearchList;
    @BindView(R.id.search_result_refresh)
    public TwinklingRefreshLayout mRefreshLayout;
    @BindView(R.id.search_btn)
    public TextView mSearchBtn;
    @BindView(R.id.search_clean_btn)
    public ImageView mCleanInputBtn;
    @BindView(R.id.search_edit)
    public EditText mSearchInputBox;

    private HomeAndSearchContentAdapter mSearchResultAdapter;

    @Override
    protected int getRootViewResId() {
        return R.layout.fragment_search;
    }

    @Override
    protected View loadRootView(LayoutInflater inflater, ViewGroup container) {
        return inflater.inflate(R.layout.fragment_search_layout, container, false);
    }

    @Override
    protected void initView(View rootView) {
        mSearchList.setLayoutManager(new LinearLayoutManager(getContext()));
        mSearchResultAdapter = new HomeAndSearchContentAdapter();
        mSearchList.setAdapter(mSearchResultAdapter);
        mSearchList.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(@NonNull @NotNull Rect outRect, @NonNull @NotNull View view, @NonNull @NotNull RecyclerView parent, @NonNull @NotNull RecyclerView.State state) {
                int topAndBottom = SizeUtils.dip2px(getContext(), 1.5f);
                outRect.top = topAndBottom;
                outRect.bottom = topAndBottom;
            }
        });

        mRefreshLayout.setEnableLoadmore(true);
        mRefreshLayout.setEnableRefresh(false);
        mRefreshLayout.setEnableOverScroll(true);
    }

    @Override
    protected void initPresenter() {
        mSearchPresenter = PresenterManager.getInstance().getSearchPresenter();
        mSearchPresenter.registerViewCallback(this);
        mSearchPresenter.getRecommendWords();
        mSearchPresenter.getHistories();
    }

    @Override
    protected void initListener() {
        mHistoryDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSearchPresenter.delHistories();
            }
        });

        mRefreshLayout.setOnRefreshListener(new RefreshListenerAdapter() {
            @Override
            public void onLoadMore(TwinklingRefreshLayout refreshLayout) {
                if (mSearchPresenter != null) {
                    mSearchPresenter.loaderMore();
                }
            }
        });

        mSearchResultAdapter.setOnListItemClickListener(this);

        mSearchInputBox.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH && mSearchPresenter != null) {
                    //发起请求
                    String keyword = v.getText().toString().trim();
                    if (TextUtils.isEmpty(keyword)) {
                        return false;
                    }
                    mSearchPresenter.doSearch(keyword);
                }
                return false;
            }
        });

        mSearchInputBox.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                mCleanInputBtn.setVisibility(hasInput(true) ? View.VISIBLE : View.GONE);
                mSearchBtn.setText(hasInput(false) ? "搜索" : "取消");
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        mSearchInputBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSearchInputBox.setText("");
                switch2HistoryPage();
            }
        });

        mSearchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (hasInput(false)) {
                    if (mSearchPresenter != null) {
                        //mSearchPresenter.doSearch(mSearchInputBox.getText().toString().trim());
                        toSearch(mSearchInputBox.getText().toString().trim());
                        KeyboardUtil.hide(getContext(), v);
                    }
                } else {
                    KeyboardUtil.hide(getContext(), v);
                }
            }
        });

        mHistoriesView.setOnFlowTextItemClickListener(this);
        mRecommendView.setOnFlowTextItemClickListener(this);
    }

    /**
     * 切换到历史和推荐界面
     */
    private void switch2HistoryPage() {
        if (mSearchPresenter != null) {
            mSearchPresenter.getHistories();
        }
        mHistoryContainer.setVisibility(mHistoriesView.getContentSize() != 0 ? View.VISIBLE : View.GONE);
        mRecommendContainer.setVisibility(mRecommendView.getContentSize() != 0 ? View.VISIBLE : View.GONE);
        mRefreshLayout.setVisibility(View.GONE);
    }

    private boolean hasInput(boolean containerSpace) {
        if (containerSpace) {
            return mSearchInputBox.getText().toString().trim().length() > 0;
        } else {
            return mSearchInputBox.getText().toString().length() > 0;
        }
    }

    @Override
    public void onHistoriesLoaded(Histories histories) {
        setUpState(State.SUCCESS);
        LogUtils.d(this, "onHistoriesLoaded  === > " + histories);
        if (histories == null || histories.getHistories().size() == 0) {
            mHistoryContainer.setVisibility(View.GONE);
        } else {
            mHistoryContainer.setVisibility(View.VISIBLE);
            mHistoriesView.setTextList(histories.getHistories());
        }
    }

    @Override
    public void onHistoriesDeleted() {
        if (mSearchPresenter != null) {
            mSearchPresenter.getHistories();
        }
    }

    @Override
    public void onSearchLoaded(SearchResult result) {
        setUpState(State.SUCCESS);
        mHistoryContainer.setVisibility(View.GONE);
        mRecommendContainer.setVisibility(View.GONE);
        try {
            mSearchResultAdapter.setData(result.getData().getTbk_dg_material_optional_response().getResult_list().getMap_data());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onMoreLoaded(SearchResult result) {
        List<SearchResult.DataBean.TbkDgMaterialOptionalResponseBean.ResultListBean.MapDataBean> moreData = result.getData().getTbk_dg_material_optional_response().getResult_list().getMap_data();
        mSearchResultAdapter.addLoadedData(moreData);
        mRefreshLayout.finishLoadmore();
    }

    @Override
    public void onMoreLoadedError() {
        mRefreshLayout.finishLoadmore();
        ToastUtils.showToast("网络异常！请稍后重视...");
    }

    @Override
    public void onMoreLoadedEmpty() {
        mRefreshLayout.finishLoadmore();
        ToastUtils.showToast("没有更多数据...");
    }

    @Override
    public void onRecommendWordsLoaded(List<SearchRecommend.DataBean> words) {
        List<String> recommendWords = new ArrayList<>();
        for (SearchRecommend.DataBean word : words) {
            recommendWords.add(word.getKeyword());
        }
        if (words == null || words.size() == 0) {
            mRecommendContainer.setVisibility(View.GONE);
        } else {
            mRecommendContainer.setVisibility(View.VISIBLE);
            mRecommendView.setTextList(recommendWords);
        }
    }

    @Override
    public void onError() {
        setUpState(State.ERROR);
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
    protected void onRetryClick() {
        if (mSearchPresenter != null) {
            mSearchPresenter.reSearch();
        }
    }

    @Override
    protected void release() {
        super.release();
        if (mSearchPresenter != null) {
            mSearchPresenter.unregisterViewCallback(this);
        }
    }

    @Override
    public void onItemClick(IHomeAndSearchGoodsItemInfo item) {
        TicketUtil.toTicketPage(getContext(),item);
    }

    @Override
    public void onFlowItemClick(String text) {
        toSearch(text);
    }

    public void toSearch(String text) {
        if (mSearchPresenter != null) {
            mSearchList.scrollToPosition(0);
            mSearchInputBox.setText(text);
            mSearchInputBox.setFocusable(true);
            mSearchInputBox.requestFocus();
            mSearchInputBox.setSelection(text.length());
            mSearchInputBox.setSelection(text.length(), text.length());
            mSearchPresenter.doSearch(text);
        }
    }
}
