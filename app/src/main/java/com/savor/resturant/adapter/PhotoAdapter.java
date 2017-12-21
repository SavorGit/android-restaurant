package com.savor.resturant.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.savor.resturant.R;
import com.savor.resturant.bean.PhotoInfo;
import com.savor.resturant.widget.CustomCoverView;

import java.io.File;
import java.util.List;

/**
 * Created by luminita on 2016/12/10.
 */

public class PhotoAdapter extends BaseAdapter {

    private LayoutInflater mInflate;
    private Context mContext;
    private List<PhotoInfo> mList;

    public PhotoAdapter(Context context, List<PhotoInfo> list) {
        mContext = context;
        mList = list;
        mInflate = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        if (mList != null && mList.size() != 0)
            return mList.size();
        return  0;
    }

    @Override
    public Object getItem(int i) {
        if (mList != null && mList.size() != 0)
            return mList.get(i);
        return i;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        final ViewHolder holder;
        PhotoInfo photoInfo = mList.get(i);
        if (view == null) {
            holder = new ViewHolder();
            view = mInflate.inflate(R.layout.item_photo_list, null);
            holder.photoCover = (CustomCoverView) view.findViewById(R.id.cover_img);
            holder.photoTitle = (TextView) view.findViewById(R.id.tv_title);
            holder.photoCount = (TextView) view.findViewById(R.id.tv_count);
            view.setTag(R.id.tag_holder, holder);
        } else {
            holder  = (ViewHolder) view.getTag(R.id.tag_holder);
        }
        String topImagePath = mList.get(i).getTopImagePath();
        File file = new File(topImagePath);
        if(file!=null&&file.exists()) {
            Glide.with(mContext).load(topImagePath).diskCacheStrategy(DiskCacheStrategy.NONE).centerCrop().into(holder.photoCover);
        }else {
            Glide.with(mContext).load(R.drawable.empty_slide).diskCacheStrategy(DiskCacheStrategy.NONE).centerCrop().into(holder.photoCover);
        }
        holder.photoTitle.setText(photoInfo.getFolderName());
        holder.photoCount.setText(Integer.toString(photoInfo.getImageCounts()));
        return view;
    }

    private class ViewHolder {
        public CustomCoverView photoCover;
        public TextView photoTitle;
        public TextView photoCount;
    }
}
