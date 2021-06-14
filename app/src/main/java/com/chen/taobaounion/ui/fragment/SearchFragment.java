package com.chen.taobaounion.ui.fragment;

import android.graphics.Rect;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chen.taobaounion.R;
import com.chen.taobaounion.base.BaseFragment;
import com.chen.taobaounion.model.bean.Histories;
import com.chen.taobaounion.model.bean.SearchRecommend;
import com.chen.taobaounion.model.bean.SearchResult;
import com.chen.taobaounion.presenter.ISearchPresenter;
import com.chen.taobaounion.ui.adapter.HomePagerContentAdapter;
import com.chen.taobaounion.ui.custom.FlowTextLayout;
import com.chen.taobaounion.utils.LogUtils;
import com.chen.taobaounion.utils.PresenterManager;
import com.chen.taobaounion.utils.SizeUtils;
import com.chen.taobaounion.view.ISearchCallback;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

public class SearchFragment extends BaseFragment implements ISearchCallback {

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
    private HomePagerContentAdapter mSearchResultAdapter;

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
        mSearchResultAdapter = new HomePagerContentAdapter();
        mSearchList.setAdapter(mSearchResultAdapter);
        mSearchList.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(@NonNull @NotNull Rect outRect, @NonNull @NotNull View view, @NonNull @NotNull RecyclerView parent, @NonNull @NotNull RecyclerView.State state) {
                int topAndBottom = SizeUtils.dip2px(getContext(), 4);
                outRect.top = topAndBottom;
                outRect.bottom = topAndBottom;
            }
        });
    }

    @Override
    protected void initPresenter() {
        mSearchPresenter = PresenterManager.getInstance().getSearchPresenter();
        mSearchPresenter.registerViewCallback(this);
        mSearchPresenter.getRecommendWords();
        mSearchPresenter.doSearch("毛衣");
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
        mSearchResultAdapter.setData(result.getData().getTbk_dg_material_optional_response().getResult_list().getMap_data());
    }

    @Override
    public void onMoreLoaded(SearchResult result) {

    }

    @Override
    public void onMoreLoadedError() {

    }

    @Override
    public void onMoreLoadedEmpty() {

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
        if (mSearchPresenter != null) {
            mSearchPresenter.unregisterViewCallback(this);
        }
    }
}
