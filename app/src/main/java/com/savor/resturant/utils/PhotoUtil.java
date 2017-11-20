package com.savor.resturant.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;

import com.common.api.utils.LogUtils;
import com.savor.resturant.SavorApplication;
import com.savor.resturant.bean.ModelPic;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class PhotoUtil {
    private static final String TAG = "PhotoUtil";
    private static SavorApplication app;
    public static final int INIT_SUCCESS = 0;

    public static void getMediaPhoto(final Context context, final Handler handler,
                                     final ArrayList<ModelPic> datas) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                final String[] columns = {MediaStore.Images.Media.DATA,
                        MediaStore.Images.Media.TITLE,
                        MediaStore.Images.Media.SIZE};
                ContentResolver mContentResolver = context.getContentResolver();
                // 只查询jpeg和png的图片
                Cursor cursor = mContentResolver.query(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI, columns,
                        MediaStore.Images.Media.MIME_TYPE + "=? or " + MediaStore.Images.Media.MIME_TYPE + "=?",
                        new String[]{"image/jpeg", "image/png"}, MediaStore.Images.Media.DATE_TAKEN + " DESC");
                while (cursor.moveToNext()) {
                    // 获取图片的路径
                    int dataColumnIndex = cursor.getColumnIndex(MediaStore.Images.Media.DATA);
                    int titleIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.TITLE);
                    int sizeIndex = cursor.getColumnIndex(MediaStore.Images.Media.SIZE);
                    String title = cursor.getString(titleIndex);
                    String filename = cursor.getString(dataColumnIndex);
                    long size = cursor.getLong(sizeIndex);
                    LogUtils.d("size是否小于8w字节= " + (size < 80000));

                    ModelPic model = new ModelPic();
                    model.setAction("2screen");
                    model.setAssetname(title);
                    model.setAssetpath(filename);
                    LogUtils.d("是否包含压缩图：" + contains(context, title));
                    LogUtils.d(app.GalleyPath + title + ".jpg");
                    if (contains(context, title)) {
                        if (app == null) {
                            app = (SavorApplication) context.getApplicationContext();
                        }
                        model.setAsseturl(app.GalleyPath + title + ".jpg");
                    } else {
                        model.setAsseturl(NetWorkUtil.getLocalUrl(context) + filename);
                    }
                    datas.add(model);
                }
                cursor.close();
                // 通知Handler扫描图片完成
                handler.sendEmptyMessage(INIT_SUCCESS);
            }
        }).start();
    }

    private static boolean contains(Context context, String title) {
        if (app == null) {
            app = (SavorApplication) context.getApplicationContext();
        }
        return new File(app.GalleyPath + title + ".jpg").exists();
    }

    public static void getMediaGroupPhotoList(Context context, Handler handler,
                                              List<ModelPic> datas, List<String> childList) {
        for (int i = 0; i < childList.size(); i++) {
            int startTitle = childList.get(i).lastIndexOf("/") + 1;
            int endTitle = childList.get(i).lastIndexOf(".");
            String title = (String) childList.get(i).subSequence(startTitle, endTitle);
            String filename = childList.get(i);
            ModelPic model = new ModelPic();
            model.setAction("2screen");
            model.setAssetname(title);
            model.setAssetpath(filename);
            LogUtils.d("是否包含压缩图：" + contains(context, title));
            LogUtils.d(app.GalleyPath + title + ".jpg");
//            if (contains(context, title)) {
//                if (app == null) {
//                    app = (SavorApplication) context.getApplicationContext();
//                }
//                model.setAsseturl(app.GalleyPath + title + ".jpg");
//            } else {
                model.setAsseturl(NetWorkUtil.getLocalUrl(context) + filename);
//            }
            datas.add(model);
            Log.i("yxc", model.toString() + "title=" + title + "-----filename=" + filename);
        }
        handler.sendEmptyMessage(INIT_SUCCESS);
    }
}
