package com.savor.resturant.bean;

import android.content.Context;
import android.content.Intent;
import android.os.CountDownTimer;

import com.savor.resturant.utils.ConstantValues;

import java.io.Serializable;

/**
 * 包间信息
 * Created by hezd on 2017/12/5.
 */

public class RoomInfo implements Serializable {

    /**
     * box_name : v0
     * box_ip : 192.168.1.10
     * room_id : 1
     * box_mac : FC2131313123123
     */

    private String box_name;
    private String box_ip;
    private String room_id;
    private String box_mac;
    private boolean isSelected;
    private boolean isWelPlay;
    private  boolean isRecommendPlay;

    @Override
    public String toString() {
        return "RoomInfo{" +
                "box_name='" + box_name + '\'' +
                ", box_ip='" + box_ip + '\'' +
                ", room_id='" + room_id + '\'' +
                ", box_mac='" + box_mac + '\'' +
                ", isSelected=" + isSelected +
                ", isWelPlay=" + isWelPlay +
                ", isRecommendPlay=" + isRecommendPlay +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        RoomInfo roomInfo = (RoomInfo) o;

        if (isSelected != roomInfo.isSelected) return false;
        if (isWelPlay != roomInfo.isWelPlay) return false;
        if (isRecommendPlay != roomInfo.isRecommendPlay) return false;
        if (box_name != null ? !box_name.equals(roomInfo.box_name) : roomInfo.box_name != null)
            return false;
        if (box_ip != null ? !box_ip.equals(roomInfo.box_ip) : roomInfo.box_ip != null)
            return false;
        if (room_id != null ? !room_id.equals(roomInfo.room_id) : roomInfo.room_id != null)
            return false;
        return box_mac != null ? box_mac.equals(roomInfo.box_mac) : roomInfo.box_mac == null;
    }

    @Override
    public int hashCode() {
        int result = box_name != null ? box_name.hashCode() : 0;
        result = 31 * result + (box_ip != null ? box_ip.hashCode() : 0);
        result = 31 * result + (room_id != null ? room_id.hashCode() : 0);
        result = 31 * result + (box_mac != null ? box_mac.hashCode() : 0);
        result = 31 * result + (isSelected ? 1 : 0);
        result = 31 * result + (isWelPlay ? 1 : 0);
        result = 31 * result + (isRecommendPlay ? 1 : 0);
        return result;
    }

    public String getBox_name() {
        return box_name;
    }

    public void setBox_name(String box_name) {
        this.box_name = box_name;
    }

    public String getBox_ip() {
        return box_ip;
    }

    public void setBox_ip(String box_ip) {
        this.box_ip = box_ip;
    }

    public String getRoom_id() {
        return room_id;
    }

    public void setRoom_id(String room_id) {
        this.room_id = room_id;
    }

    public String getBox_mac() {
        return box_mac;
    }

    public void setBox_mac(String box_mac) {
        this.box_mac = box_mac;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public boolean isWelPlay() {
        return isWelPlay;
    }

    public void setWelPlay(boolean welPlay) {
        isWelPlay = welPlay;
    }

    public boolean isRecommendPlay() {
        return isRecommendPlay;
    }

    public void setRecommendPlay(boolean recommendPlay) {
        isRecommendPlay = recommendPlay;
    }

//    public void startRecommendTimer(final Context context, int welSec, final int completeSec) {
//        if(timer!=null) {
//            timer.cancel();
//        }
//        timer = new CountDownTimer(welSec*1000, welSec*1000) {
//            @Override
//            public void onTick(long millisUntilFinished) {
//
//            }
//
//            @Override
//            public void onFinish() {
//                startCompleteTimer(context, completeSec);
//            }
//        };
//        timer.start();
//    }
//
//    /**
//     * 延迟指定时间结束投屏
//     * @param context
//     * @param completeSec
//     */
//    private void startCompleteTimer(final Context context, final int completeSec) {
//        setWelPlay(false);
//        setRecommendPlay(true);
//        Intent intent = new Intent(ConstantValues.ACTION_REFRESH_PRO_STATE_DELAYED);
//        context.sendBroadcast(intent);
//        if(completetimer !=null) {
//            completetimer.cancel();
//        }
//        completetimer = new CountDownTimer(completeSec*1000, completeSec*1000) {
//            @Override
//            public void onTick(long millisUntilFinished) {
//
//            }
//
//            @Override
//            public void onFinish() {
//                setWelPlay(false);
//                setRecommendPlay(false);
//
//                Intent intent = new Intent(ConstantValues.ACTION_REFRESH_PRO_STATE_DELAYED);
//                context.sendBroadcast(intent);
//            }
//        };
//        completetimer.start();
//    }
}
