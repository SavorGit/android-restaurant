package com.savor.resturant.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.savor.resturant.R;
import com.savor.resturant.bean.CustomerLabel;

import java.util.List;
import java.util.Random;

/**
 * 标签列表适配器
 * Created by hezd on 2017/12/27.
 */

public class FlowAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    private List<CustomerLabel> list;

    public FlowAdapter(Context context) {
        this.mContext = context;
    }

    public void setData(List<CustomerLabel> list) {
        this.list = list;
        notifyDataSetChanged();
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MyHolder(View.inflate(mContext, R.layout.flow_item, null));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        TextView textView = ((MyHolder) holder).text;
        textView.setBackgroundResource(R.drawable.label_bg);
        textView.setText(list.get(position).getLabel_name());
    }

    @Override
    public int getItemCount() {
        return list == null?0:list.size();
    }

    class MyHolder extends RecyclerView.ViewHolder {

        private TextView text;

        public MyHolder(View itemView) {
            super(itemView);
            text = (TextView) itemView.findViewById(R.id.flow_text);
        }
    }
}
