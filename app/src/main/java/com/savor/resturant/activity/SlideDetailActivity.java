package com.savor.resturant.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.common.api.core.InitViews;
import com.common.api.okhttp.OkHttpUtils;
import com.common.api.utils.AppUtils;
import com.common.api.utils.DensityUtil;
import com.common.api.utils.LogUtils;
import com.common.api.utils.ShowMessage;
import com.google.gson.Gson;
import com.savor.resturant.R;
import com.savor.resturant.adapter.RoomListAdapter;
import com.savor.resturant.adapter.SlideDetailAdapter;
import com.savor.resturant.bean.HotelBean;
import com.savor.resturant.bean.ImageProResonse;
import com.savor.resturant.bean.MediaInfo;
import com.savor.resturant.bean.RoomInfo;
import com.savor.resturant.bean.SlideSetInfo;
import com.savor.resturant.bean.SlideSettingsMediaBean;
import com.savor.resturant.bean.SmallPlatInfoBySSDP;
import com.savor.resturant.bean.SmallPlatformByGetIp;
import com.savor.resturant.bean.TvBoxSSDPInfo;
import com.savor.resturant.core.ApiRequestListener;
import com.savor.resturant.core.AppApi;
import com.savor.resturant.core.ResponseErrorMessage;
import com.savor.resturant.utils.CompressImage;
import com.savor.resturant.utils.IntentUtil;
import com.savor.resturant.utils.MediaUtils;
import com.savor.resturant.utils.ProgressDialogUtil;
import com.savor.resturant.utils.RecordUtils;
import com.savor.resturant.utils.SlideManager;
import com.savor.resturant.utils.WifiUtil;
import com.savor.resturant.widget.CommonDialog;
import com.savor.resturant.widget.CustomAlertDialog;
import com.savor.resturant.widget.LoadingProgressDialog;
import com.savor.resturant.widget.SavorDialog;
import com.savor.resturant.widget.SlideSettingsDialog;
import com.savor.resturant.widget.decoration.SpacesItemDecoration;
import com.yixia.videoeditor.adapter.UtilityAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import mabeijianxi.camera.FFMpegUtils;
import mabeijianxi.camera.VCamera;
import mabeijianxi.camera.model.VideoInfo;

import static com.savor.resturant.utils.IntentUtil.KEY_SLIDE;
import static com.savor.resturant.utils.IntentUtil.KEY_TYPE;

/**
 * 幻灯片详情界面
 * Created by luminita on 2016/12/15.
 */

public class SlideDetailActivity extends BaseActivity implements InitViews, View.OnClickListener, IBindTvView, LoadingProgressDialog.OnCancelBtnClickListener, RoomListAdapter.OnRoomItemClicklistener {
    public static final int BUFFER_SIZE = 1024 * 1024;
    private static final int QUERY_PROGRESS = 0x1;
    private static final String FFMPEG_FLAG = "compress";
    private ImageView back;
    private TextView title;
    private RelativeLayout editBar;
    private TextView editImg;
    private TextView editText;
    private CheckBox checkAll;
    private RelativeLayout toScreen;
    private ImageView delete;
    private GridView pictureGroup;
    private SlideSetInfo slideInfo;
    private Context mContext;
    private List<MediaInfo> picList = new LinkedList<>(); //已选择图片的集合
    // 所有图片名称
    public List<String> imageNameList = new ArrayList<>();
    private SlideDetailAdapter slideDetailAdapter;
    private boolean mIsEdit = false;
    private boolean mIsCheckAll = false;
    private TextView add;
    private SavorDialog mQrcodeDialog;
    private List<SlideSettingsMediaBean> slideSettingsMediaBeanResultList = new ArrayList<>();
    private String currentUploadFile = null;
    private SlideSettingsDialog settingDialog = null;
    /**
     * 投屏时遇到别人正在投屏，传1代表确认抢投，默认传0
     */
    private int force = 0;
    private CommonDialog dialog;
    private static final int TOAST_ERROR_MSG = 0x2;
    private static final int FORCE_MSG = 0x5;
    private static final int IMAGE_UPLOAD_RESPONSE = 0x6;
    private static final int VIDEO_UPLOAD_RESPONSE = 1000;
    private static final int UPLOAD_TIMEOUT = 0x7;
    /**
     * 添加图片响应码
     */
    public static final int RESULT_CODE_ADD_PIC = 0x9;
    /**
     * 当前操作类型
     */
    private int mOperationType = TYPE_PRO;
    /**
     * 当前操作点击投屏按钮
     */
    private static final int TYPE_PRO = 100;
    /**
     * 当前操作点击幻灯片设置确定按钮去绑定
     */
    private static final int TYPE_CONFIRM = 101;
    /**
     * 是否已经停止上传，比如上传过程中退出页面
     */
    private boolean isStopUpload;
    private Handler mHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case UPLOAD_TIMEOUT:
                    closeProgressBarDialog();
                    showToast("网络错误，请重试");
                    break;
                case TOAST_ERROR_MSG:
                    closeProgressBarDialog();
                    String messgae = (String) msg.obj;
                    ShowMessage.showToast(SlideDetailActivity.this, messgae);
                    break;
                case FORCE_MSG:
                    messgae = (String) msg.obj;
                    closeProgressBarDialog();
                    showConfirm(messgae);
                    break;
                case IMAGE_UPLOAD_RESPONSE:
                    handleImageUploadResponse(msg.obj);
                    break;
            }
        }
    };
    private SlideManager.SlideType slideType;
    /**
     * 视频碎片总数
     */
    private long count;
    /**
     * 当前上传视频碎片位置
     */
    private long offset;
    /**当前正在上传视频第几个*/
//    private int currentOffset;
    /**要上传的视频总数*/
    private int currentVideoCount;
    private String crruntFileUrl;
    private Handler mCompressHandler;
    private HandlerThread mCompressThread;
    private boolean isUpload;
    private SlideSettingsMediaBean currentSLideBean;
    /**当前选择的包间*/
    private RoomInfo currentRoom;
    /**当前是否是选择房间模式*/
    private boolean isSelectRoomState;
    private RecyclerView mRoomListView;
    private RoomListAdapter roomListAdapter;
    private String needLinkWifi;
    /**是否在前台*/
    private boolean isForground;
    /**请求房间列表的失败次数*/
    private int erroCount;

    private void handleImageUploadResponse(Object obj) {
        if (obj instanceof ImageProResonse) {
            ImageProResonse resonse = (ImageProResonse) obj;
            int result = resonse.getResult();
            if (result == 0) {
                if (!TextUtils.isEmpty(currentUploadFile)) {
                    for (SlideSettingsMediaBean bean : slideSettingsMediaBeanResultList) {
                        if (bean.getName() == currentUploadFile) {
                            bean.setExist(1);
                            if(!TextUtils.isEmpty(crruntFileUrl)) {
                                String compressPath = mSession.getCompressPath(SlideDetailActivity.this);
                                if(crruntFileUrl.contains(compressPath)) {
                                    File file = new File(crruntFileUrl);
                                    if(file.exists())
                                        file.delete();
                                }
                            }
                            break;
                        }
                    }
                }
                if(slideType == SlideManager.SlideType.VIDEO) {
                    mProgressBarDialog.updatePercent("正在上传",100, SlideManager.SlideType.VIDEO);
                }
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        switch (slideType) {
                            case IMAGE:
                                uploadPictureToServer();
                                break;
                            case VIDEO:
                                uploadVideoToServer();
                                break;
                        }
                    }
                },1500);

            }
        }
    }

    private CommonDialog commonDialog;
    private LoadingProgressDialog mProgressBarDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_slide_detail);
        mContext = SlideDetailActivity.this;
        slideInfo = (SlideSetInfo) getIntent().getSerializableExtra(KEY_SLIDE);
        slideType = (SlideManager.SlideType) getIntent().getSerializableExtra("type");
        getViews();
        setViews();
        setListeners();
//        initPresenter();
        initFFmpeg();
        initCompressThread();
    }

    private void initCompressThread() {
        mCompressThread = new HandlerThread(FFMPEG_FLAG);
        mCompressThread.start();
        mCompressHandler = new Handler(mCompressThread.getLooper()){
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case QUERY_PROGRESS:
                        // 查询进度，更新ui
                        final MediaInfo info = (MediaInfo) msg.obj;
                        long duration = info.getDuration();
                        final int index = slideInfo.imageList.indexOf(info);
                        int progress = 0;
                        while (UtilityAdapter.FFmpegIsRunning(FFMPEG_FLAG)) {
                            try {
                                Thread.sleep(1000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            int currentTime = UtilityAdapter.FFmpegVideoGetTransTime(0);
                            final float per = currentTime/(duration*1.0f);
                            progress = (int) (per*100);
                            if(progress>100)
                                progress = 100;

                            final int finalProgress = progress;
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if(mProgressBarDialog!=null) {
                                        mProgressBarDialog.updatePercent("正在转化第"+(index+1)+"个视频", finalProgress,slideType);
                                    }
                                }
                            });
                        }

                        if(progress == 100&&!isStopUpload) {
                            // 开始上传碎片文件
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    mProgressBarDialog.updatePercent("正在载入幻灯片",0,slideType);
                                    String assetpath = info.getAssetpath();
                                    String videoName = MediaUtils.getVideoName(assetpath, settingDialog.getQuality());
                                    String compressVideoPath = getCompressVideoPath(videoName);
                                    LogUtils.d("savor:video isRunning"+UtilityAdapter.FFmpegIsRunning(FFMPEG_FLAG));

                                    LogUtils.d("savor:video 开始上传碎片文件，压缩路径为compressVideoPath="+compressVideoPath);
                                    startUploadFragment(currentSLideBean,compressVideoPath,videoName);
                                }
                            });
                        }
                        break;
                }
            }
        };
    }

    private void initFFmpeg() {
        // 开启log输出,ffmpeg输出到logcat
//        VCamera.setDebugMode(true);
        // 初始化拍摄SDK，必须
        VCamera.initialize(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        RecordUtils.onPageStart(this, getString(R.string.slide_to_screen_open_album));
        isForground = true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        RecordUtils.onPageEndAndPause(this, this);
        isForground = false;
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        setIntent(intent);
    }

    @Override
    protected void onRestart() {
        super.onRestart();

        checkAll.setText("全选");
        mIsCheckAll = false;
        checkAll.setChecked(mIsCheckAll);
        slideInfo = (SlideSetInfo) getIntent().getSerializableExtra(KEY_SLIDE);
        refreshImageList(slideInfo);

        if (slideInfo.imageList == null || slideInfo.imageList.size() == 0) {
            finish();
            return;
        }
        picList.clear();
        imageNameList.clear();
        picList = slideInfo.imageList;
        MediaUtils.getFolderAllNames(mContext, slideInfo.imageList, imageNameList);
        slideDetailAdapter.setData(picList);
        Intent intent = getIntent();
        if (intent != null) {
            int type = intent.getIntExtra(KEY_TYPE, 0);
            if (type == IntentUtil.TYPE_SLIDE_BY_DETAIL) {
                mIsEdit = true;
                changeEditState();
            }
        }
    }

    private void refreshImageList(SlideSetInfo slideInfo) {
        List<MediaInfo> imageList = slideInfo.imageList;
        List<MediaInfo> tempList = new ArrayList<>();
        if (imageList != null && imageList.size() > 0) {
            for (int i = 0; i < imageList.size(); i++) {
                MediaInfo mediaInfo = imageList.get(i);
                String imagePath = mediaInfo.getAssetpath();
                if (!TextUtils.isEmpty(imagePath) && new File(imagePath).exists()) {
                    tempList.add(mediaInfo);
                }
            }
        }
        slideInfo.imageList = tempList;
        save();
    }

    @Override
    public void getViews() {
        back = (ImageView) findViewById(R.id.iv_back);
        add = (TextView) findViewById(R.id.add);
        title = (TextView) findViewById(R.id.tv_title);
        editBar = (RelativeLayout) findViewById(R.id.rl_edit);
        editImg = (TextView) findViewById(R.id.iv_edit);
        editText = (TextView) findViewById(R.id.tv_ok);
        checkAll = (CheckBox) findViewById(R.id.cb_checkall);
        toScreen = (RelativeLayout) findViewById(R.id.rl_toscreen);
        delete = (ImageView) findViewById(R.id.iv_delete);
        pictureGroup = (GridView) findViewById(R.id.gv_picture);
        mRoomListView = (RecyclerView) findViewById(R.id.rlv_room);
    }

    @Override
    public void setViews() {
        switch (slideType) {
            case VIDEO:
                add.setText("添加视频");
                break;
            case IMAGE:
                add.setText("添加图片");
                break;
        }
        title.setText(slideInfo.groupName);
        picList = slideInfo.imageList;
        MediaUtils.getFolderAllNames(mContext, slideInfo.imageList, imageNameList);
        slideDetailAdapter = new SlideDetailAdapter(mContext);
        pictureGroup.setAdapter(slideDetailAdapter);
        slideDetailAdapter.setData(picList);
        if (!AppUtils.isWifiNetwork(this)) {
            showChangeWifiDialog("");
        }

        initRoomList();
    }

    private void initRoomList() {
        //添加ItemDecoration，item之间的间隔
        int leftRight = DensityUtil.dip2px(this,15);
        int topBottom = DensityUtil.dip2px(this,15);
        GridLayoutManager roomLayoutManager = new GridLayoutManager(this,3);
        roomLayoutManager.setOrientation(GridLayoutManager.VERTICAL);
        mRoomListView.setLayoutManager(roomLayoutManager);
        roomListAdapter = new RoomListAdapter(this);
        mRoomListView.setAdapter(roomListAdapter);
        List<RoomInfo> roomList = mSession.getRoomList();
        mRoomListView.addItemDecoration(new SpacesItemDecoration(leftRight, topBottom, getResources().getColor(R.color.white)));
        if(roomList!=null && roomList.size()>0) {
            roomListAdapter.setData(roomList);
        }
    }

    @Override
    public void setListeners() {
        roomListAdapter.setOnRoomItemClickListener(this);
        back.setOnClickListener(this);
        title.setOnClickListener(this);
        editBar.setOnClickListener(this);
        checkAll.setOnClickListener(this);
        toScreen.setOnClickListener(this);
        delete.setOnClickListener(this);
        add.setOnClickListener(this);

        pictureGroup.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                MediaInfo mediaInfo = picList.get(i);
                if (mIsEdit) {
                    mediaInfo.setChecked(!mediaInfo.isChecked());
                    //如果照片通过单击全部选择，则相应的改变左下角全选按钮状态
                    if (!mIsCheckAll && isCheckAll()) {
                        mIsCheckAll = true;
                        checkAll.setText("取消全选");
                        checkAll.setChecked(true);
                    }
                    if (!mediaInfo.isChecked() && mIsCheckAll) {
                        mIsCheckAll = false;
                        checkAll.setText("全选");
                        checkAll.setChecked(false);
                    }
                } else {
                    Intent intent = new Intent();
                    intent.putExtra("photos", slideInfo);
                    intent.putExtra("position", i);
                    intent.setClass(SlideDetailActivity.this, SlidePreviewActivity.class);
                    intent.putExtra("type", slideType);
                    startActivity(intent);
                }
                slideDetailAdapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                if(isSelectRoomState) {
                    hideRoomList();
                }else {
                    onBackPressed();
                }
                break;
            case R.id.add:
                if (picList.size() >= 50) {
                    ShowMessage.showToast(mContext, "最多只能添加50张");
                    return;
                }

                resetMediaList(picList);
                //跳转至照片列表
                Intent intent = new Intent(SlideDetailActivity.this, PhotoActivity.class);
                intent.putExtra(KEY_TYPE, IntentUtil.TYPE_SLIDE_BY_DETAIL);
                intent.putExtra(KEY_SLIDE, slideInfo);
                intent.putExtra(IntentUtil.MEDIA_TYPE, slideType);
                startActivity(intent);
                break;
            case R.id.rl_edit:
                changeEditState();
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(),
                            0);
                }
                break;
            case R.id.cb_checkall:
                changeCheckAllState();
                break;
            case R.id.rl_toscreen:
                mOperationType = TYPE_PRO;
                // 投屏
                performProjection();
                break;
            case R.id.iv_delete:
                delMedia();
                break;
        }
    }

    private void resetMediaList(List<MediaInfo> picList) {
        for(MediaInfo info:picList) {
            info.setChecked(false);
        }
        slideDetailAdapter.notifyDataSetChanged();
    }

    private void hideRoomList() {
        if(mRoomListView.getVisibility() != View.VISIBLE)
            return;
        // 清楚房间选择记录
        resetRoomList();

        back.setImageResource(R.drawable.back);
        title.setText(slideInfo.groupName);
        editBar.setVisibility(View.VISIBLE);

        Animation animation = AnimationUtils.loadAnimation(this, R.anim.actionsheet_dialog_out);
        mRoomListView.startAnimation(animation);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mRoomListView.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        isSelectRoomState = false;
    }

    private void performProjection() {
        isStopUpload = false;
        // 判断是否选择了包间
        // 1.选择包间
        // 2.向盒子发投屏请求
        showRoomList();
        ShowMessage.showToast(this,"请选择包间");

//        String box_ip = currentRoom.getBox_ip();
//
//        String wifiName = WifiUtil.getWifiName(this);
//        String box_name = currentRoom.getBox_name();
//        String box_ip = currentRoom.getBox_ip();
//        String localIp = WifiUtil.getLocalIp(this);
//        if(!TextUtils.isEmpty(wifiName)&&wifiName.equals(box_name)&&!TextUtils.isEmpty(localIp)&&WifiUtil.isInSameNetwork(localIp,box_ip)) {
//            showSlideSettings();
//        }else {
//            needLinkWifi = box_name;
//            showChangeWifiDialog(box_name);
//        }


//        if (!isFoundTv()) {
//            showChangeWifiDialog();
//        } else {
//            if (mSession.isBindTv()) {
//                // 幻灯片设置
//                showSlideSettings();
//            } else {
//                // 搜索电视
//                mBindTvPresenter.bindTv();
//            }
//        }
    }

    private void showRoomList() {
        isSelectRoomState = true;
        back.setImageResource(R.drawable.ico_close);
        title.setText("选择投屏房间");
        editBar.setVisibility(View.GONE);

        Animation animation = AnimationUtils.loadAnimation(this, R.anim.actionsheet_dialog_in);
        mRoomListView.startAnimation(animation);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                mRoomListView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animation animation) {

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        isSelectRoomState  = true;
    }

    public void showSlideSettings() {
        settingDialog = new SlideSettingsDialog(this, slideType);
        settingDialog.builder()
                .setCancelable(false)
                .setNegativeButton("取消", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                })
                .setPositiveButton("确定", new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {

                        if(AppUtils.isNetworkAvailable(SlideDetailActivity.this)) {
                            if(!AppUtils.isFastDoubleClick(1)) {
                                settingDialog.dismiss();
                                showProgressBarDialog();
                                performSlideSettingsConfirm();
                            }
                        }else {
                            settingDialog.dismiss();
                            showToast("网络已断开，请检查");
                        }

//                        mOperationType = TYPE_CONFIRM;
//                        if (!isFoundTv()) {
//                            showChangeWifiDialog();
//                        } else {
//                            if (mSession.isBindTv()) {
//                                // 幻灯片设置
//                                performSlideSettingsConfirm();
//                            } else {
//                                // 搜索电视
//                                mBindTvPresenter.bindTv();
//                            }
//                        }

                    }
                });
        if (!settingDialog.isShowing())
            settingDialog.show();
    }

    private void performSlideSettingsConfirm() {
        force = 0;
        boolean looping = settingDialog.isLooping();
        int loopTime = 0;
        if (looping) {
            loopTime = settingDialog.getLoopTime() * 60;
        }

        switch (slideType) {
            case IMAGE:
                postImageSlideParamToServer(slideInfo.groupName, loopTime, settingDialog.getSingleTime(), force);
                break;
            case VIDEO:
                postVideoSlideParamToServer(slideInfo.groupName, loopTime, force);
                break;
        }
    }


    /**
     * 上传图片幻灯片参数到服务器端
     *
     * @param name     幻灯片名称
     * @param duration 总时长
     * @param interval 间隔时间
     */
    public void postImageSlideParamToServer(String name, int duration, int interval, int force) {
        JSONArray jsonArray = new JSONArray();
        int quality = settingDialog.getQuality();
        if (imageNameList != null && !imageNameList.isEmpty()) {
            for (String str : imageNameList) {
                JSONObject jsonObject = new JSONObject();
                try {
                    String picName = MediaUtils.getPicName(str,quality);
                    jsonObject.accumulate("name", picName);
                    jsonObject.accumulate("exist", "0");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                jsonArray.put(jsonObject);
            }

        }
        HashMap<String, Object> param = new HashMap<>();
        param.put("name", name);
        param.put("duration", duration + "");
        param.put("interval", interval + "");
        param.put("images", jsonArray);
        String url = "http://"+currentRoom.getBox_ip()+":8080";
        AppApi.postImageSlideSettingToServer(mContext, url, param, force, this);
    }

    /**
     * 上传图片幻灯片参数到服务器端
     *
     * @param name     幻灯片名称
     * @param duration 总时长
     */
    public void postVideoSlideParamToServer(String name, int duration, int force) {
        JSONArray jsonArray = new JSONArray();
        int quality = settingDialog.getQuality();
        long size = 0;
        switch (quality) {
            case SlideSettingsDialog.QUALITY_HIGH:
                for (int i = 0; i < picList.size(); i++) {
                    MediaInfo mediaInfo = picList.get(i);
                    long length = mediaInfo.getSize();
                    size += length;
                }
                break;
            case SlideSettingsDialog.QUALITY_LOW:
                for (int i = 0; i < picList.size(); i++) {
                    MediaInfo mediaInfo = picList.get(i);
                    long length = mediaInfo.getSize();
                    size += length;
                }
                size = (long) (size / 3.0f);
                break;
        }
        if (imageNameList != null && !imageNameList.isEmpty()) {
            for (String str : imageNameList) {
                JSONObject jsonObject = new JSONObject();
                try {
                    String videoName = MediaUtils.getVideoName(str, quality);
                    jsonObject.accumulate("name", videoName);
                    jsonObject.accumulate("length", size);
                    jsonObject.accumulate("exist", "0");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                jsonArray.put(jsonObject);
            }

        }
        HashMap<String, Object> param = new HashMap<>();
        param.put("name", name);
        param.put("duration", duration + "");
        param.put("videos", jsonArray);
        String url = "http://"+currentRoom.getBox_ip()+":8080";
        AppApi.postVideoSlideSettingToServer(mContext, url, param, force, this);
    }

    /**
     * 编辑状态改变
     */
    private void changeEditState() {
        mIsEdit = !mIsEdit;
        if (mIsEdit) {  //进入编辑状态，显示完成
            slideDetailAdapter.setEditState(true);
            editImg.setVisibility(View.GONE);
            editText.setVisibility(View.VISIBLE);
            toScreen.setVisibility(View.GONE);
            checkAll.setVisibility(View.VISIBLE);
            delete.setVisibility(View.VISIBLE);
            add.setVisibility(View.VISIBLE);
            resetMediaList(picList);
        } else {    //进入非编辑状态，点击完成保存数据
            save();
            slideDetailAdapter.setEditState(false);
            editImg.setVisibility(View.VISIBLE);
            editText.setVisibility(View.GONE);
            toScreen.setVisibility(View.VISIBLE);
            checkAll.setVisibility(View.GONE);
            delete.setVisibility(View.GONE);
            add.setVisibility(View.GONE);
        }
        slideDetailAdapter.notifyDataSetChanged();
    }

    /**
     * 全选状态改变
     */
    private void changeCheckAllState() {
        mIsCheckAll = !mIsCheckAll;
        checkAll.setChecked(mIsCheckAll);
        if (mIsCheckAll) {    // 全选模式
            checkAll.setText("取消全选");
            for (MediaInfo mediaInfo : picList) {
                mediaInfo.setChecked(true);
            }
        } else {     // 取消全选模式
            checkAll.setText("全选");
            for (MediaInfo mediaInfo : picList) {
                mediaInfo.setChecked(false);
            }
        }
        slideDetailAdapter.notifyDataSetChanged();
    }

    /**
     * 从幻灯片集删除选中的照片
     */
    private void delMedia() {
        int delcount = 0; //需要删除的数量
        for (MediaInfo mediaInfo : picList) {
            if (mediaInfo.isChecked()) {
                delcount++; //记录需要删除的数量
            }
        }
        if (delcount == 0) {
            String msg = "";
            switch (slideType) {
                case IMAGE:
                    msg = "请选择要删除的图片";
                    break;
                case VIDEO:
                    msg = "请选择要删除的视频";
                    break;
            }
            ShowMessage.showToast(this, msg);
            return;
        }
        String message = "";
        if (isCheckAll()) {
            switch (slideType) {
                case VIDEO:
                    message = "将删除此视频列表，但不会删除本地视频";
                    break;
                case IMAGE:
                    message = "将删除此幻灯片，但不会删除本地照片";
                    break;
            }

        } else {
            switch (slideType) {
                case IMAGE:
                    message = getString(R.string.confirm_delete_picture, delcount);
                    break;
                case VIDEO:
                    message = getString(R.string.confirm_delete_video, delcount);
                    break;
            }

        }
        new CustomAlertDialog(this)
                .builder()
                .setTitle(message)
                .setCancelable(false)
                .setPositiveButton("删除", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        delForSlide();
                        changeEditState();
                    }
                })
                .setNegativeButton("取消", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                    }
                })
                .show();
    }

    /**
     * 从幻灯片组中移除图片
     */
    private void delForSlide() {
        if (mIsCheckAll) {    //全选删除，则删除幻灯片组
            SlideManager.getInstance(slideType).removeGroup(slideInfo);
            SlideManager.getInstance(slideType).saveSlide();
            finish();
        }
        Set<MediaInfo> toRemove = new HashSet<MediaInfo>();
        //遍历该幻灯片集，把选中的添加至暂存器，并删除
        for (MediaInfo mediaInfo : picList) {
            if (mediaInfo.isChecked()) {
                toRemove.add(mediaInfo);
                slideInfo.imageList.remove(mediaInfo.getAssetpath());
                imageNameList.remove(mediaInfo.getAssetpath());
            }
        }
        picList.removeAll(toRemove);
        slideInfo.updateTime = System.currentTimeMillis();
        slideDetailAdapter.setData(picList);
    }

    /**
     * 保存编辑后的幻灯片集
     */
    private void save() {
        SlideManager instance = SlideManager.getInstance(slideType);
        //添加幻灯片组至该幻灯片列表
        if (instance.containGroup(slideInfo))
            instance.removeGroup(slideInfo);
        if (slideInfo != null && slideInfo.imageList != null && slideInfo.imageList.size() > 0) {
            instance.addList(slideInfo);
            instance.saveSlide();
        }
    }

    /**
     * 检查每一张照片是否是选择状态
     */
    private boolean isCheckAll() {
        for (MediaInfo mediaInfo : picList) {
            if (!mediaInfo.isChecked())
                return false;
        }
        return true;
    }

    private void showProgressBarDialog() {
        if (this.isFinishing())
            return;
        if (mProgressBarDialog == null) {
            mProgressBarDialog = new LoadingProgressDialog(this,this);
        }
        mProgressBarDialog.show();
    }

    private void closeProgressBarDialog() {
        if (mProgressBarDialog != null) {
            mProgressBarDialog.dismiss();
        }
    }

    /**
     * 将幻灯片的设置信息发送给盒子以后，开始上传视频到盒子
     */
    private void uploadVideoToServer() {

        if (slideSettingsMediaBeanResultList != null && slideSettingsMediaBeanResultList.size() > 0) {
            updateProgress();

            isUpload = false;
            for (final SlideSettingsMediaBean bean : slideSettingsMediaBeanResultList) {
                if (bean.getExist() == 0) {
                    currentVideoCount = slideInfo.imageList.size();
                    currentSLideBean = bean;
                    int quality = settingDialog.getQuality();
                    final int index = slideSettingsMediaBeanResultList.indexOf(bean);
                    MediaInfo mediaInfo = slideInfo.imageList.get(index);
                    String fileUrl = mediaInfo.getAssetpath();

                    final String videoName = MediaUtils.getVideoName(fileUrl, settingDialog.getQuality());
                    String mimeType = mediaInfo.getMimeType();

                    mProgressBarDialog.show();
                    int rotation = FFMpegUtils.getRotation(fileUrl);
                    if("video/mp4".equals(mimeType)) {
                        if(rotation !=0) {
                            LogUtils.d("savor:video 当前为mp4文件 角度为"+rotation);
                            compressAndUploadVideo(quality, mediaInfo, fileUrl, videoName, mimeType, rotation);
                        }else {
                            if(quality == SlideSettingsDialog.QUALITY_LOW) {
                                LogUtils.d("savor:video 当前为mp4文件 角度为0 质量普通");
                                compressAndUploadVideo(quality, mediaInfo, fileUrl, videoName, mimeType, rotation);
                            }else {
                                LogUtils.d("savor:video 当前为mp4文件 角度为0 质量高清 操作原文件"+fileUrl);
                                startUploadFragment(bean,fileUrl,videoName);
                            }
                        }
                    }else {
                        compressAndUploadVideo(quality, mediaInfo, fileUrl, videoName, mimeType, rotation);
                    }


                    if (videoName.equals(bean.getName())&&!isUpload) {
                        break;
                    }
                }else if(!isUpload){
                    offset = 0;
                }
                if (isUpload) {
                    break;
                }
            }
        }
    }

    private void compressAndUploadVideo(int quality, MediaInfo mediaInfo, String fileUrl, String videoName, String mimeType, int rotation) {
        String destPath = getCompressVideoPath(videoName);
        String info = UtilityAdapter.FFmpegVideoGetInfo(fileUrl);
        VideoInfo videoInfo = new Gson().fromJson(info, VideoInfo.class);
        String fFmpegCmd = FFMpegUtils.getFFmpegCmd(fileUrl, quality, rotation, destPath,videoInfo);
        // 开启异步压缩
        UtilityAdapter.FFmpegRun(FFMPEG_FLAG, fFmpegCmd) ;

        // 开启线程查询进度
        Message msg = Message.obtain();
        msg.obj = mediaInfo;
        msg.what = QUERY_PROGRESS;
        mCompressHandler.sendMessage(msg);
    }

    private boolean startUploadFragment(SlideSettingsMediaBean bean, String fileUrl, String videoName) {
        final String fragmentPath = mSession.getCompressPath(SlideDetailActivity.this) + File.separator + "frament";
        long size = new File(fileUrl).length();
        if (videoName.equals(bean.getName())&&!isUpload) {
            // 如果大于切片最小单位直接上传，否则切片并上传
            if (size > BUFFER_SIZE) {
                count = 0;
                offset = 0;
                if (size % BUFFER_SIZE == 0) {
                    count = size
                            / BUFFER_SIZE;
                } else {
                    count = (size / BUFFER_SIZE) + 1;
                }
                uploadVideoFragment(fileUrl, fragmentPath, videoName);
                currentUploadFile = bean.getName();
                crruntFileUrl = fileUrl;
                return true;
            }else {
                count = 1;
                offset = 0;
                uploadVideoFragment(fileUrl, fragmentPath, videoName);
                currentUploadFile = bean.getName();
                crruntFileUrl = fileUrl;
                return true;
            }

        }
        return false;
    }

    private String getCompressVideoPath(String videoName) {
        return mSession.getCompressPath(this)+videoName;
    }

    private void uploadVideoFragment(final String fileUrl, final String fragmentPath, final String videoName) {
        // 更新进度
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ProgressDialogUtil.getInstance().hideProgress();
                if(mProgressBarDialog!=null) {
                    mProgressBarDialog.updatePercent("正在上传", (int) ((offset/(count*1.0f))*100), SlideManager.SlideType.VIDEO);
                }
            }
        });

        RandomAccessFile raf = null;
        FileOutputStream tmpOut = null;
        try {
            raf = new RandomAccessFile(fileUrl, "r");
            tmpOut = new FileOutputStream(fragmentPath);
            raf.seek(offset * BUFFER_SIZE);
            byte[] buffer = new byte[BUFFER_SIZE];
            int read = raf.read(buffer);
            tmpOut.write(buffer, 0, read);
            final HashMap<String, Object> params = new HashMap<>();
            params.put("fileName", videoName);
            params.put("pptName", slideInfo.groupName);
            params.put("range", offset * BUFFER_SIZE + "-" + (offset == count - 1 ? "" : offset * BUFFER_SIZE + BUFFER_SIZE));

            final String url = "http://"+currentRoom.getBox_ip()+":8080";
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (offset == count - 1) {
                        isStopUpload = false;
                        AppApi.updateVideoFile(mContext, url, fragmentPath, params, SlideDetailActivity.this);
                    } else {
                        isStopUpload = true;
                        AppApi.updateVideoFile(mContext, url, fragmentPath, params, new ApiRequestListener() {
                            @Override
                            public void onSuccess(AppApi.Action method, Object obj) {
                                offset++;
                                uploadVideoFragment(fileUrl, fragmentPath, videoName);
                            }

                            @Override
                            public void onError(AppApi.Action method, Object obj) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        hideLoadingLayout();
                                        ShowMessage.showToast(SlideDetailActivity.this, "上传失败");
                                    }
                                });
                            }

                            @Override
                            public void onNetworkFailed(AppApi.Action method) {

                            }
                        });

                    }
                }
            });


        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (raf != null) {
                try {
                    raf.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (tmpOut != null) {
                try {
                    tmpOut.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 将幻灯片的设置信息发送给盒子以后，开始上传图片到盒子
     */
    private void uploadPictureToServer() {

        if (slideSettingsMediaBeanResultList != null && slideSettingsMediaBeanResultList.size() > 0) {
            updateProgress();

            boolean isUpload = false;
            for (SlideSettingsMediaBean bean : slideSettingsMediaBeanResultList) {
                if (bean.getExist() == 0) {
                    for (MediaInfo mediaInfo : slideInfo.imageList) {
                        int quality = settingDialog.getQuality();
                        String fileUrl = mediaInfo.getAssetpath();
                        String picName = MediaUtils.getPicName(fileUrl,quality);
                        String realName = MediaUtils.getMediaRealName(fileUrl);
                        if (picName.equals(bean.getName())) {
                            isUpload = true;
                            String copyFileUrl = fileUrl;
                            switch (quality) {
                                case SlideSettingsDialog.QUALITY_HIGH:
                                    copyFileUrl = CompressImage.compressAndSaveBitmap(this, fileUrl, realName, false);
                                    break;
                                case SlideSettingsDialog.QUALITY_LOW:
                                    copyFileUrl = CompressImage.compressAndSaveBitmap(this, fileUrl, realName, true);
                                    break;
                            }

                            HashMap<String, Object> params = new HashMap<>();
                            params.put("fileName", picName);
                            params.put("pptName", slideInfo.groupName);
                            String url = "http://"+currentRoom.getBox_ip()+":8080";
                            AppApi.updateImageFile(mContext, url, copyFileUrl, params, this);
                            currentUploadFile = bean.getName();
                            crruntFileUrl = copyFileUrl;
                            break;
                        }
                    }
                }
                if (isUpload) {
                    break;
                }
            }
        }
    }

    private void updateProgress() {
        int count = 0;
        for (int i = 0; i < slideSettingsMediaBeanResultList.size(); i++) {
            SlideSettingsMediaBean slideSettingsMediaBean = slideSettingsMediaBeanResultList.get(i);
            if (slideSettingsMediaBean.getExist() == 0) {
                count++;
            }

        }
        if (count == 0) {
            if (mProgressBarDialog != null) {
                switch (slideType) {
                    case VIDEO:
                        if(mProgressBarDialog!=null) {
                            mProgressBarDialog.updatePercent("",100,slideType);
                        }
                        setLog(slideSettingsMediaBeanResultList.size()+"","1",settingDialog.getLoopTime()*60+"","1");
                        break;
                    case IMAGE:
                        setLog(slideSettingsMediaBeanResultList.size()+"","1",settingDialog.getLoopTime()*60+"","2");
                        mProgressBarDialog.dismiss();
                        break;
                }
            }
            ShowMessage.showToast(this, getString(R.string.pro_success));
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    closeProgressBarDialog();
                    finish();
                }
            }, 100);
        }else if(slideType == SlideManager.SlideType.IMAGE) {
            double percent = ((double)count)/slideSettingsMediaBeanResultList.size();
            if(mProgressBarDialog!=null) {
                mProgressBarDialog.updatePercent("", (int) ((1-percent)*100), SlideManager.SlideType.IMAGE);
            }
        }

    }

    @Override
    public void onSuccess(AppApi.Action method, Object obj) {
        super.onSuccess(method, obj);
        switch (method) {
            case GET_HOTEL_BOX_JSON:
                if(obj instanceof List) {
                    List<RoomInfo> roomInfos = (List<RoomInfo>) obj;
                    mSession.setRoomList(roomInfos);
                    for(RoomInfo info : roomInfos) {
                        String room_id = info.getRoom_id();
                        String box_ip = info.getBox_ip();
                        if(!TextUtils.isEmpty(room_id)&&currentRoom!=null&&room_id.equals(currentRoom.getRoom_id())) {
                            currentRoom = info;
                            reConnect(info);
                            break;
                        }
                    }

                }
                break;
            case POST_VIDEO_SLIDESETTINGS_JSON:
                if (obj instanceof List<?>) {
                    slideSettingsMediaBeanResultList = (List<SlideSettingsMediaBean>) obj;
                    if (settingDialog != null && settingDialog.isShowing()) {
                        settingDialog.dismiss();
                    }
                    showProgressBarDialog();
                    force = 0;
                    isStopUpload = false;
                    uploadVideoToServer();
                }
                break;
            case POST_IMAGE_SLIDESETTINGS_JSON:
                if (obj instanceof List<?>) {
                    slideSettingsMediaBeanResultList = (List<SlideSettingsMediaBean>) obj;
                    if (settingDialog != null && settingDialog.isShowing()) {
                        settingDialog.dismiss();
                    }
                    showProgressBarDialog();
                    force = 0;
                    uploadPictureToServer();
                }
                break;
            case POST_IMAGE_PROJECTION_JSON:
                if (!isStopUpload) {
                    Message message = Message.obtain();
                    message.what = IMAGE_UPLOAD_RESPONSE;
                    message.obj = obj;
                    mHandler.sendMessage(message);
                }
                break;
            case POST_VIDEO_PROJECTION_JSON:
                if (!isStopUpload) {
                    Message message = Message.obtain();
                    message.what = IMAGE_UPLOAD_RESPONSE;
                    message.obj = obj;
                    mHandler.sendMessage(message);
                }
                break;
        }
    }


    @Override
    public void onError(AppApi.Action method, Object obj) {

        switch (method) {
            case GET_HOTEL_BOX_JSON:
                erroCount++;
                if(erroCount ==3) {
                    showToast("包间电视连接失败，请检查是否开机");
                }
                break;
            case POST_VIDEO_SLIDESETTINGS_JSON:
                if (obj instanceof ResponseErrorMessage) {
                    ResponseErrorMessage message = (ResponseErrorMessage) obj;
                    int code = message.getCode();
                    String error_msg = message.getMessage();
                    Message msg = Message.obtain();
//                    if (code == 4) {
//                        msg.what = FORCE_MSG;
//                        msg.obj = error_msg;
//                        mHandler.sendMessage(msg);
//                    } else {
//                        error_msg = "用户正在投屏，请稍后再试";
                    msg.what = TOAST_ERROR_MSG;
                    msg.obj = error_msg;
                    mHandler.sendMessage(msg);
//                    }
                } else if (obj == AppApi.ERROR_TIMEOUT) {
                    mHandler.sendEmptyMessage(UPLOAD_TIMEOUT);
                }
                break;
            case POST_IMAGE_SLIDESETTINGS_JSON:
                if (obj instanceof ResponseErrorMessage) {
                    ResponseErrorMessage message = (ResponseErrorMessage) obj;
                    int code = message.getCode();
                    String error_msg = message.getMessage();
                    Message msg = Message.obtain();
//                    if (code == 4) {
//                        msg.what = FORCE_MSG;
//                        msg.obj = error_msg;
//                        mHandler.sendMessage(msg);
//                    } else {
//                        error_msg = "用户正在投屏，请稍后再试";
                    msg.what = TOAST_ERROR_MSG;
                    msg.obj = error_msg;
                    mHandler.sendMessage(msg);
//                    }
                } else if (obj == AppApi.ERROR_TIMEOUT) {
                    mHandler.sendEmptyMessage(UPLOAD_TIMEOUT);
                }
                break;
            case POST_IMAGE_PROJECTION_JSON:
//                mRequestScreenDialog.dismiss();
                setLog(slideSettingsMediaBeanResultList.size()+"","0",settingDialog.getLoopTime()*60+"","2");
                if (obj instanceof ResponseErrorMessage) {
                    ResponseErrorMessage message = (ResponseErrorMessage) obj;
                    int code = message.getCode();
                    String error_msg = message.getMessage();
                    Message msg = Message.obtain();
                    if (code == 4) {
                        msg.what = FORCE_MSG;
                        msg.obj = error_msg;
                        mHandler.sendMessage(msg);
                    } else {
                        msg.what = TOAST_ERROR_MSG;
                        msg.obj = error_msg;
                        mHandler.sendMessage(msg);
                    }
                } else if (obj == AppApi.ERROR_TIMEOUT) {
                    mHandler.sendEmptyMessage(UPLOAD_TIMEOUT);
                } else if (obj == AppApi.ERROR_NETWORK_FAILED) {
                    mHandler.sendEmptyMessage(UPLOAD_TIMEOUT);
                }
                break;
            case POST_VIDEO_PROJECTION_JSON:
                setLog(slideSettingsMediaBeanResultList.size()+"","0",settingDialog.getLoopTime()*60+"","1");
                if (obj instanceof ResponseErrorMessage) {
                    ResponseErrorMessage message = (ResponseErrorMessage) obj;
                    int code = message.getCode();
                    String error_msg = message.getMessage();
                    Message msg = Message.obtain();
                    if (code == 4) {
                        msg.what = FORCE_MSG;
                        msg.obj = error_msg;
                        mHandler.sendMessage(msg);
                    } else {
                        msg.what = TOAST_ERROR_MSG;
                        msg.obj = error_msg;
                        mHandler.sendMessage(msg);
                    }
                } else if (obj == AppApi.ERROR_TIMEOUT) {
                    mHandler.sendEmptyMessage(UPLOAD_TIMEOUT);
                } else if (obj == AppApi.ERROR_NETWORK_FAILED) {
                    mHandler.sendEmptyMessage(UPLOAD_TIMEOUT);
                }
                break;
        }
    }


    /**
     * 不好啦，别人正在投屏，弹出是否确认抢投按钮
     *
     * @param msg
     */
    private void showConfirm(String msg) {
        if (this.isFinishing()) {
            LogUtils.d("savor:pro 当前页面已回收，不展示抢投提示");
            return;
        }
        if (dialog != null && dialog.isShowing()) {
            return;
        }

        if (settingDialog != null && settingDialog.isShowing()) {
            settingDialog.dismiss();
        }

        String content = "当前" + msg + "正在投屏,是否继续投屏?";
        dialog = new CommonDialog(this, content,
                new CommonDialog.OnConfirmListener() {
                    @Override
                    public void onConfirm() {
                        force = 1;
                        boolean looping = settingDialog.isLooping();
                        int loopTime = 0;
                        if (looping) {
                            loopTime = settingDialog.getLoopTime() * 60;
                        }
                        switch (slideType) {
                            case VIDEO:
                                postVideoSlideParamToServer(slideInfo.groupName, loopTime, force);
                                break;
                            case IMAGE:
                                postImageSlideParamToServer(slideInfo.groupName, loopTime, settingDialog.getSingleTime(), force);
                                break;
                        }

                    }
                }, new CommonDialog.OnCancelListener() {
            @Override
            public void onCancel() {
                dialog.cancel();
            }
        }, "继续投屏", true);
        dialog.show();
    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
        isStopUpload = true;
        closeProgressBarDialog();
        finish();
    }

    private void resetRoomList() {
        List<RoomInfo> roomList = mSession.getRoomList();
        if(roomList!=null) {
            for(RoomInfo info:roomList) {
                info.setSelected(false);
            }
        }
        roomListAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 清楚房间选择记录
        resetRoomList();
        OkHttpUtils.getInstance().getOkHttpClient().dispatcher().cancelAll();

        if(mCompressThread!=null) {
            mCompressThread.quit();
        }
        OkHttpUtils.getInstance().getOkHttpClient().dispatcher().cancelAll();
        boolean isRunning = UtilityAdapter.FFmpegIsRunning("");
        if(isRunning) {
            UtilityAdapter.FFmpegKill("");
        }
        isStopUpload = true;
        if (mProgressBarDialog != null) {
            mProgressBarDialog.dismiss();
            mProgressBarDialog = null;
        }
        if (dialog != null) {
            dialog.dismiss();
            dialog = null;
        }
    }

    @Override
    public void readyForQrcode() {
        if (mQrcodeDialog == null)
            mQrcodeDialog = new SavorDialog(this, "正在呼出验证码");
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
        Intent intent = new Intent(this, LinkTvActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivityForResult(intent, 0);
    }

    @Override
    public void initBindcodeResult() {
        ShowMessage.showToast(this, mSession.getSsid() + "连接成功，可以投屏");
        if (mOperationType == TYPE_PRO) {
            showSlideSettings();
        } else if (mOperationType == TYPE_CONFIRM) {
            performSlideSettingsConfirm();
        }
    }

    @Override
    public void onCancelBtnClick() {
        isStopUpload = true;
        String compressPath = mSession.getCompressPath(this);
        if(!TextUtils.isEmpty(crruntFileUrl)&&crruntFileUrl.contains(compressPath)) {
            File file = new File(crruntFileUrl);
            if(file.exists())
                file.delete();
        }
        mProgressBarDialog.loading("0%");
        UtilityAdapter.FFmpegKill(FFMPEG_FLAG);
        OkHttpUtils.getInstance().getOkHttpClient().dispatcher().cancelAll();
    }

    @Override
    public void onRoomItemClick(RoomInfo roomInfo) {
        currentRoom = roomInfo;

        erroCount = 0;

        // 判断是否连接同一个wifi并且同一网段
        String wifiName = WifiUtil.getWifiName(this);
        String box_name = roomInfo.getBox_name();
//        String box_ip = "";
        String box_ip = roomInfo.getBox_ip();
        String localIp = WifiUtil.getLocalIp(this);
        if(!TextUtils.isEmpty(wifiName)&&wifiName.equals(box_name)&&!TextUtils.isEmpty(localIp)&&WifiUtil.isInSameNetwork(localIp,box_ip)) {
            hideRoomList();
            showSlideSettings();
        }else {
            if(TextUtils.isEmpty(box_ip)) {
//                showToast("与盒子失去联系");
                SmallPlatInfoBySSDP smallPlatInfoBySSDP = mSession.getSmallPlatInfoBySSDP();
                SmallPlatformByGetIp smallPlatformByGetIp = mSession.getmSmallPlatInfoByIp();
                TvBoxSSDPInfo tvBoxSSDPInfo = mSession.getTvBoxSSDPInfo();
                int hotelid = mSession.getHotelid();
                String smallIp = "";
                // 1.通过getip请求包间列表
                if(smallPlatformByGetIp!=null&&!TextUtils.isEmpty(smallPlatformByGetIp.getLocalIp())) {
                    smallIp = smallPlatformByGetIp.getLocalIp();
                    String url = "http://"+smallIp+":8080";
                    AppApi.getHotelRoomList(SlideDetailActivity.this,url,String.valueOf(hotelid),SlideDetailActivity.this);
                }else {
                    erroCount++;
                }

                // 通过小平台ssdp获取小平台地址请求房间列表
                if(smallPlatInfoBySSDP!=null&&!TextUtils.isEmpty(smallPlatInfoBySSDP.getServerIp())) {
                    smallIp = smallPlatInfoBySSDP.getServerIp();
                    String url = "http://"+smallIp+":8080";
                    AppApi.getHotelRoomList(SlideDetailActivity.this,url,String.valueOf(hotelid),SlideDetailActivity.this);
                }else {
                    erroCount++;
                }

                if(tvBoxSSDPInfo!=null&&!TextUtils.isEmpty(tvBoxSSDPInfo.getServerIp())) {
                    smallIp = tvBoxSSDPInfo.getServerIp();
                    String url = "http://"+smallIp+":8080";
                    AppApi.getHotelRoomList(SlideDetailActivity.this,url,String.valueOf(hotelid),SlideDetailActivity.this);
                }else {
                    erroCount++;
                    if(erroCount == 3) {
                        showToast("包间电视连接失败，请检查是否开机");
                    }
                }

            }else {
                needLinkWifi = box_name;
                hideRoomList();
                showChangeWifiDialog(box_name);
            }
        }
    }

    public void reConnect(RoomInfo roomInfo) {
        currentRoom = roomInfo;

        erroCount = 0;

        // 判断是否连接同一个wifi并且同一网段
        String wifiName = WifiUtil.getWifiName(this);
        String box_name = roomInfo.getBox_name();
        String box_ip = roomInfo.getBox_ip();
        String localIp = WifiUtil.getLocalIp(this);
        if(!TextUtils.isEmpty(wifiName)&&wifiName.equals(box_name)&&!TextUtils.isEmpty(localIp)&&WifiUtil.isInSameNetwork(localIp,box_ip)) {
            hideRoomList();
            showSlideSettings();
        }else {
            if(!TextUtils.isEmpty(wifiName)&&wifiName.equals(box_name)&&TextUtils.isEmpty(box_ip)) {
//                showToast("与盒子失去联系");
                showToast("包间电视连接失败，请检查是否开机");
            }else {
                needLinkWifi = box_name;
                hideRoomList();
                showChangeWifiDialog(box_name);
            }
        }
    }

    @Override
    protected void checkWifiLinked() {
        if (!TextUtils.isEmpty(needLinkWifi) && needLinkWifi.equals(WifiUtil.getWifiName(this))&&isForground) {
            cancelWifiCheck();
            needLinkWifi = null;
            hideRoomList();
            showSlideSettings();
        }else {
            startCheckWifiLinkedTimer();
        }
    }


    private void setLog(final String count, final String result, final String length, final String type){
        final HotelBean hotel = mSession.getHotelBean();
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                AppApi.reportLog(mContext,
                        hotel.getHotel_id()+"",
                        "",hotel.getInvitation(),
                        hotel.getTel(),
                        currentRoom.getRoom_id(),
                        count,//文件个数
                        result,//投屏结果 1 成功；0失败
                        length,//总时长
                        type,//1视频 2照片 3特色菜 4宣传片 5欢迎词
                        "",
                        "",
                        SlideDetailActivity.this
                );
            }
        });

//        AppApi.reportLog(mContext,
//                hotel.getHotel_id()+"",
//                "",hotel.getInvitation(),
//                hotel.getTel(),
//                currentRoom.getRoom_id(),
//                "1",//文件个数
//                "0",//投屏结果 1 成功；0失败
//                "120",//总时长
//                "5",//1视频 2照片 3特色菜 4宣传片 5欢迎词
//                "",
//                "",
//                this
//        );
    }

}
