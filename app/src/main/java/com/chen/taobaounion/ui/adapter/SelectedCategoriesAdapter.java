package com.chen.taobaounion.ui.adapter;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.chen.taobaounion.R;
import com.chen.taobaounion.model.bean.SelectedCategories;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class SelectedCategoriesAdapter extends RecyclerView.Adapter<SelectedCategoriesAdapter.InnerHolder> {

    private List<SelectedCategories.DataBean> mData = new ArrayList<>();

    private int mCurrentSelectedPosition = 0;
    private OnCategoryItemClickListener mItemClickListener = null;

    @NonNull
    @NotNull
    @Override
    public InnerHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_selected_categories, parent, false);
        return new InnerHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull SelectedCategoriesAdapter.InnerHolder holder, int position) {
        SelectedCategories.DataBean dataBean = mData.get(position);
        TextView itemTv = holder.itemView.findViewById(R.id.selected_category_tv);
        itemTv.setText(dataBean.getFavorites_title());

        if (mCurrentSelectedPosition == position) {
            itemTv.setBackgroundColor(itemTv.getResources().getColor(R.color.color_page_bg));
        } else {
            itemTv.setBackgroundColor(Color.WHITE);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mItemClickListener != null && mCurrentSelectedPosition != position) {
                    mCurrentSelectedPosition = position;
                    mItemClickListener.onCategoryItemClick(dataBean);
                    notifyDataSetChanged();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public void setData(SelectedCategories categories) {
        List<SelectedCategories.DataBean> data = categories.getData();
        if (data != null) {
            this.mData.clear();
            this.mData.addAll(data);
            notifyDataSetChanged();
        }
        if (mData.size() > 0) {
            mItemClickListener.onCategoryItemClick(mData.get(mCurrentSelectedPosition));
        }
    }

    public class InnerHolder extends RecyclerView.ViewHolder {
        public InnerHolder(@NonNull @NotNull View itemView) {
            super(itemView);
        }
    }

    public void setOnCategoryItemClickListener(OnCategoryItemClickListener listener) {
        this.mItemClickListener = listener;
    }

    public interface OnCategoryItemClickListener {
        void onCategoryItemClick(SelectedCategories.DataBean dataBean);
    }
}
