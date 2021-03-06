package com.chen.taobaounion.ui.adapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.chen.taobaounion.model.bean.HomeCategories;
import com.chen.taobaounion.ui.fragment.HomePagerFragment;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class HomePagerAdapter extends FragmentPagerAdapter {

    private List<HomeCategories.DataBean> categoryList = new ArrayList<>();

    public HomePagerAdapter(@NonNull @NotNull FragmentManager fm) {
        super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
    }

    @NonNull
    @NotNull
    @Override
    public Fragment getItem(int position) {
        HomeCategories.DataBean dataBean = categoryList.get(position);
        HomePagerFragment homePagerFragment = HomePagerFragment.newInstance(dataBean);
        return homePagerFragment;
    }

    @Override
    public int getCount() {
        return categoryList.size();
    }

    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return categoryList.get(position).getTitle();
    }

    public void setCategories(HomeCategories homeCategories) {
        categoryList.clear();
        List<HomeCategories.DataBean> data = homeCategories.getData();
        this.categoryList.addAll(data);
        notifyDataSetChanged();
    }
}
