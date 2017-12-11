package com.savor.resturant.widget;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.KeyEvent;
import android.widget.TextView;

import com.savor.resturant.R;

/**
 * 上传投屏图片进度
 * Created by hezd on 2016/12/26.
 */

public class LoadingDialog extends Dialog {


    private final Activity mContext;
    private TextView mPercentTv;
    private TextView mHintTv;
    private TextView mCancelBtn;

    public LoadingDialog(Activity context) {
        super(context, R.style.loading_progress_bar);
        this.mContext = context;
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_loading);
        setCancelable(false);
    }

    @Override
    public boolean onKeyDown(int keyCode, @NonNull KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            dismiss();
        }
        return true;
    }
}
