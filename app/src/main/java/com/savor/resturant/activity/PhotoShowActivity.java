package com.savor.resturant.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ProgressBar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.savor.resturant.widget.PinchImageView;
import com.savor.resturant.R;


public class PhotoShowActivity extends BaseActivity implements View.OnClickListener{

    private PinchImageView mImageView;
    private ProgressBar mLoadingPb;
    private String mImageUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        setContentView(R.layout.activity_photo_show);

        handleIntent();
        getViews();
        setViews();
        setListeners();
    }

    public static void startPhotoShowActivity(Context context,String imageUrl) {
        Intent intent = new Intent(context,PhotoShowActivity.class);
        intent.putExtra("url",imageUrl);
        context.startActivity(intent);
    }

    private void handleIntent() {
        mImageUrl = getIntent().getStringExtra("url");
    }

    public void setViews() {
//        ViewGroup.LayoutParams layoutParams = mImageView.getLayoutParams();
//        layoutParams.width = DensityUtil.getScreenWidth(this);
//        layoutParams.height = DensityUtil.getScreenHeight(this)-DensityUtil.getStatusBarHeight(this);
        Glide.with(this).load(mImageUrl).listener(new RequestListener<String, GlideDrawable>() {
            @Override
            public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                mLoadingPb.setVisibility(View.GONE);
                return false;
            }

            @Override
            public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                mLoadingPb.setVisibility(View.GONE);
                return false;
            }
        }).into(mImageView);
    }

    @Override
    public void setListeners() {
        mImageView.setOnClickListener(this);
    }

    public void getViews() {
        mImageView = (PinchImageView) findViewById(R.id.iv_image);
        mLoadingPb = (ProgressBar) findViewById(R.id.progressBar);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_image:
                finish();
                break;
        }
    }
}
