package com.savor.resturant.widget;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.common.api.utils.DensityUtil;
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
    private TextView mCancelTv;

    public ChoosePicDialog(Context context) {
        super(context, R.style.loading_dialog);
    }

    public ChoosePicDialog(Context context, OnTakePhotoBtnClickListener onTakePhotoBtnClickListener, OnAlbumBtnClickListener onAlbumBtnClickListener) {
        super(context, R.style.choose_pic_dialog);
        this.onTakePhotoBtnClickListener = onTakePhotoBtnClickListener;
        this.onAlbumBtnClickListener = onAlbumBtnClickListener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_select_pic);

        Window window = this.getWindow();
        window.setGravity(Gravity.LEFT | Gravity.BOTTOM);
        WindowManager.LayoutParams params = window.getAttributes();
        params.width = DensityUtil.getScreenWidth(getContext());
//        params.height = (int) (MyApplication.getInstance().getScreenHeight() * 0.6);
        window.setAttributes(params);
        getViews();
        setViews();
        setListeners();
    }

    private void setListeners() {
        mTakePhotoTv.setOnClickListener(this);
        mAlbumTv.setOnClickListener(this);
        mCancelTv.setOnClickListener(this);
    }

    private void setViews() {

    }

    private void getViews() {
        mTakePhotoTv = (TextView) findViewById(R.id.tv_take_photo);
        mAlbumTv = (TextView) findViewById(R.id.tv_album);
        mCancelTv = (TextView) findViewById(R.id.tv_cancel);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_cancel:
                dismiss();
                break;
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
