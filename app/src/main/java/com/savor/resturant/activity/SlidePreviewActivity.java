package com.savor.resturant.activity;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.savor.resturant.R;
import com.savor.resturant.adapter.SlideAdapter;
import com.savor.resturant.bean.PictureInfo;
import com.savor.resturant.bean.SlideSetInfo;
import com.savor.resturant.utils.MediaUtils;
import com.savor.resturant.utils.SlideManager;
import com.savor.resturant.widget.LoopViewPager;

import java.util.LinkedList;

/**
 * Created by Administrator on 2017/3/17.
 */

public class SlidePreviewActivity extends BaseActivity implements View.OnClickListener,ViewPager.OnPageChangeListener{

    private LinearLayout backLayout;
    private LoopViewPager viewpager;

    private SlideAdapter previewAdapter;
    private LinkedList<PictureInfo> images = new LinkedList<>();
    private SlideSetInfo slideSetInfo;
    private int position;
    private SlideManager.SlideType slideType;
    private TextView mTitleTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_slide_preview);

        handleIntent();
        getViews();
        setViews();
        setListeners();
    }

    private void handleIntent() {
        slideType = (SlideManager.SlideType) getIntent().getSerializableExtra("type");
    }

    @Override
    public void getViews() {
        mTitleTv = (TextView) findViewById(R.id.tv_center);

        backLayout = (LinearLayout) findViewById(R.id.back);
        viewpager = (LoopViewPager) findViewById(R.id.viewpager);

        slideSetInfo = (SlideSetInfo) getIntent().getSerializableExtra("photos");
        if (slideSetInfo!=null&&slideSetInfo.imageList!=null&&slideSetInfo.imageList.size()>0){
            images.clear();
            MediaUtils.getFolderAllImg(mContext, images, slideSetInfo.imageList);
        }
        position = getIntent().getIntExtra("position",0);
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

        previewAdapter = new SlideAdapter(SlidePreviewActivity.this);
        previewAdapter.setData(images);
        viewpager.setAdapter(previewAdapter);
        viewpager.setCurrentItem(position);

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
}
