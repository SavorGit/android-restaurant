//package com.savor.resturant.activity;
//
//import android.content.ComponentName;
//import android.content.ContentResolver;
//import android.content.Context;
//import android.content.Intent;
//import android.content.ServiceConnection;
//import android.media.Ringtone;
//import android.media.RingtoneManager;
//import android.net.Uri;
//import android.os.Build;
//import android.os.Bundle;
//import android.os.Handler;
//import android.os.HandlerThread;
//import android.os.IBinder;
//import android.os.Message;
//import android.os.ParcelFileDescriptor;
//import android.support.v4.content.FileProvider;
//import android.text.TextUtils;
//import android.view.View;
//import android.widget.AdapterView;
//import android.widget.GridView;
//import android.widget.ImageView;
//import android.widget.TextView;
//
//import com.common.api.utils.AppUtils;
//import com.common.api.utils.FileUtils;
//import com.common.api.utils.LogUtils;
//import com.savor.resturant.R;
//import com.savor.resturant.SavorApplication;
//import com.savor.resturant.adapter.VideoShareAdapter;
//import com.savor.resturant.bean.LocalVideoProPesponse;
//import com.savor.resturant.bean.VideoInfo;
//import com.savor.resturant.core.AppApi;
//import com.savor.resturant.core.ResponseErrorMessage;
//import com.savor.resturant.service.ProjectionService;
//import com.savor.resturant.utils.MediaUtils;
//import com.savor.resturant.utils.NetWorkUtil;
//import com.savor.resturant.utils.ProjectionManager;
//import com.savor.resturant.utils.RecordUtils;
//import com.savor.resturant.widget.CommonDialog;
//import com.savor.resturant.widget.SavorDialog;
//
//import net.ypresto.androidtranscoder.MediaTranscoder;
//import net.ypresto.androidtranscoder.format.MediaFormatStrategyPresets;
//
//import java.io.File;
//import java.io.FileDescriptor;
//import java.io.FileOutputStream;
//import java.math.BigDecimal;
//import java.util.ArrayList;
//import java.util.HashMap;
//
///**
// * Created by hezd on 2016/12/10.
// */
//
////本机视频页面
//public class VideoShareActivity extends BaseActivity implements View.OnClickListener, AdapterView.OnItemClickListener {
//
//    private static final int QUERY_VIDEO = 0x1;
//    private static final int UPDATE_VIDEO_LIST = 0x2;
//    private static final int PROCESS_VIDEO = 0x3;
//    private static final int HANDLE_OVER = 0x4;
//    private static final int UPDATE_PROGRESS = 0x5;
//    private ImageView back;
//    private GridView videoList;
//    private TextView title;
//    private ArrayList<VideoInfo> videoInfoList = new ArrayList<VideoInfo>();
//    private int force=0;
//    private CommonDialog dialog;
////    public static final ArrayList<String> VIDEO_TYPES = new ArrayList<String>(){
////        {
////            add("avi");
////            add("mp4");
////            add("3gp");
////            add("mpg");
////            add("asf");
////            add("wmv");
////            add("mkv");
////            add("webm");
////        }
////    };
//
//    /**ui线程handler*/
//    private Handler mUiHandler = new Handler(){
//        @Override
//        public void handleMessage(Message msg) {
//            switch (msg.what) {
//                case UPDATE_VIDEO_LIST:
//                    updateVideoList();
//                    break;
//                case HANDLE_OVER:
//                    if(isFinish)
//                        return;
//                    if(mSession.isBindTv()) {
//                        VideoInfo videoInfo = (VideoInfo) msg.obj;
//                        force = 0;
//                        AppApi.localVideoPro(VideoShareActivity.this,mSession.getTVBoxUrl(),videoInfo,force,VideoShareActivity.this);
//                    }else {
//                        dismissLoadingDialog();
//                        Intent intent = new Intent(VideoShareActivity.this,LocalVideoProAcitvity.class);
//                        intent.putExtra("ModelVideo", mCurrentVideoInfo);
//                        startActivity(intent);
//                    }
//                    break;
//                case UPDATE_PROGRESS:
//                    String progress = (String) msg.obj;
//                    if(mLoadingDialog!=null) {
//                        mLoadingDialog.updateHint("   加载进度"+progress+"   ");
//                    }
//                    break;
//            }
//        }
//    };
//
//    private Handler.Callback mQueryVedioCallBack = new Handler.Callback() {
//        @Override
//        public boolean handleMessage(Message msg) {
//            switch (msg.what) {
//                case QUERY_VIDEO:
//                    MediaUtils.getVideoInfo(VideoShareActivity.this, videoInfoList);
//                    mUiHandler.sendEmptyMessage(UPDATE_VIDEO_LIST);
//                    break;
//                case PROCESS_VIDEO:
//                    VideoInfo videoInfo = (VideoInfo) msg.obj;
//                    handleVideo(videoInfo);
//                    break;
//            }
//            return true;
//        }
//    };
//    private SavorDialog mLoadingDialog;
//    private VideoInfo mCurrentVideoInfo;
//    private boolean isFinish;
//    private String projectId;
//    private String mCacheVideoPath;
//
//    private void handleVideo(VideoInfo videoInfo) {
//        mCurrentVideoInfo = videoInfo;
//        String assetpath = videoInfo.getAssetcover();
//        File srcFile = new File(assetpath);
//        int dotindex = assetpath.lastIndexOf(".");
//        if(dotindex!=-1) {
//            String type = assetpath.substring(dotindex,assetpath.length());
//            String compressPath = mSession.getCompressPath();
//            if(Build.VERSION.SDK_INT>=18) {
//                FileOutputStream fos = null;
//                try {
//                    mCacheVideoPath = compressPath+(compressPath.endsWith(File.separator)?"":File.separator)+"savorVideo"+type;
//                    Uri uriForFile = FileProvider.getUriForFile(this, "com.savor.savorphone.fileprovider", srcFile);
//                    ContentResolver contentResolver = getContentResolver();
//                    ParcelFileDescriptor pFileDesCripter = contentResolver.openFileDescriptor(uriForFile, "r");
//                    FileDescriptor fileDesCripter = pFileDesCripter.getFileDescriptor();
//                    MediaTranscoder.getInstance().transcodeVideo(fileDesCripter, mCacheVideoPath,
//                            MediaFormatStrategyPresets.createAndroid720pStrategy(200 * 1000, 128 * 1000, 1), listener);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                    handleFileCopy(videoInfo, srcFile, type, compressPath);
//                }
//            }else {
//                handleFileCopy(videoInfo, srcFile, type, compressPath);
//            }
//        }
//
//
//    }
//
//    /**文件拷贝到压缩目录下*/
//    private void handleFileCopy(VideoInfo videoInfo, File srcFile, String type, String compressPath) {
////        copyFile(srcFile, type, compressPath);
//        Message message = Message.obtain();
//        message.what = HANDLE_OVER;
//        message.obj = videoInfo;
//        mUiHandler.sendMessage(message);
//    }
//
//    private void copyFile(File srcFile, String type, String compressPath) {
//        boolean isNeedCopy = FileUtils.copyFile(srcFile,compressPath,"savorVideo"+type,new FileUtils.OnProgressChangeListener(){
//            @Override
//            public void onProgressChange(String progress) {
//                Message message = Message.obtain();
//                message.what = UPDATE_PROGRESS;
//                message.obj = progress;
//                mUiHandler.sendMessage(message);
//            }
//        });
//    }
//
//    /**查询视频子线程handler*/
//    private Handler mQueryVideoHandler;
//    private VideoShareAdapter mVideoShareAdapter;
//    private ProjectionService mProjectionService;
//    private ServiceConnection mServiceConn = new ServiceConnection() {
//        @Override
//        public void onServiceConnected(ComponentName name, IBinder service) {
//
//            mProjectionService = ((ProjectionService.ProjectionBinder) service).getService();
//        }
//
//        @Override
//        public void onServiceDisconnected(ComponentName name) {
//
//        }
//    };
//    @Override
//    protected void onCreate(Bundle arg0) {
//        super.onCreate(arg0);
//        setContentView(R.layout.activity_video_share);
//        getViews();
//        setViews();
//        setListeners();
//        // 开始查询进度
//        Intent intent = new Intent(this, ProjectionService.class);
//        intent.putExtra(ProjectionService.EXTRA_TYPE,ProjectionService.TYPE_VOD_VIDEO);
//        bindService(intent,mServiceConn, Context.BIND_AUTO_CREATE);
//    }
//
//
//    @Override
//    protected void onResume() {
//        super.onResume();
//        RecordUtils.onPageStart(this, getString(R.string.video_to_screen_list));
//    }
//
//    @Override
//    protected void onPause() {
//        super.onPause();
//        RecordUtils.onPageEndAndPause(this, this);
//    }
//
//    /**
//     * 关联控件
//     */
//    @Override
//    public void getViews() {
//        back = (ImageView) findViewById(R.id.iv_left);
//        title = (TextView) findViewById(R.id.tv_center);
//        videoList = (GridView) findViewById(R.id.gv_videos);
//        mVideoShareAdapter = new VideoShareAdapter(this);
//        videoList.setAdapter(mVideoShareAdapter);
//    }
//
//    /**
//     * 初始化控件
//     */
//    @Override
//    public void setViews() {
//        title.setText("视频列表");
//
//        initVideoList();
//    }
//
//    /**
//     * 设置监听事件
//     */
//    @Override
//    public void setListeners() {
//        back.setOnClickListener(this);
//        videoList.setOnItemClickListener(this);
//    }
//
//    @Override
//    public void onBackPressed() {
//        setResult(HotspotMainActivity.FROM_APP_BACK);
//        super.onBackPressed();
//    }
//
//    //点击事件
//    @Override
//    public void onClick(View view) {
//        switch (view.getId()) {
//            case R.id.iv_left:
//                onBackPressed();
//                break;
//        }
//    }
//
//    private void updateVideoList() {
//        if(videoInfoList!=null&&!videoInfoList.isEmpty()) {
//            mVideoShareAdapter.setData(videoInfoList);
//        }
//    }
//
//    /**
//     * 获取视频列表
//     */
//    private void initVideoList() {
//        HandlerThread queryVedioThread = new HandlerThread("query_vedio");
//        queryVedioThread.start();
//        mQueryVideoHandler = new Handler(queryVedioThread.getLooper(),mQueryVedioCallBack);
//        mQueryVideoHandler.sendEmptyMessage(QUERY_VIDEO);
//    }
//
//
//    @Override
//    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//        if(!AppUtils.isNetworkAvailable(this)) {
//            showToast(getString(R.string.network_error));
//            return;
//        }
//
//        VideoInfo modelVideo = (VideoInfo) parent.getItemAtPosition(position);
//
////        LogUtils.d( "视频地址:" + modelVideo.getAsseturl());
//        mLoadingDialog = new SavorDialog(this,"  视频初始化...  ");
//        mLoadingDialog.setCanCancle(false);
//        mLoadingDialog.setOnBackKeyDowListener(new SavorDialog.OnBackKeyDownListener() {
//            @Override
//            public void onBackKeyDown() {
//                isFinish = true;
//                finish();
//            }
//            });
//        mLoadingDialog.show();
//        Message message = Message.obtain();
//        message.what = PROCESS_VIDEO;
//        message.obj = modelVideo;
//        mQueryVideoHandler.sendMessage(message);
//    }
//
//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        mUiHandler.removeMessages(HANDLE_OVER);
//        mQueryVideoHandler.removeMessages(PROCESS_VIDEO);
//        mQueryVideoHandler.removeCallbacksAndMessages(null);
//        mUiHandler.removeCallbacksAndMessages(null);
//    }
//
//    @Override
//    public void onError(AppApi.Action method, Object obj) {
//        mLoadingDialog.dismiss();
////        super.onError(method, obj);
//        if(obj == AppApi.ERROR_TIMEOUT) {
//            showToast(getString(R.string.projection_error));
//        }
//        switch (method) {
//            case POST_LOCAL_VIDEO_PRO_JSON:
//                if(obj instanceof ResponseErrorMessage) {
//                    ResponseErrorMessage responseErrorMessage = (ResponseErrorMessage) obj;
//                    String msg = responseErrorMessage.getMessage();
//                    int code = responseErrorMessage.getCode();
//                    if (code==4){
//                        showConfirm(msg);
//                    }else{
//                        if (!TextUtils.isEmpty(msg)){
//                            showToast(msg);
//                        }
//                    }
//
//                }
//                break;
//        }
//    }
//
//    /**
//     * 不好啦，别人正在投屏，弹出是否确认抢投按钮
//     * @param msg
//     */
//    private void showConfirm(String msg){
//        final String content = "当前"+msg+"正在投屏,是否继续投屏?";
//        dialog = new CommonDialog(this, content,
//                new CommonDialog.OnConfirmListener() {
//                    @Override
//                    public void onConfirm() {
//                        HashMap<String,String> params = new HashMap<>();
//                        params.put(getString(R.string.to_screen_competition_hint),"ensure");
//                        params.put("type","video");
//                        RecordUtils.onEvent(VideoShareActivity.this,getString(R.string.to_screen_competition_hint),params);
//
//                        force = 1;
//                        AppApi.localVideoPro(VideoShareActivity.this,mSession.getTVBoxUrl(),mCurrentVideoInfo,force,VideoShareActivity.this);
//                        dialog.cancel();
//                    }
//                }, new CommonDialog.OnCancelListener() {
//            @Override
//            public void onCancel() {
//                HashMap<String,String> params = new HashMap<>();
//                params.put(getString(R.string.to_screen_competition_hint),"cancel");
//                params.put("type","video");
//                RecordUtils.onEvent(VideoShareActivity.this,getString(R.string.to_screen_competition_hint),params);
//
//                dialog.cancel();
//            }
//        },"继续投屏",true);
//        dialog.show();
//    }
//
//    MediaTranscoder.Listener listener = new MediaTranscoder.Listener() {
//        @Override
//        public void onTranscodeProgress(double progress) {
//            BigDecimal db = new BigDecimal(progress);
//            String ii = db.toPlainString();
//            Double d = Double.valueOf(ii);
//            int pro = (int) (d*100);
//            Message message = Message.obtain();
//            message.what = UPDATE_PROGRESS;
//            message.obj = String.valueOf(pro)+"%";
//            mUiHandler.sendMessage(message);
//        }
//
//        @Override
//        public void onTranscodeCompleted() {
//            Message message = Message.obtain();
//            message.what = HANDLE_OVER;
//            mCurrentVideoInfo.setAsseturl(NetWorkUtil.getLocalUrl(VideoShareActivity.this)+mCacheVideoPath);
//            message.obj = mCurrentVideoInfo;
//            mUiHandler.sendMessage(message);
//        }
//
//        @Override
//        public void onTranscodeCanceled() {
//            LogUtils.d("savor:video trans cacel");
//        }
//
//        @Override
//        public void onTranscodeFailed(Exception exception) {
//            dismissLoadingDialog();
//            String assetpath = mCurrentVideoInfo.getAssetpath();
//            File srcFile = new File(assetpath);
//            int dotindex = assetpath.lastIndexOf(".");
//            String type = ".mp4";
//            if(dotindex!=-1) {
//                type = assetpath.substring(dotindex, assetpath.length());
//            }
//            handleFileCopy(mCurrentVideoInfo, srcFile, type, mSession.getCompressPath());
////            showToast("暂不支持该视频格式");
//            LogUtils.d("savor:video trans failed");
//        }
//    };
//
//    @Override
//    public void onSuccess(AppApi.Action method, Object obj) {
//        super.onSuccess(method, obj);
//        switch (method) {
//            case POST_LOCAL_VIDEO_PRO_JSON:
//                mLoadingDialog.dismiss();
//                if(obj instanceof LocalVideoProPesponse) {
//                    LocalVideoProPesponse response = (LocalVideoProPesponse) obj;
//                    projectId = response.getProjectId();
//                    ProjectionManager.getInstance().setmProjectId(projectId);
////                    prepareSuccess(prepareResponseVo);
//                    Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
//                    Ringtone r = RingtoneManager.getRingtone(SavorApplication.getInstance(), notification);
//                    r.play();
//
//                    dismissLoadingDialog();
//
//                    LocalVideoProAcitvity.startLocalVideoProActivity(this,mCurrentVideoInfo,true,projectId);
////                    Intent intent = new Intent(this,LocalVideoProAcitvity.class);
////                    intent.putExtra("ModelVideo", mCurrentVideoInfo);
////                    startActivity(intent);
//
//                    if(mProjectionService!=null) {
//                        mProjectionService.stopSlide();
//                    }
//                    ProjectionManager.getInstance().setSlideStatus(false);
//                }
//
//                break;
//        }
////        Intent intent = new Intent(VideoShareActivity.this, LocalVideoProAcitvity.class);
////        intent.putExtra("ModelVideo", videoInfo);
////        startActivity(intent);
//    }
//
//    private void dismissLoadingDialog() {
//        if(mLoadingDialog!=null)
//            mLoadingDialog.dismiss();
//    }
//
//}
