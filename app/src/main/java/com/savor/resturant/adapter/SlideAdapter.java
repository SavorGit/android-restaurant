package com.savor.resturant.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.savor.resturant.R;
import com.savor.resturant.bean.MediaInfo;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by hezd on 2017/3/15.
 */

public class SlideAdapter extends PagerAdapter {
    private final Context mContext;
    private List<MediaInfo> mDatas;

    public SlideAdapter(Context context) {
        this.mContext = context;
    }

    public void setData(List<MediaInfo> datas) {
        this.mDatas = datas;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mDatas==null?0:mDatas.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View)object);
    }

    @Override
    public ImageView instantiateItem(ViewGroup container, int position) {
        ImageView imageView = (ImageView) View.inflate(mContext,R.layout.view_image, null);
        if(mDatas!=null) {
            MediaInfo mediaInfo = mDatas.get(position);
            String assetpath = mediaInfo.getAssetpath();
            File file  = new File(assetpath);
            if(file!=null&&file.exists()) {
                Glide.with(mContext).load(mediaInfo.getAssetpath()).placeholder(R.drawable.empty_slide).dontAnimate().diskCacheStrategy(DiskCacheStrategy.NONE).into(imageView);
            }else {
                Glide.with(mContext).load(R.drawable.ic_deleted_hint).placeholder(R.drawable.empty_slide).dontAnimate().diskCacheStrategy(DiskCacheStrategy.NONE).into(imageView);
            }
        }
        container.addView(imageView, 0);
        return imageView;
    }
}
