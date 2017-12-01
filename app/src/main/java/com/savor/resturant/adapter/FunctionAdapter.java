package com.savor.resturant.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.savor.resturant.R;
import com.savor.resturant.bean.FunctionItem;

import java.util.List;

/**
 * 首页功能列表
 * Created by hezd on 2017/11/30.
 */

public class FunctionAdapter extends RecyclerView.Adapter<FunctionAdapter.FunctionViewHolder> {
    private Context mContext;
    private List<FunctionItem> mData;

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
    public void onBindViewHolder(FunctionViewHolder holder, int position) {
        FunctionItem functionItem = mData.get(position);
        String content = functionItem.getContent();
        int resId = functionItem.getResId();
        holder.imageView.setImageResource(resId);
        holder.textView.setText(content);
    }

    @Override
    public int getItemCount() {
        return mData == null?0:mData.size();
    }

    public class FunctionViewHolder extends RecyclerView.ViewHolder {
        public ImageView imageView;
        public TextView textView;
        public FunctionViewHolder(View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.iv_icon);
            textView = (TextView) itemView.findViewById(R.id.tv_name);
        }

    }
}
