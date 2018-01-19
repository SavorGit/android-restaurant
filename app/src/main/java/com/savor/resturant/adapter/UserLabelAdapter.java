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

public class UserLabelAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    private List<CustomerLabel> list;

    public UserLabelAdapter(Context context) {
        this.mContext = context;
    }

    public void setData(List<CustomerLabel> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    public List<CustomerLabel> getData() {
        return list;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MyHolder(View.inflate(mContext, R.layout.flow_item, null));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        final TextView textView = ((MyHolder) holder).text;
        CustomerLabel customerLabel = list.get(position);
        String label_name = customerLabel.getLabel_name();
        int light = customerLabel.getLight();
        if(light == 1) {
            textView.setBackgroundResource(R.drawable.label_bg_selected);
            textView.setTextColor(mContext.getResources().getColor(R.color.white));
        }else {
            textView.setBackgroundResource(R.drawable.label_bg);
            textView.setTextColor(mContext.getResources().getColor(R.color.color_label));
        }
        textView.setText(label_name);

        textView.setTag(position);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int pos = (int) textView.getTag();
                CustomerLabel clickLabel = list.get(pos);
                int clickStatus = clickLabel.getLight();
                if(clickStatus == 1) {
                    clickLabel.setLight(0);
                }else {
                    clickLabel.setLight(1);
                }
                notifyDataSetChanged();
            }
        });
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
