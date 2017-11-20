package com.savor.resturant.utils;

import android.os.AsyncTask;

import com.savor.resturant.interfaces.AsyncCallBack;

/**
 * 异步任务简单处理类
 * Created by luminita on 2016/12/12.
 */

public class AsyncTaskUtil {

    public static void doAsync(final AsyncCallBack callBack) {
        if (callBack == null) {
            return;
        }
        new AsyncTask<Void, Void, Void>() {

            @Override
            protected void onPreExecute() {
                callBack.onPreExecute();
            }

            @Override
            protected Void doInBackground(Void... params) {
                callBack.doInBackground();
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                callBack.onPostExecute();
            }
        }.execute();
    }

}


