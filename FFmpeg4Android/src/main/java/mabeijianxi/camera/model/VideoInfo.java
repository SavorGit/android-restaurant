package mabeijianxi.camera.model;

import java.io.Serializable;

/**
 * Created by hezd on 2017/11/30.
 */

public class VideoInfo implements Serializable {

    /**
     * ver : 1
     * errcode : 0
     * vstreamcnt : 1
     * astreamcnt : 1
     * streamcnt : 2
     * format : mov,mp4,m4a,3gp,3g2,mj2
     * file : sdcard/at.3gp
     * duration : 47.35
     * starttime : 0.0
     * bitrate : 8949
     * samplerate : 8000
     * channels : 1
     * fps : 29.62
     * pixfmt : yuv420p
     * samplefmt : flt
     * vcodec : mpeg4
     * acodec : amr_nb
     * width : 1280
     * height : 720
     * vprofile : Main Profile
     * vcodectag : mp4v
     * acodectag : samr
     */

    private int ver;
    private int errcode;
    private int vstreamcnt;
    private int astreamcnt;
    private int streamcnt;
    private String format;
    private String file;
    private double duration;
    private double starttime;
    private int bitrate;
    private int samplerate;
    private int channels;
    private double fps;
    private String pixfmt;
    private String samplefmt;
    private String vcodec;
    private String acodec;
    private int width;
    private int height;
    private String vprofile;
    private String vcodectag;
    private String acodectag;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        VideoInfo videoInfo = (VideoInfo) o;

        if (ver != videoInfo.ver) return false;
        if (errcode != videoInfo.errcode) return false;
        if (vstreamcnt != videoInfo.vstreamcnt) return false;
        if (astreamcnt != videoInfo.astreamcnt) return false;
        if (streamcnt != videoInfo.streamcnt) return false;
        if (Double.compare(videoInfo.duration, duration) != 0) return false;
        if (Double.compare(videoInfo.starttime, starttime) != 0) return false;
        if (bitrate != videoInfo.bitrate) return false;
        if (samplerate != videoInfo.samplerate) return false;
        if (channels != videoInfo.channels) return false;
        if (Double.compare(videoInfo.fps, fps) != 0) return false;
        if (width != videoInfo.width) return false;
        if (height != videoInfo.height) return false;
        if (format != null ? !format.equals(videoInfo.format) : videoInfo.format != null)
            return false;
        if (file != null ? !file.equals(videoInfo.file) : videoInfo.file != null) return false;
        if (pixfmt != null ? !pixfmt.equals(videoInfo.pixfmt) : videoInfo.pixfmt != null)
            return false;
        if (samplefmt != null ? !samplefmt.equals(videoInfo.samplefmt) : videoInfo.samplefmt != null)
            return false;
        if (vcodec != null ? !vcodec.equals(videoInfo.vcodec) : videoInfo.vcodec != null)
            return false;
        if (acodec != null ? !acodec.equals(videoInfo.acodec) : videoInfo.acodec != null)
            return false;
        if (vprofile != null ? !vprofile.equals(videoInfo.vprofile) : videoInfo.vprofile != null)
            return false;
        if (vcodectag != null ? !vcodectag.equals(videoInfo.vcodectag) : videoInfo.vcodectag != null)
            return false;
        return acodectag != null ? acodectag.equals(videoInfo.acodectag) : videoInfo.acodectag == null;
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = ver;
        result = 31 * result + errcode;
        result = 31 * result + vstreamcnt;
        result = 31 * result + astreamcnt;
        result = 31 * result + streamcnt;
        result = 31 * result + (format != null ? format.hashCode() : 0);
        result = 31 * result + (file != null ? file.hashCode() : 0);
        temp = Double.doubleToLongBits(duration);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(starttime);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        result = 31 * result + bitrate;
        result = 31 * result + samplerate;
        result = 31 * result + channels;
        temp = Double.doubleToLongBits(fps);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        result = 31 * result + (pixfmt != null ? pixfmt.hashCode() : 0);
        result = 31 * result + (samplefmt != null ? samplefmt.hashCode() : 0);
        result = 31 * result + (vcodec != null ? vcodec.hashCode() : 0);
        result = 31 * result + (acodec != null ? acodec.hashCode() : 0);
        result = 31 * result + width;
        result = 31 * result + height;
        result = 31 * result + (vprofile != null ? vprofile.hashCode() : 0);
        result = 31 * result + (vcodectag != null ? vcodectag.hashCode() : 0);
        result = 31 * result + (acodectag != null ? acodectag.hashCode() : 0);
        return result;
    }

    public int getVer() {
        return ver;
    }

    public void setVer(int ver) {
        this.ver = ver;
    }

    public int getErrcode() {
        return errcode;
    }

    public void setErrcode(int errcode) {
        this.errcode = errcode;
    }

    public int getVstreamcnt() {
        return vstreamcnt;
    }

    public void setVstreamcnt(int vstreamcnt) {
        this.vstreamcnt = vstreamcnt;
    }

    public int getAstreamcnt() {
        return astreamcnt;
    }

    public void setAstreamcnt(int astreamcnt) {
        this.astreamcnt = astreamcnt;
    }

    public int getStreamcnt() {
        return streamcnt;
    }

    public void setStreamcnt(int streamcnt) {
        this.streamcnt = streamcnt;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }

    public double getDuration() {
        return duration;
    }

    public void setDuration(double duration) {
        this.duration = duration;
    }

    public double getStarttime() {
        return starttime;
    }

    public void setStarttime(double starttime) {
        this.starttime = starttime;
    }

    public int getBitrate() {
        return bitrate;
    }

    public void setBitrate(int bitrate) {
        this.bitrate = bitrate;
    }

    public int getSamplerate() {
        return samplerate;
    }

    public void setSamplerate(int samplerate) {
        this.samplerate = samplerate;
    }

    public int getChannels() {
        return channels;
    }

    public void setChannels(int channels) {
        this.channels = channels;
    }

    public double getFps() {
        return fps;
    }

    public void setFps(double fps) {
        this.fps = fps;
    }

    public String getPixfmt() {
        return pixfmt;
    }

    public void setPixfmt(String pixfmt) {
        this.pixfmt = pixfmt;
    }

    public String getSamplefmt() {
        return samplefmt;
    }

    public void setSamplefmt(String samplefmt) {
        this.samplefmt = samplefmt;
    }

    public String getVcodec() {
        return vcodec;
    }

    public void setVcodec(String vcodec) {
        this.vcodec = vcodec;
    }

    public String getAcodec() {
        return acodec;
    }

    public void setAcodec(String acodec) {
        this.acodec = acodec;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public String getVprofile() {
        return vprofile;
    }

    public void setVprofile(String vprofile) {
        this.vprofile = vprofile;
    }

    public String getVcodectag() {
        return vcodectag;
    }

    public void setVcodectag(String vcodectag) {
        this.vcodectag = vcodectag;
    }

    public String getAcodectag() {
        return acodectag;
    }

    public void setAcodectag(String acodectag) {
        this.acodectag = acodectag;
    }
}
