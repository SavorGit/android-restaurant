package com.savor.resturant.activity;

import android.graphics.PixelFormat;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.savor.resturant.R;
import com.savor.resturant.adapter.MediaPreviewAdapter;
import com.savor.resturant.adapter.SlideAdapter;
import com.savor.resturant.bean.MediaInfo;
import com.savor.resturant.bean.SlideSetInfo;
import com.savor.resturant.fragment.MediaPreViewFragment;
import com.savor.resturant.utils.SlideManager;
import com.savor.resturant.widget.LoopViewPager;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import cn.jzvd.JZVideoPlayer;

/**
 * Created by Administrator on 2017/3/17.
 */

public class SlidePreviewActivity extends BaseFragmentActivity implements View.OnClickListener,ViewPager.OnPageChangeListener{

    private LinearLayout backLayout;
    private ViewPager viewpager;

    private SlideAdapter previewAdapter;
    private List<MediaInfo> images = new LinkedList<>();
    private List<Fragment> fragments = new ArrayList<>();
    private SlideSetInfo slideSetInfo;
    private SlideManager.SlideType slideType;
    private TextView mTitleTv;
    private int position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFormat(PixelFormat.TRANSLUCENT);
        setContentView(R.layout.activity_slide_preview);

        handleIntent();
        getViews();
        setViews();
        setListeners();
    }

    private void handleIntent() {
        slideType = (SlideManager.SlideType) getIntent().getSerializableExtra("type");
        position = getIntent().getIntExtra("position",0);
    }

    @Override
    public void getViews() {
        mTitleTv = (TextView) findViewById(R.id.category_name);

        backLayout = (LinearLayout) findViewById(R.id.back);
        viewpager = (ViewPager) findViewById(R.id.viewpager);

        slideSetInfo = (SlideSetInfo) getIntent().getSerializableExtra("photos");
        if (slideSetInfo!=null&&slideSetInfo.imageList!=null&&slideSetInfo.imageList.size()>0){
            images.clear();
            images = slideSetInfo.imageList;
//            MediaUtils.getFolderAllNames(mContext, images, slideSetInfo.imageList);
        }
    }

    @Override
    public void setViews() {

        switch (slideType) {
            case VIDEO:
                mTitleTv.setText("我的视频");
                break;
            case IMAGE:
                mTitleTv.setText("我的图片");
                break;
        }

        for(MediaInfo info:images) {
            Fragment fragment = MediaPreViewFragment.newInstance(info);
            fragments.add(fragment);
        }
        MediaPreviewAdapter mMediaPreviewAdapter = new MediaPreviewAdapter(getSupportFragmentManager());
        viewpager.setAdapter(mMediaPreviewAdapter);
        mMediaPreviewAdapter.setData(fragments);
        viewpager.setCurrentItem(position);
//        previewAdapter = new SlideAdapter(SlidePreviewActivity.this);
//        previewAdapter.setData(images);
//        viewpager.setAdapter(previewAdapter);
//        viewpager.setCurrentItem(position);
//        viewpager.setOffscreenPageLimit(0);
    }

    @Override
    public void setListeners() {
        backLayout.setOnClickListener(this);
        viewpager.setOnPageChangeListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.back:
                finish();
                break;
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
//        previewAdapter.releaseALlVideo();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        JZVideoPlayer.releaseAllVideos();
    }
}
