package com.savor.resturant.service;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;

import com.common.api.utils.LogUtils;
import com.savor.resturant.bean.SmallPlatInfoBySSDP;
import com.savor.resturant.bean.TvBoxSSDPInfo;
import com.savor.resturant.core.Session;
import com.savor.resturant.presenter.SensePresenter;
import com.savor.resturant.utils.ProjectionManager;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;


/**
 * 组播发现小平台类
 * 机顶盒ssdp
 *  收到的消息是：Savor-Type:box
    Savor-HOST:192.168.0.102
 */
public class SSDPService extends IntentService {
    /**机顶盒组播*/
    private static final String TYPE_SSDP_BOX = "box";
    private static final int PORT_LISTENING = 11900;
    private static final String IP_TARGET = "238.255.255.252";
//    private static final String IP_TARGET = "238.255.255.250";

    private static final int DATA_RECEIVE_SIZE = 1024;


    private static final String TYPE_LABEL_PREFIX = "Savor-Type:";
    private static final String BOX_IP_LABEL_PREFIX = "Savor-Box-HOST:";
    private static final String IP_LABEL_PREFIX = "Savor-HOST:";
    private static final String COMMAND_PORT_LABEL_PREFIX = "Savor-Port-Command:";
    private static final String ID_HOTEL_ID_LABLE_PREFIX = "Savor-Hotel-ID:";
    private static final String CRLF = "\r\n";
    private static final int CLOSE_FIRSTUSE_SERVICE = 0x2;


    private MulticastSocket mSocketReceive;
    private Handler mHandler  = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case CLOSE_FIRSTUSE_SERVICE:
                    isLooping = false;
                    break;
            }
        }
    };
    private WifiManager.MulticastLock multicastLock;
    private boolean isLooping = true;
    private String operation_type = "";

    public SSDPService() {
        super("SSDPService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        LogUtils.d("savor:ssdp 开始接收ssdp");
        isLooping = true;
        if(intent!=null&&intent.hasExtra("type")) {
            operation_type = intent.getStringExtra("type");
        }else {
            operation_type = null;
        }
        ProjectionManager.getInstance().setLookingSSDP(true);
        stopFirstUserServiceDelayed();
        startReceive();
    }
    private void stopFirstUserServiceDelayed() {
        mHandler.removeMessages(CLOSE_FIRSTUSE_SERVICE);
        mHandler.sendEmptyMessageDelayed(CLOSE_FIRSTUSE_SERVICE,20*1000);
    }

    private void startReceive() {
        WifiManager wm = (WifiManager) this.getSystemService(Context.WIFI_SERVICE);
        multicastLock = wm.createMulticastLock("multicastLock");
//        multicastLock.setReferenceCounted(false);
        multicastLock.acquire();

        mSocketReceive = null;

        try {
            mSocketReceive = new MulticastSocket(PORT_LISTENING);
            mSocketReceive.setLoopbackMode(true);
//            mSocketReceive.setTimeToLive(0);
//            mSocketReceive.setSoTimeout(1000*12);
            mSocketReceive.joinGroup(InetAddress.getByName(IP_TARGET));

            String type = null;
            String address = null;
            String boxAddress = null;
            int hotelId = 0;
            int nettyPort = -1, commandPort = -1, downloadPort = -1;

            do {
                DatagramPacket packetReceived = new DatagramPacket(new byte[DATA_RECEIVE_SIZE], DATA_RECEIVE_SIZE);
                mSocketReceive.receive(packetReceived);

                final String msgReceived = new String(packetReceived.getData(), 0, packetReceived.getLength()).trim();
                LogUtils.d("savor:ssdp 收到的消息是：" + msgReceived + "\ngetHostAddress:" + packetReceived.getAddress().getHostAddress());
                LogUtils.d("savor:hotel ssdp 收到的消息是：" + msgReceived + "\ngetHostAddress:" + packetReceived.getAddress().getHostAddress());

                if (msgReceived.length() > 0) {
                    // 解析并保存小平台信息到Session
                    type = parseStringMetadata(msgReceived, TYPE_LABEL_PREFIX);
                    address = parseStringMetadata(msgReceived, IP_LABEL_PREFIX);
                    commandPort = parseIntMetadata(msgReceived, COMMAND_PORT_LABEL_PREFIX);
                    hotelId = parseIntMetadata(msgReceived, ID_HOTEL_ID_LABLE_PREFIX);
                    if(TYPE_SSDP_BOX.equals(type)) {
                        boxAddress = parseStringMetadata(msgReceived,BOX_IP_LABEL_PREFIX);
                    }
                    LogUtils.d("type：" + type + " address:" + address + " nettyPort:" + nettyPort +
                            " commandPort:" + commandPort + " downloadPort:" + downloadPort);
                }

                Session session = Session.get(SSDPService.this);
                int hd = session.getHotelid();
                LogUtils.d("savor:hotel ssdp 当前获取酒店id="+hotelId+"缓存酒店id="+hd);

                if(TYPE_SSDP_BOX.equals(type)) {
                    LogUtils.d("savor:ssdp 发现机顶盒广播ip地址--"+boxAddress);
                    if(!TextUtils.isEmpty(boxAddress)) {
                        TvBoxSSDPInfo oldInfo = session.getTvBoxSSDPInfo();
                        if(oldInfo==null||!String.valueOf(hotelId).equals(session.getHotelid())) {
                            TvBoxSSDPInfo tvBoxSSDPInfo = new TvBoxSSDPInfo(TextUtils.isEmpty(type)?"":type.toLowerCase()
                                    ,address,String.valueOf(commandPort),boxAddress,hotelId+"");
                            int hid = session.getHotelid();
                            if(hotelId!=hid) {
                                session.setHotelid(hotelId);
                                sendSpFoundReceiver();
                            }
                            session.setTvBoxSSDPInfo(tvBoxSSDPInfo);
                        }
                    }
                }else {
                    LogUtils.d("savor:ssdp 发现小平台保存ip成功"+address);
                    SmallPlatInfoBySSDP smallPlatInfoBySSDP = new SmallPlatInfoBySSDP(TextUtils.isEmpty(type)?"":type.toLowerCase(),address, String.valueOf(commandPort),hotelId);
                    session.setSmallPlatInfoBySSDP(smallPlatInfoBySSDP);
                    session.setSmallIp(address);
                    if(hotelId!=-1) {
                        session.setHotelid(hotelId);
                    }
                    sendSpFoundReceiver();

                }

            } while (isLooping);


        } catch (IOException e) {
            LogUtils.d("savor:ssdp ssdp异常，错误信息："+e.getMessage());
            e.printStackTrace();
        } finally {
            closeSocketReceive();
        }

        try {
            multicastLock.release();
        }catch (Exception e){
        }
    }

    private void sendSpFoundReceiver() {
        // 发送发现小平台广播
        Intent intent = new Intent(SensePresenter.SMALL_PLATFORM);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        sendBroadcast(intent);
    }

    private void closeSocketReceive() {
        if (mSocketReceive != null && !mSocketReceive.isClosed()) {
            mSocketReceive.close();
        }
    }

    private int parseIntMetadata(String data, String labelPrefix) {
        int metadata = -1;
        if (!TextUtils.isEmpty(data) && !TextUtils.isEmpty(labelPrefix)) {
            // Label开始
            int startIndex = data.indexOf(labelPrefix) + labelPrefix.length();
            // Label以换行结束时换行符的位置，endIndex可能是该项为message最末尾
            int endIndex = data.indexOf(CRLF, startIndex);
            if (startIndex >= 0 && (endIndex > startIndex || endIndex == -1)) {
                try {
                    if (endIndex == -1) {
                        metadata = Integer.parseInt(data.substring(startIndex).trim());
                    } else {
                        metadata = Integer.parseInt(data.substring(startIndex, endIndex).trim());
                    }
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
            }
        }
        return metadata;
    }

    private String parseStringMetadata(String data, String labelPrefix) {
        String metadata = null;
        if (!TextUtils.isEmpty(data) && !TextUtils.isEmpty(labelPrefix)&&data.indexOf(labelPrefix)!=-1) {
            // Label开始
            int startIndex = data.indexOf(labelPrefix) + labelPrefix.length();
            // Label以换行结束时换行符的位置，endIndex可能是该项为message最末尾
            int endIndex = data.indexOf(CRLF, startIndex);
            if (startIndex >= 0 && (endIndex > startIndex || endIndex == -1)) {
                metadata = data.substring(startIndex, endIndex).trim();
            }
        }
        return metadata;
    }

    @Override
    public void onDestroy() {
        LogUtils.d("savor:ssdp onDestroy关闭ssdp服务");
        super.onDestroy();
        isLooping = false;
        ProjectionManager.getInstance().setLookingSSDP(false);
        try {
            multicastLock.release();
        }catch (Exception e){}
    }
}