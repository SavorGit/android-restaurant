package com.savor.resturant.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.savor.resturant.R;
import com.savor.resturant.bean.OrderListBean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by bushlee on 2017/9/7.
 */

public class BookAdapter extends BaseAdapter {

    private Context context;
    private List<OrderListBean> list = new ArrayList<OrderListBean>();
    private LayoutInflater mInflater;
    public BookAdapter(Context context) {
        this.context = context;
        mInflater = LayoutInflater.from(context);
    }

    public void setData(List<OrderListBean> data) {
//        if (isup) {
//            list.clear();
//        }
        this.list = data;
        notifyDataSetChanged();
    }

    public void clear() {
        this.list.clear();
        notifyDataSetChanged();
    }
    @Override
    public int getCount() {
        return list == null?20:list.size();
        //return 20;
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        BookAdapter.ViewHolder holder;
        if(convertView == null) {
            convertView = View.inflate(context, R.layout.item_booking,null);
            holder = new BookAdapter.ViewHolder();
            holder.room_name = (TextView) convertView.findViewById(R.id.room_name);
            holder.custom_num = (TextView) convertView.findViewById(R.id.custom_num);
            holder.time = (TextView) convertView.findViewById(R.id.time);
            holder.custom_name = (TextView) convertView.findViewById(R.id.custom_name);
            holder.custom_mobile = (TextView) convertView.findViewById(R.id.custom_mobile);
            holder.is_hyc = (ImageView) convertView.findViewById(R.id.is_hyc);
            holder.is_tjc = (ImageView) convertView.findViewById(R.id.is_tjc);
            holder.is_xfjl = (ImageView) convertView.findViewById(R.id.is_xfjl);

            convertView.setTag(holder);
        }else {
            holder = (BookAdapter.ViewHolder) convertView.getTag();
        }
        OrderListBean item = (OrderListBean) getItem(position);

        String moment_str = item.getMoment_str();
        if (!TextUtils.isEmpty(moment_str)) {
            holder.time.setVisibility(View.VISIBLE);
            holder.time.setText(moment_str);
        }else {
            holder.time.setVisibility(View.INVISIBLE);
        }



        if (position == 0||position%4 == 0) {
            holder.room_name.setBackgroundResource(R.drawable.corner_remote_b3);
        }else if (position%1 == 0) {
            holder.room_name.setBackgroundResource(R.drawable.corner_remote_b1);
        }else if (position%2 == 0) {
            holder.room_name.setBackgroundResource(R.drawable.corner_remote_b2);
        }else if (position%3 == 0) {
            holder.room_name.setBackgroundResource(R.drawable.corner_remote_b4);
        }else {
            holder.room_name.setBackgroundResource(R.drawable.corner_remote_b3);
        }
        String room_name = item.getRoom_name();
        if (!TextUtils.isEmpty(room_name)) {
            holder.room_name.setVisibility(View.VISIBLE);
            holder.room_name.setText(room_name);
        }else {
            holder.room_name.setVisibility(View.INVISIBLE);
        }

        String person_nums = item.getPerson_nums();
        if (!TextUtils.isEmpty(person_nums)) {
            holder.custom_num.setVisibility(View.VISIBLE);
            holder.custom_num.setText(person_nums+"人");
        }else {
            holder.custom_num.setVisibility(View.INVISIBLE);
        }

        String order_name = item.getOrder_name();
        if (!TextUtils.isEmpty(order_name)) {
            holder.custom_name.setVisibility(View.VISIBLE);
            holder.custom_name.setText(order_name);
        }else {
            holder.custom_name.setVisibility(View.INVISIBLE);
        }

        String order_mobile = item.getOrder_mobile();
        if (!TextUtils.isEmpty(order_mobile)) {
            holder.custom_mobile.setVisibility(View.VISIBLE);
            holder.custom_mobile.setText(order_mobile);
        }else {
            holder.custom_mobile.setVisibility(View.VISIBLE);
            holder.custom_mobile.setText("未输入电话");
        }

        int is_expense = item.getIs_expense();
        if (is_expense == 1) {
            holder.is_xfjl.setImageResource(R.drawable.sy_xfjl);
        }else {
            holder.is_xfjl.setImageResource(R.drawable.sy_xfjl2);
        }

        String is_welcome = item.getIs_welcome();
        if (!TextUtils.isEmpty(is_welcome)) {
            if ("1".equals(is_welcome)) {
                holder.is_hyc.setImageResource(R.drawable.sy_hyc);
            }else {
                holder.is_hyc.setImageResource(R.drawable.sy_hyc2);
            }
        }else {
            holder.is_hyc.setImageResource(R.drawable.sy_hyc2);
        }


        String is_recfood = item.getIs_recfood();
        if (!TextUtils.isEmpty(is_recfood)) {
            if ("1".equals(is_recfood)) {
                holder.is_tjc.setImageResource(R.drawable.sy_tjc);
            }else {
                holder.is_tjc.setImageResource(R.drawable.sy_tjc2);
            }
        }else {
            holder.is_tjc.setImageResource(R.drawable.sy_tjc2);
        }


        return convertView;
    }

    public class ViewHolder {
        public TextView room_name;
        public TextView time;
        public TextView custom_num;
        public TextView custom_name;
        public TextView custom_mobile;
        public ImageView is_hyc;
        public ImageView is_tjc;
        public ImageView is_xfjl;


    }
}
