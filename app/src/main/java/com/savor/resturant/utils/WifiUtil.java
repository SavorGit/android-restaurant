package com.savor.resturant.utils;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.text.TextUtils;

/**
 * Created by hezd on 2017/2/11.
 */

public class WifiUtil {

    /**
     * 本机ip地址跟盒子是否同一网段
     * @param locaIp
     * @param boxIp
     * @return
     */
    public static boolean isInSameNetwork(String locaIp,String boxIp) {
        // 如果判断不在一个网段或者wifi不是同一个提示切换wifi
        if (TextUtils.isEmpty(boxIp)||!locaIp.substring(0,locaIp.lastIndexOf(".")).equals(boxIp.substring(0, boxIp.lastIndexOf(".")))) {
            return false;
        }
        return true;
    }

    public static String getLocalIp(Context context) {
        WifiManager wifiManger = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManger.getConnectionInfo();
        String localIp = intToIp(wifiInfo.getIpAddress());
        return localIp;
    }

    public  static String getWifiName(Context context) {
        WifiManager wifiMgr = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        WifiInfo info = wifiMgr.getConnectionInfo();
        String wifiId = info != null ? info.getSSID() : null;
        if(!TextUtils.isEmpty(wifiId)) {
            wifiId = wifiId.replace("\"","");
        }
        return wifiId;
    }

    public static boolean checkWifiState(Context context) {
        WifiManager wifiManger = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        return wifiManger.getWifiState()==WifiManager.WIFI_STATE_ENABLED;
    }

    public static boolean isHotelNewWork(Context context) {
        WifiManager wifiManger = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        //获取wifi服务
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        //判断wifi是否开启
        if (!wifiManager.isWifiEnabled()) {
            return false;
        }else {
            WifiInfo wifiInfo = wifiManger.getConnectionInfo();
            String localIp = intToIp(wifiInfo.getIpAddress());
            if("192.168.43".equals(localIp))
                return true;
            else
                return false;
        }
    }
    private static String intToIp(int i) {

        return (i & 0xFF ) + "." +
                ((i >> 8 ) & 0xFF) + "." +
                ((i >> 16 ) & 0xFF) + "." ;
    }
}
