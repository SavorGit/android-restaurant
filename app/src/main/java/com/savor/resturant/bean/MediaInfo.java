package com.savor.resturant.bean;

import java.io.Serializable;

/**
 * Created by hezd on 2017/11/24.
 */

public class MediaInfo extends Base2ScreenInfo implements Serializable{
    /**
     * 媒体类型图片
     */
    public static final int MEDIA_TYPE_PIC = 1;
    /**
     * 媒体类型视频
     */
    public static final int MEDIA_TYPE_VIDEO = 2;
    /**
     * 文件路径
     */
    private String assetpath;
    /**文件压缩路径*/
    private String compressPath;
    private String assetcover;
    private boolean isChecked = false;
    private String mimeType;
    /**媒体类型1.图片 2 视频*/
    private int mediaType;
    /**如果是视频类型，时长（毫秒）*/
    private long duration;
    /**文件大小*/
    private long size;
    /**视频缩略图路径*/
    private String thumbPath;

    @Override
    public String toString() {
        return "MediaInfo{" +
                "assetpath='" + assetpath + '\'' +
                ", compressPath='" + compressPath + '\'' +
                ", assetcover='" + assetcover + '\'' +
                ", isChecked=" + isChecked +
                ", mimeType='" + mimeType + '\'' +
                ", mediaType=" + mediaType +
                ", duration=" + duration +
                ", size=" + size +
                ", thumbPath='" + thumbPath + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MediaInfo mediaInfo = (MediaInfo) o;

        if (isChecked != mediaInfo.isChecked) return false;
        if (mediaType != mediaInfo.mediaType) return false;
        if (duration != mediaInfo.duration) return false;
        if (size != mediaInfo.size) return false;
        if (assetpath != null ? !assetpath.equals(mediaInfo.assetpath) : mediaInfo.assetpath != null)
            return false;
        if (compressPath != null ? !compressPath.equals(mediaInfo.compressPath) : mediaInfo.compressPath != null)
            return false;
        if (assetcover != null ? !assetcover.equals(mediaInfo.assetcover) : mediaInfo.assetcover != null)
            return false;
        if (mimeType != null ? !mimeType.equals(mediaInfo.mimeType) : mediaInfo.mimeType != null)
            return false;
        return thumbPath != null ? thumbPath.equals(mediaInfo.thumbPath) : mediaInfo.thumbPath == null;
    }

    @Override
    public int hashCode() {
        int result = assetpath != null ? assetpath.hashCode() : 0;
        result = 31 * result + (compressPath != null ? compressPath.hashCode() : 0);
        result = 31 * result + (assetcover != null ? assetcover.hashCode() : 0);
        result = 31 * result + (isChecked ? 1 : 0);
        result = 31 * result + (mimeType != null ? mimeType.hashCode() : 0);
        result = 31 * result + mediaType;
        result = 31 * result + (int) (duration ^ (duration >>> 32));
        result = 31 * result + (int) (size ^ (size >>> 32));
        result = 31 * result + (thumbPath != null ? thumbPath.hashCode() : 0);
        return result;
    }

    public String getAssetpath() {
        return assetpath;
    }

    public void setAssetpath(String assetpath) {
        this.assetpath = assetpath;
    }

    public String getCompressPath() {
        return compressPath;
    }

    public void setCompressPath(String compressPath) {
        this.compressPath = compressPath;
    }

    public String getAssetcover() {
        return assetcover;
    }

    public void setAssetcover(String assetcover) {
        this.assetcover = assetcover;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    /**媒体类型1.图片 2 视频*/
    public int getMediaType() {
        return mediaType;
    }

    public void setMediaType(int mediaType) {
        this.mediaType = mediaType;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public String getThumbPath() {
        return thumbPath;
    }

    public void setThumbPath(String thumbPath) {
        this.thumbPath = thumbPath;
    }
}
