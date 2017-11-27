//package com.savor.resturant.activity;
//
//import android.content.ComponentName;
//import android.content.Context;
//import android.content.Intent;
//import android.content.ServiceConnection;
//import android.graphics.Bitmap;
//import android.media.Ringtone;
//import android.media.RingtoneManager;
//import android.net.Uri;
//import android.os.Bundle;
//import android.os.Handler;
//import android.os.IBinder;
//import android.os.Message;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.View.OnClickListener;
//import android.view.ViewGroup;
//import android.widget.AdapterView;
//import android.widget.AdapterView.OnItemClickListener;
//import android.widget.BaseAdapter;
//import android.widget.Button;
//import android.widget.CheckBox;
//import android.widget.GridView;
//import android.widget.ImageView;
//import android.widget.ProgressBar;
//
//import com.bumptech.glide.Glide;
//import com.bumptech.glide.load.engine.DiskCacheStrategy;
//import com.common.api.utils.AppUtils;
//import com.common.api.utils.LogUtils;
//import com.google.gson.Gson;
//import com.google.gson.reflect.TypeToken;
//import com.savor.resturant.R;
//import com.savor.resturant.SavorApplication;
//import com.savor.resturant.bean.BaseProReqeust;
//import com.savor.resturant.bean.ImageProResonse;
//import com.savor.resturant.bean.ModelPic;
//import com.savor.resturant.bean.MediaInfo;
//import com.savor.resturant.core.AppApi;
//import com.savor.resturant.core.ResponseErrorMessage;
//import com.savor.resturant.service.ProjectionService;
//import com.savor.resturant.utils.CompressImage;
//import com.savor.resturant.utils.NetWorkUtil;
//import com.savor.resturant.utils.PhotoUtil;
//import com.savor.resturant.utils.ProjectionManager;
//import com.savor.resturant.utils.RecordUtils;
//import com.savor.resturant.widget.CommonDialog;
//import com.savor.resturant.widget.SavorDialog;
//
//import java.io.BufferedOutputStream;
//import java.io.File;
//import java.io.FileNotFoundException;
//import java.io.FileOutputStream;
//import java.io.IOException;
//import java.lang.reflect.Type;
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.LinkedList;
//import java.util.List;
//
///**
// * 照片分享
// *
// * @author savor
// */
//public class PhotoShareActivity extends BaseActivity implements OnItemClickListener, OnClickListener {
//    private static final long PIC_SIZE_MAX = 200;
//    private static final int SHOW = 0x1;
//    private static final int ERROR_MSG = 0x2;
//    private static final int FORCE_MSG = 0x4;
//    private GridView mPhotos;
//    private CheckBox mCheckBox;
//    private View mBottom;
//    private View mBack;
//    private View mShow;
//    private Button mChoose;
//    //转换适配器需要数据
//    private ArrayList<ModelPic> mDatas = new ArrayList<ModelPic>();
//    //选择多张照片幻灯片播放
//    private LinkedList<ModelPic> mCheckPicList = new LinkedList<ModelPic>();
//    private PhotoAdapter mAdapter;
//    private boolean mIsChoose = false;
//    private boolean mCheckAll = false;
//    private boolean isSlides = false;
//    /**图片路径*/
//    private ArrayList<String> childList;
//    /**
//     * 首先进来投屏先显示小图，在显示大图
//     */
//    private int small=0;
//    /**
//     * 当投屏时遇到大屏正在投屏中，抢投传1，代表确认抢投，默认传0
//     */
//    private int force=0;
//    private CommonDialog dialog;
//    /**
//     * 当前投屏图片
//     */
//    private MediaInfo pictureInfo=null;
//
//
//
//    private Handler mHandler = new Handler() {
//        public void handleMessage(android.os.Message msg) {
//            switch (msg.what) {
//                case PhotoUtil.INIT_SUCCESS:
//                    progressBar.setVisibility(View.GONE);
//                    initDatas();
//                    break;
//                case SHOW:
//                    MediaInfo pictureInfo = (MediaInfo) msg.obj;
//                    BaseProReqeust baseProReqeust = getBasePrepareInfo(pictureInfo);
//                    AppApi.updateScreenProjectionFile(mContext,mSession.getTVBoxUrl(), baseProReqeust,pictureInfo.getCompressPath(),small,force,PhotoShareActivity.this);
//                    break;
//                case ERROR_MSG:
//                    String message = (String) msg.obj;
//                    showToast(message);
//                    break;
//                case FORCE_MSG:
//                    message = (String)msg.obj;
//                    showConfirm(message);
//                    break;
//            }
//        }
//
//        ;
//    };
//    private SavorDialog mRequestScreenDialog;
//    private int mCurrentPosition;
//    private String mProjectId;
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
//    private ProgressBar progressBar;
//
//    @Override
//    protected void onCreate(Bundle arg0) {
//        super.onCreate(arg0);
//        setContentView(R.layout.activity_photo_share);
//        mPhotos = (GridView) findViewById(R.id.photos);
//        mBack = findViewById(R.id.back);
//        mChoose = (Button) findViewById(R.id.choose);
//        mBottom = findViewById(R.id.bottom);
//        mCheckBox = (CheckBox) findViewById(R.id.checkall);
//        progressBar = (ProgressBar) findViewById(R.id.progressbar);
//        mShow = findViewById(R.id.show);
//        mBack.setOnClickListener(this);
//        mChoose.setOnClickListener(this);
//        mCheckBox.setOnClickListener(this);
//        mShow.setOnClickListener(this);
////        childList = getIntent().getStringArrayListExtra("data");
//        childList = ProjectionManager.getInstance().getImgList();
//        if (mDatas.isEmpty() || mDatas.size() <= 0) {
//            progressBar.setVisibility(View.VISIBLE);
//            new Thread(){
//                @Override
//                public void run() {
//                    PhotoUtil.getMediaGroupPhotoList(PhotoShareActivity.this, mHandler, mDatas, childList);
//                }
//            }.start();
//
//        } else {
//            initDatas();
//        }
//        // 开始查询进度
//        Intent intent = new Intent(this, ProjectionService.class);
//        intent.putExtra(ProjectionService.EXTRA_TYPE,ProjectionService.TYPE_VOD_NOMARL);
//        bindService(intent,mServiceConn, Context.BIND_AUTO_CREATE);
//    }
//
//    @Override
//    protected void onResume() {
//        super.onResume();
//
//        RecordUtils.onPageStart(this, getString(R.string.picture_to_screen_photo_album));
//    }
//
//    @Override
//    protected void onPause() {
//        super.onPause();
//        RecordUtils.onPageEndAndPause(this, this);
//    }
//
//    private void initDatas() {
//        mAdapter = new PhotoAdapter();
//        mPhotos.setAdapter(mAdapter);
//        mPhotos.setOnItemClickListener(this);
//    }
//
//    @Override
//    public void onClick(View v) {
//        switch (v.getId()) {
//            case R.id.back:
//
//                RecordUtils.onEvent(this,getString(R.string.picture_to_screen_back_album));
//                onBackPressed();
//                break;
//            case R.id.choose:
//                mCheckBox.setChecked(false);
//                mIsChoose = !mIsChoose;
//                if (mIsChoose) {  // 选择模式
//                    mChoose.setText("取消");
//                    mBottom.setVisibility(View.VISIBLE);
//                    return;
//                } else {  // 非选择模式
//
//                    mChoose.setText("选择");
//                    // 取消全选模式
//                    for (ModelPic modelPic : mDatas) {
//                        modelPic.setChecked(false);
//                        if (mCheckPicList.contains(modelPic)) {
//                            mCheckPicList.remove(modelPic);
//                        }
//                    }
//                    mCheckAll = false;
//                    mCheckBox.setChecked(false);
//                    mBottom.setVisibility(View.GONE);
//                }
//                break;
//            case R.id.checkall:
//                mCheckAll = !mCheckAll;
//                changeCheckState(mCheckAll);
//                break;
//            case R.id.show:
//                if (!AppUtils.isNetworkAvailable(this)) {
//                    showToast(getString(R.string.network_error));
//                    return;
//                }
//                if (mSession.isBindTv()) {
//                    if (mCheckPicList.isEmpty()) {
//                        showToast("请选择要添加到幻灯片里的照片");
//                        return;
//                    }
//                    if (mCheckPicList.size() < 2) {
//                        showToast("请选两张以上照片");
//                        return;
//                    }
//                    RecordUtils.onEvent(this,getString(R.string.picture_to_screen_photo_slide));
//                    isSlides = true;
//                    String json = new Gson().toJson(mCheckPicList);
//                    Type listType = new TypeToken<LinkedList<MediaInfo>>() {
//                    }.getType();
//                    List<MediaInfo> picList = new Gson().fromJson(json, listType);
//                    if (picList != null && picList.size()>0) {
//                        if(mRequestScreenDialog == null)
//                            mRequestScreenDialog = new SavorDialog(this,"正在投屏…");
//                        mRequestScreenDialog.show();
//                        small = 1;
//                        force = 0;
//                        pictureInfo = picList.get(0);
//                        ProjectionManager.getInstance().setSeriesId(System.currentTimeMillis()+"");
//                        pictureInfo.setImageId(System.currentTimeMillis()+"");
//                        ProjectionManager.getInstance().setImageType("3");
//                        showImageToScreen();
//                    }
//
//                } else {
//                    showToast("使用该功能需要先配对电视");
//                    return;
//                }
//                break;
//        }
//    }
//
//    private void displayPic() {
//        try {
//            if (pictureInfo==null){
//                return;
//            }
//            Bitmap bitmap = null;
//            String copyPath = "";
//            if (small==1){
//                copyPath = CompressImage.compressAndSaveBitmap(PhotoShareActivity.this, pictureInfo.getAssetpath(),pictureInfo.getAssetname(),true);
//            }else{
//                copyPath = CompressImage.compressAndSaveBitmap(PhotoShareActivity.this, pictureInfo.getAssetpath(),pictureInfo.getAssetname(),false);
//            }
////            if (bitmap == null){
////                return;
////            }
//
//            pictureInfo.setAsseturl(NetWorkUtil.getLocalUrl(this)+copyPath);
//            pictureInfo.setCompressPath(copyPath);
////            pictureInfo.setAssetpath(copyPath);
//
//            Message message = Message.obtain();
//            message.what = SHOW;
//            message.obj = pictureInfo;
//            mHandler.removeMessages(SHOW);
//            mHandler.sendMessage(message);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    public void saveBitmap(Bitmap bitmap, String path) {
//        File f = new File(path);
//        if (!f.exists()) {
//            try {
//                BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(f));
//                bitmap.compress(Bitmap.CompressFormat.JPEG, 80, out);
//                out.flush();
//                out.close();
//            } catch (FileNotFoundException e) {
//                e.printStackTrace();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//    }
//    private BaseProReqeust getBasePrepareInfo(MediaInfo pictureInfo){
//        BaseProReqeust prepareInfo = new BaseProReqeust();
//        prepareInfo.setAction(pictureInfo.getAction());
//        prepareInfo.setAssetname(pictureInfo.getAssetname());
//        prepareInfo.setImageId(pictureInfo.getImageId());
//        String imageType = ProjectionManager.getInstance().getImageType();
//        if("3".equals(imageType)) {
//            prepareInfo.setImageType("3");
//        }
//        return prepareInfo;
//    }
//    /**
//     * 改变全选状态
//     *
//     * @param isChecked
//     */
//    private void changeCheckState(boolean isChecked) {
//        mCheckBox.setChecked(isChecked);
//        if (isChecked) {
//            // 全选模式
//            for (ModelPic modelPic : mDatas) {
//                modelPic.setChecked(true);
//                if (!mCheckPicList.contains(modelPic)) {
//                    mCheckPicList.add(modelPic);
//                }
//            }
//
//        } else {
//            // 取消全选模式
//            LogUtils.d("取消全选动作");
//            for (ModelPic modelPic : mDatas) {
//                modelPic.setChecked(false);
//                if (mCheckPicList.contains(modelPic)) {
//                    mCheckPicList.remove(modelPic);
//                }
//            }
//        }
//        if (mAdapter != null) {
//            mAdapter.notifyDataSetChanged();
//        }
//    }
//
//    @Override
//    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//        mCurrentPosition = position;
//        ProjectionManager.getInstance().setImageType("1");
//        if (!AppUtils.isNetworkAvailable(this)) {
//            showToast(getString(R.string.network_error));
//            return;
//        }
//        ModelPic modelPic = mDatas.get(position);
//        if (mIsChoose) { //选择模式
//            RecordUtils.onEvent(mContext, getString(R.string.picture_to_screen_photo_select));
//
//            if (mCheckPicList.contains(modelPic)) {
//                mCheckPicList.remove(modelPic);
//                modelPic.setChecked(false);
//            } else {
//                mCheckPicList.add(modelPic);
//                modelPic.setChecked(true);
//            }
//            // 幻灯片模式
//            if (!modelPic.isChecked() && mCheckAll) {
//                mCheckAll = false;
//                mCheckBox.setText("全选");
//            }
//            if (!mCheckAll && isCheckAll()) {
//                mCheckAll = true;
//                mCheckBox.setText("全选");
//            }
//            RecordUtils.onEvent(mContext, getString(R.string.picture_to_screen_photo_select_all));
//            mCheckBox.setChecked(mCheckAll);
//        } else {  // 单张投屏模式
//
//            RecordUtils.onEvent(mContext, getString(R.string.picture_to_screen_click_item));
//            if(mSession.isBindTv()) {
//                if(mRequestScreenDialog == null)
//                    mRequestScreenDialog = new SavorDialog(this,"正在投屏…");
//                mRequestScreenDialog.show();
//                Gson gson = new Gson();
//                String picJson = gson.toJson(modelPic);
//                pictureInfo = gson.fromJson(picJson,MediaInfo.class);
//                small=1;
//                force=0;
//                if (pictureInfo==null){
//                    return;
//                }
//                pictureInfo.setImageId(System.currentTimeMillis()+"");
//                showImageToScreen();
//            }else {
//                ProjectionManager.getInstance().setPicList(null);
//                ProjectionManager.getInstance().setImgList(childList);
//                ProjectionManager.getInstance().setPicList(mDatas);
//                ImageGalleryActivity.startImageGallery(PhotoShareActivity.this, childList,position,false,mProjectId);
//            }
//        }
//        mAdapter.notifyDataSetChanged();
//    }
//
//    /**
//     *
//     */
//    private void showImageToScreen() {
//        new Thread(){
//            @Override
//            public void run() {
//                displayPic();
//            }
//        }.start();
//    }
//
//    /**
//     * 单个条目选择，更新全选按钮
//     *
//     * @return
//     */
//    private boolean isCheckAll() {
//        for (ModelPic modelPic : mDatas) {
//            if (!modelPic.isChecked()) {
//                return false;
//            }
//        }
//        return true;
//    }
//
//    @Override
//    public void getViews() {
//
//    }
//
//    @Override
//    public void setViews() {
//
//    }
//
//    @Override
//    public void setListeners() {
//
//    }
//
//    private class PhotoAdapter extends BaseAdapter {
//        private LayoutInflater inflater;
//
//        public PhotoAdapter() {
//            inflater = LayoutInflater.from(PhotoShareActivity.this);
//        }
//
//        @Override
//        public int getCount() {
//            return mDatas.size();
//        }
//
//        @Override
//        public ModelPic getItem(int position) {
//            return mDatas.get(position);
//        }
//
//        @Override
//        public long getItemId(int position) {
//            return position;
//        }
//
//        @Override
//        public View getView(int position, View convertView, ViewGroup parent) {
//            ViewHolder holder;
//            if (convertView == null) {
//                holder = new ViewHolder();
//                convertView = inflater.inflate(R.layout.item_photo_in_grid, null);
//                holder.mImageContent = (ImageView) convertView.findViewById(R.id.image_content);
//                holder.mCheckBox = (CheckBox) convertView.findViewById(R.id.checkbox);
//                convertView.setTag(R.id.tag_holder, holder);
//            } else {
//                holder = (ViewHolder) convertView.getTag(R.id.tag_holder);
//            }
//
//            if (mIsChoose) {
//                holder.mCheckBox.setVisibility(View.VISIBLE);
//                holder.mCheckBox.setChecked(getItem(position).isChecked());
//            } else {
//                holder.mCheckBox.setVisibility(View.GONE);
//            }
//
//            Glide.with(mContext).load(mDatas.get(position).getAssetpath()).diskCacheStrategy(DiskCacheStrategy.NONE).centerCrop().into(holder.mImageContent);
//            return convertView;
//        }
//
//        private class ViewHolder {
//            public CheckBox mCheckBox;
//            public ImageView mImageContent;
//        }
//    }
//
//    @Override
//    public void onSuccess(AppApi.Action method, Object obj) {
//        super.onSuccess(method, obj);
//        switch (method) {
//            case POST_IMAGE_PROJECTION_JSON:
//                if(mRequestScreenDialog!=null) {
//                    mRequestScreenDialog.dismiss();
//                }
//                if(mProjectionService!=null)
//                    mProjectionService.stopQuerySeek();
//                if(obj instanceof ImageProResonse) {
//                    ImageProResonse proResonseInfo = (ImageProResonse) obj;
//                    mProjectId = proResonseInfo.getProjectId();
//                    ProjectionManager.getInstance().setmProjectId(mProjectId);
//
//                }
//                if (small==1){
//                    small = 0;
//                    showImageToScreen();
//                    handeScreenSucess();
//                    return;
//                }
//                break;
//
//        }
//    }
//
//    private void handeScreenSucess() {
//        Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
//        Ringtone r = RingtoneManager.getRingtone(SavorApplication.getInstance(), notification);
//        r.play();
//
//        if (isSlides) {
//            Gson gson = new Gson();
//            String json = gson.toJson(mCheckPicList);
//            Type listType = new TypeToken<LinkedList<MediaInfo>>() {
//            }.getType();
//            LinkedList<MediaInfo> pictureInfos = gson.fromJson(json,listType);
//            ProjectionManager.getInstance().setmSlideList(pictureInfos);
//            SlidesActivity.startSlidesActivity(this,0,json,true,ProjectionManager.getInstance().getSeriesId(),true);
//        }else {
//            if(mProjectionService!=null) {
//                mProjectionService.stopSlide();
//            }
//            ProjectionManager.getInstance().setSlideStatus(false);
//            ProjectionManager.getInstance().setImgList(childList);
//            ProjectionManager.getInstance().setPicList(mDatas);
//            ImageGalleryActivity.startImageGallery(PhotoShareActivity.this, childList,mCurrentPosition,true,mProjectId);
//        }
//        isSlides = false;
//    }
//
//    @Override
//    public void onError(AppApi.Action method, Object obj) {
//        if(obj == AppApi.ERROR_TIMEOUT) {
//            showToast("网络超时");
//            mRequestScreenDialog.dismiss();
//            return;
//        }
//        switch (method) {
//            case POST_IMAGE_PROJECTION_JSON:
//                isSlides = false;
//                if(mRequestScreenDialog!=null) {
//                    mRequestScreenDialog.dismiss();
//                }
//                if(obj instanceof ResponseErrorMessage) {
//                    ResponseErrorMessage responseErrorMessage = (ResponseErrorMessage) obj;
//                    String msg = responseErrorMessage.getMessage();
//                    Message message = Message.obtain();
//                    if (responseErrorMessage.getCode()==4){
//                        message.what = FORCE_MSG;
//                        message.obj = msg;
//                        mHandler.sendMessage(message);
//                    }else{
//                        message.what = ERROR_MSG;
//                        message.obj = msg;
//                        mHandler.removeMessages(ERROR_MSG);
//                        mHandler.sendMessage(message);
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
//        String content = "当前"+msg+"正在投屏,是否继续投屏?";
//        dialog = new CommonDialog(this, content,
//            new CommonDialog.OnConfirmListener() {
//            @Override
//            public void onConfirm() {
//                HashMap<String,String> params = new HashMap<>();
//                params.put(getString(R.string.to_screen_competition_hint),"ensure");
//                params.put("type","pic");
//                RecordUtils.onEvent(PhotoShareActivity.this,getString(R.string.to_screen_competition_hint),params);
//                force = 1;
//                Message message = Message.obtain();
//                message.what = SHOW;
//                message.obj = pictureInfo;
//                mHandler.removeMessages(SHOW);
//                mHandler.sendMessage(message);
//                dialog.cancel();
//            }
//        }, new CommonDialog.OnCancelListener() {
//            @Override
//            public void onCancel() {
//                HashMap<String,String> params = new HashMap<>();
//                params.put(getString(R.string.to_screen_competition_hint),"cancel");
//                params.put("type","pic");
//                RecordUtils.onEvent(PhotoShareActivity.this,getString(R.string.to_screen_competition_hint),params);
//                dialog.cancel();
//            }
//        },"继续投屏",true);
//        dialog.show();
//    }
//
//
//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        if(mRequestScreenDialog!=null) {
//            mRequestScreenDialog.dismiss();
//            mRequestScreenDialog = null;
//
//        }
//    }
//}
