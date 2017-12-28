package com.savor.resturant.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.savor.resturant.R;
import com.savor.resturant.bean.CustomerLabel;

import java.util.List;

/**
 * 标签列表适配器
 * Created by hezd on 2017/12/27.
 */

public class SelectLabelAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    private List<CustomerLabel> list;

    public SelectLabelAdapter(Context context) {
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
        CustomerLabel customerLabel = list.get(position);
        int light = customerLabel.getLight();
        TextView textView = ((MyHolder) holder).text;
        if(light == 1) {
            textView.setTextColor(mContext.getResources().getColor(R.color.white));
            textView.setBackgroundResource(R.drawable.label_bg_selected);
        }else {
            textView.setTextColor(mContext.getResources().getColor(R.color.black));
            textView.setBackgroundResource(R.drawable.label_bg);
        }
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
