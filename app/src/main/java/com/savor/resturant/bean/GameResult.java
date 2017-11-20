package com.savor.resturant.bean;

import java.io.Serializable;

/**
 * Created by bushlee on 2017/5/9.
 */

public class GameResult implements Serializable {
    private static final long serialVersionUID = -1;
    private int result;
    private String info;
    private int progress;
    private int done;
    private int win;
    private int prize_id;
    private String prize_name;
    private String prize_time;
    private int prize_level;

    public int getResult() {
        return result;
    }

    public void setResult(int result) {
        this.result = result;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    public int getDone() {
        return done;
    }

    public void setDone(int done) {
        this.done = done;
    }

    public int getWin() {
        return win;
    }

    public void setWin(int win) {
        this.win = win;
    }

    public int getPrize_id() {
        return prize_id;
    }

    public void setPrize_id(int prize_id) {
        this.prize_id = prize_id;
    }

    public String getPrize_name() {
        return prize_name;
    }

    public void setPrize_name(String prize_name) {
        this.prize_name = prize_name;
    }

    public String getPrize_time() {
        return prize_time;
    }

    public void setPrize_time(String prize_time) {
        this.prize_time = prize_time;
    }

    public int getPrize_level() {
        return prize_level;
    }

    public void setPrize_level(int prize_level) {
        this.prize_level = prize_level;
    }


    @Override
    public String toString() {
        return "GameResult{" +
                "result=" + result +
                ", info='" + info + '\'' +
                ", progress=" + progress +
                ", done=" + done +
                ", win=" + win +
                ", prize_id=" + prize_id +
                ", prize_name='" + prize_name + '\'' +
                ", prize_time='" + prize_time + '\'' +
                ", prize_level=" + prize_level +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof GameResult)) return false;

        GameResult that = (GameResult) o;

        if (result != that.result) return false;
        if (progress != that.progress) return false;
        if (done != that.done) return false;
        if (win != that.win) return false;
        if (prize_id != that.prize_id) return false;
        if (prize_level != that.prize_level) return false;
        if (!info.equals(that.info)) return false;
        if (!prize_name.equals(that.prize_name)) return false;
        return prize_time.equals(that.prize_time);

    }

    @Override
    public int hashCode() {
        int result1 = result;
        result1 = 31 * result1 + info.hashCode();
        result1 = 31 * result1 + progress;
        result1 = 31 * result1 + done;
        result1 = 31 * result1 + win;
        result1 = 31 * result1 + prize_id;
        result1 = 31 * result1 + prize_name.hashCode();
        result1 = 31 * result1 + prize_time.hashCode();
        result1 = 31 * result1 + prize_level;
        return result1;
    }
}
