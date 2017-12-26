package com.savor.resturant.widget;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.savor.resturant.R;

/**
 * Created by hezd on 2016/12/26.
 */

public class ChoosePicDialog extends Dialog implements View.OnClickListener {

    private OnTakePhotoBtnClickListener onTakePhotoBtnClickListener;
    private OnAlbumBtnClickListener onAlbumBtnClickListener;
    private boolean iscolor = false;
    private TextView mTakePhotoTv;
    private TextView mAlbumTv;

    public ChoosePicDialog(Context context) {
        super(context, R.style.loading_dialog);
    }

    public ChoosePicDialog(Context context, OnTakePhotoBtnClickListener onTakePhotoBtnClickListener, OnAlbumBtnClickListener onAlbumBtnClickListener) {
        super(context, R.style.loading_dialog);
        this.onTakePhotoBtnClickListener = onTakePhotoBtnClickListener;
        this.onAlbumBtnClickListener = onAlbumBtnClickListener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_select_pic);
        getViews();
        setViews();
        setListeners();
    }

    private void setListeners() {
        mTakePhotoTv.setOnClickListener(this);
        mAlbumTv.setOnClickListener(this);
    }

    private void setViews() {

    }

    private void getViews() {
        mTakePhotoTv = (TextView) findViewById(R.id.tv_take_photo);
        mAlbumTv = (TextView) findViewById(R.id.tv_album);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_take_photo:
                if(onTakePhotoBtnClickListener!=null) {
                    onTakePhotoBtnClickListener.onTakePhotoClick();
                }
                dismiss();
                break;

            case R.id.tv_album:
                if(onAlbumBtnClickListener!=null) {
                    onAlbumBtnClickListener.onAlbumBtnClick();
                }
                dismiss();
                break;
        }
    }

    public interface OnTakePhotoBtnClickListener {
        void onTakePhotoClick();
    }

    public interface OnAlbumBtnClickListener {
        void onAlbumBtnClick();
    }
}
