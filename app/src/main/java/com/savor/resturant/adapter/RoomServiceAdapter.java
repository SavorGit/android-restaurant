package com.savor.resturant.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.savor.resturant.R;
import com.savor.resturant.activity.Recommend4ServiceActivity;
import com.savor.resturant.activity.RecommendFoodActivity;
import com.savor.resturant.bean.RoomInfo;

import java.io.Serializable;
import java.util.List;

/**
 * 餐厅服务包间列表适配器
 * Created by hezd on 2018/1/31.
 */

public class RoomServiceAdapter extends RecyclerView.Adapter<RoomServiceAdapter.RommServiceHolder> {
    private Context mContext;
    private List<RoomInfo> mData;
    private OnItemClickListener onItemClickListener;
    private OnWelBtnClickListener onWelBtnClickListener;
    private OnRecommendBtnClickListener onRecommendBtnClickListener;

    /**
     * 投屏类型，欢迎词，推荐菜
     */
    public enum ProType implements Serializable {
        TYPE_WELCOM,
        TYPE_RECOMMEND,
    }

    public RoomServiceAdapter(Context context) {
        this.mContext = context;
    }

    public void setData(List<RoomInfo> data) {
        this.mData = data;
        notifyDataSetChanged();
    }

    @Override
    public RommServiceHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new RommServiceHolder(LayoutInflater.from(mContext).inflate(R.layout.item_room_service,parent,false));
    }

    @Override
    public void onBindViewHolder(final RommServiceHolder holder, int position) {
        RoomInfo info = mData.get(position);
        String box_name = info.getBox_name();
        boolean recommendPlay = info.isRecommendPlay();
        boolean welPlay = info.isWelPlay();

        holder.tv_box_name.setText(box_name);

        holder.ll_recommend_service.setTag(position);
        holder.ll_recommend_service.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = (int) holder.ll_recommend_service.getTag();
                RoomInfo clickRoom = mData.get(position);
                if(onItemClickListener!=null) {
                    onItemClickListener.onItemClick(clickRoom,ProType.TYPE_RECOMMEND);
                }
            }
        });

        holder.ll_welcome_item.setTag(position);
        holder.ll_welcome_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = (int) holder.ll_welcome_item.getTag();
                RoomInfo clickRoom = mData.get(position);
                if(onItemClickListener!=null) {
                    onItemClickListener.onItemClick(clickRoom,ProType.TYPE_WELCOM);
                }
            }
        });

        holder.tv_welcome_pro.setTag(position);
        holder.tv_welcome_pro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = (int) holder.tv_welcome_pro.getTag();
                RoomInfo clickRoom = mData.get(position);
                if(onWelBtnClickListener!=null) {
                    onWelBtnClickListener.onWelBtnClick(clickRoom,ProType.TYPE_WELCOM);
                }
            }
        });

        holder.tv_recommend_pro.setTag(position);
        holder.tv_recommend_pro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = (int) holder.tv_recommend_pro.getTag();
                RoomInfo clickRoom = mData.get(position);
                if(onRecommendBtnClickListener!=null) {
                    onRecommendBtnClickListener.onRecommendBtnClick(clickRoom,ProType.TYPE_RECOMMEND);
                }
            }
        });

        holder.tv_recommend_playing.setVisibility(recommendPlay?View.VISIBLE:View.GONE);
        holder.tv_wel_playing.setVisibility(welPlay?View.VISIBLE:View.GONE);

        if(recommendPlay) {
            holder.tv_recommend_pro.setTextColor(mContext.getResources().getColor(R.color.white));
            holder.tv_recommend_pro.setBackgroundResource(R.drawable.bg_pro_btn);
            holder.tv_recommend_pro.setText("退出");
        }else {
            holder.tv_recommend_pro.setTextColor(mContext.getResources().getColor(R.color.colorPrimaryDark));
            holder.tv_recommend_pro.setBackgroundResource(R.drawable.bg_pro_exit_btn);
            holder.tv_recommend_pro.setText("投屏");
        }

        if(welPlay) {
            holder.tv_welcome_pro.setTextColor(mContext.getResources().getColor(R.color.white));
            holder.tv_welcome_pro.setBackgroundResource(R.drawable.bg_pro_btn);
            holder.tv_welcome_pro.setText("退出");
        }else {
            holder.tv_welcome_pro.setTextColor(mContext.getResources().getColor(R.color.colorPrimaryDark));
            holder.tv_welcome_pro.setBackgroundResource(R.drawable.bg_pro_exit_btn);
            holder.tv_welcome_pro.setText("投屏");
        }
    }

    @Override
    public int getItemCount() {
        return mData == null?0:mData.size();
    }

    public class RommServiceHolder extends RecyclerView.ViewHolder {

        public LinearLayout ll_recommend_service;
        public LinearLayout ll_welcome_item;
        public TextView tv_welcome_pro;
        public TextView tv_recommend_pro;
        public TextView tv_box_name;
        public TextView tv_wel_playing;
        public TextView tv_recommend_playing;
        public RommServiceHolder(View itemView) {
            super(itemView);
            ll_recommend_service = (LinearLayout) itemView.findViewById(R.id.ll_recommend_service);
            ll_welcome_item = (LinearLayout) itemView.findViewById(R.id.ll_welcome_item);
            tv_welcome_pro = (TextView) itemView.findViewById(R.id.tv_welcome_pro);
            tv_recommend_pro = (TextView) itemView.findViewById(R.id.tv_recommend_pro);
            tv_box_name = (TextView) itemView.findViewById(R.id.tv_box_name);
            tv_wel_playing = (TextView) itemView.findViewById(R.id.tv_wel_playing);
            tv_recommend_playing = (TextView) itemView.findViewById(R.id.tv_recommend_playing);
        }
    }

    public interface OnItemClickListener {
        void onItemClick(RoomInfo roomInfo,ProType type);
    }

    public void setOnItemClickListener (OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public interface OnWelBtnClickListener {
        void onWelBtnClick(RoomInfo roomInfo,ProType type);
    }

    /**
     * 点击欢迎词投屏
     * @param onWelBtnClickListener
     */
    public void setOnWelBtnClickListener (OnWelBtnClickListener onWelBtnClickListener) {
        this.onWelBtnClickListener = onWelBtnClickListener;
    }

    public interface OnRecommendBtnClickListener {
        void onRecommendBtnClick(RoomInfo roomInfo,ProType type);
    }

    /**
     * 点击推荐菜投屏
     * @param onRecommendBtnClickListener
     */
    public void setOnRecommendBtnClickListener (OnRecommendBtnClickListener onRecommendBtnClickListener) {
        this.onRecommendBtnClickListener = onRecommendBtnClickListener;
    }
}
