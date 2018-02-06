package com.savor.resturant.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.common.api.utils.AppUtils;
import com.common.api.utils.LogUtils;
import com.common.api.utils.ShowMessage;
import com.savor.resturant.R;
import com.savor.resturant.core.ApiRequestListener;
import com.savor.resturant.core.AppApi;
import com.savor.resturant.core.ResponseErrorMessage;
import com.savor.resturant.core.Session;
import com.savor.resturant.interfaces.IBaseView;
import com.savor.resturant.service.SSDPService;
import com.savor.resturant.utils.ActivitiesManager;
import com.savor.resturant.utils.ProjectionManager;
import com.savor.resturant.utils.StatusBarUtil;


public abstract class BaseFragmentActivity extends FragmentActivity implements IBaseView,ApiRequestListener {
	protected FrameLayout backFL;
	protected ImageView backIV;
	protected TextView backTV;
	protected FrameLayout nextFL;
	protected ImageView nextIV;
	protected TextView nextTV;
	protected TextView titleTV;
	
//	protected PictureUtils pictureUtils;
//	protected BitmapDisplayConfig config;
	private static final int MSG_STOP_SSDP = 0x5;
	protected Session mSession;
	protected Context mContext;
	private FrameLayout mParentLayout;
	private ContentLoadingProgressBar mLoadingPb;
	private Handler mHandler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
				case MSG_STOP_SSDP:
					if(ProjectionManager.getInstance().isLookingSSDP()) {
						stopSSdpService();
					}
					break;
			}
		}
	};
	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		mSession = Session.get(getApplicationContext());
		mContext = this;
		ActivitiesManager.getInstance().pushActivity(this);
//		StatusBarUtil.setStatusBarLightMode(getWindow());
//		EtagoClientApplication.setApplicationContext(this);
	}

//	@Override
//	public void setContentLayout(int resId) {
//		mParentLayout = (FrameLayout) findViewById(R.id.fl_parent);
//		mLoadingPb = (ContentLoadingProgressBar) findViewById(R.id.pb_loading);
//		View childView = View.inflate(this, resId, null);
//		mParentLayout.addView(childView,0);
//	}

	@Override
	public void showLoadingLayout() {
		mLoadingPb.setVisibility(View.VISIBLE);
	}

	@Override
	public void hideLoadingLayout() {
		mLoadingPb.setVisibility(View.GONE);
	}

	public void showToast(String message) {
		ShowMessage.showToastSavor(this,message);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		ActivitiesManager.getInstance().popActivity(this);
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
//			if(1006==code||12056==code) {
//				ActivityPagerUtils.launchLoginActivity(this);
//				mSession.signOut();
//
//				Intent intent = new Intent(this, RegisterGCMSNSService.class);
//				intent.setAction("com.etago.life.pushgcm");
//				startService(intent);
//			}
		}
	}

	@Override
	public void onNetworkFailed(AppApi.Action method) {

	}

	/**组播阻塞方式获取小平台发送的本身地址*/
	protected void startServerDiscoveryService() {
		LogUtils.d("savor:sp 当前wifi状态接受ssdp");
		Intent intent = new Intent(this, SSDPService.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startService(intent);

		mHandler.sendEmptyMessageDelayed(MSG_STOP_SSDP,1000*20);
	}

	protected void getSmallPlatformUrl() {
		//  判断是否获取到小平台地址，如果没有获取到请求云平台（小平台是局域网）获取小平台ip
		if(AppUtils.isWifiNetwork(this)) {
			LogUtils.d("savor:sp 当前wifi可用请求getip");
			AppApi.getSmallPlatformIp(this,this);
		}else {
			LogUtils.d("savor:sp 当前wifi状态不可用不请求getip");
		}
	}

	protected void restartService() {
		if(ProjectionManager.getInstance().isLookingSSDP()) {
			mHandler.removeMessages(MSG_STOP_SSDP);
			stopSSdpService();
		}
		startServerDiscoveryService();
		getSmallPlatformUrl();
	}
	public void stopSSdpService() {
		Intent intent = new Intent(this, SSDPService.class);
		stopService(intent);
	}
}
