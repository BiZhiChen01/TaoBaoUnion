package com.chen.taobaounion.ui.adapter;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.chen.taobaounion.R;
import com.chen.taobaounion.model.bean.SelectedCategoryContent;
import com.chen.taobaounion.utils.Constants;
import com.chen.taobaounion.utils.UrlUtils;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SelectContentAdapter extends RecyclerView.Adapter<SelectContentAdapter.InnerHolder> {

    private List<SelectedCategoryContent.DataBean.TbkDgOptimusMaterialResponseBean.ResultListBean.MapDataBean> mData = new ArrayList<>();
    private OnSelectedContentItemClickListener mContentItemClickListener = null;

    @NonNull
    @NotNull
    @Override
    public InnerHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_selected_content, parent, false);
        return new InnerHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull SelectContentAdapter.InnerHolder holder, int position) {
        SelectedCategoryContent.DataBean.TbkDgOptimusMaterialResponseBean.ResultListBean.MapDataBean item = mData.get(position);
        holder.setData(item);
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public void setData(SelectedCategoryContent content) {
        if (content.getCode() == Constants.SUCCESS_CODE) {
            List<SelectedCategoryContent.DataBean.TbkDgOptimusMaterialResponseBean.ResultListBean.MapDataBean> data = content.getData().getTbk_dg_optimus_material_response().getResult_list().getMap_data();
            this.mData.clear();
            this.mData.addAll(data);
            notifyDataSetChanged();
        }
    }

    public class InnerHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.selected_cover)
        public ImageView cover;
        @BindView(R.id.selected_title)
        public TextView title;
        @BindView(R.id.selected_off_price)
        public TextView offPrice;
        @BindView(R.id.selected_buy_btn)
        public TextView buyBtn;
        @BindView(R.id.selected_original_price)
        public TextView originalPrice;

        public InnerHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void setData(SelectedCategoryContent.DataBean.TbkDgOptimusMaterialResponseBean.ResultListBean.MapDataBean item) {
            String coverPath = UrlUtils.getCoverPath(item.getPict_url());
            if (TextUtils.isEmpty(item.getCoupon_click_url())) {
                originalPrice.setText("晚啦，没有优惠券了");
                buyBtn.setVisibility(View.GONE);
            } else {
                originalPrice.setText("原价：" + item.getZk_final_price());
                buyBtn.setVisibility(View.VISIBLE);
            }
            if (TextUtils.isEmpty(item.getCoupon_info())) {
                offPrice.setVisibility(View.GONE);
            } else {
                offPrice.setVisibility(View.VISIBLE);
                offPrice.setText(item.getCoupon_info());
            }

            Glide.with(itemView.getContext()).load(coverPath).into(cover);
            title.setText(item.getTitle());

            buyBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mContentItemClickListener != null) {
                        mContentItemClickListener.onContentItemClick(item);
                    }
                }
            });
        }
    }

    public void setOnSelectedContentItemClickListener(OnSelectedContentItemClickListener listener) {
        this.mContentItemClickListener = listener;
    }

    public interface OnSelectedContentItemClickListener {
        void onContentItemClick(SelectedCategoryContent.DataBean.TbkDgOptimusMaterialResponseBean.ResultListBean.MapDataBean item);
    }
}
