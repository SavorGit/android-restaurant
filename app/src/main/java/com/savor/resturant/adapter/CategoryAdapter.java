package com.savor.resturant.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.savor.resturant.R;
import com.savor.resturant.bean.CategoryItemVo;
import com.savor.resturant.utils.ConstantValues;


import java.util.ArrayList;
import java.util.List;

/**
 * Created by savor on 2016/11/10.
 */
public class CategoryAdapter extends BaseAdapter {
    private Context mContext;
    private List<CategoryItemVo> mList = new ArrayList<>();

    public CategoryAdapter(Context context,List<CategoryItemVo> mContents) {
        this.mList = mContents;
        mContext = context;
    }


    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = View.inflate(mContext,R.layout.item_category, null);
            holder.category_item_layout = (RelativeLayout) convertView.findViewById(R.id.category_item_layout);
            holder.iv_cover = (ImageView) convertView.findViewById(R.id.iv_icon);
            holder.tv_category_name = (TextView) convertView.findViewById(R.id.tv_category_name);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        CategoryItemVo categoryItemVo = mList.get(position);
        if (categoryItemVo!=null){
            if (categoryItemVo.getState()==1){
                holder.iv_cover.setVisibility(View.VISIBLE);
                holder.category_item_layout.setBackgroundColor(mContext.getResources().getColor(R.color.white));
                holder.tv_category_name.setTextColor(mContext.getResources().getColor(R.color.title_text_color));
            }else{
                holder.iv_cover.setVisibility(View.GONE);
                holder.category_item_layout.setBackgroundColor(mContext.getResources().getColor(R.color.content_bg_gray));
                holder.tv_category_name.setTextColor(Color.parseColor("#acacac"));
            }

            int id = categoryItemVo.getId();
            switch (id) {
                case ConstantValues.CATEGORY_SLIDE:
                    holder.iv_cover.setImageResource(R.drawable.ico_slide);
                    break;
                case ConstantValues.CATEGORY_VIDEO:
                    holder.iv_cover.setImageResource(R.drawable.ico_slide_video);
                    break;
            }

            holder.tv_category_name.setText(categoryItemVo.getName());
        }
        return convertView;
    }

    private class ViewHolder {
        public RelativeLayout category_item_layout;
        public ImageView iv_cover;
        public TextView tv_category_name;
    }
}
