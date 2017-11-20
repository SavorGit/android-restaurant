package com.savor.resturant.activity;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Process;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RemoteViews;
import android.widget.TextView;

import com.common.api.http.callback.FileDownProgress;
import com.common.api.utils.AppUtils;
import com.common.api.utils.LogUtils;
import com.common.api.utils.ShowMessage;
import com.savor.resturant.R;
import com.savor.resturant.adapter.CategoryAdapter;
import com.savor.resturant.bean.CategoryItemVo;
import com.savor.resturant.bean.TvBoxInfo;
import com.savor.resturant.bean.TvBoxSSDPInfo;
import com.savor.resturant.bean.UpgradeInfo;
import com.savor.resturant.core.AppApi;
import com.savor.resturant.presenter.BindTvPresenter;
import com.savor.resturant.presenter.SensePresenter;
import com.savor.resturant.service.ClearImageCacheService;
import com.savor.resturant.service.LocalJettyService;
import com.savor.resturant.service.SSDPService;
import com.savor.resturant.utils.ActivitiesManager;
import com.savor.resturant.utils.ConstantValues;
import com.savor.resturant.utils.GlideImageLoader;
import com.savor.resturant.utils.STIDUtil;
import com.savor.resturant.utils.SlideManager;
import com.savor.resturant.utils.WifiUtil;
import com.savor.resturant.widget.HotsDialog;
import com.savor.resturant.widget.SavorDialog;
import com.savor.resturant.widget.UpgradeDialog;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.savor.resturant.activity.LinkTvActivity.EXRA_TV_BOX;
import static com.savor.resturant.activity.LinkTvActivity.EXTRA_TV_INFO;

/**
 * 首页功能操作列表
 */
public class MainActivity extends BaseActivity implements View.OnClickListener, IBindTvView {
    public static final String SMALL_PLATFORM = "small_platform";
    /**退出投屏按钮状态，退出投屏*/
    private static final int TYPE_GO_SETTINGS = 0x1;
    /**退出投屏按钮状态，去设置*/
    private static final int TYPE_STOP_PRO = 0x2;
    /**退出投屏*/
    private static final int TYPE_LINK_TV = 0x3;
    private static final int CHECK_LINK_STATUS = 1;
    public static final String ACTION_TV_LINK = "action_tv_link";
    private TextView tv_center;
    private ImageView iv_left;
    private ListView listView;
    private TextView connectTipTV;
    private TextView operationBtnTV;
    private CategoryAdapter categoryAdapter=null;
    private List<CategoryItemVo> mList = new ArrayList<>();
    private BindTvPresenter mBindTvPresenter;
    private UpgradeInfo upGradeInfo;
    private UpgradeDialog mUpgradeDialog;
    private NotificationManager manager;
    private Notification notif;
    private final int NOTIFY_DOWNLOAD_FILE=10001;
    /**是否是自动检查更新，如果不是那就是手动检查提示有版本更新否则不提示*/
    private boolean ismuteUp = true;
    /**退出投屏按钮状态，当前展示退出投屏还是去设置*/
    private int mProBtnType;
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case CHECK_LINK_STATUS:
                    checkLinkStatus();
                    break;
            }
        }
    };
    private SmallPlatformReciver smallPlatformReciver;
    private SavorDialog mQrcodeDialog;
    private long exitTime;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getViews();
        setViews();
        setListeners();
        initPresenter();
        regitsterSmallPlatformReciever();
        registerNetWorkReceiver();
        upgrade();
    }
    /**
     * 注册小平台发现广播
     */
    public void regitsterSmallPlatformReciever() {
        smallPlatformReciver = new SmallPlatformReciver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(SMALL_PLATFORM);
        mContext.registerReceiver(smallPlatformReciver,filter);
    }

    private void checkLinkStatus() {
        TvBoxSSDPInfo tvBoxSSDPInfo = mSession.getTvBoxSSDPInfo();
        if(tvBoxSSDPInfo!=null && !TextUtils.isEmpty(tvBoxSSDPInfo.getBoxIp())) {
            closeLinkingDialog();
            AppApi.notifyTvBoxStop(mContext,mSession.getTVBoxUrl(),this);
        }else {
            showLinkErrorDialog();
        }
    }

    public void showLinkErrorDialog() {
        if(!this.isFinishing()){
            new HotsDialog(this)
                    .builder()
                    .setMsg("连接失败，请重新连接")
                    .setCancelable(false)
                    .setPositiveButton("重新连接", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            reLink();
                        }
                    })
                    .setNegativeButton("取消", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            closeLinkingDialog();
                        }
                    })
                    .show();
        }

    }

    @Override
    public void reLink() {
        linkTv();
    }


    private void initPresenter() {
        mBindTvPresenter = new BindTvPresenter(this,this);
    }

    /**
     * 注册监控网络状态改变广播
     */
    private void registerNetWorkReceiver() {
        mBindTvPresenter.registerNetWorkReceiver();
    }

    @Override
    public void getViews() {
        iv_left = (ImageView) findViewById(R.id.iv_left);
        tv_center = (TextView)findViewById(R.id.tv_center);
        listView = (ListView) findViewById(R.id.category_list);
        connectTipTV = (TextView)findViewById(R.id.connect_tip);
        operationBtnTV = (TextView)findViewById(R.id.operation_btn);
    }

    @Override
    public void setViews() {
        iv_left.setVisibility(View.GONE);
        tv_center.setText(getString(R.string.app_name));

        initWIfiHint();
        mList.clear();
        CategoryItemVo categoryItemVo = new CategoryItemVo();
        categoryItemVo.setId(ConstantValues.CATEGORY_SLIDE);
        categoryItemVo.setName("图片与幻灯片");
        categoryItemVo.setState(1);
        mList.add(categoryItemVo);

        categoryItemVo = new CategoryItemVo();
        categoryItemVo.setId(ConstantValues.CATEGORY_VIDEO);
        categoryItemVo.setName("视频");
        categoryItemVo.setState(1);
        mList.add(categoryItemVo);

        categoryItemVo = new CategoryItemVo();
        categoryItemVo.setId(ConstantValues.CATEGORY_FILE);
        categoryItemVo.setName("文件");
        categoryItemVo.setState(0);
        mList.add(categoryItemVo);

        categoryAdapter = new CategoryAdapter(mContext,mList);
        listView.setAdapter(categoryAdapter);
    }


    @Override
    protected void onRestart() {
        super.onRestart();
        initWIfiHint();
    }

    public void initWIfiHint() {
        String btnText = "";
        if (isFoundTv()){
            boolean bindTv = mSession.isBindTv();
            if(!bindTv) {
                mProBtnType = TYPE_LINK_TV;
                btnText = "连接电视";
                connectTipTV.setText(R.string.found_tv_hint);
                connectTipTV.setOnClickListener(null);
            }else {
                mProBtnType = TYPE_STOP_PRO;
                connectTipTV.setText("已连接"+ WifiUtil.getWifiName(mContext)+",点击断开连接>>");
                btnText = "退出投屏";
                connectTipTV.setOnClickListener(this);
            }
        }else {
            mProBtnType = TYPE_GO_SETTINGS;
            connectTipTV.setText("请连接包间WIFI后进行操作");
            btnText = "去设置";
            connectTipTV.setOnClickListener(null);
        }
        operationBtnTV.setText(btnText);
    }

    @Override
    public void setListeners() {
        listView.setOnItemClickListener(itemClickListener);
        operationBtnTV.setOnClickListener(this);
    }

    AdapterView.OnItemClickListener itemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
           CategoryItemVo categoryItemVo = (CategoryItemVo) parent.getItemAtPosition(position);
            if (categoryItemVo!=null){
                Intent intent;
                switch (categoryItemVo.getId()){
                    case ConstantValues.CATEGORY_SLIDE:
                        intent = new Intent(mContext,SlideListActivity.class);
                        intent.putExtra("type", SlideManager.SlideType.IMAGE);
                        startActivity(intent);
                    case ConstantValues.CATEGORY_VIDEO:
                        intent = new Intent(mContext,SlideListActivity.class);
                        intent.putExtra("type", SlideManager.SlideType.VIDEO);
                        startActivity(intent);
                        break;
                    case ConstantValues.CATEGORY_FILE:
                        ShowMessage.showToast(mContext,"该功能暂未开通,敬请期待");
                        break;
                }
            }
        }
    };

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.operation_btn:
                buttonStopProClick();
                break;
            case R.id.connect_tip:
                unLinkTv();
                break;
        }
    }

    private void unLinkTv() {
        new HotsDialog(this).builder().setMsg("是否与电视断开，\n断开后将无法投屏？").setTitle("提示").setPositiveButton("断开连接", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSession.resetPlatform();
                initWIfiHint();
                mBindTvPresenter.removeTvBoxInfo();
            }
        }) .setNegativeButton("取消", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                    }
                }).show();
    }



    private void buttonStopProClick() {
        if(mProBtnType==TYPE_STOP_PRO) {
            if(AppUtils.isWifiNetwork(mContext)) {
                TvBoxSSDPInfo tvBoxSSDPInfo = mSession.getTvBoxSSDPInfo();
                if(tvBoxSSDPInfo!=null && !TextUtils.isEmpty(tvBoxSSDPInfo.getBoxIp())) {
                    // 退出投屏
                    new HotsDialog(this).builder().setMsg("是否退出\""+WifiUtil.getWifiName(mContext)+"\"包间的投屏吗?").setPositiveButton("确定", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            AppApi.notifyTvBoxStop(mContext,mSession.getTVBoxUrl(),MainActivity.this);
                        }
                    }) .setNegativeButton("取消", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                        }
                    }).show();

                }else {
                    // 连接电视
                    linkTv();
                }
            }else {
                showChangeWifiDialog();
            }
        }else if(mProBtnType == TYPE_GO_SETTINGS) {
            Intent intent = new Intent();
            intent.setAction("android.net.wifi.PICK_WIFI_NETWORK");
            startActivity(intent);
        }else if(mProBtnType == TYPE_LINK_TV){
            mBindTvPresenter.bindTv();
        }
    }

    private void linkTv() {
//        showLinkingDialog();
        startSSDPServiceInOperation();
        checkLinkStatusDelayed();
    }

    /**
     * 15秒以后如果还没有收到ssdp，提示连接失败，重新连接
     */
    private void checkLinkStatusDelayed() {
        mHandler.sendEmptyMessageDelayed(CHECK_LINK_STATUS,15*1000);
    }

    public void onTvLiked() {
        LogUtils.d("savor:ssdp onTvLinked");
        AppApi.notifyTvBoxStop(this,mSession.getTVBoxUrl(),this);
    }

    private View.OnClickListener forUpdateListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            mUpgradeDialog.dismiss();
            downLoadApk(upGradeInfo.getOss_addr());
            // downLoadApk("http://download.savorx.cn/app-xiaomi-debug.apk");
        }
    };

    private View.OnClickListener cancelListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
//			mMDialog.dismiss();
            mUpgradeDialog.dismiss();
        }
    };
    private void upgrade(){
        AppApi.Upgrade(mContext,this,mSession.getVersionCode());
    }

    private void setUpGrade(){
        String upgradeUrl = upGradeInfo.getOss_addr();

        if (!TextUtils.isEmpty(upgradeUrl)) {
            if (STIDUtil.needUpdate(mSession, upGradeInfo)) {
//                HashMap<String,String> hashMap = new HashMap<>();
//                hashMap.put(getString(R.string.home_update),"ensure");

                String[] content = upGradeInfo.getRemark();
                if (upGradeInfo.getUpdate_type() == 1) {
                    mUpgradeDialog = new UpgradeDialog(
                            mContext,
                           TextUtils.isEmpty(upGradeInfo.getVersion_code()+"")?"":"新版本：V"+upGradeInfo.getVersion_code(),
                            content,
                            this.getString(R.string.confirm),
                            forUpdateListener
                    );
                    mUpgradeDialog.show();
                }else {
                    mUpgradeDialog = new UpgradeDialog(
                            mContext,
                           TextUtils.isEmpty(upGradeInfo.getVersion_code()+"")?"":"新版本：V"+upGradeInfo.getVersion_code(),
                            content,
                            this.getString(R.string.cancel),
                            this.getString(R.string.confirm),
                            cancelListener,
                            forUpdateListener
                    );
                    mUpgradeDialog.show();
                }


            }else{
                if (!ismuteUp){
                    ShowMessage.showToast(mContext, "当前为最新版本");
                }

            }
        }else {
            if (!ismuteUp){
                ShowMessage.showToast(mContext, "当前为最新版本");
            }
        }


    }

    protected void downLoadApk(String apkUrl) {
        //测试，记得去掉
//		apkUrl = "http://test.ailemy.com/mobile/download/aileBuy.apk";
        if (!mSession.isApkDownloading()){
            mSession.setApkDownloading(true);
            // 下载apk包
            initNotification();
            AppApi.downApp(mContext,apkUrl, MainActivity.this);
        }else{
            ShowMessage.showToast(mContext,"下载中,请稍候");
        }
    }

    /**
     * 初始化Notification
     */
    private void initNotification() {
        manager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        notif = new Notification();
        notif.icon = R.drawable.ic_launcher;
        notif.tickerText = "下载通知";
        // 通知栏显示所用到的布局文件
        notif.contentView = new RemoteViews(mContext.getPackageName(),
                R.layout.download_content_view);
        notif.contentIntent = PendingIntent.getBroadcast(mContext, 0, new Intent(
                mContext.getPackageName()+".debug"), PendingIntent.FLAG_CANCEL_CURRENT);
        // notif.defaults = Notification.DEFAULT_ALL;
        manager.notify(NOTIFY_DOWNLOAD_FILE, notif);
    }

    int msg = 0;
    @Override
    public void onSuccess(AppApi.Action method, Object obj) {
        switch (method) {
            case POST_NOTIFY_TVBOX_STOP_JSON:
                showToast("退出投屏成功");
                mHandler.removeMessages(CHECK_LINK_STATUS);
                break;
            case POST_UPGRADE_JSON:
                if (obj instanceof UpgradeInfo) {
                    upGradeInfo = (UpgradeInfo) obj;
                    if (upGradeInfo != null) {
                        setUpGrade();
                    }
                }
                break;
            case TEST_DOWNLOAD_JSON:
                downLoadAPk(obj);
                break;
        }
    }

    private void downLoadAPk(Object obj) {
        if (obj instanceof FileDownProgress) {
            FileDownProgress fs = (FileDownProgress) obj;
            long now = fs.getNow();
            long total = fs.getTotal();
            if ((int) (((float) now / (float) total) * 100) - msg >= 5) {
                msg = (int) (((float) now / (float) total) * 100);
                notif.contentView.setTextViewText(R.id.content_view_text1,
                        (Integer) msg + "%");
                notif.contentView.setProgressBar(R.id.content_view_progress,
                        100, (Integer) msg, false);
                manager.notify(NOTIFY_DOWNLOAD_FILE, notif);
            }

        } else if (obj instanceof File) {
            mSession.setApkDownloading(false);
            File f = (File) obj;
            byte[] fRead;
            String md5Value = null;
            try {
                fRead = FileUtils.readFileToByteArray(f);
                md5Value = AppUtils.getMD5(fRead);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            //比较本地文件版本是否与服务器文件一致，如果一致则启动安装
            if (md5Value != null && md5Value.equals(upGradeInfo.getMd5())) {
                //ShowMessage.showToast(this, f.getAbsolutePath());
                if (manager != null) {
                    manager.cancel(NOTIFY_DOWNLOAD_FILE);
                }
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                i.setDataAndType(Uri.parse("file://" + f.getAbsolutePath()),
                        "application/vnd.android.package-archive");
                startActivity(i);
            } else {
                if (manager != null) {
                    manager.cancel(NOTIFY_DOWNLOAD_FILE);
                }
                ShowMessage.showToast(mContext, "下载失败");
                setUpGrade();
            }

        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onError(AppApi.Action method, Object obj) {

        switch (method) {
            case POST_NOTIFY_TVBOX_STOP_JSON:
                if(obj == AppApi.ERROR_TIMEOUT) {
                    closeLinkingDialog();
                    ShowMessage.showToast(this,"网络超时，请重试");
                    return;
                }
                closeLinkingDialog();
                if(obj instanceof String ) {
                    String msg = (String) obj;
                    showToast(msg);
                }else {
                    String wifiName = WifiUtil.getWifiName(this);
                    ShowMessage.showToast(this,wifiName+"包间没有投屏");
                }
                break;
        }
    }

    @Override
    public void readyForQrcode() {
        if(mQrcodeDialog==null)
            mQrcodeDialog = new SavorDialog(this);
        mQrcodeDialog.show();
    }

    @Override
    public void closeQrcodeDialog() {
        if (mQrcodeDialog != null) {
            mQrcodeDialog.dismiss();
            mQrcodeDialog = null;
        }
    }

    @Override
    public void startLinkTv() {
        Intent intent = new Intent(this,LinkTvActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivityForResult(intent,0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == EXTRA_TV_INFO){
            if(data!=null) {
                TvBoxInfo boxInfo = (TvBoxInfo) data.getSerializableExtra(EXRA_TV_BOX);
                mBindTvPresenter.handleBindCodeResult(boxInfo);
            }
        }
    }

    public class SmallPlatformReciver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction().equals(SensePresenter.SMALL_PLATFORM)) {
                LogUtils.d("savor:ssdp 收到小平台接受广播");
                initWIfiHint();
            }
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
                if ((System.currentTimeMillis() - exitTime) > 2000) {
                    ShowMessage.showToast(this,getString(R.string.confirm_exit_app));
                    exitTime = System.currentTimeMillis();
                } else {
                    exitApp();
                }
        }
        return true;
    }

    private void exitApp() {
        // 清楚图片内存缓存
        GlideImageLoader.getInstance().clearMemory(getApplicationContext());
        // 清楚activity任务栈
        ActivitiesManager.getInstance().popAllActivities();

        // 关闭jetty服务
        Intent stopIntent = new Intent(this,LocalJettyService.class);
        stopService(stopIntent);

        // 关闭发现小平台的service
        Intent stopDescoveryIntent = new Intent(this,SSDPService.class);
        stopService(stopDescoveryIntent);

        Intent intent = new Intent(this, ClearImageCacheService.class);
        intent.putExtra("path",mSession.getCompressPath());
        startService(intent);


        Process.killProcess(android.os.Process.myPid());

    }
}
