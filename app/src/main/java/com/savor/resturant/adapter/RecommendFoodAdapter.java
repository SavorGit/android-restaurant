package com.savor.resturant.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.target.ImageViewTarget;
import com.common.api.utils.DensityUtil;
import com.savor.resturant.R;
import com.savor.resturant.bean.RecommendFood;
import com.savor.resturant.widget.MyBitmapImageViewTarget;

import java.util.List;

/**
 * 推荐菜适配器
 * Created by hezd on 2017/12/4.
 */

public class RecommendFoodAdapter extends RecyclerView.Adapter<RecommendFoodAdapter.RecommendHolder> {
    private final Context mContext;
    private List<RecommendFood> mData;
    private static final double IMAGE_SCALE = 0.7164179104477612;
    private OnSingleProBtnClickListener onSingleProBtnClickListener;
    private OnCheckStateChangeListener onCheckStateChangeListener;

    public RecommendFoodAdapter(Context context) {
        this.mContext = context;
    }

    public void setData(List<RecommendFood> data) {
        this.mData = data;
        notifyDataSetChanged();
    }

    public List<RecommendFood> getData() {
        return mData;
    }

    @Override
    public RecommendHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_recommend_food,parent,false);
        ImageView imageView = (ImageView) view.findViewById(R.id.iv_image);
        int width = (DensityUtil.getScreenWidth(mContext)-DensityUtil.dip2px(mContext,15*3))/2;
        int height = (int) (width*IMAGE_SCALE);

        ViewGroup.LayoutParams layoutParams = imageView.getLayoutParams();
        layoutParams.width = width;
        layoutParams.height = height;
        RecommendHolder holder = new RecommendHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(final RecommendHolder holder, int position) {
        final RecommendFood recommendFood = mData.get(position);
        final String oss_path = recommendFood.getOss_path();
        String chinese_name = recommendFood.getChinese_name();

        Glide.with(mContext).load(oss_path).asBitmap().dontAnimate().centerCrop().
                placeholder(R.drawable.empty_slide).
                into(new MyBitmapImageViewTarget(holder.imageView));

        holder.foodNameTv.setText(chinese_name);


        holder.proBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(onSingleProBtnClickListener!=null) {
                    onSingleProBtnClickListener.onSingleProBtnClick(recommendFood);
                }
            }
        });

        holder.cb_select.setTag(position);
        holder.cb_select.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                int position = (int) holder.cb_select.getTag();
                mData.get(position).setSelected(isChecked);

                if(onCheckStateChangeListener!=null) {
                    onCheckStateChangeListener.onCheckStateChange();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mData == null? 0:mData.size();
    }

    public class RecommendHolder extends RecyclerView.ViewHolder {
        public ImageView imageView;
        public TextView foodNameTv;
        public TextView proBtn;
        public CheckBox cb_select;
        public RecommendHolder(View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.iv_image);
            foodNameTv = (TextView) itemView.findViewById(R.id.tv_food_name);
            proBtn = (TextView) itemView.findViewById(R.id.tv_pro_single);
            cb_select = (CheckBox) itemView.findViewById(R.id.cb_select);
        }
    }

    public void setOnSingleProBtnClickListener(OnSingleProBtnClickListener onSingleProBtnClickListener) {
        this.onSingleProBtnClickListener = onSingleProBtnClickListener;
    }

    public interface OnSingleProBtnClickListener {
        void onSingleProBtnClick(RecommendFood recommendFood);
    }

    public void setOnCheckStateChangeListener(OnCheckStateChangeListener onCheckStateChangeListener) {
        this.onCheckStateChangeListener = onCheckStateChangeListener;
    }

    public interface OnCheckStateChangeListener {
        void onCheckStateChange();
    }
}
