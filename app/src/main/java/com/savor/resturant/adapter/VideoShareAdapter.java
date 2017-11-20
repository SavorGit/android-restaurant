package com.savor.resturant.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.common.api.utils.DateUtil;
import com.savor.resturant.R;
import com.savor.resturant.bean.VideoInfo;

import java.util.List;

/**
 * Created by luminita on 2016/12/12.
 */

public class VideoShareAdapter extends BaseAdapter {

    private Context mContext;
    private LayoutInflater mInflater;
    private List<VideoInfo> mList ;

    public VideoShareAdapter(Context context) {
        mContext = context;
        mInflater = LayoutInflater.from(context);
    }

    public void setData(List<VideoInfo> videoList) {
        if(videoList!=null&&videoList.size()>0) {
            mList = videoList;
            notifyDataSetChanged();
        }
    }

    @Override
    public int getCount() {
        if (mList!=null&&!mList.isEmpty())
            return mList.size();
        return 0;
    }

    @Override
    public Object getItem(int i) {
        if (mList!=null&&!mList.isEmpty())
            return mList.get(i);
        return null;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        final ViewHolder holder;
        if (view == null) {
            holder = new ViewHolder();
            view = mInflater.inflate(R.layout.item_video_list, null);
            holder.videoCover = (ImageView) view.findViewById(R.id.iv_cover);
            holder.videoTitle = (TextView) view.findViewById(R.id.tv_title);
            view.setTag(R.id.tag_holder, holder);
        } else {
            holder = (ViewHolder) view.getTag(R.id.tag_holder);
        }
        VideoInfo videoInfo = mList.get(i);
        String duration = Long.toString(videoInfo.getAssetlength()/1000);
        holder.videoTitle.setText(DateUtil.formatSecondsTime(duration));
        Glide.with(mContext).load(videoInfo.getAssetcover()).diskCacheStrategy(DiskCacheStrategy.NONE).placeholder(R.drawable.ic_video_bg).centerCrop().into(holder.videoCover);
//        ImageLoaderManager.getImageLoader().loadImage(mContext, videoInfo.getAssetcover(), holder.videoCover, R.drawable.ic_loadding2,R.drawable.ic_loadding2);
        return view;
    }

    private class ViewHolder {
        public ImageView videoCover;
        public TextView videoTitle;
    }
}
