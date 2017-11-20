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
    }

    public void updatePercent(double percent) {
        int per = (int) (percent*100);
        mPercentTv.setText(per+"%");
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        dismiss();
        mContext.finish();
    }
}
