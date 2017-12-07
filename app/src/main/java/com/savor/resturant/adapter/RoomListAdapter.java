package com.savor.resturant.adapter;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.content.res.XmlResourceParser;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.savor.resturant.R;
import com.savor.resturant.bean.RoomInfo;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.List;

/**
 * 房间列表适配器
 * Created by hezd on 2017/12/6.
 */

public class RoomListAdapter extends RecyclerView.Adapter<RoomListAdapter.RoomHolder> {

    private Context mContext;
    private List<RoomInfo> mData;
    private OnRoomItemClicklistener listener;

    public RoomListAdapter(Context context) {
        this.mContext = context;
    }

    public void setData(List<RoomInfo> data) {
        this.mData = data;
    }

    @Override
    public RoomHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_room_layout,parent,false);
        return new RoomHolder(view);
    }

    @Override
    public void onBindViewHolder(final RoomHolder holder, int position) {
        final RoomInfo roomInfo = mData.get(position);
        String box_name = roomInfo.getBox_name();
        holder.roomNameTv.setText(box_name);
        holder.roomNameTv.setTag(position);

        boolean selected = roomInfo.isSelected();
        if(selected) {
            holder.roomNameTv.setBackgroundResource(R.drawable.bg_pro_onekey_enable);
            holder.roomNameTv.setTextColor(mContext.getResources().getColor(R.color.white));
        }else {
            holder.roomNameTv.setBackgroundResource(R.drawable.room_selector);
            ColorStateList colorStateList = mContext.getResources().getColorStateList(R.color.bg_text_selector);
            holder.roomNameTv.setTextColor(colorStateList);
        }

        holder.roomNameTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reset();
                int pos = (int) holder.roomNameTv.getTag();
                RoomInfo info = mData.get(pos);
                info.setSelected(true);
                notifyDataSetChanged();
                if(listener!=null) {
                    listener.onRoomItemClick(roomInfo);
                }
            }
        });
    }

    private void reset() {
        for(RoomInfo info:mData) {
            info.setSelected(false);
        }
    }

    @Override
    public int getItemCount() {
        return mData == null? 0:mData.size();
    }

    public class RoomHolder extends RecyclerView.ViewHolder {
        public TextView roomNameTv;
        public RoomHolder(View itemView) {
            super(itemView);
            roomNameTv = (TextView) itemView.findViewById(R.id.tv_room_name);
        }
    }

    public void setOnRoomItemClickListener (OnRoomItemClicklistener listener) {
        this.listener = listener;
    }

    public interface OnRoomItemClicklistener {
        void onRoomItemClick(RoomInfo roomInfo);
    }
}
