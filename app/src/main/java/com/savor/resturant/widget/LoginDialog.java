package com.savor.resturant.widget;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.savor.resturant.R;

/**
 * Created by hezd on 2016/12/26.
 */

public class LoginDialog extends Dialog implements View.OnClickListener {

    private final String mContent;
    private OnConfirmListener mConfirmListener;
    private OnCancelListener mCancelListener;
    private TextView mContentTv;
    private TextView mConfirmBtn;
    private TextView ts;
    private TextView mCancelBtn;
    private View mDividerView;
    private String confirm;
    private boolean iscolor = false;

    public LoginDialog(Context context, String content) {
        super(context, R.style.loading_dialog);
        this.mContent = content;
    }

    public LoginDialog(Context context, String content, OnConfirmListener confirmListener, OnCancelListener cancelListener) {
        super(context, R.style.loading_dialog);
        this.mContent = content;
        this.mConfirmListener = confirmListener;
        this.mCancelListener = cancelListener;

    }

    public LoginDialog(Context context, String content, OnConfirmListener confirmListener, OnCancelListener cancelListener, String confirm) {
        super(context, R.style.loading_dialog);
        this.mContent = content;
        this.mConfirmListener = confirmListener;
        this.mCancelListener = cancelListener;
        this.confirm = confirm;

    }
    public LoginDialog(Context context, String content, OnConfirmListener confirmListener, OnCancelListener cancelListener, String confirm, boolean iscolor) {
        super(context, R.style.loading_dialog);
        this.mContent = content;
        this.mConfirmListener = confirmListener;
        this.mCancelListener = cancelListener;
        this.confirm = confirm;
        this.iscolor = iscolor;

    }

    public LoginDialog(Context context, String content, OnConfirmListener confirmListener) {
        super(context, R.style.loading_dialog);
        this.mContent = content;
        this.mConfirmListener = confirmListener;
//        if(mCancelListener!=null) {
//            mDividerView.setVisibility(View.VISIBLE);
//            mCancelBtn.setVisibility(View.VISIBLE);
//        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_login_dialog);
        getViews();
        setViews();
        setListeners();
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
        if(!TextUtils.isEmpty(mContent))
            mContentTv.setText(mContent);
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
