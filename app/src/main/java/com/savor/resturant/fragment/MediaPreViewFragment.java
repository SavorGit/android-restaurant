package com.savor.resturant.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.savor.resturant.R;
import com.savor.resturant.bean.MediaInfo;
import com.universalvideoview.UniversalMediaController;
import com.universalvideoview.UniversalVideoView;

import java.io.File;

/**
 * 图片，视频预览
 * @author hezd
 * create an instance of this fragment.
 */
public class MediaPreViewFragment extends BaseFragment {
    private static final String PARAMS_MEDIA = "param_media";
    public static final int MEDIA_TYPE_IMAGE = 1;
    public static final int MEDIA_TYPE_VIDEO = 2;
    private MediaInfo mMediaInfo;
    private View mParentLayout;
    private ImageView mImageView;
    private UniversalVideoView videoView;
    private UniversalMediaController mMediaController;
    private int mSeekPosition;

    public MediaPreViewFragment() {
        // Required empty public constructor
    }

    /**
     *创建实例
     * @param mediaInfo Parameter 1.
     * @return A new instance of fragment MediaPreViewFragment.
     */
    public static MediaPreViewFragment newInstance(MediaInfo mediaInfo) {
        MediaPreViewFragment fragment = new MediaPreViewFragment();
        Bundle args = new Bundle();
        args.putSerializable(PARAMS_MEDIA, mediaInfo);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mMediaInfo = (MediaInfo) getArguments().getSerializable(PARAMS_MEDIA);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mParentLayout = inflater.inflate(R.layout.fragment_media, container, false);
        initViews(mParentLayout);
        setViews();
        setListeners();
        return mParentLayout;
    }

    @Override
    public void onResume() {
        super.onResume();
        videoView.seekTo(mSeekPosition);
        videoView.resume();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (videoView != null && videoView.isPlaying()) {
            mSeekPosition = videoView.getCurrentPosition();
            videoView.pause();
        }
    }

    private void initViews(View parent) {
        mImageView = (ImageView) parent.findViewById(R.id.image);
        videoView = (UniversalVideoView) parent.findViewById(R.id.videoview);
        mMediaController = (UniversalMediaController) parent.findViewById(R.id.media_controller);
    }

    @Override
    public String getFragmentName() {
        return this.getClass().getSimpleName();
    }



    @Override
    public void setViews() {
        if(mMediaInfo!=null) {
            int mediaType = mMediaInfo.getMediaType();
            final String assetpath = mMediaInfo.getAssetpath();
            switch (mediaType) {
                case MEDIA_TYPE_IMAGE:
                    videoView.setVisibility(View.GONE);
                    mMediaController.setVisibility(View.GONE);
                    File file = new File(assetpath);
                    if (file.exists()) {
                        Glide.with(this).load(assetpath).centerCrop().placeholder(R.drawable.empty_slide).into(mImageView);
                    } else {
                        Glide.with(this).load(assetpath).centerCrop().placeholder(R.drawable.ic_deleted_hint).into(mImageView);
                    }
                    break;
                case MEDIA_TYPE_VIDEO:
                    videoView.setVisibility(View.VISIBLE);
                    mMediaController.setVisibility(View.VISIBLE);
                    videoView.post(new Runnable() {
                        @Override
                        public void run() {
                            videoView.setVideoPath(assetpath);
                            videoView.pause();
                        }
                    });

                    break;
            }
        }

    }

    @Override
    public void setListeners() {

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(videoView!=null) {
            videoView.closePlayer();
            videoView = null;
        }

        if(mMediaController!=null) {
            mMediaController.hide();
            mMediaController = null;
        }
    }
}
