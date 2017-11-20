package com.savor.resturant.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.savor.resturant.R;
import com.savor.resturant.utils.ImageLoaderManager;

/**
 * Created by luminita on 2016/12/14.
 */

public class PhotoDetailActivity extends BaseActivity implements View.OnClickListener {

    private ImageView back;
    private TextView title;
    private ImageView photo;
    private String photoPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_detail);
        getViews();
        setViews();
        setListeners();
        getData();
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this,SlideListActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        super.onBackPressed();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_photo:
                onBackPressed();
                break;
            case R.id.iv_left:
                onBackPressed();
                break;
        }
    }

    @Override
    public void getViews() {
        back = (ImageView) findViewById(R.id.iv_left);
        title = (TextView) findViewById(R.id.tv_center);
        photo = (ImageView) findViewById(R.id.iv_photo);
    }

    @Override
    public void setViews() {
        back.setVisibility(View.VISIBLE);
        title.setText("预览");

    }

    @Override
    public void setListeners() {
        photo.setOnClickListener(this);
        back.setOnClickListener(this);
    }

    private void getData(){
        photoPath = getIntent().getStringExtra("photoPath");
        if (!TextUtils.isEmpty(photoPath)) {
            //ImageLoaderManager.getImageLoader().loadImage(PhotoDetailActivity.this, "file://" + photoPath, photo, R.drawable.ic_loadding2);
            ImageLoaderManager.getImageLoader().loadImage(PhotoDetailActivity.this, "file://" + photoPath, photo);
        }
    }
}
