package com.chen.taobaounion.ui.adapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.chen.taobaounion.model.bean.Categories;
import com.chen.taobaounion.ui.fragment.HomePagerFragment;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class HomePagerAdapter extends FragmentPagerAdapter {

    private List<Categories.DataBean> categoryList = new ArrayList<>();

    public HomePagerAdapter(@NonNull @NotNull FragmentManager fm) {
        super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
    }

    @NonNull
    @NotNull
    @Override
    public Fragment getItem(int position) {
        HomePagerFragment homePagerFragment = new HomePagerFragment();
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

    public void setCategories(Categories categories) {
        categoryList.clear();
        List<Categories.DataBean> data = categories.getData();
        this.categoryList.addAll(data);
        notifyDataSetChanged();
    }
}
