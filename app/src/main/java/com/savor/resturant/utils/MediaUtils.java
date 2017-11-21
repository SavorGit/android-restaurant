package com.savor.resturant.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.provider.MediaStore;

import com.common.api.utils.LogUtils;
import com.savor.resturant.SavorApplication;
import com.savor.resturant.activity.PhotoSelectActivity;
import com.savor.resturant.bean.PhotoInfo;
import com.savor.resturant.bean.PictureInfo;
import com.savor.resturant.bean.VideoInfo;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * 本地多媒体文件工具类
 * Created by luminita on 2016/12/13.
 */

public class MediaUtils {

    private static SavorApplication application;

    /**
     * 获取本地图片信息
     * @param context
     * @param map
     */
    public static void getImgInfo(Context context, HashMap<String, ArrayList<String>> map) {
        //获取图片信息表
        Uri imageUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        ContentResolver mContentResolver = context.getContentResolver();
        Cursor mCursor = mContentResolver.query(imageUri, null,
                MediaStore.Images.Media.MIME_TYPE + "=? or " + MediaStore.Images.Media.MIME_TYPE + "=?",
                new String[]{"image/jpeg", "image/png"}, MediaStore.Images.Media.DATE_TAKEN + " DESC");
        while (mCursor.moveToNext()) {
            String imgPath = mCursor.getString(mCursor.getColumnIndex(MediaStore.Images.Media.DATA));
            //获取图片父路径
            String parentPath = new File(imgPath).getParentFile().getName();
            if (!map.containsKey(parentPath)) {
                ArrayList<String> childList = new ArrayList<String>();
                childList.add(imgPath);
                map.put(parentPath, childList);
            } else {
                map.get(parentPath).add(imgPath);
            }
        }
        mCursor.close();
    }

    /**
     * 获取本地视频信息
     * @param context
     */
    public static void getVideoInfo(Context context, HashMap<String, ArrayList<String>> map) {
        //获取视频信息表
        Uri videoUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
        final String orderBy = MediaStore.Video.Media.DATE_TAKEN;
        final String[] columns = {MediaStore.Video.Media._ID, MediaStore.Video.Media.DATA,
                MediaStore.Video.Media.TITLE, MediaStore.Video.Media.DURATION};
        Cursor videocursor = context.getContentResolver().query(videoUri, columns, null, null, orderBy + " DESC");
        while (videocursor.moveToNext()) {
            String imgPath = videocursor.getString(videocursor.getColumnIndex(MediaStore.Images.Media.DATA));
            //获取图片父路径
            String parentPath = new File(imgPath).getParentFile().getName();
            if (!map.containsKey(parentPath)) {
                ArrayList<String> childList = new ArrayList<String>();
                childList.add(imgPath);
                map.put(parentPath, childList);
            } else {
                map.get(parentPath).add(imgPath);
            }
        }
        videocursor.close();
    }

    /**
     * 获取本地视频信息
     * @param context
     * @param list
     */
    public static void getVideoInfo(Context context, ArrayList<VideoInfo> list) {
        //获取视频信息表
        Uri videoUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
        final String orderBy = MediaStore.Video.Media.DATE_TAKEN;
        final String[] columns = {MediaStore.Video.Media._ID, MediaStore.Video.Media.DATA,
                MediaStore.Video.Media.TITLE, MediaStore.Video.Media.DURATION};
        Cursor videocursor = context.getContentResolver().query(videoUri, columns, null, null, orderBy + " DESC");
        while (videocursor.moveToNext()) {
            String title = videocursor.getString(videocursor.getColumnIndexOrThrow(MediaStore.Video.Media.TITLE));
            String filename = videocursor.getString(videocursor.getColumnIndex(MediaStore.Video.Media.DATA));
            String id = videocursor.getString(videocursor.getColumnIndexOrThrow(MediaStore.Video.Media._ID));
            long duration = videocursor.getLong(videocursor.getColumnIndexOrThrow(MediaStore.Video.Media.DURATION));

            try {
                File file = new File(filename);
                if (!file.exists()) {
                    continue;
                }
            } catch (Exception e) {
                continue;
            }

            VideoInfo model = new VideoInfo();
            model.setAction("2screen");
            model.setAssetname(title);
            model.setAssetpath("file:/" + filename.toString());
            model.setAsseturl(NetWorkUtil.getLocalUrl(context) + filename.replaceAll(" ", "%20"));
            model.setAssetcover(filename);
            model.setAssetlength(duration);
            list.add(model);
        }
        videocursor.close();
    }

    /**
     * 组装分组界面ListView的数据源，因为我们扫描手机的时候将图片信息放在HashMap中
     * 所以需要遍历HashMap将数据组装成List
     *
     * @param
     * @return
     */
    public static List<PhotoInfo> subGroupOfImage(HashMap<String, ArrayList<String>> hashMap) {
        if (hashMap == null || hashMap.size() == 0) {
            LogUtils.i("hashMap为空");
            return null;
        }
        List<PhotoInfo> list = new ArrayList<PhotoInfo>();
        Iterator<Map.Entry<String, ArrayList<String>>> it = hashMap.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, ArrayList<String>> entry = it.next();
            PhotoInfo mImageBean = new PhotoInfo();
            String key = entry.getKey();
            List<String> value = entry.getValue();
            mImageBean.setFolderName(key);
            mImageBean.setImageCounts(value.size());
            mImageBean.setTopImagePath(value.get(0));//获取该组的第一张图片
            list.add(mImageBean);
        }
        LogUtils.i("LIST:" + list.size());
        return list;
    }

    /**
     * 获取当前相册下面所有照片的信息
     * @param context
     * @param datas 用来保存图片信息集合
     * @param childList 当前相册下所有图片的路径
     */
    public static void getFolderAllImg(Context context, List<PictureInfo> datas, List<String> childList) {
        for (int i = 0; i < childList.size(); i++) {
            int startTitle = childList.get(i).lastIndexOf("/") + 1;
            int endTitle = childList.get(i).lastIndexOf(".");
            String title = (String) childList.get(i).subSequence(startTitle, endTitle);
            String filename = childList.get(i);

            PictureInfo model = new PictureInfo();
            model.setFunction(ConstantsWhat.FunctionsIds.PREPARE);
            model.setAction("2screen");
            model.setAssettype("pic");
            model.setAssetname(title);
            model.setAssetpath(filename);
            model.setAsseturl(NetWorkUtil.getLocalUrl(context) + filename);
            datas.add(model);
        }
    }

    /**
     * 获取当前相册下面所有照片的信息
     * @param context
     * @param datas 用来保存图片信息集合
     * @param childList 当前相册下所有图片的路径
     */
    public static void getFolderAllImg(Context context, List<PictureInfo> datas, List<String> childList,List<String> imageNames) {
        for (int i = 0; i < childList.size(); i++) {
            int startTitle = childList.get(i).lastIndexOf("/") + 1;
            int endTitle = childList.get(i).lastIndexOf(".");
            String title = (String) childList.get(i).subSequence(startTitle, endTitle);
            String filename = childList.get(i);
            File file = new File(filename);
            if (file.exists()){
                PictureInfo model = new PictureInfo();
                model.setFunction(ConstantsWhat.FunctionsIds.PREPARE);
                model.setAction("2screen");
                model.setAssettype("pic");
                model.setAssetname(title);
                model.setAssetpath(filename);
                model.setAsseturl(NetWorkUtil.getLocalUrl(context) + filename);
                datas.add(model);
                imageNames.add(childList.get(i));
            }

        }
    }

    public static void getFolderAllImg(Context context, List<PictureInfo> datas, List<String> childList,final Handler handler) {
        for (int i = 0; i < childList.size(); i++) {
            int startTitle = childList.get(i).lastIndexOf("/") + 1;
            int endTitle = childList.get(i).lastIndexOf(".");
            String title = (String) childList.get(i).subSequence(startTitle, endTitle);
            String filename = childList.get(i);

            PictureInfo model = new PictureInfo();
            model.setFunction(ConstantsWhat.FunctionsIds.PREPARE);
            model.setAction("2screen");
            model.setAssettype("pic");
            model.setAssetname(title);
            model.setAssetpath(filename);
//            if (contains(context, title)) {
//                if (application == null) {
//                    application = (SavorApplication) context.getApplicationContext();
//                }
//                model.setAsseturl(application.GalleyPath + title + ".jpg");
//            } else {
            model.setAsseturl(NetWorkUtil.getLocalUrl(context) + filename);
//            }
            datas.add(model);
        }

        handler.sendEmptyMessage(PhotoSelectActivity.INIT_SUCCESS);
       // calback.setData();
    }

    public static ArrayList<String> getSpecialFolderImg(Context context,String folder) {
//        Cursor mCursor = mContentResolver.query(imageUri, null,
//                MediaStore.Images.Media.MIME_TYPE + "=? or " + MediaStore.Images.Media.MIME_TYPE + "=?",
//                new String[]{"image/jpeg", "image/png"}, MediaStore.Images.Media.DATE_TAKEN + " DESC");
        //selection: 指定查询条件
        String selection = MediaStore.Images.Media.DATA + " like ? and ("+MediaStore.Images.Media.MIME_TYPE + "=? or " + MediaStore.Images.Media.MIME_TYPE + "=?)";
        //定义selectionArgs：
        String[] selectionArgs = {"%"+folder+"%","image/jpeg","image/png"};
        Cursor cursor = context.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,null,
                selection, selectionArgs, null);
        ArrayList<String> photolist = new ArrayList<String>();
        while (cursor.moveToNext()) {
            String imgPath = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            photolist.add(imgPath);
        }
        cursor.close();
        return photolist;
    }

//    private static boolean contains(Context context, String title) {
//        if (app == null) {
//            app = (SavorApplication) context.getApplicationContext();
//        }
//        return new File(app.GalleyPath + title + ".jpg").exists();
//    }

    /**
     * 获取带有父目录的文件名称
     * @param filePath
     * @return
     */
    public static String getPicName(String filePath) {
        File file = new File(filePath);
        String parent = file.getParent();
        int parentIndex = parent.lastIndexOf("/") + 1;
        String substring = parent.substring(parentIndex, parent.length());
        int startTitle = filePath.lastIndexOf("/") + 1;
        int endTitle = filePath.lastIndexOf(".");
        String title = (String) filePath.subSequence(startTitle, endTitle);
        String result = substring+"_"+title;
        return result;
    }

    public static String getPicRealName(String filePath) {
        int startTitle = filePath.lastIndexOf("/") + 1;
        int endTitle = filePath.lastIndexOf(".");
        String title = (String) filePath.subSequence(startTitle, endTitle);
        return title;
    }
}
