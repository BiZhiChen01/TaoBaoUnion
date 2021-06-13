package com.chen.taobaounion.ui.adapter;

import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.chen.taobaounion.R;
import com.chen.taobaounion.model.bean.HomeCategoryContent;
import com.chen.taobaounion.utils.LogUtils;
import com.chen.taobaounion.utils.UrlUtils;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class HomePagerContentAdapter extends RecyclerView.Adapter<HomePagerContentAdapter.InnerHolder> {

    List<HomeCategoryContent.DataBean> mData = new ArrayList<>();
    private OnListItemClickListener mItemClickListener = null;

    @NonNull
    @NotNull
    @Override
    public InnerHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_home_pager_content, parent, false);
        return new InnerHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull InnerHolder holder, int position) {
        LogUtils.d(this, "onBindViewHolder === > " + position);
        HomeCategoryContent.DataBean dataBean = mData.get(position);
        holder.setData(dataBean);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mItemClickListener != null) {
                    HomeCategoryContent.DataBean item = mData.get(position);
                    mItemClickListener.onItemClick(item);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public void setData(List<HomeCategoryContent.DataBean> contents) {
        mData.clear();
        mData.addAll(contents);
        notifyDataSetChanged();
    }

    public void addLoadedData(List<HomeCategoryContent.DataBean> contents) {
        mData.addAll(contents);
        notifyItemRangeChanged(mData.size(), contents.size());
    }

    public void addRefreshData(List<HomeCategoryContent.DataBean> contents) {
        mData.addAll(0, contents);
        notifyItemRangeChanged(0, contents.size());
    }

    public class InnerHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.goods_cover)
        public ImageView cover;
        @BindView(R.id.goods_title)
        public TextView title;
        @BindView(R.id.goods_off_price)
        public TextView offPriceTv;
        @BindView(R.id.goods_after_off_price)
        public TextView afterOffPriceTv;
        @BindView(R.id.goods_original_price)
        public TextView originalPriceTv;
        @BindView(R.id.goods_sell_count)
        public TextView sellCountPriceTv;

        public InnerHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void setData(HomeCategoryContent.DataBean dataBean) {
            String finalPrice = dataBean.getZk_final_price();
            Integer couponAmount = dataBean.getCoupon_amount();
            float resultPrice = Float.parseFloat(finalPrice) - couponAmount;

            ViewGroup.LayoutParams coverLayoutParams = cover.getLayoutParams();
            int width = coverLayoutParams.width;
            int height = coverLayoutParams.height;
            int coverSize = width > height ? width : height;

            Glide.with(itemView.getContext()).load(UrlUtils.getCoverPath(dataBean.getPict_url(), coverSize)).into(cover);
            title.setText(dataBean.getTitle());
            afterOffPriceTv.setText(String.format("%.2f", resultPrice));
            offPriceTv.setText(String.format(itemView.getContext().getString(R.string.text_goods_off_price), couponAmount));
            originalPriceTv.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG);
            originalPriceTv.setText(String.format(itemView.getContext().getString(R.string.text_goods_original_price), finalPrice));
            sellCountPriceTv.setText(String.format(itemView.getContext().getString(R.string.text_goods_sell_count), dataBean.getVolume()));
        }
    }

    public void setOnListItemClickListener(OnListItemClickListener listener) {
        this.mItemClickListener = listener;
    }

    public interface OnListItemClickListener {
        void onItemClick(HomeCategoryContent.DataBean item);
    }
}
