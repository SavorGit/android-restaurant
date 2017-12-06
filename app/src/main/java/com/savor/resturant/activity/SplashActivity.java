package com.savor.resturant.activity;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.common.api.bitmap.BitmapCommonUtils;
import com.common.api.utils.AppUtils;
import com.common.api.utils.FileUtils;
import com.common.api.utils.LogUtils;
import com.savor.resturant.R;
import com.savor.resturant.SavorApplication;
import com.savor.resturant.bean.AliLogBean;
import com.savor.resturant.bean.RoomInfo;
import com.savor.resturant.bean.SmallPlatInfoBySSDP;
import com.savor.resturant.bean.SmallPlatformByGetIp;
import com.savor.resturant.bean.StartUpSettingsBean;
import com.savor.resturant.bean.TvBoxSSDPInfo;
import com.savor.resturant.core.AppApi;
import com.savor.resturant.core.Session;
import com.savor.resturant.presenter.SensePresenter;
import com.savor.resturant.service.LocalJettyService;
import com.savor.resturant.service.SSDPService;
import com.savor.resturant.service.UpLoadLogService;
import com.savor.resturant.utils.AliLogFileUtil;
import com.savor.resturant.utils.ImageCacheUtils;
import com.savor.resturant.utils.RecordUtils;
import com.savor.resturant.utils.STIDUtil;
import com.savor.resturant.widget.SplashDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static com.savor.resturant.presenter.SensePresenter.SMALL_PLATFORM;


/**
 * 启动页面
 *
 * @author savor
 *         Update by wmm on 2016/11/20.
 */
public class SplashActivity extends BaseActivity {

    private static final int CHECK_START_UP = 4;
    private static final int CLOSE_FIRSTUSE_SERVICE = 0x3;
    private final int FIRST_START = 1;
    private final int SWITCH_HOME = 2;

    //获取权限
    private static final int REQUEST_CODE = 3;
    private static final String TAG = "savor:splash";

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case CHECK_START_UP:
                    getStartUpSettings();
                    break;
                case SWITCH_HOME:
                    // 启动跳转到首页
                    Intent homeIntent = new Intent(SplashActivity.this, MainActivity.class);
                    Intent intent = getIntent();
                    if(intent!=null&&("application/pdf").equals(intent.getType())) {
                        Uri data = getIntent().getData();
                        homeIntent.setDataAndType(data,intent.getType());
                    }
                    startActivity(homeIntent);
//                    finish();
                    break;
            }
        }
    };
    private StartUpSettingsBean latestSettingsBean;
    private ImageView mStartUpImageView;
    private long delayedTime = 1500;
    private SurfaceView mSurfaceView;
    private MediaPlayer mp;
    private RelativeLayout mParentLayout;
    private boolean isJumped;
    private SmallPlatformReciver smallPlatformReciver;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if ((getIntent().getFlags() & Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT) != 0) {
            finish();
            return;
        }
        setContentView(R.layout.activity_splash);
        showSplashVideo();
        getViews();
        setViews();
        setListeners();
        startServerDiscoveryService();
        startJettyServer();
        getSmallPlatformUrl();
        regitsterSmallPlatformReciever();
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

    public void unregistetSpReceiver() {
        if(smallPlatformReciver!=null) {
            unregisterReceiver(smallPlatformReciver);
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

    }

    private void getSmallPlatformUrl() {
        //  判断是否获取到小平台地址，如果没有获取到请求云平台（小平台是局域网）获取小平台ip
        if(AppUtils.isWifiNetwork(this)) {
            LogUtils.d("savor:sp 当前wifi可用请求getip");
            AppApi.getSmallPlatformIp(this,this);
        }else {
            LogUtils.d("savor:sp 当前wifi状态不可用不请求getip");
        }
    }

    private void showSplashVideo() {
        final SplashDialog splashDialog = new SplashDialog(this);
        splashDialog.setPlayOverListener(new SplashDialog.OnPlayOverListener() {
            @Override
            public void onSplashPlayOver() {
                mHandler.sendEmptyMessageDelayed(CHECK_START_UP,0);
                splashDialog.dismiss();
            }
        });
        splashDialog.show();
    }

    /**延时跳转*/
    private void jumpDelayed() {
        if(isJumped)
            return;
        isJumped = true;
        //第一次进入引导页，否则直接进入主页
//        if (mSession.isNeedGuide()) {
//            mHandler.sendEmptyMessageDelayed(FIRST_START, delayedTime);
//        } else {
            mHandler.sendEmptyMessageDelayed(SWITCH_HOME, delayedTime);
//        }
    }



    /**
     * 获取启动配置，启动图或启动视频
     */
    private void getStartUpSettings() {
        StartUpSettingsBean startUpSettings = mSession.getStartUpSettings();
        /**
         * 如果有缓存数据，判断缓存文件是否存在如果存在就展示图片或视频，如果不存在展示默认图
         * */
        if(startUpSettings!=null) {
            String url = startUpSettings.getUrl();
            String duration = startUpSettings.getDuration();

            String status = startUpSettings.getStatus();
            String cacheKeyByUrl = ImageCacheUtils.getCacheKeyByUrl(url);
            String cachePath = SavorApplication.getInstance().getSplashCachePath();
            final String cacheFilepath = cachePath+File.separator+cacheKeyByUrl;
            File cacheFile = new File(cacheFilepath);
            if(cacheFile.exists()) {
                if("1".equals(status)) {
                    // 展示图片
                    int[] imageSize = BitmapCommonUtils.getImageSize(cacheFilepath);
                    if(imageSize[0]>0&&imageSize[1]>0) {
                        LogUtils.d(TAG+"加载缓存启动图"+cacheFilepath + ",图片分辨率="+imageSize[0]+"x"+imageSize[1]);
                        showCacheImage(cacheFilepath);
                        try {
                            delayedTime = Integer.valueOf(duration)*1000;
                        }catch (Exception e){}
                        jumpDelayed();
                    }else {
                        LogUtils.d(TAG+"图片宽或高为0，显示默认启动图");
                        jumpDelayed();
                        mSession.setStartUpSettings(null);
                    }
                }else if("2".equals(status)) {
                    // 展示视频
                    LogUtils.d(TAG+"加载缓存启动视频"+cacheFilepath);
                    showVideo(cacheFilepath);
                }
            }else {
                mSession.setStartUpSettings(null);
                // 缓存文件不存在展示默认图
                delayedTime = 0;
                jumpDelayed();
                LogUtils.d(TAG+" 缓存文件不存在--"+cacheFilepath);
            }
        }else {
            //  展示默认图
            LogUtils.d(TAG+"无缓存数据");
            delayedTime = 0;
            jumpDelayed();
        }

//        AppApi.getStartUpSettings(this,this);
    }

    private void showVideo(final String cacheFilepath) {
        mSurfaceView.setVisibility(View.VISIBLE);
        mp = new MediaPlayer();
        mSurfaceView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return false;
            }
        });
        mSurfaceView.getHolder().setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        mSurfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                try {
                    mp.setSurface(holder.getSurface());
                    mp.setDataSource(cacheFilepath);
                    mp.setAudioStreamType(AudioManager.STREAM_MUSIC);
                    mp.prepare();
                    mp.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                        @Override
                        public void onPrepared(MediaPlayer mp) {
                            mp.seekTo(0);
                            mp.start();
                        }
                    });

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
            }
        });
        mp.setOnVideoSizeChangedListener(new MediaPlayer.OnVideoSizeChangedListener() {
            @Override
            public void onVideoSizeChanged(MediaPlayer mp, int width, int height) {
                ViewGroup.LayoutParams layoutParams = mSurfaceView.getLayoutParams();
                int viewWidth = mSurfaceView.getWidth();
                int viewHeight = mSurfaceView.getHeight();
                if (((double) viewWidth / width) * height > viewHeight) {
                    layoutParams.width = (int) (((double) viewHeight / height) * width);
                    layoutParams.height = viewHeight;
                } else {
                    layoutParams.width = viewWidth;
                    layoutParams.height = (int) (((double) viewWidth / width) * height);
                }
            }
        });
        mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mp.setOnCompletionListener(null);
                if(mp!=null&&mp.isPlaying()) {
                    mp.stop();
                    mp.release();
                }
                delayedTime = 0;
                jumpDelayed();
            }
        });
    }

    private void showCacheImage(String cacheFilepath) {
        mStartUpImageView.setVisibility(View.VISIBLE);
        Glide.with(this).load(cacheFilepath).centerCrop().diskCacheStrategy(DiskCacheStrategy.NONE).skipMemoryCache(true).into(mStartUpImageView);
    }

    @Override
    protected void onResume() {
        super.onResume();
        RecordUtils.onPageStartAndResume(this,this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        RecordUtils.onPageEndAndPause(this,this);
    }

    @Override
    public void onSuccess(AppApi.Action method, Object obj) {
        super.onSuccess(method, obj);
        switch (method) {
            case GET_HOTEL_BOX_JSON:
                if(obj instanceof List) {
                    List<RoomInfo> roomInfos = (List<RoomInfo>) obj;
                    mSession.setRoomList(roomInfos);
                }
                break;
            case GET_SAMLL_PLATFORMURL_JSON:
                // 获取小平台地址
                if(obj instanceof SmallPlatformByGetIp) {
                    SmallPlatformByGetIp smallPlatformByGetIp = (SmallPlatformByGetIp) obj;
                    if (smallPlatformByGetIp != null) {
                        String localIp = smallPlatformByGetIp.getLocalIp();
                        String hotelId = smallPlatformByGetIp.getHotelId();
                        // 保存酒店id
                        try {
                            Integer hid = Integer.valueOf(hotelId);
                            if(hid>0) {
                                mSession.setHotelid(hid);
                            }
                        }catch (Exception e) {
                        }
                        // 保存云平台获取的小平台信息
                        if (!TextUtils.isEmpty(localIp)) {
                            mSession.setSmallPlatInfoByGetIp(smallPlatformByGetIp);
                        }
                        if(!TextUtils.isEmpty(hotelId)) {
                            List<Object> requsetPool = mSession.getRequsetPool();
                            if(!requsetPool.contains(smallPlatformByGetIp)) {
                                AppApi.getHotelRoomList(this,localIp,hotelId,this);
                                requsetPool.add(smallPlatformByGetIp);
                                mSession.setRequestPool(requsetPool);
                            }
                        }
                    }
                }
                break;
        }
    }

    @Override
    public void onError(AppApi.Action method, Object obj) {
    }

    /**
     * 启动jetty服务service
     */
    private void startJettyServer() {
        Intent intent = new Intent(this, LocalJettyService.class);
        this.startService(intent);
    }

    /**组播阻塞方式获取小平台发送的本身地址*/
    private void startServerDiscoveryService() {
        if(!AppUtils.isWifiNetwork(this)) {
            LogUtils.d("savor:sp 当前网络不可用不接受ssdp");
        }else {
            LogUtils.d("savor:sp 当前wifi状态接受ssdp");
            Intent intent = new Intent(this, SSDPService.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startService(intent);
        }
    }

    @Override
    public void getViews() {
        mParentLayout = (RelativeLayout) findViewById(R.id.parent);
        mStartUpImageView = (ImageView) findViewById(R.id.iv_start_up);
        mSurfaceView = (SurfaceView) findViewById(R.id.surfaceView);
    }

    @Override
    public void setViews() {

    }

    @Override
    public void setListeners() {
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregistetSpReceiver();
        if(mp!=null&&mp.isPlaying()) {
            mp.stop();
            mp.release();
            mp = null;
        }
    }

    @Override
    public void onBackPressed() {
        if(mp!=null) {
            mp.stop();
            mp.release();
            mp = null;
        }
        super.onBackPressed();
    }

    public class SmallPlatformReciver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction().equals(SensePresenter.SMALL_PLATFORM)) {
                LogUtils.d("savor:ssdp 收到小平台接受广播");
                List<Object> requsetPool = mSession.getRequsetPool();
                SmallPlatInfoBySSDP smallPlatInfoBySSDP = mSession.getSmallPlatInfoBySSDP();
                TvBoxSSDPInfo tvBoxSSDPInfo = mSession.getTvBoxSSDPInfo();
                if(smallPlatInfoBySSDP!=null&&!requsetPool.contains(smallPlatInfoBySSDP)) {
                    String serverIp = smallPlatInfoBySSDP.getServerIp();
                    int hotelId = smallPlatInfoBySSDP.getHotelId();
                    String hid = "";
                    try {
                        hid = String.valueOf(hotelId);
                    }catch (Exception e) {}
                    AppApi.getHotelRoomList(SplashActivity.this,serverIp,hid,SplashActivity.this);
                    requsetPool.add(smallPlatInfoBySSDP);
                    mSession.setRequestPool(requsetPool);
                }

                if(tvBoxSSDPInfo!=null&&!requsetPool.contains(tvBoxSSDPInfo)) {
                    String serverIp = tvBoxSSDPInfo.getServerIp();
                    String hotelId = tvBoxSSDPInfo.getHotelId();
                    AppApi.getHotelRoomList(SplashActivity.this,serverIp,hotelId,SplashActivity.this);
                    requsetPool.add(tvBoxSSDPInfo);
                    mSession.setRequestPool(requsetPool);
                }
            }
        }
    }
}
