package com.chen.taobaounion.ui.activity;

import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.chen.taobaounion.R;
import com.chen.taobaounion.base.BaseActivity;
import com.chen.taobaounion.base.BaseFragment;
import com.chen.taobaounion.ui.fragment.HomeFragment;
import com.chen.taobaounion.ui.fragment.RedPacketFragment;
import com.chen.taobaounion.ui.fragment.SearchFragment;
import com.chen.taobaounion.ui.fragment.SelectedFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.jetbrains.annotations.NotNull;

import butterknife.BindView;

public class MainActivity extends BaseActivity {

    @BindView(R.id.main_nav_bar)
    public BottomNavigationView mNavigationView;
    private HomeFragment mHomeFragment;
    private SelectedFragment mSelectedFragment;
    private RedPacketFragment mRedPacketFragment;
    private SearchFragment mSearchFragment;
    private FragmentManager mManager;

    @Override
    protected void initView() {
        mHomeFragment = new HomeFragment();
        mSelectedFragment = new SelectedFragment();
        mRedPacketFragment = new RedPacketFragment();
        mSearchFragment = new SearchFragment();

        mManager = getSupportFragmentManager();
        switchFragment(mHomeFragment);
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_main;
    }

    @Override
    protected void initPresenter() {

    }

    @Override
    protected void initEvent() {
        mNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull @NotNull MenuItem item) {
                if (item.getItemId() == R.id.home) {
                    switchFragment(mHomeFragment);
                } else if (item.getItemId() == R.id.selected) {
                    switchFragment(mSelectedFragment);
                } else if (item.getItemId() == R.id.red_packet) {
                    switchFragment(mRedPacketFragment);
                } else if (item.getItemId() == R.id.search) {
                    switchFragment(mSearchFragment);
                }
                return true;
            }
        });
    }

    private BaseFragment lastOneFragment = null;

    private void switchFragment(BaseFragment targetFragment) {
        FragmentTransaction transaction = mManager.beginTransaction();
        if (!targetFragment.isAdded()) {
            transaction.add(R.id.main_page_content, targetFragment);
        } else {
            transaction.show(targetFragment);
        }
        if (lastOneFragment != null) {
            transaction.hide(lastOneFragment);
        }
        lastOneFragment = targetFragment;
        transaction.commit();
    }
}