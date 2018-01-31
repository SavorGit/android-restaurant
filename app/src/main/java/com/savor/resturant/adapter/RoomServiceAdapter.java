package com.savor.resturant.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.savor.resturant.R;

/**
 * 餐厅服务包间列表适配器
 * Created by hezd on 2018/1/31.
 */

public class RoomServiceAdapter extends RecyclerView.Adapter<RoomServiceAdapter.RommServiceHolder> {
    private Context mContext;

    public RoomServiceAdapter(Context context) {
        this.mContext = context;
    }

    @Override
    public RommServiceHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new RommServiceHolder(LayoutInflater.from(mContext).inflate(R.layout.item_room_service,parent,false));
    }

    @Override
    public void onBindViewHolder(RommServiceHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 10;
    }

    public class RommServiceHolder extends RecyclerView.ViewHolder {

        public RommServiceHolder(View itemView) {
            super(itemView);
        }
    }

}
