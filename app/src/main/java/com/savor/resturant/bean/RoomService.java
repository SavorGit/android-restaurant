package com.savor.resturant.bean;

import android.content.Context;
import android.content.Intent;
import android.os.CountDownTimer;

import com.savor.resturant.utils.ConstantValues;

/**
 * Created by hezd on 2018/2/2.
 */

public class RoomService {
    private RoomInfo roomInfo;
    private CountDownTimer timer;
    private CountDownTimer completetimer;


    @Override
    public String toString() {
        return "RoomService{" +
                "roomInfo=" + roomInfo +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        RoomService that = (RoomService) o;

        return roomInfo != null ? roomInfo.equals(that.roomInfo) : that.roomInfo == null;
    }

    @Override
    public int hashCode() {
        return roomInfo != null ? roomInfo.hashCode() : 0;
    }

    public RoomInfo getRoomInfo() {
        return roomInfo;
    }

    public void setRoomInfo(RoomInfo roomInfo) {
        this.roomInfo = roomInfo;
    }

    public void startTimer(final Context context, int welSec, final int completeSec) {
        if(timer!=null) {
            timer.cancel();
        }
        if(completetimer !=null) {
            completetimer.cancel();
        }
        timer = new CountDownTimer(welSec*1000, welSec*1000) {
            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {
                handleCompleteDelayed(context, completeSec);
            }
        };
        timer.start();
    }

    /**
     * 延迟指定时间结束投屏
     * @param context
     * @param completeSec
     */
    private void handleCompleteDelayed(final Context context, final int completeSec) {
        roomInfo.setWelPlay(false);
        roomInfo.setRecommendPlay(true);
        Intent intent = new Intent(ConstantValues.ACTION_REFRESH_PRO_STATE_DELAYED);
        context.sendBroadcast(intent);

        completetimer = new CountDownTimer(completeSec*1000, completeSec*1000) {
            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {
                roomInfo.setWelPlay(false);
                roomInfo.setRecommendPlay(false);

                Intent intent = new Intent(ConstantValues.ACTION_REFRESH_PRO_STATE_DELAYED);
                context.sendBroadcast(intent);
            }
        };
        completetimer.start();
    }
}
