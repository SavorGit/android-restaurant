package com.savor.resturant.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.savor.resturant.R;
import com.savor.resturant.activity.Recommend4ServiceActivity;
import com.savor.resturant.activity.RecommendFoodActivity;
import com.savor.resturant.bean.KeyWordBean;
import com.savor.resturant.bean.RoomInfo;
import com.savor.resturant.bean.RoomService;
import com.savor.resturant.core.Session;

import java.io.Serializable;
import java.util.List;

/**
 * 餐厅服务包间列表适配器
 * Created by hezd on 2018/1/31.
 */

public class RoomServiceAdapter extends RecyclerView.Adapter<RoomServiceAdapter.RommServiceHolder> {
    private Context mContext;
    private List<RoomService> mData;
    private OnItemClickListener onItemClickListener;
    private OnWelBtnClickListener onWelBtnClickListener;
    private OnRecommendBtnClickListener onRecommendBtnClickListener;
    private final Session session;

    /**
     * 投屏类型，欢迎词，推荐菜
     */
    public enum ProType implements Serializable {
        TYPE_WELCOM,
        TYPE_RECOMMEND,
    }

    public RoomServiceAdapter(Context context) {
        this.mContext = context;
        session = Session.get(context);
    }

    public void setData(List<RoomService> data) {
        this.mData = data;
        notifyDataSetChanged();
    }

    public List<RoomService> getData() {
        return mData;
    }

    @Override
    public RommServiceHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new RommServiceHolder(LayoutInflater.from(mContext).inflate(R.layout.item_room_service,parent,false));
    }

    @Override
    public void onBindViewHolder(final RommServiceHolder holder, int position) {
        RoomService roomService = mData.get(position);
        RoomInfo info = roomService.getRoomInfo();
        String box_name = info.getBox_name();
        boolean recommendPlay = info.isRecommendPlay();
        boolean welPlay = info.isWelPlay();
        String word = info.getWord();

        holder.tv_box_name.setText(box_name);

        holder.ll_recommend_service.setTag(position);
        holder.ll_recommend_service.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = (int) holder.ll_recommend_service.getTag();
                RoomService clickRoom = mData.get(position);
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
                RoomService clickRoom = mData.get(position);
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
                RoomService clickRoom = mData.get(position);
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
                RoomService clickRoom = mData.get(position);
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

        KeyWordBean keyWordBean = session.getKeyWordBean();
        if(welPlay) {
            if(!TextUtils.isEmpty(word)) {
                holder.tv_wel.setText(word);
            }else {
                holder.tv_wel.setText("欢迎光临，祝您用餐愉快！");
            }
        }else {
            if(keyWordBean!=null) {
                String keyWord = keyWordBean.getKeyWord();
                boolean aDefault = keyWordBean.isDefault();
                if(!aDefault) {
                    if(!TextUtils.isEmpty(word)) {
                        holder.tv_wel.setText(word);
                    }else {
                        holder.tv_wel.setText("欢迎光临，祝您用餐愉快！");
                    }
                }else {
                    if(!TextUtils.isEmpty(keyWord)) {
                        holder.tv_wel.setText(keyWord);
                        info.setTemplateId(keyWordBean.getTemplateId());
                        info.setWord(keyWord);
                    }else {
                        holder.tv_wel.setText("欢迎光临，祝您用餐愉快！");
                    }
                }

            }else {
                if(!TextUtils.isEmpty(word)) {
                    holder.tv_wel.setText(word);
                }else {
                    holder.tv_wel.setText("欢迎光临，祝您用餐愉快！");
                }
            }
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
        public TextView tv_wel;
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
            tv_wel = (TextView) itemView.findViewById(R.id.tv_wel);
        }
    }

    public interface OnItemClickListener {
        void onItemClick(RoomService roomInfo,ProType type);
    }

    public void setOnItemClickListener (OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public interface OnWelBtnClickListener {
        void onWelBtnClick(RoomService roomInfo,ProType type);
    }

    /**
     * 点击欢迎词投屏
     * @param onWelBtnClickListener
     */
    public void setOnWelBtnClickListener (OnWelBtnClickListener onWelBtnClickListener) {
        this.onWelBtnClickListener = onWelBtnClickListener;
    }

    public interface OnRecommendBtnClickListener {
        void onRecommendBtnClick(RoomService roomInfo,ProType type);
    }

    /**
     * 点击推荐菜投屏
     * @param onRecommendBtnClickListener
     */
    public void setOnRecommendBtnClickListener (OnRecommendBtnClickListener onRecommendBtnClickListener) {
        this.onRecommendBtnClickListener = onRecommendBtnClickListener;
    }
}
