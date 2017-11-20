//package com.savor.resturant.activity;
//
//import android.content.Context;
//import android.content.Intent;
//import android.graphics.Bitmap;
//import android.os.Bundle;
//import android.os.Environment;
//import android.os.Handler;
//import android.os.Message;
//import android.support.v4.view.ViewPager;
//import android.support.v7.widget.Toolbar;
//import android.text.TextUtils;
//import android.view.MenuItem;
//import android.view.View;
//import android.widget.LinearLayout;
//import android.widget.RelativeLayout;
//import android.widget.TextView;
//
//import com.common.api.okhttp.OkHttpUtils;
//import com.common.api.utils.AppUtils;
//import com.common.api.utils.FileUtils;
//import com.common.api.utils.LogUtils;
//import com.common.api.utils.ShowMessage;
//import com.savor.resturant.R;
//import com.savor.resturant.adapter.ImageGalleryAdapter;
//import com.savor.resturant.bean.ImageProResonse;
//import com.savor.resturant.bean.ModelPic;
//import com.savor.resturant.bean.RotateRequest;
//import com.savor.resturant.bean.RotateProResponse;
//import com.savor.resturant.bean.TvBoxInfo;
//import com.savor.resturant.core.ApiRequestListener;
//import com.savor.resturant.core.AppApi;
//import com.savor.resturant.core.ResponseErrorMessage;
//import com.savor.resturant.core.Session;
//import com.savor.resturant.interfaces.IHotspotSenseView;
//import com.savor.resturant.presenter.BindTvPresenter;
//import com.savor.resturant.utils.ActivitiesManager;
//import com.savor.resturant.utils.CompressImage;
//import com.savor.resturant.utils.ProjectionManager;
//import com.savor.resturant.utils.RecordUtils;
//import com.savor.resturant.utils.WifiUtil;
//import com.savor.resturant.widget.CommonDialog;
//import com.savor.resturant.widget.HotsDialog;
//import com.savor.resturant.widget.SavorDialog;
//
//import java.io.File;
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.LinkedList;
//import java.util.List;
//
//import static com.savor.resturant.activity.LinkTvActivity.EXRA_TV_BOX;
//import static com.savor.resturant.activity.LinkTvActivity.EXTRA_TV_INFO;
//
//public class ImageGalleryActivity extends BaseActivity implements View.OnClickListener, ApiRequestListener, IHotspotSenseView, IBindTvView {
//
//    // region Constants
//    public static final String KEY_IMAGES = "KEY_IMAGES";
//    private static final int EDIT_PIC = 1000;
//    public static final String KEY_PIC_LIST = "key_pic_list";
//    private static final String KEY_PIC_POSITION = "key_pic_position";
//    private static final int ROTATE = 1;
//    private static final int SHOW = 4;
//    private static final int DISPLAY = 3;
//    /**保存合成图*/
//    private static final int SAVE_COMPOUND_FINISH = 5;
//    private static final int ERROR_MSG = 6;
//    private static final int SCREEN_SUCESS = 7;
//    private static final int CHECK_WIFI_LINKED = 8;
//    private static final int CANCEL_CHECK_WIFI = 9;
//    private static final int FORCE_MSG = 11;
//    /**是否正在投屏*/
//    private static final String KEY_IS_PROJECTING = "key_is_projecting";
//    /**图片操作时需要的projectinId*/
//    private static final String KEY_PROJECT_ID = "key_project_id";
//    /**
//     * 首先进来投屏先显示小图，在显示大图
//     */
//    private int small=0;
//    /**
//     * 遇到抢投传1，代表确认，默认传0
//     */
//    private int force=0;
//    private CommonDialog dialog;
//    /**是否第一次刚进入当前页面*/
//    private boolean isfirst = true;
//    private LinkedList<ModelPic> modePool = new LinkedList<>();
//    private Handler mHandler = new Handler() {
//        public void handleMessage(Message msg) {
//            switch (msg.what) {
//                case CANCEL_CHECK_WIFI:
//                    mSession.setTvBoxUrl(null);
//                    mHandler.removeMessages(CHECK_WIFI_LINKED);
//                    break;
//                case CHECK_WIFI_LINKED:
//                    if(!mSession.isBindTv()) {
//                        TvBoxInfo tvboxInfo = mSession.getTvboxInfo();
//                        if(tvboxInfo!=null) {
//                            checkWifiLinked(tvboxInfo);
//                        }
//                    }else {
//                        mHandler.removeMessages(CANCEL_CHECK_WIFI);
//                    }
//                    break;
//                case SCREEN_SUCESS:
//                    //tv_exit.setVisibility(View.GONE);
//                    tv_exit.setText("退出投屏");
//                   // iv_exit.setVisibility(View.VISIBLE);
//                    if (small==1){
//                        String imageId = (String) msg.obj;
//                        int currentItem = viewPager.getCurrentItem();
////                        ModelPic modelPic = photoBeanList.get(currentItem);
////                        String currentImageId = modelPic.getImageId();
//                        // 小图上传成功后判断如果imageid为当前页的imageid时才上传大图
//                        if(imageId.equals(currentImageId)) {
//                            small = 0;
//                            showImageToScreen();
//                        }
//                        return;
//                    }
//                    break;
//                case ERROR_MSG:
//                    String erromsg = (String) msg.obj;
//                    showToast(erromsg);
//                    stopPro();
//                    break;
//                case DISPLAY:
//                    //创建一个可重用固定线程数的线程池
//                    LogUtils.d("savor:imagegallery 图片投屏");
//                    showImageToScreen();
////                    showImageToScreen(true);
//                    break;
//                case SAVE_COMPOUND_FINISH:
//                    if(mSavePicDialog!=null)
//                        mSavePicDialog.dismiss();
//                    if(isProjecting) {
//                        int currentItem = viewPager.getCurrentItem();
//                        ActivitiesManager.getInstance().setPicProjectionActivity(ImageGalleryActivity.class,photoBeanList,currentItem);
//                    }else {
//                        ActivitiesManager.getInstance().resetProjection();
//                    }
//                    setResult(HotspotMainActivity.FROM_APP_BACK);
//                    finish();
//                    break;
//                case SHOW:
//                    final ModelPic pictureInfo = (ModelPic) msg.obj;
//                    final String imageId = pictureInfo.getImageId();
//                    LogUtils.d("savor:imagegallery imageid"+pictureInfo.getImageId());
//                    AppApi.updateScreenProjectionFile(ImageGalleryActivity.this, mSession.getTVBoxUrl(), pictureInfo, pictureInfo.getCompressPath(), small,force, new ApiRequestListener() {
//                        @Override
//                        public void onSuccess(AppApi.Action method, Object obj) {
//                            dismissScreenDialog();
//                            switch (method) {
//                                case POST_IMAGE_PROJECTION_JSON:
//                                    isProjecting = true;
//                                    if(obj instanceof ImageProResonse) {
//                                        ImageProResonse proResonseInfo = (ImageProResonse) obj;
//                                        projectId = proResonseInfo.getProjectId();
//                                        ProjectionManager.getInstance().setmProjectId(projectId);
//                                    }
//                                    Message message = Message.obtain();
//                                    message.what = SCREEN_SUCESS;
//                                    message.obj = imageId;
//                                    if(mHandler!=null)
//                                        mHandler.sendMessage(message);
//                                    break;
//                            }
//                        }
//
//                        @Override
//                        public void onError(AppApi.Action method, Object obj) {
//                            dismissScreenDialog();
//                            if(obj instanceof ResponseErrorMessage) {
//                                ResponseErrorMessage message = (ResponseErrorMessage) obj;
//                                int code = message.getCode();
//                                Message msg = Message.obtain();
//                                if (code==4){
//                                    pictureInfo.setMobileUser(message.getMessage());
//                                    msg.what = FORCE_MSG;
//                                    msg.obj = pictureInfo;
//                                    mHandler.sendMessage(msg);
//                                }else{
//                                    msg.what = ERROR_MSG;
//                                    msg.obj = message.getMessage();
//                                    mHandler.sendMessage(msg);
//                                }
//
//                            }else {
//                                Message msg = Message.obtain();
//                                msg.what = ERROR_MSG;
//                                msg.obj = "投屏失败";
//                                mHandler.sendMessage(msg);
//                            }
//                        }
//
//                        @Override
//                        public void onNetworkFailed(AppApi.Action method) {
//
//                        }
//                    });
//                    break;
//                case FORCE_MSG:
//                    ModelPic modelPic = (ModelPic)msg.obj;
//                    showConfirm(modelPic);
//                    break;
//            }
//        }
//
//    };
//    /**
//     * 不好啦，别人正在投屏，弹出是否确认抢投按钮
//     * @param pictureInfo
//     */
//    private void showConfirm(final ModelPic pictureInfo){
//        String content = "当前"+pictureInfo.getMobileUser()+"正在投屏,是否继续投屏?";
//        dialog = new CommonDialog(this, content,
//                new CommonDialog.OnConfirmListener() {
//                    @Override
//                    public void onConfirm() {
//                        HashMap<String,String> params = new HashMap<>();
//                        params.put(getString(R.string.to_screen_competition_hint),"ensure");
//                        params.put("type","pic");
//                        RecordUtils.onEvent(ImageGalleryActivity.this,getString(R.string.to_screen_competition_hint),params);
//                        force = 1;
//                        Message message = Message.obtain();
//                        message.what = SHOW;
//                        message.obj = pictureInfo;
//                        mHandler.removeMessages(SHOW);
//                        mHandler.sendMessage(message);
//                        dialog.cancel();
//                    }
//                }, new CommonDialog.OnCancelListener() {
//            @Override
//            public void onCancel() {
//                HashMap<String,String> params = new HashMap<>();
//                params.put(getString(R.string.to_screen_competition_hint),"cancel");
//                params.put("type","pic");
//                RecordUtils.onEvent(ImageGalleryActivity.this,getString(R.string.to_screen_competition_hint),params);
//                dialog.cancel();
//            }
//        },"继续投屏",true);
//        dialog.show();
//    }
//    // endregion
//
//    // region Views
//    private Toolbar toolbar;
//    private ViewPager viewPager;
//    // endregion
//
//    // region Member Variables
//    private List<String> images;
//    private int position;
//    // endregion
//    List<ModelPic> photoBeanList = new ArrayList<>();
//    private int rotateValue=0;
//    // region Listeners
//    private final ViewPager.OnPageChangeListener viewPagerOnPageChangeListener = new ViewPager.OnPageChangeListener() {
//        @Override
//        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
//
//        }
//
//        @Override
//        public void onPageSelected(int position) {
//            RecordUtils.onEvent(mContext,getString(R.string.picture_to_screen_switch));
//            ImageGalleryActivity.this.position = position;
//            small = 1;
//            force = 0;
//            OkHttpUtils.getInstance().getOkHttpClient().dispatcher().cancelAll();
//            if (viewPager != null) {
//                viewPager.setCurrentItem(position);
//                currentImageId = System.currentTimeMillis()+"";
//                photoBeanList.get(position).setImageId(currentImageId);
//                if (isProjecting) {
////                        if(!isfirst) {
//                            mHandler.removeMessages(DISPLAY);
//                            mHandler.removeMessages(SHOW);
//                            mHandler.sendEmptyMessageDelayed(DISPLAY, 100);
////                        }else {
////                            isfirst = false;
////                        }
//
//                }
//            }
//        }
//
//        @Override
//        public void onPageScrollStateChanged(int state) {
//
//        }
//    };
//
//
//    private ImageGalleryAdapter fullScreenImageGalleryAdapter;
//    private LinearLayout mBackLayout;
//    /**是否为投屏状态*/
//    private boolean isProjecting;
//    private Session mSession;
//    private RelativeLayout mExitLayout;
//    private SavorDialog mToScreenDialog;
//   // private ImageView iv_exit;
//    private TextView tv_exit;
//    private BindTvPresenter mBindPresenter;
//    private SavorDialog mSavePicDialog;
//    private SavorDialog mQrcodeDialog;
//    private HotsDialog hotsDialog;
//    private HotsDialog mChangeWifiDiallog;
//    private String currentImageId;
//    private ArrayList<String> picList;
//    private String projectId;
//    private Context mContext;
//    // endregion
//
//    // region Lifecycle Methods
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        mSession = Session.get(this);
//        setContentView(R.layout.activity_local_image_gallery);
//        mContext = this;
//        handleIntent();
//        initPresenter();
//        bindViews();
//        setViews();
//        setListeners();
//        setUpViewPager();
//        if(isProjecting) {
//            ProjectionManager.getInstance().setImageProjection(ImageGalleryActivity.class,position,photoBeanList,projectId);
//        }
//        registerStopReceiver();
//    }
//
//    private void registerStopReceiver() {
//
//    }
//
//    private void handleImageShow(){
//        mHandler.removeMessages(DISPLAY);
//        mHandler.removeMessages(SHOW);
//        mHandler.sendEmptyMessageDelayed(DISPLAY, 100);
//    }
//
//
//    private void initPresenter() {
//        mBindPresenter = new BindTvPresenter(this,this,this,this);
//    }
//
//    @Override
//    public void getViews() {
//
//    }
//
//    @Override
//    public void setViews() {
//        if(isProjecting) {
//            //iv_exit.setVisibility(View.VISIBLE);
//           // tv_exit.setVisibility(View.GONE);
//            tv_exit.setText("退出投屏");
//        }else {
//          //  tv_exit.setVisibility(View.VISIBLE);
//            tv_exit.setText("投屏");
//           // iv_exit.setVisibility(View.GONE);
//        }
//
//        if(!mSession.isBindTv()) {
//            if(hotsDialog==null) {
//                hotsDialog = new HotsDialog(this)
//                        .builder()
//                        .setTitle("提示")
//                        .setMsg(getString(R.string.click_link_tv))
//                        .setPositiveButton(getString(R.string.link_tv), new View.OnClickListener() {
//                            @Override
//                            public void onClick(View v) {
//                                mBindPresenter.bindTv();
//                            }
//                        })
//                        .setNegativeButton("取消", new View.OnClickListener() {
//                            @Override
//                            public void onClick(View v) {
//                                hotsDialog.dismiss();
//                            }
//                        });
//            }
//            hotsDialog.show();
//        }
//    }
//
//    public void setListeners() {
//        mBackLayout.setOnClickListener(this);
//        mExitLayout.setOnClickListener(this);
//    }
//
////    @Override
////    public void showToast(String msg) {
////        ShowMessage.showToast(this,msg);
////    }
//
//    @Override
//    public void showLoadingLayout() {
//
//    }
//
//    @Override
//    public void hideLoadingLayout() {
//
//    }
//
//    private void handleIntent() {
//        picList = ProjectionManager.getInstance().getImgList();
//        position = getIntent().getIntExtra(KEY_PIC_POSITION,0);
//        isProjecting = getIntent().getBooleanExtra(KEY_IS_PROJECTING, false);
//        projectId = getIntent().getStringExtra(KEY_PROJECT_ID);
//        List<ModelPic> modelPics = ProjectionManager.getInstance().getmPicList();
//        if(modelPics!=null&&modelPics.size()>0) {
//            photoBeanList = modelPics;
//        }else {
//            photoBeanList = ProjectionManager.getInstance().getmPicList();
//        }
//    }
//
//    public static void startImageGallery(Context context,ArrayList<String> piclist, int position,boolean isProjecting,String projectionId) {
//        Intent intent = new Intent(context,ImageGalleryActivity.class);
////        intent.putStringArrayListExtra(KEY_PIC_LIST, piclist);
//        intent.putExtra(KEY_PIC_POSITION,position);
//        intent.putExtra(KEY_IS_PROJECTING,isProjecting);
//        intent.putExtra(KEY_PROJECT_ID,projectionId);
//        context.startActivity(intent);
//    }
//
//
//    public void edit(View view) {
//
//        RecordUtils.onEvent(mContext,getString(R.string.picture_to_screen_add_text));
//        Intent intent = new Intent(this,ImageEditActivity.class);
//        int currentItem = viewPager.getCurrentItem();
//        ModelPic bean = photoBeanList.get(currentItem);
//        intent.putExtra("pic",bean);
//        String path = bean.getAssetpath();
//        intent.putExtra("path",path);
//
//        intent.putExtra("rotate",bean.getRotatevalue());
//        startActivityForResult(intent,EDIT_PIC);
//    }
//
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (resultCode == ImageEditActivity.COMPLETE) {
//            String compressPath = mSession.getCompressPath();
//            int currentItem = viewPager.getCurrentItem();
//            String filePath = compressPath+photoBeanList.get(currentItem).getAssetname()+"coumpund.png";
//            File file = new File(filePath);
//            if(file.exists()) {
//                fullScreenImageGalleryAdapter.setCompoundPath(filePath,viewPager.getCurrentItem());
//                photoBeanList.get(currentItem).setComRotateValue(0);
//                photoBeanList.get(currentItem).setCompoundPath(filePath);
//                fullScreenImageGalleryAdapter.notifyDataSetChanged();
//                if(data!=null) {
//                    small =1;
//                    currentImageId = System.currentTimeMillis()+"";
//                    ModelPic photoBean = (ModelPic) data.getSerializableExtra("pic");
//                    ModelPic photoB = photoBeanList.get(currentItem);
//                    photoB.setPrimaryText(photoBean.getPrimaryText());
//                    photoB.setDesText(photoBean.getDesText());
//                    photoB.setDateText(photoBean.getDateText());
//                    photoB.setImageId(currentImageId);
//                }
//                //  显示合成图
//                mHandler.removeMessages(DISPLAY);
//                mHandler.sendEmptyMessageDelayed(DISPLAY,100);
//            }
//        }else if (resultCode == QRCodeScanActivity.SCAN_QR) {
//            if(data!=null) {
//                String scanResult = data.getStringExtra("scan_result");
//                mBindPresenter.handleQrcodeResult(scanResult);
//                LogUtils.d("扫描结果：" + scanResult);
//            }
////            showToast(scanResult);
//        }else if(resultCode == EXTRA_TV_INFO){
//            if(data!=null) {
//                TvBoxInfo boxInfo = (TvBoxInfo) data.getSerializableExtra(EXRA_TV_BOX);
//                mBindPresenter.handleCallCodeResult(boxInfo);
//            }
//        }
//    }
//
//    @Override
//    protected void onRestart() {
//        super.onRestart();
//        // 判断如果为绑定并且当前ssid与缓存机顶盒ssid相同提示绑定成功
//        if(!mSession.isBindTv()) {
//            TvBoxInfo tvBoxInfo = mSession.getTvboxInfo();
//            if(tvBoxInfo!=null) {
//                checkWifiLinked(tvBoxInfo);
//            }
//        }
//    }
//
//    /**
//     * 检查是否在同一wifi，如果三分钟内连接到同一wifi提示连接成功
//     */
//    private void checkWifiLinked(TvBoxInfo tvBoxInfo) {
//        String ssid = tvBoxInfo.getSsid();
//        String localSSid = WifiUtil.getWifiName(this);
//        if(!TextUtils.isEmpty(ssid)) {
//            if(ssid.endsWith(localSSid)) {
//                mSession.setWifiSsid(ssid);
//                mSession.setTvBoxUrl(tvBoxInfo);
//                initQrcodeResult();
//                mHandler.removeMessages(CANCEL_CHECK_WIFI);
//            }
//        }else {
//            // 每隔一秒检测是否已连接同一wifi
//            startCheckWifiLinkedTimer();
//        }
//    }
//    /**开启检查是否是同一wifi定时器每隔一秒检查一次*/
//    private void startCheckWifiLinkedTimer() {
//        mHandler.removeMessages(CHECK_WIFI_LINKED);
//        mHandler.sendEmptyMessageDelayed(CHECK_WIFI_LINKED,1000);
//    }
//
//    @Override
//    protected void onStop() {
//        super.onStop();
//    }
//
//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        removeListeners();
//        if(mChangeWifiDiallog!=null) {
//            mChangeWifiDiallog.dismiss();
//            mChangeWifiDiallog = null;
//        }
//        if(mHandler!=null) {
//            mHandler.removeCallbacksAndMessages(null);
//            mHandler = null;
//        }
//    }
//    // endregion
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        if (item.getItemId() == android.R.id.home) {
//            onBackPressed();
//            return true;
//        } else {
//            return super.onOptionsItemSelected(item);
//        }
//    }
//
//
//    // region Helper Methods
//    private void bindViews() {
////        isProjecting = mSession.isBindTv();
////        iv_exit= (ImageView) findViewById(R.id.iv_exit);
//        tv_exit = (TextView) findViewById(R.id.tv_exit);
//        viewPager = (ViewPager) findViewById(R.id.vp);
//        toolbar = (Toolbar) findViewById(R.id.toolbar);
//        mBackLayout = (LinearLayout) findViewById(R.id.back);
//        mExitLayout = (RelativeLayout) findViewById(R.id.tv_exit_projection);
//
//        getViews();
//    }
//
//    private void setUpViewPager() {
//        fullScreenImageGalleryAdapter = new ImageGalleryAdapter(this,photoBeanList);
//        viewPager.setAdapter(fullScreenImageGalleryAdapter);
//        viewPager.addOnPageChangeListener(viewPagerOnPageChangeListener);
//
////        if(position!=0)
//            viewPager.setCurrentItem(position);
//    }
//
//
//    private void removeListeners() {
//        viewPager.removeOnPageChangeListener(viewPagerOnPageChangeListener);
//    }
//
//    public void rotate(View view) {
//        RecordUtils.onEvent(mContext,getString(R.string.picture_to_screen_rotating));
//        if(isProjecting) {
//            RotateRequest rotateRequest = new RotateRequest();
//            rotateRequest.setProjectId(projectId);
//            AppApi.notifyTvBoxRotate(this,mSession.getTVBoxUrl(), rotateRequest,this);
//        }else {
//            int currentItem = viewPager.getCurrentItem();
//            final ModelPic bean = photoBeanList.get(currentItem);
//            int rotateValue = bean.getRotatevalue();
//            String filePath = fullScreenImageGalleryAdapter.getmFilePath();
//            bean.setRotatevalue(rotateValue+90);
//            if(!TextUtils.isEmpty(filePath)) {
//                int rotateV = bean.getComRotateValue();
//                bean.setComRotateValue(rotateV+90);
//            }
//            fullScreenImageGalleryAdapter.notifyDataSetChanged();
//        }
//
//    }
//
//    @Override
//    public void onClick(View v) {
//        switch (v.getId()) {
//            case R.id.back:
//                RecordUtils.onEvent(mContext,getString(R.string.picture_to_screen_back_list));
//                onBackPressed();
//                break;
//            case R.id.tv_exit_projection:
//                exitProjection();
//                break;
//        }
//    }
//
//    private void exitProjection() {
//        if(isProjecting) {
//            mHandler.removeMessages(SHOW);
//            mHandler.removeMessages(DISPLAY);
//           // tv_exit.setVisibility(View.VISIBLE);
//            //iv_exit.setVisibility(View.GONE);
//            stopProjection();
//        }else {
//            RecordUtils.onEvent(mContext,getString(R.string.picture_to_screen_play));
//            if(!AppUtils.isWifiNetwork(this)) {
//                new CommonDialog(this,"请前往手机设置，连接至电视同一WiFi下").show();
//            }else {
//                if(mSession.isBindTv()) {
//                    showToScreenDialog("正在投屏");
//                    small = 1;
//                    force = 0;
//                    isProjecting = true;
//                    int currentItem = viewPager.getCurrentItem();
//                    if(photoBeanList!=null) {
//                        ModelPic modelPic = photoBeanList.get(currentItem);
//                        if(modelPic!=null) {
//                            currentImageId = System.currentTimeMillis()+"";
//                            modelPic.setImageId(currentImageId);
//                            mHandler.removeMessages(DISPLAY);
//                            mHandler.sendEmptyMessageDelayed(DISPLAY,100);
//                        }
//                        if(isProjecting) {
//                            ProjectionManager.getInstance().setImageProjection(ImageGalleryActivity.class,position,photoBeanList,projectId);
//                        }
//                    }
//                }else {
//                    mBindPresenter.bindTv();
//                }
//            }
//
//        }
//    }
//
//    private void stopProjection() {
//        showToScreenDialog("退出投屏...");
//
//        RecordUtils.onEvent(mContext,getString(R.string.picture_to_screen_exit_screen));
//        AppApi.notifyTvBoxStop(this,mSession.getTVBoxUrl(),projectId,this);
//    }
//
//    private void showToScreenDialog(String content) {
//        mToScreenDialog = new SavorDialog(this,content);
//        mToScreenDialog.show();
//    }
//
//    @Override
//    public void onBackPressed() {
//        // 如果当前展示的是合成图将合成图保存到热点文件夹，判断是否是投屏状态，如果是保存投屏状态
//        String filePath = fullScreenImageGalleryAdapter.getmFilePath();
//        if(!TextUtils.isEmpty(filePath)) {
//            final File file = new File(filePath);
//            String absolutePath = Environment.getExternalStorageDirectory().getAbsolutePath();
//            absolutePath += absolutePath.endsWith(File.separator) ? "" : File.separator;
//            final String fileDir = absolutePath+"热点儿";
//            if(!file.exists())
//                file.mkdirs();
//
//            // 保存合成图
//            new Thread(){
//                @Override
//                public void run() {
//                    FileUtils.copyFile(file,fileDir,System.currentTimeMillis()+".png",null);
//                    mHandler.sendEmptyMessage(SAVE_COMPOUND_FINISH);
//                }
//            }.start();
//        }
//
//        // 判断当前是否是投屏状态,如果是保存
//        if(isProjecting) {
//            ProjectionManager.getInstance().setImageProjection(ImageGalleryActivity.class,position,photoBeanList,projectId);
//        }else {
//            ProjectionManager.getInstance().resetProjection();
//        }
//        super.onBackPressed();
//    }
//
//    @Override
//    public void onSuccess(AppApi.Action method, Object obj) {
//        dismissScreenDialog();
//        switch (method) {
//            case POST_NOTIFY_TVBOX_STOP_JSON:
//                stopPro();
//                break;
//            case POST_PHOTO_ROTATE_JSON:
//                int currentItem = viewPager.getCurrentItem();
//                final ModelPic bean = photoBeanList.get(currentItem);
//                int rotateValue = bean.getRotatevalue();
//                String filePath = fullScreenImageGalleryAdapter.getmFilePath();
//                bean.setRotatevalue(rotateValue+90);
//                if(!TextUtils.isEmpty(filePath)) {
//                    int rotateV = bean.getComRotateValue();
//                    bean.setComRotateValue(rotateV+90);
//                }
//                fullScreenImageGalleryAdapter.notifyDataSetChanged();
//                break;
//        }
//    }
//
//    public void stopPro() {
//
//        mHandler.post(new Runnable() {
//            @Override
//            public void run() {
//                isProjecting = false;
//                tv_exit.setText("投屏");
//            }
//        });
//    }
//
//    private void showImageToScreen() {
//        new Thread(){
//            @Override
//            public void run() {
//                displayPic();
//            }
//        }.start();
//    }
//
//    private void displayPic() {
//        long startTime = System.currentTimeMillis();
//        LogUtils.d("savor:图片处理开始时间戳 "+startTime);
//        int currentItem = viewPager.getCurrentItem();
//        ModelPic modelPic = photoBeanList.get(currentItem);
//        String filePath = photoBeanList.get(currentItem).getCompoundPath();
//        int positin = fullScreenImageGalleryAdapter.getmCompoundPositin();
//        if(!TextUtils.isEmpty(filePath)) {
//            ModelPic compoundPic = new ModelPic();
//            compoundPic.setAction(modelPic.getAction());
//            compoundPic.setAssetname(modelPic.getAssetname());
//            compoundPic.setAssetcover(modelPic.getAssetcover());
//            compoundPic.setAssetpath(filePath);
//            compoundPic.setImageId(modelPic.getImageId());
//            modelPic = compoundPic;
//        }
//        try {
//            Bitmap bitmap = null;
//            String copyPath  = "";
////            int small = modelPic.getSmall();
//            if (small==1){
//                copyPath = CompressImage.compressAndSaveBitmap(ImageGalleryActivity.this, modelPic.getAssetpath(),modelPic.getAssetname(),true);
//            }else{
//                copyPath =  CompressImage.compressAndSaveBitmap(ImageGalleryActivity.this, modelPic.getAssetpath(),modelPic.getAssetname(),false);
//            }
//            long endTime = System.currentTimeMillis();
//            LogUtils.d("savor:图片处理结束时间戳 "+System.currentTimeMillis()+";共用时 "+(endTime-startTime)/1000+"秒");
//            modelPic.setCompressPath(copyPath);
//            Message message = Message.obtain();
//            message.obj = modelPic;
//            message.what = SHOW;
////            mHandler.removeMessages(SHOW);
//            mHandler.sendMessage(message);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    private void dismissScreenDialog() {
//        if(mToScreenDialog != null) {
//            mToScreenDialog.dismiss();
//        }
//    }
//
//    @Override
//    public void onError(AppApi.Action method, Object obj) {
//        dismissScreenDialog();
//        Message msg = Message.obtain();
//        msg.what = ERROR_MSG;
//        if(obj instanceof ResponseErrorMessage) {
//            ResponseErrorMessage message = (ResponseErrorMessage) obj;
//            int code = message.getCode();
//            msg.obj = message.getMessage();
//            mHandler.sendMessage(msg);
//        }else {
//            msg.obj = "操作失败";
//            mHandler.sendMessage(msg);
//        }
//    }
//
//    @Override
//    public void onNetworkFailed(AppApi.Action method) {
//
//    }
//
//    @Override
//    public void initSenseState() {
//
//    }
//
//    @Override
//    public void showBindButton(boolean isBind) {
//
//    }
//
//    @Override
//    public void hideBindButton() {
//
//    }
//
//    @Override
//    public void refreshData() {
//
//    }
//
//    @Override
//    public void checkSense() {
//
//    }
//
//    @Override
//    public void showChangeWifiDialog() {
//        mChangeWifiDiallog = new HotsDialog(this)
//                .builder()
//                .setTitle("连接失败")
//                .setMsg(getString(R.string.tv_bind_wifi)+""+ (TextUtils.isEmpty(mSession.getSsid())?"":mSession.getSsid()))
//                .setCancelable(false)
//                .setPositiveButton("去设置", new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//                        Intent intent = new Intent();
//                        intent.setAction("android.net.wifi.PICK_WIFI_NETWORK");
//                        startActivity(intent);
//                    }
//                })
//                .setNegativeButton("我知道了", new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//                    }
//                });
//        mChangeWifiDiallog.show();
//    }
//
//    @Override
//    public void readyForQrcode() {
//        if(mQrcodeDialog==null)
//            mQrcodeDialog = new SavorDialog(this);
//        mQrcodeDialog.show();
//    }
//
//    @Override
//    public void closeQrcodeDialog() {
//        if (mQrcodeDialog != null) {
//            mQrcodeDialog.dismiss();
//            mQrcodeDialog = null;
//        }
//    }
//
//    @Override
//    public void initQrcodeResult() {
//        ShowMessage.showToast(this,"连接电视成功");
//        isProjecting = true;
//        ProjectionManager.getInstance().setImageProjection(ImageGalleryActivity.class,position,photoBeanList,projectId);
//        isfirst = false;
//        small = 1;
//        currentImageId = System.currentTimeMillis()+"";
//        photoBeanList.get(position).setImageId(currentImageId);
//        mHandler.removeMessages(SHOW);
//        mHandler.removeMessages(DISPLAY);
//        mHandler.sendEmptyMessage(DISPLAY);
//    }
//
//    @Override
//    public void startQrcode() {
//        Intent intent = new Intent(this,LinkTvActivity.class);
//        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
//        startActivityForResult(intent,0);
//    }
//
//    @Override
//    public void showUnLinkDialog() {
//
//    }
//
//    @Override
//    public void rotate(RotateProResponse rotateResponse) {
//
//    }
//
//    @Override
//    public void reCheckPlatform() {
//
//    }
//
//}
