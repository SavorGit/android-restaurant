package com.savor.resturant.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.common.api.utils.DateUtil;
import com.savor.resturant.R;
import com.savor.resturant.bean.MediaInfo;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by luminita on 2016/12/14.
 */

public class PhotoSelectAdapter extends BaseAdapter {

    private  Context mContext;
    private List<MediaInfo> mList = new ArrayList<>();
    private  LayoutInflater mInflater;

    public PhotoSelectAdapter (Context context, List<MediaInfo> list) {
        mContext = context;
        mList = list;
        mInflater = LayoutInflater.from(context);
    }

    public PhotoSelectAdapter (Context context) {
        mContext = context;
        mInflater = LayoutInflater.from(context);
    }
    public void setData(List<MediaInfo> list) {

        if(list!=null&&list.size()>0) {
            mList.clear();
            mList.addAll(list);
            notifyDataSetChanged();
        }
    }

    @Override
    public int getCount() {
        if (mList != null && mList.size() > 0)
            return mList.size();
        return 0;
    }

    @Override
    public Object getItem(int i) {
        if (mList != null && mList.size() > 0)
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
            view = mInflater.inflate(R.layout.item_photo_select, null);
            holder.imgContent = (ImageView) view.findViewById(R.id.iv_content);
            holder.check = (CheckBox) view.findViewById(R.id.cb_check);
            holder.fullScreen = (ImageView) view.findViewById(R.id.iv_fullscreen);
            holder.tv_time = (TextView) view.findViewById(R.id.tv_time);
            //绘制图片大小
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        final MediaInfo mediaInfo = mList.get(i);
        String assetpath = mediaInfo.getAssetpath();
        File file = new File(assetpath);
        if(file!=null&&file.exists()) {
            Glide.with(mContext).
                    load(mediaInfo.getAssetpath()).
                    centerCrop().
                    placeholder(R.drawable.empty_slide)
                    .dontAnimate().
                    diskCacheStrategy(DiskCacheStrategy.NONE).
                    into(holder.imgContent);
        }else {
            Glide.with(mContext).
                    load(R.drawable.ic_deleted_hint).
                    centerCrop().
                    placeholder(R.drawable.empty_slide)
                    .dontAnimate().
                    diskCacheStrategy(DiskCacheStrategy.NONE).
                    into(holder.imgContent);
        }

        holder.check.setChecked(mediaInfo.isChecked());
        //设置照片全屏点击事件
        holder.fullScreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mOnItemFullScreenClickListener != null)
                    mOnItemFullScreenClickListener.onItemFullScreenClick(mediaInfo);
            }
        });
        int mediaType = mediaInfo.getMediaType();
        if(mediaType == MediaInfo.MEDIA_TYPE_VIDEO) {
            holder.tv_time.setVisibility(View.VISIBLE);
            String time = DateUtil.formatSecondsTime(String.valueOf(mediaInfo.getDuration()/1000));
            holder.tv_time.setText(time);
        }else {
            holder.tv_time.setVisibility(View.GONE);
        }
        return view;
    }

    private class ViewHolder {
        public CheckBox check;
        public ImageView imgContent;
        public ImageView fullScreen;
        public TextView tv_time;
    }

    private OnItemFullScreenClickListener mOnItemFullScreenClickListener = null;

    /**
     * 照片全屏回调接口
     */
    public interface OnItemFullScreenClickListener {
        void onItemFullScreenClick (MediaInfo info);
    }

    /**
     * 设置照片全屏监听事件
     * @param listener
     */
    public void  setOnItemFullScreenClickListener (OnItemFullScreenClickListener listener) {
        mOnItemFullScreenClickListener = listener;
    }
}
