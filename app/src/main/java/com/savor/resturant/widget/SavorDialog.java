package com.savor.resturant.widget;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.drawable.AnimationDrawable;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.common.api.utils.DensityUtil;
import com.savor.resturant.R;

/**
 * Created by hezd on 2016/12/29.
 */

public class SavorDialog {
    private final Activity mActivity;
    private String mHint;
    private Dialog mDialog;
    private TextView mHintTv;
    private boolean canCancel = true;
    private OnBackKeyDownListener mOnBackKeyDownListener;

    public SavorDialog(Activity context) {
        this.mActivity = context;
    }

    public SavorDialog(Activity context,String hint) {
        this.mActivity = context;
        this.mHint = hint;
    }

    public void updateHint(String mHint) {
        if(mHintTv!=null)
            mHintTv.setText(mHint);
    }

    public void show() {
        LayoutInflater layoutInflater = LayoutInflater.from(mActivity);
        View inflate = layoutInflater.inflate(R.layout.view_capture_in, null);
        View mStatusImage = inflate.findViewById(R.id.status_image);
        mHintTv = (TextView) inflate.findViewById(R.id.tv_hint);
        AnimationDrawable anim = (AnimationDrawable) mStatusImage.getBackground();
        anim.start();
        if(mDialog == null) {
            mDialog = new Dialog(mActivity);
            mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            mDialog.setContentView(inflate);
            mDialog.setCancelable(canCancel);
            // 设置屏幕位置
            Window window = mDialog.getWindow();
            WindowManager.LayoutParams lp = window.getAttributes();
            lp.gravity = Gravity.CENTER;
            lp.width = DensityUtil.dip2px(mActivity,220);// 宽高可设置具体大小
            lp.height = DensityUtil.dip2px(mActivity,220);
            mDialog.getWindow().setAttributes(lp);
            if(!TextUtils.isEmpty(mHint)) {
                mHintTv.setText(mHint);
            }
        }
        mDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if(keyCode == KeyEvent.KEYCODE_BACK) {
                    if(mOnBackKeyDownListener!=null) {
                        mOnBackKeyDownListener.onBackKeyDown();
                        return true;
                    }
                }
                return false;
            }
        });
        mDialog.show();
    }

    public void setCanCancle(boolean canCancle) {
        this.canCancel = canCancle;
    }

    public void dismiss() {
        if(mActivity!=null&&!mActivity.isFinishing()&&mDialog!=null&&mDialog.isShowing()) {
            mDialog.dismiss();
        }
    }

    public void setOnBackKeyDowListener (OnBackKeyDownListener listener){
        this.mOnBackKeyDownListener = listener;
    }

    public interface OnBackKeyDownListener {
        void onBackKeyDown();
    }
}
