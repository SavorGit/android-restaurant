package com.savor.resturant.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.common.api.core.InitViews;
import com.common.api.utils.AppUtils;
import com.common.api.utils.LogUtils;
import com.common.api.utils.ShowMessage;
import com.savor.resturant.R;
import com.savor.resturant.adapter.SlideDetailAdapter;
import com.savor.resturant.bean.ImageProResonse;
import com.savor.resturant.bean.PictureBean;
import com.savor.resturant.bean.PictureInfo;
import com.savor.resturant.bean.SlideSetInfo;
import com.savor.resturant.bean.TvBoxInfo;
import com.savor.resturant.bean.TvBoxSSDPInfo;
import com.savor.resturant.core.AppApi;
import com.savor.resturant.core.ResponseErrorMessage;
import com.savor.resturant.presenter.BindTvPresenter;
import com.savor.resturant.utils.CompressImage;
import com.savor.resturant.utils.IntentUtil;
import com.savor.resturant.utils.MediaUtils;
import com.savor.resturant.utils.RecordUtils;
import com.savor.resturant.utils.SlideManager;
import com.savor.resturant.utils.ToastUtil;
import com.savor.resturant.widget.CommonDialog;
import com.savor.resturant.widget.CustomAlertDialog;
import com.savor.resturant.widget.HotsDialog;
import com.savor.resturant.widget.LoadingProgressDialog;
import com.savor.resturant.widget.SavorDialog;
import com.savor.resturant.widget.SlideSettingsDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import static com.savor.resturant.activity.LinkTvActivity.EXRA_TV_BOX;
import static com.savor.resturant.activity.LinkTvActivity.EXTRA_TV_INFO;

/**
 * 幻灯片详情界面
 * Created by luminita on 2016/12/15.
 */

public class SlideDetailActivity extends BaseActivity implements InitViews, View.OnClickListener, IBindTvView {

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
    private LinkedList<PictureInfo> picList = new LinkedList<>(); //已选择图片的集合
    // 所有图片名称
    public List<String> imageNameList = new ArrayList<>();
    private SlideDetailAdapter slideDetailAdapter;
    private boolean mIsEdit =  false;
    private boolean mIsCheckAll = false;
    private TextView add;
    private SavorDialog mQrcodeDialog;
    private List<PictureBean> pictureBeanResultList = new ArrayList<>();
    private String currentUploadPicture=null;
    private SlideSettingsDialog settingDialog=null;
    /**
     * 投屏时遇到别人正在投屏，传1代表确认抢投，默认传0
     */
    private int force=0;
    private CommonDialog dialog;
    private static final int TOAST_ERROR_MSG = 0x2;
    private static final int FORCE_MSG = 0x5;
    private static final int IMAGE_UPLOAD_RESPONSE = 0x6;
    private static final int UPLOAD_TIMEOUT = 0x7;
    private static final int CHECK_LINK_STATUS = 0x8;
    /**添加图片响应码*/
    public static final int RESULT_CODE_ADD_PIC = 0x9;
    /**当前操作类型*/
    private int mOperationType = TYPE_PRO;
    /**当前操作点击投屏按钮*/
    private static final int TYPE_PRO =  100;
    /**当前操作点击幻灯片设置确定按钮去绑定*/
    private static final int TYPE_CONFIRM=  101;
    /**是否已经停止上传，比如上传过程中退出页面*/
    private boolean isStopUpload;
    private Handler mHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case CHECK_LINK_STATUS:
                    checkLinkStatus();
                    break;
                case UPLOAD_TIMEOUT:
                    closeProgressBarDialog();
                    showToast("网络错误，请重试");
                    break;
                case TOAST_ERROR_MSG:
                    closeProgressBarDialog();
                    String messgae = (String) msg.obj;
                    ShowMessage.showToast(SlideDetailActivity.this,messgae);
                    break;
                case FORCE_MSG:
                    messgae = (String)msg.obj;
                    closeProgressBarDialog();
                    showConfirm(messgae);
                    break;
                case IMAGE_UPLOAD_RESPONSE:
                    handleImageUploadResponse(msg.obj);
                    break;
            }
        }
    };
    private BindTvPresenter mBindTvPresenter;

    private void checkLinkStatus() {
        TvBoxSSDPInfo tvBoxSSDPInfo = mSession.getTvBoxSSDPInfo();
        if(tvBoxSSDPInfo!=null && !TextUtils.isEmpty(tvBoxSSDPInfo.getBoxIp())) {
            closeLinkingDialog();
            showSlideSettings();
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
                            searchTv();
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


    private void handleImageUploadResponse(Object obj) {
        if (obj instanceof ImageProResonse){
            ImageProResonse resonse = (ImageProResonse) obj;
            int result = resonse.getResult();
            if (result==0){
                if (!TextUtils.isEmpty(currentUploadPicture)){
                    for (PictureBean bean:pictureBeanResultList){
                        if (bean.getName()==currentUploadPicture){
                            bean.setExist(1);
                            break;
                        }
                    }
                }

                uploadPictureToServer();
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
        slideInfo = (SlideSetInfo) getIntent().getSerializableExtra(IntentUtil.KEY_SLIDE);
        getViews();
        setViews();
        setListeners();
        initPresenter();
    }

    private void initPresenter() {
        mBindTvPresenter = new BindTvPresenter(this,this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        RecordUtils.onPageStart(this, getString(R.string.slide_to_screen_open_album));
    }

    @Override
    protected void onPause() {
        super.onPause();
        RecordUtils.onPageEndAndPause(this, this);
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
        slideInfo = (SlideSetInfo) getIntent().getSerializableExtra(IntentUtil.KEY_SLIDE);
        refreshImageList(slideInfo);

        if(slideInfo.imageList==null||slideInfo.imageList.size()==0) {
           finish();
            return;
        }
        picList.clear();
        imageNameList.clear();
        MediaUtils.getFolderAllImg(mContext, picList, slideInfo.imageList,imageNameList);
        slideDetailAdapter.setData(picList);
        Intent intent = getIntent();
        if(intent!=null) {
            int type = intent.getIntExtra(IntentUtil.KEY_TYPE, 0);
            if(type == IntentUtil.TYPE_SLIDE_BY_DETAIL) {
                mIsEdit = true;
                changeEditState();
            }
        }
    }

    private void refreshImageList(SlideSetInfo slideInfo) {
        List<String> imageList = slideInfo.imageList;
        List<String> tempList = new ArrayList<>();
        if(imageList!=null&&imageList.size()>0) {
            for(int i = 0;i<imageList.size();i++) {
                String imagePath = imageList.get(i);
                if(!TextUtils.isEmpty(imagePath)&&new File(imagePath).exists()) {
                    tempList.add(imagePath);
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
    }

    @Override
    public void setViews() {
        title.setText(slideInfo.groupName);
        MediaUtils.getFolderAllImg(mContext, picList, slideInfo.imageList,imageNameList);
        slideDetailAdapter = new SlideDetailAdapter(mContext);
        pictureGroup.setAdapter(slideDetailAdapter);
        slideDetailAdapter.setData(picList);
        if(!AppUtils.isWifiNetwork(this)) {
            showChangeWifiDialog();
        }
    }

    @Override
    public void setListeners() {
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
              PictureInfo pictureInfo = picList.get(i);
              if (mIsEdit) {
                  pictureInfo.setChecked(!pictureInfo.isChecked());
                  //如果照片通过单击全部选择，则相应的改变左下角全选按钮状态
                  if (!mIsCheckAll && isCheckAll()){
                      mIsCheckAll = true;
                      checkAll.setText("取消全选");
                      checkAll.setChecked(true);
                  }
                  if (!pictureInfo.isChecked() && mIsCheckAll) {
                      mIsCheckAll = false;
                      checkAll.setText("全选");
                      checkAll.setChecked(false);
                  }
              } else {
                  Intent intent = new Intent();
                  intent.putExtra("photos",slideInfo);
                  intent.putExtra("position",i);
                  intent.setClass(SlideDetailActivity.this,SlidePreviewActivity.class);
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
                onBackPressed();
                break;
            case R.id.add:
                if (picList.size() >= 50) {
                    ShowMessage.showToast(mContext, "最多只能添加50张");
                    return;
                }
                //跳转至照片列表
                IntentUtil.openActivity(SlideDetailActivity.this, PhotoActivity.class, IntentUtil.TYPE_SLIDE_BY_DETAIL, slideInfo);
                break;
            case R.id.rl_edit:
                changeEditState();
                InputMethodManager imm =  (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                if(imm != null) {
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
                delPicture();
                break;
        }
    }

    private void performProjection() {
        isStopUpload = false;
        if(!isFoundTv()) {
            showChangeWifiDialog();
        }else {
            if(mSession.isBindTv()) {
                // 幻灯片设置
                showSlideSettings();
            }else{
                // 搜索电视
                mBindTvPresenter.bindTv();
            }
        }
    }

    public void showSlideSettings() {
        settingDialog = new SlideSettingsDialog(this);
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
                        mOperationType = TYPE_CONFIRM;
                        if(!isFoundTv()) {
                            showChangeWifiDialog();
                        }else {
                            if(mSession.isBindTv()) {
                                // 幻灯片设置
                                performSlideSettingsConfirm();
                            }else{
                                // 搜索电视
                                mBindTvPresenter.bindTv();
                            }
                        }

                    }
                });
        if(!settingDialog.isShowing())
        settingDialog.show();
    }

    private void performSlideSettingsConfirm() {
        force = 0;
        boolean looping = settingDialog.isLooping();
        int loopTime = 0;
        if(looping) {
            loopTime = settingDialog.getLoopTime()*60;
        }
        postSlideParamToServer(slideInfo.groupName,loopTime,settingDialog.getSingleTime(),force);
    }

    private void searchTv() {
        showLinkingDialog();
        startSSDPServiceInOperation();
        checkLinkStatusDelayed();
    }

    /**
     * 15秒以后如果还没有收到ssdp，提示连接失败，重新连接
     */
    private void checkLinkStatusDelayed() {
        mHandler.removeMessages(CHECK_LINK_STATUS);
        mHandler.sendEmptyMessageDelayed(CHECK_LINK_STATUS,15*1000);
    }
    /**
     * 上传幻灯片参数到服务器端
     * @param name 幻灯片名称
     * @param duration 总时长
     * @param interval 间隔时间
     */
    public void postSlideParamToServer(String name,int duration,int interval,int force){
        JSONArray jsonArray = new JSONArray();
        if (imageNameList!=null&&!imageNameList.isEmpty()){
            for (String str:imageNameList){
                JSONObject jsonObject = new JSONObject();
                try {
                    String picName = MediaUtils.getPicName(str);
                    jsonObject.accumulate("name",picName);
                    jsonObject.accumulate("exist","0");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                jsonArray.put(jsonObject);
            }

        }
        HashMap<String,Object> param = new HashMap<>();
        param.put("name",name);
        param.put("duration",duration+"");
        param.put("interval",interval+"");
        param.put("images",jsonArray);
        AppApi.postSlideSettingToServer(mContext,mSession.getTVBoxUrl(),param,force,this);
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
            for (PictureInfo pictureInfo : picList) {
                pictureInfo.setChecked(true);
            }
        } else {     // 取消全选模式
            checkAll.setText("全选");
            for (PictureInfo pictureInfo : picList) {
                pictureInfo.setChecked(false);
            }
        }
        slideDetailAdapter.notifyDataSetChanged();
    }

    /**
     * 从幻灯片集删除选中的照片
     */
    private void delPicture() {
        int delcount = 0; //需要删除的数量
        for (PictureInfo pictureInfo : picList) {
            if (pictureInfo.isChecked()) {
                delcount++; //记录需要删除的数量
            }
        }
        if (delcount == 0) {
            ShowMessage.showToast(this, "请选择要删除的图片");
            return;
        }
        String message = "";
        if(isCheckAll()) {
            message = "将删除此幻灯片，但不会删除本地照片";
        }else {
            message = getString(R.string.confirm_delete_picture,delcount);
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
            SlideManager.getInstance(SlideManager.SlideType.IMAGE).removeGroup(slideInfo);
            SlideManager.getInstance(SlideManager.SlideType.IMAGE).saveSlide();
            finish();
        }
        Set<PictureInfo> toRemove = new HashSet<PictureInfo>();
        //遍历该幻灯片集，把选中的添加至暂存器，并删除
        for (PictureInfo pictureInfo : picList) {
            if (pictureInfo.isChecked()) {
                toRemove.add(pictureInfo);
                slideInfo.imageList.remove(pictureInfo.getAssetpath());
                imageNameList.remove(pictureInfo.getAssetpath());
            }
        }
        picList.removeAll(toRemove);
        slideInfo.updateTime = System.currentTimeMillis();
        slideDetailAdapter.setData(picList);
    }

    /**
     * 保存编辑后的幻灯片集
     */
    private void save () {
        SlideManager instance = SlideManager.getInstance(SlideManager.SlideType.IMAGE);
        //添加幻灯片组至该幻灯片列表
        if (instance.containGroup(slideInfo))
            instance.removeGroup(slideInfo);
        if(slideInfo!=null&&slideInfo.imageList!=null&&slideInfo.imageList.size()>0) {
            instance.addList(slideInfo);
            instance.saveSlide();
        }
    }

    /**
     * 检查每一张照片是否是选择状态
     */
    private boolean isCheckAll() {
        for (PictureInfo pictureInfo : picList) {
            if (!pictureInfo.isChecked())
                return false;
        }
        return true;
    }

    private void showProgressBarDialog() {
        if(this.isFinishing())
            return;
        if(mProgressBarDialog==null) {
            mProgressBarDialog = new LoadingProgressDialog(this);
        }
        mProgressBarDialog.show();
    }

    private void closeProgressBarDialog() {
        if(mProgressBarDialog!=null) {
            mProgressBarDialog.dismiss();
        }
    }

    /**
     * 将幻灯片的设置信息发送给盒子以后，开始上传图片到盒子
     */
    private void uploadPictureToServer(){

        if (pictureBeanResultList!=null&&pictureBeanResultList.size()>0){
            updateProgress();

            boolean isUpload=false;
            for (PictureBean bean:pictureBeanResultList){
                if (bean.getExist()==0){
                    for (String fileUrl:slideInfo.imageList){
                        String picName = MediaUtils.getPicName(fileUrl);
                        String realName = MediaUtils.getPicRealName(fileUrl);
                        if (picName.equals(bean.getName())){
                            isUpload = true;
                            String copyFileUrl = CompressImage.compressAndSaveBitmap(this, fileUrl,realName,false);
                            HashMap<String,Object> params = new HashMap<>();
                            params.put("fileName",picName);
                            params.put("pptName",slideInfo.groupName);
                            AppApi.updateScreenProjectionFile(mContext,mSession.getTVBoxUrl(),copyFileUrl,params,this);
                            currentUploadPicture = bean.getName();
                            break;
                        }
                    }
                }
                if (isUpload){
                    break;
                }
            }
        }
    }

    private void updateProgress() {
        int count = 0;
        for(int i = 0;i<pictureBeanResultList.size();i++) {
            PictureBean pictureBean = pictureBeanResultList.get(i);
            if(pictureBean.getExist() == 0) {
                count++;
            }

        }
        if(count == 0) {
            if(mProgressBarDialog!=null) {
                mProgressBarDialog.updatePercent(1);
            }
            ShowMessage.showToast(this,getString(R.string.pro_success));
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    closeProgressBarDialog();
                    finish();
                }
            },100);
        }else {
            double percent = ((double)count)/pictureBeanResultList.size();
            if(mProgressBarDialog!=null) {
                mProgressBarDialog.updatePercent(1-percent);
            }
        }

    }

    @Override
    public void onSuccess(AppApi.Action method, Object obj) {
        super.onSuccess(method, obj);
        switch (method) {
            case POST_UPLOAD_SLIDESETTINGS_JSON:
                if (obj instanceof List<?>){
                    pictureBeanResultList = (List<PictureBean>) obj;
                    if (settingDialog!=null&&settingDialog.isShowing()){
                        settingDialog.dismiss();
                    }
                    showProgressBarDialog();
                    force = 0;
                    uploadPictureToServer();
                }
                break;
            case POST_IMAGE_PROJECTION_JSON:
                if(!isStopUpload) {
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
            case POST_UPLOAD_SLIDESETTINGS_JSON:
                if(obj instanceof ResponseErrorMessage) {
                    ResponseErrorMessage message = (ResponseErrorMessage) obj;
                    int code = message.getCode();
                    String error_msg = message.getMessage();
                    Message msg = Message.obtain();
                    if (code==4){
                        msg.what = FORCE_MSG;
                        msg.obj = error_msg;
                        mHandler.sendMessage(msg);
                    }else {
                        msg.what = TOAST_ERROR_MSG;
                        msg.obj = error_msg;
                        mHandler.sendMessage(msg);
                    }
                }else if( obj == AppApi.ERROR_TIMEOUT) {
                    mHandler.sendEmptyMessage(UPLOAD_TIMEOUT);
                }
                break;
            case POST_IMAGE_PROJECTION_JSON:
//                mRequestScreenDialog.dismiss();
                if(obj instanceof ResponseErrorMessage) {
                    ResponseErrorMessage message = (ResponseErrorMessage) obj;
                    int code = message.getCode();
                    String error_msg = message.getMessage();
                    Message msg = Message.obtain();
                    if (code==4){
                        msg.what = FORCE_MSG;
                        msg.obj = error_msg;
                        mHandler.sendMessage(msg);
                    }else {
                        msg.what = TOAST_ERROR_MSG;
                        msg.obj = error_msg;
                        mHandler.sendMessage(msg);
                    }
                }else if( obj == AppApi.ERROR_TIMEOUT) {
                    mHandler.sendEmptyMessage(UPLOAD_TIMEOUT);
                }else if( obj == AppApi.ERROR_NETWORK_FAILED) {
                    mHandler.sendEmptyMessage(UPLOAD_TIMEOUT);
                }
                break;
        }
    }


    /**
     * 不好啦，别人正在投屏，弹出是否确认抢投按钮
     * @param msg
     */
    private void showConfirm(String msg){
        if(this.isFinishing()) {
            LogUtils.d("savor:pro 当前页面已回收，不展示抢投提示");
            return;
        }
        if (dialog!=null&&dialog.isShowing()){
            return;
        }

        if(settingDialog!=null&&settingDialog.isShowing()) {
            settingDialog.dismiss();
        }

        String content = "当前"+msg+"正在投屏,是否继续投屏?";
        dialog = new CommonDialog(this, content,
                new CommonDialog.OnConfirmListener() {
                    @Override
                    public void onConfirm() {
                        force = 1;
                        boolean looping = settingDialog.isLooping();
                        int loopTime = 0;
                        if(looping) {
                            loopTime = settingDialog.getLoopTime()*60;
                        }
                        postSlideParamToServer(slideInfo.groupName,loopTime,settingDialog.getSingleTime(),force);
                    }
                }, new CommonDialog.OnCancelListener() {
            @Override
            public void onCancel() {
                dialog.cancel();
            }
        },"继续投屏",true);
        dialog.show();
    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
        isStopUpload = true;
        closeProgressBarDialog();
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        isStopUpload = true;
        if(mProgressBarDialog!=null) {
            mProgressBarDialog.dismiss();
            mProgressBarDialog = null;
        }
        if(dialog!=null) {
            dialog.dismiss();
            dialog = null;
        }
    }

    @Override
    public void readyForQrcode() {
        if(mQrcodeDialog==null)
            mQrcodeDialog = new SavorDialog(this,"正在呼出验证码");
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
    public void initBindcodeResult() {
        ShowMessage.showToast(this,mSession.getSsid()+"连接成功，可以投屏");
        if(mOperationType == TYPE_PRO) {
            showSlideSettings();
        }else if(mOperationType == TYPE_CONFIRM) {
            performSlideSettingsConfirm();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == EXTRA_TV_INFO) {
            if(data!=null) {
                TvBoxInfo boxInfo = (TvBoxInfo) data.getSerializableExtra(EXRA_TV_BOX);
                mBindTvPresenter.handleBindCodeResult(boxInfo);
            }

        }
    }


}
