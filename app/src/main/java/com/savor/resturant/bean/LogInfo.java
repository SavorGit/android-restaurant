package com.savor.resturant.bean;

import java.io.Serializable;

/**
 * Created by bushlee on 2017/11/27.
 */

public class LogInfo implements Serializable {
    private static final long serialVersionUID = -1;
    private String signle_play;
    private String loop;
    private int loop_time;
    public String getSignle_play() {
        return signle_play;
    }

    public void setSignle_play(String signle_play) {
        this.signle_play = signle_play;
    }

    public String getLoop() {
        return loop;
    }

    public void setLoop(String loop) {
        this.loop = loop;
    }

    public int getLoop_time() {
        return loop_time;
    }

    public void setLoop_time(int loop_time) {
        this.loop_time = loop_time;
    }

    @Override
    public String toString() {
        return "LogInfo{" +
                "signle_play='" + signle_play + '\'' +
                ", loop='" + loop + '\'' +
                ", loop_time=" + loop_time +
                '}';
    }
}
