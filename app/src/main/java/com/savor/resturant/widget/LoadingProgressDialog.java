package com.savor.resturant.widget;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.savor.resturant.R;
import com.savor.resturant.utils.SlideManager;

import java.text.DecimalFormat;

/**
 * 上传投屏图片进度
 * Created by hezd on 2016/12/26.
 */

public class LoadingProgressDialog extends Dialog implements View.OnClickListener {


    private final Activity mContext;
    private final OnCancelBtnClickListener listener;
    private TextView mPercentTv;
    private TextView mHintTv;
    private TextView mCancelBtn;

    public LoadingProgressDialog(Activity context,OnCancelBtnClickListener listener) {
        super(context, R.style.loading_progress_bar);
        this.mContext = context;
        this.listener = listener;
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_loading_progress);
        setCancelable(false);
        mPercentTv = (TextView) findViewById(R.id.tv_percent);
        mHintTv = (TextView) findViewById(R.id.tv_loading_hint);
        mCancelBtn = (TextView) findViewById(R.id.tv_cancel);

        mCancelBtn.setOnClickListener(this);
    }

    public void updatePercent(String hint, int progress, SlideManager.SlideType slideType) {
        mPercentTv.setVisibility(View.VISIBLE);
        mPercentTv.setText(progress+"%");
        if(!TextUtils.isEmpty(hint)) {
            mHintTv.setText(hint);
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        dismiss();
        mContext.finish();
    }

    public void loading(String msg) {
        mPercentTv.setVisibility(View.GONE);
        mHintTv.setText(msg);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_cancel:
                if(listener!=null) {
                    listener.onCancelBtnClick();
                }
                mPercentTv.setText("0%");
                dismiss();
                break;
        }
    }



    public interface OnCancelBtnClickListener {
        void onCancelBtnClick();
    }
}
