package com.savor.resturant.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.text.TextUtils;
import android.view.View;
import android.widget.FrameLayout;

import com.common.api.utils.LogUtils;
import com.common.api.utils.ShowMessage;
import com.common.api.utils.ShowProgressDialog;
import com.savor.resturant.R;
import com.savor.resturant.bean.SmallPlatInfoBySSDP;
import com.savor.resturant.bean.SmallPlatformByGetIp;
import com.savor.resturant.bean.TvBoxInfo;
import com.savor.resturant.bean.TvBoxSSDPInfo;
import com.savor.resturant.core.ApiRequestListener;
import com.savor.resturant.core.AppApi;
import com.savor.resturant.core.ResponseErrorMessage;
import com.savor.resturant.core.Session;
import com.savor.resturant.interfaces.IBaseView;
import com.savor.resturant.receiver.NetworkConnectChangedReceiver;
import com.savor.resturant.service.SSDPService;
import com.savor.resturant.utils.ActivitiesManager;
import com.savor.resturant.utils.ProjectionManager;
import com.savor.resturant.utils.StatusBarUtil;
import com.savor.resturant.utils.WifiUtil;
import com.savor.resturant.widget.CommonDialog;
import com.savor.resturant.widget.HotsDialog;
import com.savor.resturant.widget.LinkingDialog;
import com.umeng.analytics.MobclickAgent;

import static com.savor.resturant.activity.LinkTvActivity.EXRA_TV_BOX;
import static com.savor.resturant.activity.LinkTvActivity.EXTRA_TV_INFO;

/**
 * Created by hezd on 2016/12/13.
 */
public abstract class BaseActivity extends Activity implements ApiRequestListener,IBaseView {

    private static final int CLOSE_SSDP_SERVICE = 0x1;
    /**取消检测wifi*/
    private static final int CANCEL_CHECK_WIFI = 0x103;
    private static final int CHECK_WIFI_LINKED = 0x102;
    protected Session mSession;
    protected Activity mContext;
    private FrameLayout mParentLayout;
    private ContentLoadingProgressBar mLoadingPb;
    private ProgressDialog mProgressDialog;
    private CommonDialog mHintDialog;
    private LinkingDialog mLinkingDialog;

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case CLOSE_SSDP_SERVICE:
                    LogUtils.d("savor:ssdp base超过15s关闭ssdpservice");
                    stopSSdpService();
                    break;
                case CANCEL_CHECK_WIFI:
                    cancelWifiCheck();
                    break;
                case CHECK_WIFI_LINKED:
                    checkWifiLinked();
                    break;
            }
        }
    };
    protected NetworkConnectChangedReceiver mChangedReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 统计应用启动数据,如果不调用此方法，将会导致按照"几天不活跃"条件来推送失效。
        mSession = Session.get(getApplicationContext());
        mContext = this;
        ActivitiesManager.getInstance().pushActivity(this);
        StatusBarUtil.setStatusBarLightMode(getWindow());
    }

    @Override
    public void showLoadingLayout() {
        if(mProgressDialog == null)
            mProgressDialog = ShowProgressDialog.showProgressDialog(getApplicationContext(), AlertDialog.THEME_HOLO_LIGHT, getString(R.string.loading_hint));
    }

    @Override
    public void hideLoadingLayout() {
        if(mProgressDialog!=null&&mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
            mProgressDialog = null;
        }
    }

    public void showChangeWifiDialog(String ssid) {
        String hint = TextUtils.isEmpty(ssid)?getString(R.string.tv_bind_wifi):"请将wifi连接至"+ssid;
        new HotsDialog(this)
                .builder()
                .setMsg(hint)
                .setCancelable(false)
                .setPositiveButton("去设置", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        startCheckWifiLinkedTimer();

                        cancelWifiCheckDelayed();
                        Intent intent = new Intent();
                        intent.setAction("android.net.wifi.PICK_WIFI_NETWORK");
                        startActivity(intent);
                    }
                })
                .setNegativeButton("取消", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                    }
                })
                .show();
    }

    private void cancelWifiCheckDelayed() {
        mHandler.removeMessages(CANCEL_CHECK_WIFI);
        mHandler.sendEmptyMessageDelayed(CANCEL_CHECK_WIFI,1000*60*3);
    }

    public void registerNetWorkReceiver(Handler handler) {
        if(mChangedReceiver==null)
            mChangedReceiver = new NetworkConnectChangedReceiver(handler);
        IntentFilter filter = new IntentFilter();
        filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        filter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        mContext.registerReceiver(mChangedReceiver, filter);
    }
    public void reLink() {}

    public void stopSSdpService() {
        Intent intent = new Intent(this, SSDPService.class);
        stopService(intent);
    }

    /**
     * 展示正在连接包间弹窗
     */
    public void showLinkingDialog() {
        if(mLinkingDialog == null) {
            mLinkingDialog = new LinkingDialog(this);
        }
        mLinkingDialog.show();
    }

    public void closeLinkingDialog() {
        if(mLinkingDialog!=null) {
            mLinkingDialog.dismiss();
        }
    }

    /**
     * 当交互时需要重新获取ssdp，比如首页点击退出投屏
     */
    public void startSSDPServiceInOperation() {
        boolean lookingSSDP = ProjectionManager.getInstance().isLookingSSDP();
        if(lookingSSDP) {
            stopSSdpService();
        }
        Intent intent = new Intent(this, SSDPService.class);
        intent.putExtra("type","operation");
        startService(intent);

        mHandler.removeMessages(CLOSE_SSDP_SERVICE);
        mHandler.sendEmptyMessageDelayed(CLOSE_SSDP_SERVICE,15*1000);
    }

    public void showToast(String message) {
        if(this.isFinishing()) {
            return;
        }
        if(mHintDialog == null) {
            mHintDialog = new CommonDialog(this,message);
        }
        mHintDialog.setContent(message);
        mHintDialog.show();
    }

    protected void showErrorToast(Object obj, String defaultMsg) {
        if (obj instanceof ResponseErrorMessage) {
            ResponseErrorMessage errorMessage = (ResponseErrorMessage) obj;
            if (!TextUtils.isEmpty(errorMessage.getMessage())) {
                defaultMsg = errorMessage.getMessage();
            }
        }
        ShowMessage.showToastSavor(getApplicationContext(), defaultMsg);
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
        MobclickAgent.onPageStart(this.getClass().getName());
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
        MobclickAgent.onPageEnd(this.getClass().getName());
    }

    /**
     * 是否获取到小平台地址或盒子ip地址
     * @return
     */
    public boolean isFoundTv() {
        SmallPlatformByGetIp smallPlatformByGetIp = mSession.getmSmallPlatInfoByIp();
        SmallPlatInfoBySSDP smallPlatInfoBySSDP = mSession.getSmallPlatInfoBySSDP();
        TvBoxSSDPInfo tvBoxSSDPInfo = mSession.getTvBoxSSDPInfo();
        TvBoxInfo tvboxInfo = mSession.getTvboxInfo();
        if(smallPlatformByGetIp!=null&&!TextUtils.isEmpty(smallPlatformByGetIp.getLocalIp())) {
            return true;
        }

        if(smallPlatInfoBySSDP!=null&&!TextUtils.isEmpty(smallPlatInfoBySSDP.getServerIp())) {
            return true;
        }

        if(tvBoxSSDPInfo!=null&&!TextUtils.isEmpty(tvBoxSSDPInfo.getServerIp())) {
            return true;
        }

        if(tvBoxSSDPInfo!=null&&!TextUtils.isEmpty(tvBoxSSDPInfo.getBoxIp())) {
            return true;
        }

        if(tvboxInfo!=null&&!TextUtils.isEmpty(tvboxInfo.getBox_ip())) {
            return true;
        }
        return false;
    }

    @Override
    public void onSuccess(AppApi.Action method, Object obj) {

    }

    @Override
    public void onError(AppApi.Action method, Object obj) {

        if(obj instanceof ResponseErrorMessage) {
            ResponseErrorMessage message = (ResponseErrorMessage) obj;
            int code = message.getCode();
            String msg = message.getMessage();
            showToast(msg);
        }else if(obj == AppApi.ERROR_TIMEOUT) {
            showToast("网络连接超时，请重试");
        }else if(obj == AppApi.ERROR_NETWORK_FAILED) {
            showToast("网络已断开请检查");
        }
    }

    @Override
    public void onNetworkFailed(AppApi.Action method) {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivitiesManager.getInstance().popActivity(this);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
//        // 判断如果为绑定并且当前ssid与缓存机顶盒ssid相同提示绑定成功
//        if(!mSession.isBindTv()) {
//            TvBoxInfo tvBoxInfo = mSession.getTvboxInfo();
//            if(tvBoxInfo!=null) {
//                checkWifiLinked(tvBoxInfo);
//            }
//        }
    }

    /**
     * 检查是否在同一wifi，如果三分钟内连接到同一wifi提示连接成功
     */
    protected void checkWifiLinked() {
    }

    /**开启检查是否是同一wifi定时器每隔一秒检查一次*/
    protected void startCheckWifiLinkedTimer() {
        mHandler.removeMessages(CHECK_WIFI_LINKED);
        mHandler.sendEmptyMessageDelayed(CHECK_WIFI_LINKED,1000);
    }

    public void initBindcodeResult() {
        Activity currentActivity = ActivitiesManager.getInstance().getCurrentActivity();
        if(currentActivity instanceof MainActivity) {
            MainActivity mainActivity = (MainActivity) currentActivity;
            mainActivity.initWIfiHint();
        }
        ShowMessage.showToast(this,mSession.getSsid()+"连接成功，可以投屏");
    }

    protected void cancelWifiCheck(){
        mHandler.removeMessages(CHECK_WIFI_LINKED);
        mHandler.removeMessages(CANCEL_CHECK_WIFI);
    }

}
