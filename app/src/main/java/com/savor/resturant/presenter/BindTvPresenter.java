//package com.savor.resturant.presenter;
//
//import android.app.Activity;
//import android.app.ActivityManager;
//import android.content.Context;
//import android.content.Intent;
//import android.content.IntentFilter;
//import android.net.ConnectivityManager;
//import android.net.wifi.WifiInfo;
//import android.net.wifi.WifiManager;
//import android.os.Handler;
//import android.os.Message;
//import android.text.TextUtils;
//
//import com.common.api.utils.AppUtils;
//import com.common.api.utils.LogUtils;
//import com.savor.resturant.activity.IBindTvView;
//import com.savor.resturant.activity.LinkTvActivity;
//import com.savor.resturant.activity.MainActivity;
//import com.savor.resturant.activity.QRCodeScanActivity;
//import com.savor.resturant.bean.SmallPlatInfoBySSDP;
//import com.savor.resturant.bean.SmallPlatformByGetIp;
//import com.savor.resturant.bean.TvBoxInfo;
//import com.savor.resturant.bean.TvBoxSSDPInfo;
//import com.savor.resturant.core.ApiRequestListener;
//import com.savor.resturant.core.AppApi;
//import com.savor.resturant.core.ResponseErrorMessage;
//import com.savor.resturant.core.Session;
//import com.savor.resturant.receiver.NetworkConnectChangedReceiver;
//import com.savor.resturant.service.SSDPService;
//import com.savor.resturant.utils.ActivitiesManager;
//import com.savor.resturant.utils.WifiUtil;
//
//import java.util.List;
//
//import static com.savor.resturant.core.AppApi.Action.GET_CALL_CODE_BY_BOXIP_JSON;
//
///**
// * 绑定电视主导器
// * Created by hezd on 2016/12/16.
// */
//public class BindTvPresenter extends BasePresenter implements ApiRequestListener {
//    private static final int START_LINKTV = 100;
//    private static final int CLOSE_DIALOG = 101;
//    /**清除机顶盒信息*/
//    private static final int REMOVE_BOX_INFO = 108;
//    private static final int SAVE_BOX_INFO = 109;
//    private static final int CLOSE_SSDP_SERVICE = 110;
//    private NetworkConnectChangedReceiver mChangedReceiver;
//    private Handler mHandler = new Handler(){
//        @Override
//        public void handleMessage(Message msg) {
//            switch (msg.what) {
//                case SAVE_BOX_INFO:
//                    Object obj = msg.obj;
//                    if(obj instanceof TvBoxInfo) {
//                        TvBoxInfo tvBoxInfo = (TvBoxInfo) obj;
//                        mSession.setTvBoxUrl(tvBoxInfo);
//                        mBindTvView.initBindcodeResult();
//                    }
//                    break;
//                case REMOVE_BOX_INFO:
//                    mSession.setTvBoxUrl(null);
//                    break;
//                case CLOSE_SSDP_SERVICE:
//                    LogUtils.d("savor:ssdp 搜索15秒，关闭ssdp服务");
//                    Intent ssdpIntent = new Intent(mContext, SSDPService.class);
//                    mContext.stopService(ssdpIntent);
//                    break;
//                case WifiManager.WIFI_STATE_ENABLED:
//                    LogUtils.d("savor:网络变为可用");
//                    // 为了解决多次重复发送请求利用延时发送方式
////                    getSmallPlatformUrl();
//                    break;
//                case WifiManager.WIFI_STATE_DISABLED:
//                    List<Object> requsetPool = mSession.getRequsetPool();
//                    requsetPool.clear();
//                    mSession.setRequestPool(requsetPool);
//                    LogUtils.d("savor:hotel 网络不可用重置酒店id为0");
//                    resetLinkStatus();
//                    break;
//                case START_LINKTV:
//                    if(mBindTvView!=null) {
//                        mBindTvView.closeQrcodeDialog();
//                        String topNameActivity = getTopActivityName(mContext);
//                        if(!topNameActivity.equals(LinkTvActivity.class.getClass().getName())){
//                            mBindTvView.startLinkTv();
//                        }
//                    }
//                case CLOSE_DIALOG:
//                    if(mBindTvView!=null)
//                        mBindTvView.closeQrcodeDialog();
//                    break;
//
//            }
//        }
//    };
//
//    /**处理三位数字结果*/
//    public void handleBindCodeResult(TvBoxInfo tvBoxInfo) {
//        if (tvBoxInfo == null) {
//            mBindTvView.showToast("无效的机顶盒信息");
//            return;
//        }
//
//        // 将机顶盒内容缓存到session，延时3分钟清除。每当应用切回到前台，判断ssid是否与当前相同如果相同
//        // 绑定成功
//        mSession.setTvBoxInfo(tvBoxInfo);
//        mHandler.removeMessages(REMOVE_BOX_INFO);
//        mHandler.sendEmptyMessageDelayed(REMOVE_BOX_INFO,3*60*1000);
//
//        WifiManager wifiManger = (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);
//        WifiInfo wifiInfo = wifiManger.getConnectionInfo();
//        String wifiName = WifiUtil.getWifiName(mContext);
//        if(!TextUtils.isEmpty(wifiName)) {
//            wifiName = wifiName.replace("\"","");
//        }
//        String sid = tvBoxInfo.getSsid();
//        String localIp = intToIp(wifiInfo.getIpAddress());
//        String ipStr = tvBoxInfo.getBox_ip();
//        mSession.setLocalIp(localIp);
//        mSession.setWifiSsid(sid);
////        // 如果判断不在一个网段或者wifi不是同一个提示切换wifi
//        if (TextUtils.isEmpty(ipStr)||!localIp.substring(0,localIp.lastIndexOf(".")).equals(ipStr.substring(0, ipStr.lastIndexOf(".")))
//                ||TextUtils.isEmpty(wifiName)
//                ||!wifiName.equals(sid)) {
//            mBindTvView.showChangeWifiDialog(mSession.getSsid());
//            return;
//        }
//
//        // 扫码完判断如果酒店id和之前酒店id不同则刷新热点数据
//        Message obtain = Message.obtain();
//        obtain.what = SAVE_BOX_INFO;
//        obtain.obj = tvBoxInfo;
//        mHandler.sendMessage(obtain);
//    }
//
//    private void resetLinkStatus() {
//        mSession.resetPlatform();
//        mSession.setTvBoxSSDPInfo(null);
//        mSession.setSmallPlatInfoByGetIp(null);
//        mSession.setSmallPlatInfoBySSDP(null);
//        mSession.setHotelid(0);
//        mSession.setWifiSsid(null);
//        refreshLinkWifiHint();
//    }
//
//    private IBindTvView mBindTvView;
//    private boolean mOpenQrCode;
//    private int callQrcodeErrorCount;
//    private int errorMax;
//    private String errorMsg;
//
//    /**
//     * 更新首页当前连接wifi状态
//     */
//    private void refreshLinkWifiHint() {
//        Activity currentActivity = ActivitiesManager.getInstance().getCurrentActivity();
//        if(currentActivity instanceof MainActivity) {
//            MainActivity mainActivity = (MainActivity) currentActivity;
//            mainActivity.initWIfiHint();
//        }
//    }
//
//    private Session mSession;
//
//    public BindTvPresenter(Context context,IBindTvView bindTvView) {
//        super(context,null);
//        this.mBindTvView = bindTvView;
//    }
//
//    @Override
//    public void onCreate() {
//        mSession = Session.get(mContext);
//    }
//
//    public void registerNetWorkReceiver() {
//        if(mChangedReceiver==null)
//            mChangedReceiver = new NetworkConnectChangedReceiver(mHandler);
//        IntentFilter filter = new IntentFilter();
//        filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
//        filter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
//        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
//        mContext.registerReceiver(mChangedReceiver, filter);
//    }
//
//    /**
//     * 设置两个标识扫码页面是否已打开和当前失败次数。
//     * 首先重置状态，然后如果页面已经打开不在重新打开页面。
//     * 如果失败count为指定次数在提示扫码失败
//     *
//     */
//    public void bindTv() {
//        if(AppUtils.isWifiNetwork(mContext)) {
//            if(!AppUtils.isNetworkAvailable(mContext)) {
//                mBindTvView.showToast("网络不可用");
//                return;
//            }
//            mBindTvView.readyForQrcode();
//            callQrcode();
//        }else {
//            mBindTvView.showChangeWifiDialog(mSession.getSsid());
//        }
//    }
//
//    public void callQrcode() {
//        resetCallQrcodeState();
//        //  1.小平台ssdp机顶盒ssdp 云平台getip都获取到，进行三次呼玛errormax 3
//        SmallPlatInfoBySSDP smallPlatInfoBySSDP = mSession.getSmallPlatInfoBySSDP();
//        if(smallPlatInfoBySSDP!=null
//                &&!TextUtils.isEmpty(smallPlatInfoBySSDP.getServerIp())
//                &&!TextUtils.isEmpty(smallPlatInfoBySSDP.getType())) {
//            errorMax++;
//            AppApi.callQrcodeBySPSSDP(mContext,this);
//        }
//
//        SmallPlatformByGetIp smallPlatformByGetIp = mSession.getmSmallPlatInfoByIp();
//        if(smallPlatformByGetIp!=null
//                &&!TextUtils.isEmpty(smallPlatformByGetIp.getLocalIp())
//                &&!TextUtils.isEmpty(smallPlatformByGetIp.getType())) {
//            errorMax++;
//            AppApi.callQrcodeByClound(mContext,this);
//        }
//
//        TvBoxSSDPInfo tvBoxSSDPInfo = mSession.getTvBoxSSDPInfo();
//        if(tvBoxSSDPInfo!=null
//                &&!TextUtils.isEmpty(tvBoxSSDPInfo.getServerIp())) {
//            errorMax++;
//            AppApi.callQrcodeFromBoxInfo(mContext,tvBoxSSDPInfo,this);
//        }
//
//        if(tvBoxSSDPInfo!=null
//                &&!TextUtils.isEmpty(tvBoxSSDPInfo.getBoxIp())) {
//            errorMax++;
//            String boxUrl = "http://"+tvBoxSSDPInfo.getBoxIp()+":8080";
//            AppApi.callCodeByBoxIp(mContext,boxUrl,this);
//        }
//
//    }
//
//    /**
//     * 重置扫码状态
//     * mOpenQrCode 是否已经打开扫码页面，false未打开，true已打开
//     * callQrcodeErrorCount 当前扫码请求失败次数，如果为2（点对点和小平台请求扫码）呼码失败
//     */
//    private void resetCallQrcodeState() {
//        mOpenQrCode = false;
//        callQrcodeErrorCount = 0;
//        errorMax = 0;
//    }
//
//    /**当应用切回前台时重新获取小平台地址
//     * */
//    public void getSmallPlatformUrl() {
//        if(!AppUtils.isWifiNetwork(mContext)) {
//            LogUtils.d("savor:sp 当前wifi不可用请求getip，不启动ssdp获取酒店id");
//            mSession.setHotelid(0);
//            return;
//        }
//
//        LogUtils.d("savor:sp 当前wifi状态getip ，ssdp获取hotelid");
//        // 组播获取小平台地址
//        Intent intent = new Intent(mContext, SSDPService.class);
//        mContext.startService(intent);
//        mHandler.sendEmptyMessageDelayed(CLOSE_SSDP_SERVICE,15*1000);
//
//        //  判断是否获取到小平台地址，如果没有获取到请求云平台（小平台是局域网）获取小平台ip
//        //请求接口
//        AppApi.getSmallPlatformIp(mContext,this);
//    }
//
//    /**
//     * 格式化ip
//     *
//     * @param i
//     * @return
//     */
//    public String intToIp(int i) {
//        return (i & 0xFF) + "." + ((i >> 8) & 0xFF) + "." + ((i >> 16) & 0xFF) + "." + (i >> 24 & 0xFF);
//    }
//
//
//    @Override
//    public void onDestroy() {
//        mViewCallBack = null;
//        if(mChangedReceiver!=null) {
//            mContext.unregisterReceiver(mChangedReceiver);
//            mChangedReceiver = null;
//        }
//
//        Intent intent = new Intent(mContext,SSDPService.class);
//        mContext.stopService(intent);
//    }
//
//    private void handleErroResponse(AppApi.Action method, Object obj) {
//        if(obj instanceof ResponseErrorMessage) {
//            if(method != GET_CALL_CODE_BY_BOXIP_JSON) {
//                ResponseErrorMessage msg = (ResponseErrorMessage) obj;
//                String message = msg.getMessage();
//                errorMsg = message;
//            }else {
//                errorMsg = "呼码失败";
//            }
//        }else {
//            errorMsg = "呼码失败";
//        }
//    }
//
//    /**
//     * 处理呼码失败
//     */
//    private void handleCallQrcodeError() {
//        callQrcodeErrorCount++;
//        if(callQrcodeErrorCount == errorMax) {
//            if(mBindTvView!=null) {
//                mBindTvView.showToast(errorMsg);
//                mHandler.sendEmptyMessage(CLOSE_DIALOG);
//            }
//        }
//    }
//    /**
//     * 处理扫码成功
//     */
//    private void handleQrcodeSuccess() {
//        if(!mOpenQrCode) {
//            if(!mOpenQrCode) {
//                mHandler.removeMessages(START_LINKTV);
//                mHandler.sendEmptyMessage(START_LINKTV);
//                mOpenQrCode = true;
//            }
//        }
//    }
//    @Override
//    public void onSuccess(AppApi.Action method, Object obj) {
//        switch (method) {
//            case GET_CALL_CODE_BY_BOXIP_JSON:
//            case GET_CALL_QRCODE_JSON:
//                handleQrcodeSuccess();
//                break;
//            case GET_SAMLL_PLATFORMURL_JSON:
//                // 获取小平台地址
//                if(obj instanceof SmallPlatformByGetIp) {
//                    SmallPlatformByGetIp cloudServerInfo = (SmallPlatformByGetIp) obj;
//                    LogUtils.d("savor:hotel sp info:"+cloudServerInfo);
//                    if (cloudServerInfo != null) {
//                        String hotelId = cloudServerInfo.getHotelId();
//                        // 保存酒店id
//                        int hid = 0;
//                        try {
//                            hid = Integer.valueOf(hotelId);
//                        }catch (Exception e) {
//                        }
//
//                        // 如果hotelid不同并且isneedrefresh为true
//                        if(hid != mSession.getHotelid()) {
//                            if(hid>0) {
//                                mSession.setHotelid(hid);
//                            }
//                        }
//                        // 保存小平台信息
//                        mSession.setSmallPlatInfoByGetIp(cloudServerInfo);
//                    }
//                }
//                break;
//        }
//
//    }
//
//    @Override
//    public void onError(AppApi.Action method, Object obj) {
//        switch (method) {
//            case GET_SAMLL_PLATFORMURL_JSON:
//                LogUtils.d("savor:hotel getIp获取失败");
//                break;
//            case GET_CALL_QRCODE_JSON:
//            case GET_CALL_CODE_BY_BOXIP_JSON:
//                handleErroResponse(method, obj);
//                handleCallQrcodeError();
//                break;
//        }
//    }
//
//    @Override
//    public void onNetworkFailed(AppApi.Action method) {
//
//    }
//
//    private String getTopActivityName(Context context) {
//        ActivityManager manager = ((ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE));
//        //getRunningTasks() is deprecated since API Level 21 (Android 5.0)
//        List localList = manager.getRunningTasks(1);
//        ActivityManager.RunningTaskInfo localRunningTaskInfo = (ActivityManager.RunningTaskInfo)localList.get(0);
//        return localRunningTaskInfo.topActivity.getClassName();
//    }
//
//    public void removeTvBoxInfo() {
//        mHandler.removeMessages(REMOVE_BOX_INFO);
//        mHandler.sendEmptyMessage(REMOVE_BOX_INFO);
//    }
//
//}
