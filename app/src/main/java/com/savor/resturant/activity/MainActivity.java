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
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RemoteViews;
import android.widget.TextView;

import com.common.api.http.callback.FileDownProgress;
import com.common.api.utils.AppUtils;
import com.common.api.utils.DensityUtil;
import com.common.api.utils.FileUtils;
import com.common.api.utils.LogUtils;
import com.common.api.utils.ShowMessage;
import com.savor.resturant.R;
import com.savor.resturant.adapter.CategoryAdapter;
import com.savor.resturant.adapter.FunctionAdapter;
import com.savor.resturant.bean.FunctionItem;
import com.savor.resturant.bean.HotelBean;
import com.savor.resturant.bean.TvBoxSSDPInfo;
import com.savor.resturant.bean.UpgradeInfo;
import com.savor.resturant.core.AppApi;
import com.savor.resturant.presenter.SensePresenter;
import com.savor.resturant.service.ClearImageCacheService;
import com.savor.resturant.service.LocalJettyService;
import com.savor.resturant.service.SSDPService;
import com.savor.resturant.utils.ActivitiesManager;
import com.savor.resturant.utils.GlideImageLoader;
import com.savor.resturant.utils.STIDUtil;
import com.savor.resturant.utils.WifiUtil;
import com.savor.resturant.widget.HotsDialog;
import com.savor.resturant.widget.SavorDialog;
import com.savor.resturant.widget.UpgradeDialog;
import com.savor.resturant.widget.decoration.SpacesItemDecoration;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 首页功能操作列表
 */
public class MainActivity extends BaseActivity implements View.OnClickListener, IBindTvView, FunctionAdapter.OnNoHotelClickListener {
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
    private RecyclerView listView;
    private TextView connectTipTV;
    private TextView operationBtnTV;
    private CategoryAdapter categoryAdapter=null;
    private List<FunctionItem> mList = new ArrayList<>();
//    private BindTvPresenter mBindTvPresenter;
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
    private TextView mHintTv;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getViews();
        setViews();
        setListeners();
        regitsterSmallPlatformReciever();
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
    }


//    private void initPresenter() {
//        mBindTvPresenter = new BindTvPresenter(this,this);
//    }

//    /**
//     * 注册监控网络状态改变广播
//     */
//    private void registerNetWorkReceiver() {
//        mBindTvPresenter.registerNetWorkReceiver();
//    }

    @Override
    public void getViews() {
        mHintTv = (TextView) findViewById(R.id.tv_wifi_hint);
        iv_left = (ImageView) findViewById(R.id.iv_left);
        tv_center = (TextView)findViewById(R.id.tv_center);
        listView = (RecyclerView) findViewById(R.id.category_list);
        connectTipTV = (TextView)findViewById(R.id.connect_tip);
        operationBtnTV = (TextView)findViewById(R.id.operation_btn);
    }

    @Override
    public void setViews() {
        iv_left.setVisibility(View.GONE);
        tv_center.setText(getString(R.string.app_name));

        mList.clear();

        FunctionItem recommandItem = new FunctionItem();
        recommandItem.setContent("推荐菜");
        recommandItem.setResId(R.drawable.ico_recommand);
        recommandItem.setType(FunctionItem.FunctionType.TYPE_RECOMMAND_FOODS);
        mList.add(recommandItem);

        FunctionItem advertItem = new FunctionItem();
        advertItem.setContent("宣传片");
        advertItem.setResId(R.drawable.ico_advert);
        advertItem.setType(FunctionItem.FunctionType.TYPE_ADVERT);
        mList.add(advertItem);

        FunctionItem welcomeItem = new FunctionItem();
        welcomeItem.setContent("欢迎词");
        welcomeItem.setResId(R.drawable.ico_welcom_word);
        welcomeItem.setType(FunctionItem.FunctionType.TYPE_WELCOME_WORD);
        mList.add(welcomeItem);

        FunctionItem picItem = new FunctionItem();
        picItem.setContent("照片");
        picItem.setResId(R.drawable.ico_pic);
        picItem.setType(FunctionItem.FunctionType.TYPE_PIC);
        mList.add(picItem);

        FunctionItem videoItem = new FunctionItem();
        videoItem.setContent("视频");
        videoItem.setResId(R.drawable.ico_video);
        videoItem.setType(FunctionItem.FunctionType.TYPE_VIDEO);
        mList.add(videoItem);

        FunctionAdapter mFunctionAdapter = new FunctionAdapter(this);
        GridLayoutManager layoutManager = new GridLayoutManager(this,2);
        layoutManager.setOrientation(GridLayoutManager.VERTICAL);
        listView.setLayoutManager(layoutManager);
        mFunctionAdapter.setData(mList);
        listView.setAdapter(mFunctionAdapter);
        mFunctionAdapter.setOnNoHotelClickListener(this);

        //添加ItemDecoration，item之间的间隔
        int leftRight = DensityUtil.dip2px(this,5);
        int topBottom = DensityUtil.dip2px(this,15);

        listView.addItemDecoration(new SpacesItemDecoration(leftRight, topBottom, getResources().getColor(R.color.color_eeeeee)));

        initWIfiHint();

    }


    @Override
    protected void onRestart() {
        super.onRestart();
        initWIfiHint();
    }

    public void initWIfiHint() {
        // 判断当前是否是酒店环境
        int hotelid = mSession.getHotelid();
        HotelBean loginResponse = mSession.getHotelBean();
        if(loginResponse!=null) {
            String hid = loginResponse.getHotel_id();
            if(String.valueOf(hotelid).equals(hid)) {
                mHintTv.setText("当前连接酒楼\""+loginResponse.getHotel_name()+"\"");
                mHintTv.setTextColor(getResources().getColor(R.color.color_0da606));
                mHintTv.setCompoundDrawables(null,null,null,null);
            }else {
                mHintTv.setText("请链接“"+loginResponse.getHotel_name()+"”的wifi后继续操作");
                mHintTv.setTextColor(getResources().getColor(R.color.color_e43018));
                mHintTv.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.ico_exe_hint),null,null,null);
            }
        }

    }

    @Override
    public void setListeners() {
        operationBtnTV.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
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
    public void onNoHotelClick() {
        Animation shake = AnimationUtils.loadAnimation(this, R.anim.shake);
        mHintTv.startAnimation(shake);
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
