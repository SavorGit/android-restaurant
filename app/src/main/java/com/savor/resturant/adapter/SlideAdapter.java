package com.savor.resturant.adapter;

import android.content.Context;
import android.net.Uri;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.RelativeLayout;
import android.widget.VideoView;

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
    public static final int MEDIA_TYPE_IMAGE = 1;
    public static final int MEDIA_TYPE_VIDEO = 2;

    private boolean isRelaseVideo;


    public SlideAdapter(Context context) {
        this.mContext = context;
    }

    public void setData(List<MediaInfo> datas) {
        this.mDatas = datas;
        notifyDataSetChanged();
    }

    public void releaseALlVideo() {
        isRelaseVideo = true;
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
    public RelativeLayout instantiateItem(ViewGroup container, int position) {
        RelativeLayout parentLayout = (RelativeLayout) View.inflate(mContext,R.layout.view_image, null);
        ImageView mImageView = (ImageView) parentLayout.findViewById(R.id.image);
        VideoView videoView = (VideoView) parentLayout.findViewById(R.id.videoview);
        if(mDatas!=null) {
            MediaInfo mediaInfo = mDatas.get(position);
            String assetpath = mediaInfo.getAssetpath();
            File file  = new File(assetpath);
            int mediaType = mediaInfo.getMediaType();
            switch (mediaType) {
                case MEDIA_TYPE_IMAGE:
                    if(file!=null&&file.exists()) {
                        Glide.with(mContext).load(mediaInfo.getAssetpath()).placeholder(R.drawable.empty_slide).dontAnimate().diskCacheStrategy(DiskCacheStrategy.NONE).into(mImageView);
                    }else {
                        Glide.with(mContext).load(R.drawable.ic_deleted_hint).placeholder(R.drawable.empty_slide).dontAnimate().diskCacheStrategy(DiskCacheStrategy.NONE).into(mImageView);
                    }
                    videoView.setVisibility(View.GONE);
                    break;
                case MEDIA_TYPE_VIDEO:
                    videoView.setVisibility(View.VISIBLE);
                    if(isRelaseVideo) {
                        videoView.pause();
                        videoView.suspend();
                        videoView.setVisibility(View.GONE);
                    }else {
                        MediaController controller = new MediaController(mContext);
                        Uri uri = Uri.parse(assetpath);
                        videoView.setMediaController(controller);
                        videoView.setVideoURI(uri);
                        videoView.seekTo(10);
                        videoView.pause();
                    }
                    break;
            }

        }
        container.addView(parentLayout, 0);
        return parentLayout;
    }

    @Override
    public void destroyItem(View container, int position, Object object) {
        super.destroyItem(container, position, object);
        VideoView videoView = (VideoView) container.findViewById(R.id.videoview);
        videoView.pause();
    }
}
