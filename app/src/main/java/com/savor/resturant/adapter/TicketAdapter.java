package com.savor.resturant.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.savor.resturant.R;
import com.savor.resturant.activity.PhotoShowActivity;
import com.savor.resturant.bean.ConRecBean;

import java.util.List;

/**
 * 小票适配器
 * Created by hezd on 2018/1/5.
 */

public class TicketAdapter extends BaseAdapter {
    private Context mContext;
    private List<ConRecBean> mData;

    public TicketAdapter(Context context) {
        this.mContext = context;
    }

    public void setData(List<ConRecBean> data) {
        this.mData = data;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        if(mData == null || mData.size() == 0) {
            return 0;
        }else if(mData.size()%2 == 0){
            return mData.size()/2;
        }else {
            return mData.size()/2+1;
        }
    }

    @Override
    public Object getItem(int position) {
        return mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TicketHolder holder = null;
        if(convertView == null) {
            holder = new TicketHolder();
            convertView = View.inflate(mContext, R.layout.item_ticket_layout,null);
            holder.iv_left = (ImageView) convertView.findViewById(R.id.iv_left);
            holder.iv_right = (ImageView) convertView.findViewById(R.id.iv_right);
            convertView.setTag(holder);
        }else {
            holder = (TicketHolder) convertView.getTag();
        }
        String leftUrl = mData.get(position*2).getBigrecipt();
        final String  lurl = mData.get(position*2).getBigrecipt();
        Glide.with(mContext).load(leftUrl).centerCrop().placeholder(R.drawable.empty_slide).into(holder.iv_left);
        holder.iv_left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                new ShowPicDialog(context,item.getFault_img_url()).show();
                PhotoShowActivity.startPhotoShowActivity(mContext,lurl);
            }
        });

        if(position*2+1<mData.size()) {
            holder.iv_right.setVisibility(View.VISIBLE);
            String righUrl = mData.get(position*2+1).getBigrecipt();
            final String  rurl = mData.get(position*2+1).getBigrecipt();;
            Glide.with(mContext).load(righUrl).centerCrop().placeholder(R.drawable.empty_slide).into(holder.iv_right);

            holder.iv_right.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                new ShowPicDialog(context,item.getFault_img_url()).show();
                    PhotoShowActivity.startPhotoShowActivity(mContext,rurl);
                }
            });
        }else {
            holder.iv_right.setVisibility(View.INVISIBLE);
        }

        return convertView;
    }

    public class TicketHolder {
        public ImageView iv_left;
        public ImageView iv_right;
    }
}
