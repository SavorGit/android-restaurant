package com.savor.resturant.widget;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.common.api.utils.ShowMessage;
import com.savor.resturant.R;

import java.util.ArrayList;
import java.util.List;

/**
 * 导入通讯录提示框
 * Created by hezd on 2017/12/19.
 */

public class ImportDialog extends Dialog implements View.OnClickListener {

    private final Context mContext;
    private TextView mCancelBtn;
    private Button mImportBtn;
    private OnImportBtnClickListener listener;

    public ImportDialog(@NonNull Context context,OnImportBtnClickListener listener) {
        super(context);
        this.mContext = context;
        this.listener = listener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_import_contacts);

        setCancelable(false);
        getViews();
        setViews();
        setListeners();
    }

    private void getViews() {
        mImportBtn = (Button) findViewById(R.id.btn_import);
        mCancelBtn = (TextView) findViewById(R.id.tv_cancel);
    }

    private void setViews() {

    }

    private void setListeners() {
        mImportBtn.setOnClickListener(this);
        mCancelBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_import:
                if(listener!=null) {
                    listener.onImportBtnClick();
                }
                dismiss();
                break;
            case R.id.tv_cancel:
                dismiss();
                break;

        }
    }

    public interface OnImportBtnClickListener {
        void onImportBtnClick();
    }
}
