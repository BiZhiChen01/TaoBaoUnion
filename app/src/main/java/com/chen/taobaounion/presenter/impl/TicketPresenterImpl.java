package com.chen.taobaounion.presenter.impl;

import com.chen.taobaounion.model.Api;
import com.chen.taobaounion.model.TicketParams;
import com.chen.taobaounion.model.bean.TicketResult;
import com.chen.taobaounion.presenter.ITicketPresenter;
import com.chen.taobaounion.utils.LogUtils;
import com.chen.taobaounion.utils.RetrofitManager;
import com.chen.taobaounion.utils.UrlUtils;
import com.chen.taobaounion.view.ITicketCallback;

import java.net.HttpURLConnection;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class TicketPresenterImpl implements ITicketPresenter {

    private String mCover;
    private TicketResult mResult;

    enum LoadState { NONE, LOADING, SUCCESS, ERROR }

    private LoadState mCurrentState = LoadState.NONE;

    private ITicketCallback mViewCallback = null;

    @Override
    public void getTicket(String title, String url, String cover) {
        this.handleOnTicketLoading();
        this.mCover = cover;
        String targetUrl = UrlUtils.getTicketUrl(url);
        Retrofit retrofit = RetrofitManager.getInstance().getRetrofit();
        Api api = retrofit.create(Api.class);
        TicketParams ticketParams = new TicketParams(title, targetUrl);
        Call<TicketResult> task = api.getTicket(ticketParams);
        task.enqueue(new Callback<TicketResult>() {
            @Override
            public void onResponse(Call<TicketResult> call, Response<TicketResult> response) {
                int code = response.code();
                LogUtils.d(TicketPresenterImpl.this, "result code === > " + code);
                if (code == HttpURLConnection.HTTP_OK) {
                    mResult = response.body();
                    LogUtils.d(TicketPresenterImpl.this, "result === > " + mResult.toString());
                    handleOnTicketLoadedSuccess();
                } else {
                    handleOnLoadedTicketError();
                }
            }

            @Override
            public void onFailure(Call<TicketResult> call, Throwable t) {
                LogUtils.e(TicketPresenterImpl.this, "error === > " + t.toString());
                handleOnLoadedTicketError();
            }
        });
    }

    public void handleOnTicketLoadedSuccess() {
        if (mViewCallback != null) {
            mViewCallback.onTicketLoaded(mCover, mResult);
        } else {
            mCurrentState = LoadState.SUCCESS;
        }
    }

    private void handleOnLoadedTicketError() {
        if (mViewCallback != null) {
            mViewCallback.onError();
        } else {
            mCurrentState = LoadState.ERROR;
        }
    }

    private void handleOnTicketLoading() {
        if (mViewCallback != null) {
            mViewCallback.onLoading();
        } else {
            mCurrentState = LoadState.LOADING;
        }
    }

    @Override
    public void registerViewCallback(ITicketCallback callback) {
        if (mCurrentState != LoadState.NONE) {
            if (mCurrentState == LoadState.SUCCESS) {
                handleOnTicketLoadedSuccess();
            } else if (mCurrentState == LoadState.ERROR) {
                handleOnLoadedTicketError();
            } else if (mCurrentState == LoadState.LOADING) {
                handleOnTicketLoading();
            }
        }
        this.mViewCallback = callback;
    }

    @Override
    public void unregisterViewCallback(ITicketCallback callback) {
        this.mViewCallback = null;
    }
}
