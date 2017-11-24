package com.savor.resturant.utils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.savor.resturant.SavorApplication;
import com.savor.resturant.bean.SlideSetInfo;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * 幻灯片管理器
 * created by lumintia on 2016/12/13.
 */
public class SlideManager {
    private static final String FILE_DIR = SavorApplication.getInstance().getFilesDir().toString();
    private static final String FILE_IMAGE_NAME = "lantern_image_slide.json";
    private static final String FILE_VIDEO_NAME = "lantern_video_slide.json";
    private static SlideManager instance;
    private SlideType type = SlideType.IMAGE;
    private ArrayList<SlideSetInfo> mData = new ArrayList<>();
    private boolean needSave;
    private SlideComparator mSlideComparator = new SlideComparator();

    public enum SlideType implements Serializable{
        IMAGE,
        VIDEO,
    }


    private SlideManager(SlideType type) {
        this.type = type;
        readFile();
    }

    public void setType(SlideType type) {
        this.type = type;
    }

    public static SlideManager getInstance(SlideType type) {
        if (instance == null) {
            instance = new SlideManager(type);
        }
        instance.setType(type);
        return instance;
    }

    /**
     * 从本地获取幻灯片集列表
     * @return
     */
    public ArrayList<SlideSetInfo> getData() {
        readFile();
        Collections.sort(mData, mSlideComparator);
        return mData;
    }

    /**
     * 最多创建50个幻灯片
     *
     * @return
     */
    public int getSize() {
        return mData.size();
    }

    /**
     * 存储数据
     * 把幻灯片集保存至本地
     */
    public void saveSlide() {
        if (needSave) {
            ArrayList<SlideSetInfo> toRemove = new ArrayList<>();
            for (SlideSetInfo bean : mData) {
                if (bean.isNewCreate) {
                    if (bean.imageList.size() < 1) {
                        toRemove.add(bean);
                    } else {
                        bean.isNewCreate = false;
                    }
                }
            }
            mData.removeAll(toRemove);
            writeFile(type);
        }
    }

    public void removeImageByGroup(SlideSetInfo group, String imagePath) {
        needSave = true;
        group.imageList.remove(imagePath);
    }

    /**
     * 把幻灯片集信息添加至数据存储器
     * @param group
     */
    public void addList(SlideSetInfo group) {
        needSave = true;
        mData.add(group);
    }

    /**
     * 判断是否包含该幻灯片集
     * @param group
     * @return
     */
    public boolean containGroup(SlideSetInfo group) {
        return mData.contains(group);
    }

    /**
     * 将幻灯片集移除
     * @param group
     */
    public void removeGroup(SlideSetInfo group) {
        needSave = true;
        mData.remove(group);
    }

    /**
     * 写入文件内容
     */
    private void writeFile(SlideType type) {
        try {
            FileWriter fw = new FileWriter(FILE_DIR + File.separator + (type==SlideType.IMAGE?FILE_IMAGE_NAME:FILE_VIDEO_NAME), false);
            BufferedWriter bw = new BufferedWriter(fw);
            String json = new Gson().toJson(mData);
            bw.write(json);
            bw.flush();
            bw.close();
            needSave = false;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 读取文件内容
     */
    private void readFile() {
        StringBuilder sb = new StringBuilder();
        try {
            BufferedReader br = new BufferedReader(new FileReader(new File(FILE_DIR, type==SlideType.IMAGE?FILE_IMAGE_NAME:FILE_VIDEO_NAME)));//构造一个BufferedReader类来读取文件
            String s;
            while ((s = br.readLine()) != null) {
                sb.append(s);
            }
            br.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        ArrayList<SlideSetInfo> list = new Gson().fromJson(sb.toString(), new TypeToken<List<SlideSetInfo>>() {
        }.getType());

        mData.clear();
        if (list != null && list.size() > 0) {
            for (SlideSetInfo bean : list) {
                if ( bean.imageList == null || bean.imageList.size()==0) {
                    return;
                }
                for (int i = bean.imageList.size() - 1; i >= 0; i--) {
                    boolean exists = fileIsExists(bean.imageList.get(i).getAssetpath());
                    if (!exists) {
                        bean.imageList.remove(i);
                    }
                }
                if(bean.imageList!=null&&bean.imageList.size()>0) {
                    mData.add(bean);
                }
            }
        }
    }

    /**
     * 判断文件是否存在
     *
     * @return
     */
    public boolean fileIsExists(String filePath) {
        File f = new File(filePath);
        if (f.exists()) {
            return true;
        }
        return false;
    }

    static final class SlideComparator implements Comparator<SlideSetInfo> {
        @Override
        public int compare(SlideSetInfo lhs, SlideSetInfo rhs) {
            return (int) (rhs.updateTime - lhs.updateTime);
        }
    }
}
