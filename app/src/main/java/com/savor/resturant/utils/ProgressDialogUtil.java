package com.savor.resturant.utils;

import android.app.ProgressDialog;
import android.content.Context;
import android.view.Window;

import mabeijianxi.camera.util.StringUtils;

/**
 * Created by hezd on 2017/11/26.
 */

public class ProgressDialogUtil {

    private ProgressDialog mProgressDialog;
    private static ProgressDialogUtil instance ;
    private ProgressDialogUtil(){}

    public static ProgressDialogUtil getInstance() {
        if(instance == null) {
            synchronized (ProgressDialogUtil.class) {
                if(instance == null) {
                    instance = new ProgressDialogUtil();
                }
            }
        }
        return instance;
    }

    public void showProgress(Context context,String title, String message, int theme) {
        if (mProgressDialog == null) {
            if (theme > 0)
                mProgressDialog = new ProgressDialog(context, theme);
            else
                mProgressDialog = new ProgressDialog(context);
            mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            mProgressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            mProgressDialog.setCanceledOnTouchOutside(false);// 不能取消
            mProgressDialog.setCancelable(false);
            mProgressDialog.setIndeterminate(true);// 设置进度条是否不明确
        }

        if (!StringUtils.isEmpty(title))
            mProgressDialog.setTitle(title);
        mProgressDialog.setMessage(message);
        mProgressDialog.show();
    }

    public void hideProgress() {
        if (mProgressDialog != null&&mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
        mProgressDialog = null;
    }
}
