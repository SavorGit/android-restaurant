package com.savor.resturant.widget;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.savor.resturant.R;

import java.text.DecimalFormat;

/**
 * 上传投屏图片进度
 * Created by hezd on 2016/12/26.
 */

public class LoadingProgressDialog extends Dialog  {


    private final Activity mContext;
    private TextView mPercentTv;
    private TextView mHintTv;

    public LoadingProgressDialog(Activity context) {
        super(context, R.style.loading_progress_bar);
        this.mContext = context;
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_loading_progress);
        setCancelable(false);
        mPercentTv = (TextView) findViewById(R.id.tv_percent);
        mHintTv = (TextView) findViewById(R.id.tv_loading_hint);
    }

    public void updatePercent(String hint,double percent) {
        mPercentTv.setVisibility(View.VISIBLE);
        int per = (int) (percent*100);
        mPercentTv.setText(hint+per+"%");
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
}
