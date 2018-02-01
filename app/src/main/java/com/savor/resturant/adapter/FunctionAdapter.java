package com.savor.resturant.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.savor.resturant.R;
import com.savor.resturant.activity.RecommendFoodActivity;
import com.savor.resturant.activity.ResturantServiceActivity;
import com.savor.resturant.activity.SlideListActivity;
import com.savor.resturant.activity.WelComeSetTextActivity;
import com.savor.resturant.bean.FunctionItem;
import com.savor.resturant.bean.HotelBean;
import com.savor.resturant.core.Session;
import com.savor.resturant.utils.SlideManager;

import java.util.List;

/**
 * 首页功能列表
 * Created by hezd on 2017/11/30.
 */

public class FunctionAdapter extends RecyclerView.Adapter<FunctionAdapter.FunctionViewHolder> {
    private Context mContext;
    private List<FunctionItem> mData;
    private OnNoHotelClickListener listener;

    public FunctionAdapter(Context context) {
        this.mContext = context;
    }

    public void setData(List<FunctionItem> data) {
        this.mData = data;
    }

    @Override
    public FunctionViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View inflate = LayoutInflater.from(mContext).inflate(R.layout.item_function_layout, parent, false);
        return new FunctionViewHolder(inflate);
    }

    @Override
    public void onBindViewHolder(final FunctionViewHolder holder, int position) {
        final FunctionItem functionItem = mData.get(position);
        String content = functionItem.getContent();
        int resId = functionItem.getResId();
        final FunctionItem.FunctionType type = functionItem.getType();

        holder.imageView.setImageResource(resId);
        holder.textView.setText(content);

        holder.ll_parent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int hotelid = Session.get(mContext).getHotelid();
                HotelBean loginResponse = Session.get(mContext).getHotelBean();
                if(loginResponse!=null) {
                    String hid = loginResponse.getHotel_id();
                    if (String.valueOf(hotelid).equals(hid)) {
                        Intent intent;
                        switch (type) {
                            case TYPE_RESTURANT_SERVICE:
                                intent = new Intent(mContext, ResturantServiceActivity.class);
                                mContext.startActivity(intent);
                                break;
                            case TYPE_RECOMMAND_FOODS:
                                intent = new Intent(mContext, RecommendFoodActivity.class);
                                intent.putExtra("type", RecommendFoodActivity.OperationType.TYPE_RECOMMEND_FOODS);
                                mContext.startActivity(intent);
                                break;
                            case TYPE_WELCOME_WORD:
                                intent = new Intent(mContext, WelComeSetTextActivity.class);
                                mContext.startActivity(intent);
                                break;
                            case TYPE_ADVERT:
                                intent = new Intent(mContext, RecommendFoodActivity.class);
                                intent.putExtra("type", RecommendFoodActivity.OperationType.TYPE_ADVERT);
                                mContext.startActivity(intent);
                                break;
                            case TYPE_VIDEO:
                                intent = new Intent(mContext,SlideListActivity.class);
                                intent.putExtra("type", SlideManager.SlideType.VIDEO);
                                mContext.startActivity(intent);
                                break;
                            case TYPE_PIC:
                                intent = new Intent(mContext,SlideListActivity.class);
                                intent.putExtra("type", SlideManager.SlideType.IMAGE);
                                mContext.startActivity(intent);
                                break;
                        }
                    }else {
                        if(listener!=null) {
                            listener.onNoHotelClick();
                        }
                    }
                }else {
                    if(listener!=null) {
                        listener.onNoHotelClick();
                    }
                }

            }
        });
    }

    @Override
    public int getItemCount() {
        return mData == null?0:mData.size();
    }

    public class FunctionViewHolder extends RecyclerView.ViewHolder {
        public ImageView imageView;
        public TextView textView;
        public LinearLayout ll_parent;
        public FunctionViewHolder(View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.iv_icon);
            textView = (TextView) itemView.findViewById(R.id.tv_name);
            ll_parent = (LinearLayout) itemView.findViewById(R.id.ll_parent);
        }

    }

    public void setOnNoHotelClickListener (OnNoHotelClickListener listener) {
        this.listener = listener;
    }

    public interface OnNoHotelClickListener {
        void onNoHotelClick();
    }
}
