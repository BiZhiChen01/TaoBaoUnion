package com.chen.taobaounion.ui.activity;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.chen.taobaounion.R;
import com.chen.taobaounion.base.BaseActivity;
import com.chen.taobaounion.model.bean.TicketResult;
import com.chen.taobaounion.presenter.ITicketPresenter;
import com.chen.taobaounion.utils.LogUtils;
import com.chen.taobaounion.utils.PresenterManager;
import com.chen.taobaounion.utils.ToastUtils;
import com.chen.taobaounion.utils.UrlUtils;
import com.chen.taobaounion.view.ITicketCallback;

import butterknife.BindView;

public class TicketActivity extends BaseActivity implements ITicketCallback {

    private ITicketPresenter mTicketPresenter;

    private boolean mHasTaoBaoApp = false;

    @BindView(R.id.toolbar)
    public Toolbar mToolbar;
    @BindView(R.id.ticket_cover)
    public ImageView mCover;
    @BindView(R.id.ticket_code)
    public EditText mCodeEt;
    @BindView(R.id.ticket_copy_or_open)
    public Button mOpenOrCopyBtn;
    @BindView(R.id.ticket_cover_loading)
    public ProgressBar mCoverLoadingView;
    @BindView(R.id.ticket_load_retry)
    public TextView mCoverRetryTv;

    @Override
    protected void initPresenter() {
        mTicketPresenter = PresenterManager.getInstance().getTicketPresenter();
        mTicketPresenter.registerViewCallback(this);

        //检查手机是否有淘宝
        PackageManager packageManager = getPackageManager();
        try {
            PackageInfo packageInfo = packageManager.getPackageInfo("com.taobao.taobao", PackageManager.MATCH_UNINSTALLED_PACKAGES);
            mHasTaoBaoApp = packageInfo != null;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            mHasTaoBaoApp = false;
        }
        LogUtils.d(this, "mHasTaoBaoApp === > " + mHasTaoBaoApp);
        mOpenOrCopyBtn.setText(mHasTaoBaoApp ? "打开淘宝领券" : "复制口令");
    }

    @Override
    protected void initEvent() {
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mOpenOrCopyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //复制淘口令
                String ticketCode = mCodeEt.getText().toString().trim();
                LogUtils.d(TicketActivity.this, "ticketCode === > " + ticketCode);
                ClipboardManager clipboardManager = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clipData = ClipData.newPlainText("syla_taobao_ticket_code", ticketCode);
                clipboardManager.setPrimaryClip(clipData);

                if (mHasTaoBaoApp) {
                    Intent intent = new Intent();
                    ComponentName componentName = new ComponentName("com.taobao.taobao", "com.taobao.tao.TBMainActivity");
                    intent.setComponent(componentName);
                    startActivity(intent);
                } else {
                    ToastUtils.showToast("复制成功！粘贴分享或打开淘宝");
                }
            }
        });
    }

    @Override
    protected void initView() {

    }

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_ticket;
    }

    @Override
    public void onTicketLoaded(String cover, TicketResult result) {
        if (mCover != null && !TextUtils.isEmpty(cover)) {
            String coverPath = UrlUtils.getCoverPath(cover);
            Glide.with(this).load(coverPath).into(mCover);
        }
        if (result != null && result.getData().getTbk_tpwd_create_response() != null) {
            mCodeEt.setText(result.getData().getTbk_tpwd_create_response().getData().getModel());
        }
        if (mCoverLoadingView != null) {
            mCoverLoadingView.setVisibility(View.GONE);
        }
    }

    @Override
    public void onError() {
        if (mCoverLoadingView != null) {
            mCoverLoadingView.setVisibility(View.GONE);
        }
        if (mCoverRetryTv != null) {
            mCoverRetryTv.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onLoading() {
        if (mCoverLoadingView != null) {
            mCoverLoadingView.setVisibility(View.VISIBLE);
        }
        if (mCoverRetryTv != null) {
            mCoverRetryTv.setVisibility(View.GONE);
        }
    }

    @Override
    public void onEmpty() {

    }

    @Override
    protected void release() {
        if (mTicketPresenter != null) {
            mTicketPresenter.unregisterViewCallback(this);
        }
    }
}
