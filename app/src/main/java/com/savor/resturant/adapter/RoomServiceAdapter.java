package com.savor.resturant.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.savor.resturant.R;
import com.savor.resturant.activity.Recommend4ServiceActivity;
import com.savor.resturant.activity.RecommendFoodActivity;

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
        holder.ll_recommend_service.setTag(position);
        holder.ll_recommend_service.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, Recommend4ServiceActivity.class);
                intent.putExtra("type", RecommendFoodActivity.OperationType.TYPE_RECOMMEND_FOODS);
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return 10;
    }

    public class RommServiceHolder extends RecyclerView.ViewHolder {

        public LinearLayout ll_recommend_service;
        public RommServiceHolder(View itemView) {
            super(itemView);
            ll_recommend_service = (LinearLayout) itemView.findViewById(R.id.ll_recommend_service);
        }
    }

}
