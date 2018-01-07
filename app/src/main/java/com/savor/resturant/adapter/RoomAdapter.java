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
import com.savor.resturant.bean.RoomListBean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by bushlee on 2017/9/7.
 */

public class RoomAdapter extends BaseAdapter {

    private Context context;
    private List<RoomListBean> list = new ArrayList<RoomListBean>();
    private LayoutInflater mInflater;
    public RoomAdapter(Context context) {
        this.context = context;
        mInflater = LayoutInflater.from(context);
    }

    public void setData(List<RoomListBean> data) {
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
        RoomAdapter.ViewHolder holder;
        if(convertView == null) {
            convertView = View.inflate(context, R.layout.room_item_layout,null);
            holder = new RoomAdapter.ViewHolder();
            holder.room_name = (TextView) convertView.findViewById(R.id.room_name);
            convertView.setTag(holder);
        }else {
            holder = (RoomAdapter.ViewHolder) convertView.getTag();
        }
        RoomListBean item = (RoomListBean) getItem(position);
        holder.room_name.setText(item.getRoom_name());


        return convertView;
    }

    public class ViewHolder {
        public TextView room_name;



    }
}
