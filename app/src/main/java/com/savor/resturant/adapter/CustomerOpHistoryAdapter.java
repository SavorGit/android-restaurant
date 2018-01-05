package com.savor.resturant.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.savor.resturant.R;
import com.savor.resturant.bean.CustomerHistoryBean;
import com.savor.resturant.utils.GlideCircleTransform;

import java.util.List;

/**
 * 客户操作历史
 * Created by hezd on 2018/1/2.
 */

public class CustomerOpHistoryAdapter extends RecyclerView.Adapter<CustomerOpHistoryAdapter.HistoryHolder> {
    private Context mContext;
    private List<CustomerHistoryBean> mData;

    public CustomerOpHistoryAdapter(Context context) {
        this.mContext = context;
    }

    public void setData(List<CustomerHistoryBean> data) {
        this.mData = data;
        notifyDataSetChanged();
    }

    @Override
    public HistoryHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View parentView = LayoutInflater.from(mContext).inflate(R.layout.item_customer_history, parent, false);
        return new HistoryHolder(parentView);
    }

    @Override
    public void onBindViewHolder(HistoryHolder holder, int position) {
        CustomerHistoryBean customerHistoryBean = mData.get(position);
        String create_time = customerHistoryBean.getCreate_time();
        String face_url = customerHistoryBean.getFace_url();
        String type = customerHistoryBean.getType();
        String username = customerHistoryBean.getUsername();
        String usermobile = customerHistoryBean.getUsermobile();

        if(!TextUtils.isEmpty(face_url)) {
            holder.headerTv.setVisibility(View.GONE);
            holder.headerIv.setVisibility(View.VISIBLE);
            Glide.with(mContext).load(face_url).transform(new GlideCircleTransform(mContext)).placeholder(R.drawable.ico_header_default).into(holder.headerIv);
        }else {
            holder.headerTv.setVisibility(View.VISIBLE);
            holder.headerIv.setVisibility(View.GONE);
            if(!TextUtils.isEmpty(username)&&username.length()>0) {
                String label = String.valueOf(username.charAt(0));
                holder.headerTv.setText(label);
            }
        }

        holder.tv_num.setText(usermobile);
        holder.item_contact_title.setText(username);
        holder.tv_history_time.setText(create_time+" "+type);
    }

    @Override
    public int getItemCount() {
        return mData == null?0:mData.size();
    }

    public class HistoryHolder extends RecyclerView.ViewHolder {
        public RelativeLayout rl_hitory;
        public ImageView headerIv;
        public TextView headerTv;
        public TextView item_contact_title;
        public TextView tv_num;
        public TextView tv_history_time;

        public HistoryHolder(View itemView) {
            super(itemView);
            rl_hitory = (RelativeLayout) itemView.findViewById(R.id.rl_hitory);
            headerIv = (ImageView) itemView.findViewById(R.id.iv_header);
            headerTv = (TextView) itemView.findViewById(R.id.tv_label);
            item_contact_title = (TextView) itemView.findViewById(R.id.item_contact_title);
            tv_num = (TextView) itemView.findViewById(R.id.tv_num);
            tv_history_time = (TextView) itemView.findViewById(R.id.tv_history_time);
        }
    }

    public interface OnItemClickListener {
        void onItemClick(CustomerHistoryBean historyBean);
    }
}
