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
import com.chen.taobaounion.model.bean.OnSellContent;
import com.chen.taobaounion.utils.UrlUtils;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class OnSellContentAdapter extends RecyclerView.Adapter<OnSellContentAdapter.InnerHolder> {

    private List<OnSellContent.DataBean.TbkDgOptimusMaterialResponseBean.ResultListBean.MapDataBean> mData = new ArrayList();
    private OnSellItemClickListener mContentItemClickListener = null;

    @NonNull
    @NotNull
    @Override
    public InnerHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_on_sell_content, parent, false);
        return new InnerHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull OnSellContentAdapter.InnerHolder holder, int position) {
        OnSellContent.DataBean.TbkDgOptimusMaterialResponseBean.ResultListBean.MapDataBean item = mData.get(position);
        holder.setData(item);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mContentItemClickListener != null) {
                    mContentItemClickListener.onSellItemClick(item);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public void setData(OnSellContent content) {
        mData.clear();
        mData.addAll(content.getData().getTbk_dg_optimus_material_response().getResult_list().getMap_data());
        notifyDataSetChanged();
    }

    public void setMoreData(OnSellContent content) {
        List<OnSellContent.DataBean.TbkDgOptimusMaterialResponseBean.ResultListBean.MapDataBean> moreData = content.getData().getTbk_dg_optimus_material_response().getResult_list().getMap_data();
        mData.addAll(moreData);
        notifyItemRangeChanged(mData.size() - 1, moreData.size());
    }

    public class InnerHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.on_sell_cover)
        public ImageView cover;
        @BindView(R.id.on_sell_title)
        public TextView title;
        @BindView(R.id.on_sell_origin_price_tv)
        public TextView originPrice;
        @BindView(R.id.on_sell_off_price_tv)
        public TextView offPrice;

        public InnerHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void setData(OnSellContent.DataBean.TbkDgOptimusMaterialResponseBean.ResultListBean.MapDataBean item) {
            String originalPrice = item.getZk_final_price();
            int couponAmount = item.getCoupon_amount();
            float originalPriceFloat = Float.parseFloat(originalPrice);
            float finalPrice = originalPriceFloat = couponAmount;

            Glide.with(itemView.getContext()).load(UrlUtils.getCoverPath(item.getPict_url())).into(cover);
            title.setText(item.getTitle());
            originPrice.setText("￥" + originalPrice);
            offPrice.setText("券后价：" + String.format("%.2f",finalPrice));

            originPrice.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG);
        }
    }

    public void setOnSellItemClickListener(OnSellItemClickListener listener) {
        this.mContentItemClickListener = listener;
    }

    public interface OnSellItemClickListener {
        void onSellItemClick(OnSellContent.DataBean.TbkDgOptimusMaterialResponseBean.ResultListBean.MapDataBean item);
    }
}
