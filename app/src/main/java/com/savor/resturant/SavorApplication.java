package com.savor.resturant;

import android.content.Context;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;

import com.common.api.utils.AppUtils;
import com.github.tamir7.contacts.Contacts;
import com.savor.resturant.core.Session;
import com.umeng.analytics.MobclickAgent;
import com.umeng.commonsdk.UMConfigure;

import java.io.File;

/**
 * Created by hezd on 2016/12/13.
 */

public class SavorApplication extends MultiDexApplication {

    private static SavorApplication mInstance;

    private String mSplashCachePath;
    private String mSplashTempPath;
    public String VodTypePath;
    public String VodStorePath;
    public String ImageCachePath;
    public String ImageSplash;
    public String GalleyPath;
    public String PdfJsPath;
    public String OfficePath;
    private String mLogFilePath;
    private String mLogTempFilePath;
    /**抽奖剩余次数本地缓存文件*/
    private String mLottoryNumDir;
    /**抽奖随机数缓存路径*/
    private String mLottoryRandomDir;
    /**当前抽奖次数*/
    private String mLottoryCountDir;

    public static SavorApplication getInstance() {
        return mInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        // 设置异常捕获处理类
        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this));
        Session.get(this);
        mInstance = this;
        MobclickAgent.openActivityDurationTrack(false);
        //初始化友盟分享
        //开启debug模式，方便定位错误，具体错误检查方式可以查看http://dev.umeng.com/social/android/quick-integration的报错必看，正式发布，请关闭该模式
//        Config.DEBUG = true;
//       UMShareAPI.get(this);
        initCacheFile(this);
        UMConfigure.init(this, UMConfigure.DEVICE_TYPE_PHONE, "");
//        initUmengPush();
        Contacts.initialize(this);

    }


    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

//    private void initUmengPush() {
//        LogUtils.d("savor:push initUmengPush");
//        PushAgent mPushAgent = PushAgent.getInstance(this);
//        LogUtils.d("savor:push deviceToken="+mPushAgent.getRegistrationId());
//        //注册推送服务，每次调用register方法都会回调该接口
//        mPushAgent.register(new IUmengRegisterCallback() {
//
//            @Override
//            public void onSuccess(String deviceToken) {
//                //注册成功会返回device token
//                LogUtils.d("savor:push deviceToken="+deviceToken);
//            }
//
//            @Override
//            public void onFailure(String s, String s1) {
//                LogUtils.d("savor:push register failed ,message="+s);
//            }
//        });
//
//        /**
//         * 自定义行为的回调处理，参考文档：高级功能-通知的展示及提醒-自定义通知打开动作
//         * UmengNotificationClickHandler是在BroadcastReceiver中被调用，故
//         * 如果需启动Activity，需添加Intent.FLAG_ACTIVITY_NEW_TASK
//         * */
//        UmengNotificationClickHandler notificationClickHandler = new UmengNotificationClickHandler() {
//            @Override
//            public void dealWithCustomAction(Context context, UMessage msg) {
//                // 点击收到推送，友盟埋点
//                RecordUtils.onEvent(getApplicationContext(),R.string.click_notification);
//                Map<String,String> custom = msg.extra;
//                boolean isRunning = ActivitiesManager.getInstance().contains(HotspotMainActivity.class);
//                String type = custom.get("type");
//                String params = custom.get("params");
//                if("1".equals(type)) {
//                    RecordUtils.onEvent(getApplicationContext(),getString(R.string.home_start));
//                    if(!isRunning) {
//                        Intent intent = new Intent(context, SplashActivity.class);
//                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                        context.startActivity(intent);
//                    }
//                }else if("2".equals(type)) {
//                    if(!isRunning) {
//                        Intent intent = new Intent(context, HotspotMainActivity.class);
//                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                        context.startActivity(intent);
//                    }
//
//                    VodBean vodBean = new Gson().fromJson(params, VodBean.class);
//                    if(vodBean!=null) {
//                        int vodType = vodBean.getType();
//                        if(vodType == 4) {
//                            Intent videoIntent = new Intent(context, VideoOnlyActivity.class);
//                            videoIntent.putExtra("topicItem", vodBean);
//                            videoIntent.putExtra("category_id", "-1");
//                            videoIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                            context.startActivity(videoIntent);
//                        }else {
//                            Intent intent = new Intent(context, VideoVodActivity.class);
//                            intent.putExtra("topicItem", vodBean);
//                            intent.putExtra("category_id", "-1");
//                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                            context.startActivity(intent);
//                        }
//                    }
//                }
//            }
//        };
//
//        UmengMessageHandler messageHandler = new UmengMessageHandler(){
//            @Override
//            public void dealWithNotificationMessage(Context context, UMessage uMessage) {
//                super.dealWithNotificationMessage(context, uMessage);
//                // 收到推送
//                RecordUtils.onEvent(getApplicationContext(),R.string.receive_notification);
//            }
//        };
//        mPushAgent.setMessageHandler(messageHandler);
//        //使用自定义的NotificationHandler，来结合友盟统计处理消息通知，参考http://bbs.umeng.com/thread-11112-1-1.html
//        //CustomNotificationHandler notificationClickHandler = new CustomNotificationHandler();
//        mPushAgent.setNotificationClickHandler(notificationClickHandler);
//        mPushAgent.setDebugMode(false);
//    }

    /**
     * 初始化bugtags sdk
     */
    private void initBugtagsConfig() {
        // bugtags sdk
//        BugtagsOptions options = new BugtagsOptions.Builder()
//                .trackingLocation(true)//是否获取位置
//                .trackingCrashLog(true)//是否收集crash
//                .crashWithScreenshot(true)//crash是否附带图
//                .trackingConsoleLog(true)//是否收集console log
//                .trackingUserSteps(true)//是否收集用户操作步骤
//                .build();
//        Bugtags.start("69e7fc8116b3a63848f3b8d0ac0e3b0b", this, Bugtags.BTGInvocationEventNone, options);
    }

    private void initCacheFile(Context context) {
        String cachePath = AppUtils.getSDCardPath()+"savor"+File.separator;
        mSplashCachePath = cachePath+"splash";
        mSplashTempPath = cachePath+"temp";
        mLogFilePath = cachePath + "log";
        mLogTempFilePath = cachePath + "log"+File.separator+"temp";
        mLottoryNumDir = cachePath+"lottery"+File.separator+"num";
        mLottoryRandomDir = cachePath+"lottery"+File.separator+"random";
        mLottoryCountDir = cachePath+"lottery"+File.separator+"count";

        File externalCacheDir = context.getExternalCacheDir();
        File fileDir = context.getFilesDir();
        VodTypePath = externalCacheDir + File.separator + ".VodTypeFile";
        VodStorePath = fileDir + File.separator + ".VodStoreFile";
        ImageCachePath = externalCacheDir + File.separator + ".ImageCacheFile";
        ImageSplash = ImageCachePath + File.separator + ".bg_splash.png";
        GalleyPath = ImageCachePath + "/galley";
        PdfJsPath = externalCacheDir + File.separator;
        OfficePath = fileDir + File.separator + "documents/";
    }

    public String getLottoryCountDir() {
        return mLottoryCountDir;
    }

    public String getLogTempFilePath() {
        return mLogTempFilePath;
    }

    public String getLottoryRandomDir() {
        return mLottoryRandomDir;
    }

    public String getLottoryNumDir() {
        return mLottoryNumDir;
    }

    public String getLogFilePath() {
        return mLogFilePath;
    }

    /**
     * 获取启动图或视频缓存路径
     * @return
     */
    public String getSplashCachePath() {
        return mSplashCachePath;
    }

    /**
     * 获取启动图或视频临时文件路径
     * @return
     */
    public String getSplashTempPath() {
        return mSplashTempPath;
    }


    //    /**
//     * Umeng 分享
//     */
//    private void initUmengSocialPlatform() {
//
//    }

    //各个平台的配置，建议放在全局Application或者程序入口
//    {
//        PlatformConfig.setWeixin("wx59643f058e9b544c", "ad5cf8b259673427421a1181614c33c7");
//        PlatformConfig.setQQZone("1105235421", "wZ1iLVjm6vRUyxbv");
//        PlatformConfig.setSinaWeibo("258257010", "7b2701caad98239314089869bec08982","http://sns.whalecloud.com/sina2/callback");
////        PlatformConfig.setSinaWeibo("258257010", "7b2701caad98239314089869bec08982","https://api.weibo.com/oauth2/default.html");
//    }
//        Config.REDIRECT_URL = "http://sns.whalecloud.com/sina2/callback";
}
