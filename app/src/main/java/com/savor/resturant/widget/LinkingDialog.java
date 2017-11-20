package com.savor.resturant.widget;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.savor.resturant.R;

/**
 * 正在连接对话框
 * Created by hezd
 */

public class LinkingDialog extends Dialog implements View.OnClickListener {

    private static final int CHANGE_LOADING_STATE = 0x1;
    private OnConfirmListener mConfirmListener;
    private OnCancelListener mCancelListener;
    private TextView mContentTv;
    private TextView mConfirmBtn;
    private TextView ts;
    private TextView mCancelBtn;
    private View mDividerView;
    private String confirm;
    private boolean iscolor = false;

    public int count;
    private TextView mLoadingHintTv;

    public LinkingDialog(Context context) {
        super(context, R.style.linking_dialog);
    }

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case CHANGE_LOADING_STATE:
                    if(count>2) {
                        count = 0;
                    }
                    if(count == 0) {
                        mLoadingHintTv.setText("正在连接包间.");
                    }else if(count == 1) {
                        mLoadingHintTv.setText("正在连接包间..");
                    }else if(count == 2) {
                        mLoadingHintTv.setText("正在连接包间...");
                    }
                    startAnimation();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_linking);

        startAnimation();

        mLoadingHintTv = (TextView) findViewById(R.id.tv_loading_hint);
//        getViews();
//        setViews();
//        setListeners();
    }

    private void startAnimation() {
        count++;
        mHandler.sendEmptyMessageDelayed(CHANGE_LOADING_STATE,500);
    }

    private void getViews() {
        mDividerView = findViewById(R.id.view_divider);
        mContentTv = (TextView) findViewById(R.id.tv_content);
        mConfirmBtn = (TextView) findViewById(R.id.tv_confirm);
        mCancelBtn = (TextView) findViewById(R.id.tv_cancel);
        ts = (TextView) findViewById(R.id.ts);
    }

    public void setContent(String content) {
        if(!TextUtils.isEmpty(content)&&mContentTv!=null) {
            mContentTv.setText(content);
        }
    }

    private void setViews() {
//        if(!TextUtils.isEmpty(mContent))
//            mContentTv.setText(mContent);
        if(mCancelListener!=null) {
            mDividerView.setVisibility(View.VISIBLE);
            mCancelBtn.setVisibility(View.VISIBLE);

            if (iscolor) {
                mCancelBtn.setTextColor(getContext().getResources().getColor(R.color.head_title_text));
                ts.setText("抢投提示");
            }
        }
        if (!TextUtils.isEmpty(confirm)) {
            mConfirmBtn.setText(confirm);
        }
    }

    private void setListeners() {
        mCancelBtn.setOnClickListener(this);
        mConfirmBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_confirm:
                if(mConfirmListener!=null) {
                    mConfirmListener.onConfirm();
                }
                dismiss();
                break;
            case R.id.tv_cancel:
                if(mCancelListener!=null) {
                    mCancelListener.onCancel();
                }
                dismiss();

                break;
        }
    }

    public interface OnConfirmListener {
        void onConfirm();
    }

    public interface  OnCancelListener {
        void onCancel();
    }
}
