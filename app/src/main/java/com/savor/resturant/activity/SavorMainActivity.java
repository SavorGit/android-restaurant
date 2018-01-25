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
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.RemoteViews;

import com.common.api.http.callback.FileDownProgress;
import com.common.api.utils.AppUtils;
import com.common.api.utils.LogUtils;
import com.common.api.utils.ShowMessage;
import com.common.api.widget.customTab.MyTabWidget;
import com.savor.resturant.R;
import com.savor.resturant.bean.ContactFormat;
import com.savor.resturant.bean.OperationFailedItem;
import com.savor.resturant.bean.UpgradeInfo;
import com.savor.resturant.core.AppApi;
import com.savor.resturant.fragment.BookFragment;
import com.savor.resturant.fragment.CustomerFragment;
import com.savor.resturant.fragment.MyFragment;
import com.savor.resturant.fragment.ProjectionFragment;
import com.savor.resturant.presenter.SensePresenter;
import com.savor.resturant.service.ClearImageCacheService;
import com.savor.resturant.service.LocalJettyService;
import com.savor.resturant.service.ReRequestService;
import com.savor.resturant.service.SSDPService;
import com.savor.resturant.utils.ActivitiesManager;
import com.savor.resturant.utils.ConstantValues;
import com.savor.resturant.utils.GlideImageLoader;
import com.savor.resturant.utils.STIDUtil;
import com.savor.resturant.widget.ImportDialog;
import com.savor.resturant.widget.UpgradeDialog;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class SavorMainActivity extends BaseFragmentActivity implements MyTabWidget.OnTabSelectedListener {
    public static final String SMALL_PLATFORM = "small_platform";
    private FragmentManager supportFragmentManager;
    private MyTabWidget tabWidget;
    private int currentIndex;
    private BookFragment bookFragment;
    private CustomerFragment customerFragment;
    private ProjectionFragment projectionFragment;
    private MyFragment myFragment;
    private SmallPlatformReciver smallPlatformReciver;
    private long exitTime;
    private UpgradeInfo upGradeInfo;
    private UpgradeDialog mUpgradeDialog;
    private NotificationManager manager;
    private Notification notif;
    private final int NOTIFY_DOWNLOAD_FILE=10001;
    /**是否是自动检查更新，如果不是那就是手动检查提示有版本更新否则不提示*/
    private boolean ismuteUp = true;
    /**退出投屏按钮状态，当前展示退出投屏还是去设置*/
    private int mProBtnType;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_savor_main);


        supportFragmentManager = getSupportFragmentManager();
        getViews();
        setViews();
        setListeners();
//        showImportDialog();
        checkShouldShowImportDialog();

        regitsterSmallPlatformReciever();
        startReRequestService();
        upgrade();
    }

    private void upgrade(){
        AppApi.Upgrade(mContext,this,mSession.getVersionCode());
    }
    private void checkShouldShowImportDialog() {
        List<ContactFormat> customerList = mSession.getCustomerList().getCustomerList();
        String is_import_customer = mSession.getHotelBean().getIs_import_customer();
        boolean showImportDialog = mSession.isShowImportDialog();
        if(!"1".equals(is_import_customer)&&(customerList==null||customerList.size() == 0)&&!showImportDialog) {
            showImportDialog();
        }
    }

    /**
     * 操作失败的请求列表重新发起请求
     */
    private void startReRequestService() {
        List<OperationFailedItem> opFailedList = mSession.getOpFailedList();
        if(opFailedList!=null&&opFailedList.size()>0) {
            LogUtils.d("savor:opr 有操作失败记录开始重新发起请求\n "+opFailedList);
            ReRequestService.startActionRequest(this);
        }else {
            LogUtils.d("savor:opr 没有操作失败记录");
        }
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

    private void showImportDialog() {
        mSession.setIsShowImportDialog(true);
        new ImportDialog(this, new ImportDialog.OnImportBtnClickListener() {
            @Override
            public void onImportBtnClick() {
                Intent intent = new Intent(SavorMainActivity.this,ContactCustomerListActivity.class);
                intent.putExtra("type", ContactCustomerListActivity.OperationType.CONSTACT_LIST_FIRST);
                startActivity(intent);
            }
        }).show();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt("tab_index", currentIndex);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        currentIndex = savedInstanceState.getInt("tab_index");

    }

    @Override
    public void getViews() {
        tabWidget = (MyTabWidget) findViewById(R.id.tabwidget);
    }

    @Override
    public void setViews() {
        onTabSelected(currentIndex);
        tabWidget.setTabsDisplay(currentIndex);
    }

    @Override
    public void setListeners() {
        tabWidget.setOnTabSelectedListener(this);
    }

    @Override
    public void onTabSelected(int index) {
        FragmentTransaction fragmentTransaction = supportFragmentManager.beginTransaction();
        hideFragments(fragmentTransaction);
        switch (index) {
            case ConstantValues.HomeTabIndex.TAB_INDEX_BOOK:
                if(bookFragment == null) {
                    bookFragment = BookFragment.newInstance();
                    fragmentTransaction.add(R.id.center_layout,bookFragment,"book_fragment");
                }else {
                    fragmentTransaction.show(bookFragment);
                }
                break;
                case ConstantValues.HomeTabIndex.TAB_INDEX_CUSTOMER:
                    if(customerFragment == null) {
                        customerFragment = CustomerFragment.newInstance();
                        fragmentTransaction.add(R.id.center_layout,customerFragment,"customer_fragment");
                    }else {
                        fragmentTransaction.show(customerFragment);
                    }
                    break;
                case ConstantValues.HomeTabIndex.TAB_INDEX_SERVICE:
                    if(projectionFragment == null) {
                        projectionFragment = ProjectionFragment.newInstance();
                        fragmentTransaction.add(R.id.center_layout, projectionFragment,"service_fragment");
                    }else {
                        fragmentTransaction.show(projectionFragment);
                    }
                    break;
                case ConstantValues.HomeTabIndex.TAB_INDEX_MY:
                    if(myFragment == null) {
                        myFragment = MyFragment.newInstance();
                        fragmentTransaction.add(R.id.center_layout,myFragment,"my_fragment");
                    }else {
                        fragmentTransaction.show(myFragment);
                    }
                    break;
        }

        currentIndex = index;
        fragmentTransaction.commitNowAllowingStateLoss();

    }

    private void hideFragments(FragmentTransaction fragmentTransaction) {
        if(bookFragment!=null) {
            fragmentTransaction.hide(bookFragment);
        }

        if(customerFragment!=null) {
            fragmentTransaction.hide(customerFragment);
        }

        if(projectionFragment !=null) {
            fragmentTransaction.hide(projectionFragment);
        }

        if(myFragment!=null) {
            fragmentTransaction.hide(myFragment);
        }

    }


    public class SmallPlatformReciver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction().equals(SensePresenter.SMALL_PLATFORM)) {
                LogUtils.d("savor:ssdp 收到小平台接受广播");
                if(projectionFragment!=null) {
                    projectionFragment.initWIfiHint();
                }
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(smallPlatformReciver!=null) {
            unregisterReceiver(smallPlatformReciver);
        }
    }


    int msg = 0;
    @Override
    public void onSuccess(AppApi.Action method, Object obj) {
        switch (method) {
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

    protected void downLoadApk(String apkUrl) {
        //测试，记得去掉
//		apkUrl = "http://test.ailemy.com/mobile/download/aileBuy.apk";
        if (!mSession.isApkDownloading()){
            mSession.setApkDownloading(true);
            // 下载apk包
            initNotification();
            AppApi.downApp(mContext,apkUrl, SavorMainActivity.this);
        }else{
            ShowMessage.showToast(mContext,"下载中,请稍候");
        }
    }

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
