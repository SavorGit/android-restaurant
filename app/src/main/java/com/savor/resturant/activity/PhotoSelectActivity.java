package com.savor.resturant.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.common.api.core.InitViews;
import com.common.api.utils.ShowMessage;
import com.savor.resturant.R;
import com.savor.resturant.adapter.PhotoSelectAdapter;
import com.savor.resturant.bean.MediaInfo;
import com.savor.resturant.bean.SlideSetInfo;
import com.savor.resturant.interfaces.IphotoData;
import com.savor.resturant.utils.IntentUtil;
import com.savor.resturant.utils.MediaUtils;
import com.savor.resturant.utils.ProjectionManager;
import com.savor.resturant.utils.RecordUtils;
import com.savor.resturant.utils.SlideManager;
import com.savor.resturant.utils.ToastUtil;
import com.savor.resturant.widget.CustomAlertPPtDialog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

/**
 * 幻灯片选择界面
 * Created by luminita on 2016/12/13.
 */

public class PhotoSelectActivity extends BaseActivity implements InitViews, View.OnClickListener,IphotoData, AdapterView.OnItemClickListener {

    private GridView photoGroup;
    private TextView title;
    private TextView numMark;
    private CheckBox checkAll;
    private Button add;
    private ImageView back;
    private int type;
    private SlideSetInfo slideInfo;
    /**
     * 当前相册下所有照片路径
     */
    private ArrayList<MediaInfo> phoList;
//    /**
//     * 当前相册下所有照片信息
//     */
//    private ArrayList<MediaInfo> imgInfoList = new ArrayList<MediaInfo>();
    /**
     * 已选择的照片
     */
    private LinkedList<MediaInfo> selectedImgList = new LinkedList<MediaInfo>();
    private boolean isCheckAll = false;
    private int picCount;
    private PhotoSelectActivity mContext;
    private PhotoSelectAdapter selectAdapter;
    private RelativeLayout photoSelect;
    private LinearLayout photoDetail;
    private ImageView photoFull;
    public static final int INIT_SUCCESS = 0;

    private Handler mHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case INIT_SUCCESS:
                    progressBar.setVisibility(View.GONE);
                    setData();
                    break;
            }
        }
    };
    private ProgressBar progressBar;
    private SlideManager.SlideType slideType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_select);
        mContext = PhotoSelectActivity.this;
        //获取从PhotoActivity传递的intent
        Intent intent = getIntent();
        type = intent.getIntExtra(IntentUtil.KEY_TYPE, IntentUtil.TYPE_SLIDE_BY_LIST);
        slideInfo = (SlideSetInfo) intent.getSerializableExtra(IntentUtil.KEY_SLIDE);
        phoList = ProjectionManager.getInstance().getImgList();
        slideType = (SlideManager.SlideType) intent.getSerializableExtra(IntentUtil.MEDIA_TYPE);
//        phoList = intent.getStringArrayListExtra(IntentUtil.KEY_PHOTO_LIST);/

        getViews();
        setViews();
        setListeners();
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    /**
     * 关联控件
     */
    @Override
    public void getViews() {
        progressBar = (ProgressBar) findViewById(R.id.progressbar);
        photoGroup = (GridView) findViewById(R.id.gv_photo);
        title = (TextView) findViewById(R.id.tv_title);
        numMark = (TextView) findViewById(R.id.tv_num);
        checkAll = (CheckBox) findViewById(R.id.cb_check_all);
        add = (Button) findViewById(R.id.btn_add);
        back = (ImageView) findViewById(R.id.iv_back);
        photoSelect = (RelativeLayout) findViewById(R.id.rl_list);
        photoDetail = (LinearLayout) findViewById(R.id.ll_detail);
        photoFull = (ImageView) findViewById(R.id.iv_photo);
    }

    /**
     * 初始化控件
     */
    @Override
    public void setViews() {
        //设置幻灯片选择界面，当前幻灯片集的名称与照片数量
        if (slideInfo != null) {
            title.setText(slideInfo.groupName);
            if (slideInfo.imageList != null && slideInfo.imageList.size() > 0) {
                picCount = slideInfo.imageList.size();
                numMark.setText("(" + picCount + "/50)");
            }
        }
        String name = slideInfo.groupName;
        if(type == IntentUtil.TYPE_SLIDE_BY_LIST) {
            switch (slideType) {
                case VIDEO:
                    add.setText(R.string.please_select_video);
                    break;
                case IMAGE:
                    add.setText(R.string.please_select_pic);
                    break;
            }

            add.setTextColor(getResources().getColor(R.color.dialog_text_black));
        }else {
            if (TextUtils.isEmpty(name)) {
                switch (slideType) {
                    case IMAGE:
                        add.setText("添加至幻灯片");
                        break;

                    case VIDEO:
                        add.setText("添加至视频列表");
                        break;
                }

            }else {
                if(name.length()>8) {
                    name = name.substring(0,8)+"...";
                }
                switch (slideType) {
                    case VIDEO:
                        add.setText(getString(R.string.add_video_slide, name));
                        break;

                    case IMAGE:
                        add.setText(getString(R.string.add_lantern_slide, name));
                        break;
                }


            }
        }


        selectAdapter = new PhotoSelectAdapter(mContext);
        photoGroup.setAdapter(selectAdapter);
        progressBar.setVisibility(View.VISIBLE);

        progressBar.setVisibility(View.GONE);
        selectAdapter.setData(phoList);
//        new Thread(){
//            @Override
//            public void run() {
//                //获取相册下所有照片，并展示
//                MediaUtils.getFolderAllNames(mContext, imgInfoList, phoList,mHandler);
//            }
//        }.start();

    }

    /**
     * 设置监听事件
     */
    @Override
    public void setListeners() {
        back.setOnClickListener(this);
        photoFull.setOnClickListener(this);
        checkAll.setOnClickListener(this);
        add.setOnClickListener(this);
        //GroupView的item点击事件
        photoGroup.setOnItemClickListener(this);
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                onBackPressed();
                break;
            case R.id.iv_photo:
                photoDetail.setVisibility(View.GONE);
                break;
            case R.id.cb_check_all:
                setCheckAll();
                break;
            case R.id.btn_add:
                if (isChecked()) {
//                    ToastUtil.showToastSavor(mContext, "请选择要添加的照片");
                    return;
                }
                if(type == IntentUtil.TYPE_SLIDE_BY_LIST) {
                    createDialog();
                }else {
                    String name = slideInfo.groupName;
                    if (TextUtils.isEmpty(name)) {
                        createDialog();
                    }else {
                        SlideSaveAndSkip ();
                    }
                }

                break;
        }
    }

    /**
     * 创建幻灯片弹窗
     */
    private void createDialog() {
        String hint = "";
        switch (slideType) {
            case VIDEO:
                hint = getString(R.string.input_video_lantern);
                break;

            case IMAGE:
                hint = getString(R.string.input_slide_lantern);
                break;
        }
        final CustomAlertPPtDialog dialog = new CustomAlertPPtDialog(this);
        dialog.builder()
                .setInputHint(hint)
                .setCancelable(false)
                .setPositiveButton(getString(R.string.create), new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String inputText = dialog.getInputText();
                        //创建幻灯片
                       createSlideLantern(inputText);
                    }
                })
                .setNegativeButton(getString(R.string.cancel), new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {
                    }
                })
                .show();


    }


    /**
     * 新建幻灯片
     *
     * @param filename 幻灯片文件名
     */
    private void createSlideLantern(String filename) {

        //RecordUtils.onEvent(this, getString(R.string.slide_to_screen_creat));
        HashMap<String,String> hashMap = new HashMap<String, String>();
        hashMap.put(getString(R.string.slide_to_screen_creat),"creat");
        RecordUtils.onEvent(this,getString(R.string.slide_to_screen_creat),hashMap);
        // 创建文件夹，记录文件名
        if (TextUtils.isEmpty(filename)) {
            switch (slideType) {
                case IMAGE:
                    ToastUtil.showToastSavor(PhotoSelectActivity.this, getString(R.string.slidename_not_null));
                    break;
                case VIDEO:
                    ToastUtil.showToastSavor(PhotoSelectActivity.this, getString(R.string.video_slidename_not_null));
                    break;
            }

            return;
        }
        // 新建幻灯片信息
       // SlideSetInfo slideSetInfo = new SlideSetInfo();
        slideInfo.isNewCreate = true;
        slideInfo.groupName = filename;
        slideInfo.updateTime = System.currentTimeMillis();
        //判断幻灯片是否已存在
        int size = SlideManager.getInstance(SlideManager.SlideType.IMAGE).getSize();
        boolean isExist = SlideManager.getInstance(SlideManager.SlideType.IMAGE).containGroup(slideInfo);
        if (size >= 50) {
            switch (slideType) {
                case IMAGE:
                    ToastUtil.showToastSavor(PhotoSelectActivity.this, getString(R.string.create_more));
                    break;

                case VIDEO:
                    ToastUtil.showToastSavor(PhotoSelectActivity.this, getString(R.string.create_video_more));
                    break;
            }

            return;
        }
        if (isExist) {
            switch (slideType) {
                case VIDEO:
                    ToastUtil.showToastSavor(PhotoSelectActivity.this, getString(R.string.create_video_exist));
                    break;

                case IMAGE:
                    ToastUtil.showToastSavor(PhotoSelectActivity.this, getString(R.string.create_exist));
                    break;
            }

            return;
        }

        SlideSaveAndSkip ();
    }

    /**
     * 照片的全部选择与取消
     */
    private void setCheckAll() {
        isCheckAll = !isCheckAll;
        checkAll.setChecked(isCheckAll);
        if (isCheckAll) {
            checkAll.setText("取消全选");
            for (MediaInfo picInfo : phoList) {
                if (picCount < 50) {
                    picInfo.setChecked(true);
                    if (!selectedImgList.contains(picInfo)) {
                        selectedImgList.add(picInfo);
                        picCount++;
                    }
                }

            }

        } else {

            checkAll.setText("全选");
            for (MediaInfo picInfo : phoList) {
                picInfo.setChecked(false);
                if (selectedImgList.contains(picInfo)) {
                    selectedImgList.remove(picInfo);
                    picCount--;
                }
            }
        }
        if (selectAdapter != null){
            selectAdapter.notifyDataSetChanged();
            //selectAdapter.setData(phoList);
        }

        if(type == IntentUtil.TYPE_SLIDE_BY_LIST) {
            initCreateSlideBtn();
        }
        numMark.setText("(" + picCount + "/50)");
    }

    /**
     * 判断照片是否没选中
     * @return false:有被选中照片
     */
    private boolean isChecked() {
        for (MediaInfo picInfo : phoList) {
            if (picInfo.isChecked())
                return false;
        }
        return true;
    }

    /**
     * 判断照片是否全选
     * @return false:有被选中照片
     */
    private boolean isCheckedAll() {
        for (MediaInfo picInfo : phoList) {
            if (!picInfo.isChecked())
                return false;
        }
        return true;
    }

    /**
     * 保存幻灯片信息并跳转界面
     */
    private void SlideSaveAndSkip () {
        saveSlide();
        ShowMessage.showToast(this,"添加成功");
        switch (type) {
            case IntentUtil.TYPE_SLIDE_BY_LIST:
                RecordUtils.onEvent(mContext, getString(R.string.slide_to_screen_click_album));
                Intent intent = new Intent(this, SlideListActivity.class);
                intent.putExtra(IntentUtil.KEY_TYPE, type);
                intent.putExtra(IntentUtil.KEY_SLIDE, slideInfo);
                startActivity(intent);
                break;
            case IntentUtil.TYPE_SLIDE_BY_DETAIL:
                Intent detailIntent = new Intent(this, SlideDetailActivity.class);
                detailIntent.putExtra(IntentUtil.KEY_TYPE, type);
                detailIntent.putExtra(IntentUtil.KEY_SLIDE, slideInfo);
                detailIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                detailIntent.putExtra("type",slideType);
                startActivity(detailIntent);
                this.finish();
                break;
        }
    }

    /**
     * 添加至幻灯片信息集合,并保存到本地
     */
    private void saveSlide() {
        SlideManager instance = SlideManager.getInstance(slideType);
        for (MediaInfo pic : selectedImgList) {
            pic.setChecked(false);
            //添加图片至该幻灯片组
            slideInfo.imageList.add(pic);
        }
        //判断是否包含该幻灯片集，如果包含则删除，并重新添加
        if (instance.containGroup(slideInfo))
            instance.removeGroup(slideInfo);
        //更新时间
        slideInfo.updateTime = System.currentTimeMillis();
        instance.addList(slideInfo);
        instance.saveSlide();
    }

    @Override
    public void setData() {
        selectAdapter.setData(phoList);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        //选择与取消单张照片
        MediaInfo mediaInfo = (MediaInfo) parent.getItemAtPosition(position);
        if (selectedImgList.contains(mediaInfo)) {
            RecordUtils.onEvent(mContext, getString(R.string.slide_to_screen_select_cancel));
            mediaInfo.setChecked(false);
            // phoList.get(i).setChecked(false);
            selectedImgList.remove(mediaInfo);
            picCount--;
            if(isCheckAll) {
                checkAll.setText("全选");
                checkAll.setChecked(false);
                isCheckAll = false;
            }
        } else {
            if (picCount >= 50) {
                ShowMessage.showToast(mContext, "最多只能选择50张");
                return;
            }

            RecordUtils.onEvent(mContext, getString(R.string.slide_to_screen_select));
            mediaInfo.setChecked(true);
            //phoList.set(i,mediaInfo);
            selectedImgList.add(mediaInfo);
            picCount++;

            if(isCheckedAll()) {
                isCheckAll = true;
                checkAll.setChecked(true);
                checkAll.setText("取消全选");
            }
        }
        //selectAdapter.setData(phoList);
        selectAdapter.notifyDataSetChanged();
        numMark.setText("(" + picCount + "/50)");

        if(type == IntentUtil.TYPE_SLIDE_BY_LIST) {
            initCreateSlideBtn();
        }
    }

    private void initCreateSlideBtn() {
        if(picCount ==0 ) {
            switch (slideType) {
                case IMAGE:
                    add.setText(R.string.please_select_pic);
                    break;
                case VIDEO:
                    add.setText(R.string.please_select_video);
                    break;
            }

            add.setTextColor(getResources().getColor(R.color.dialog_text_black));
        }else {
            switch (slideType) {
                case IMAGE:
                    add.setText(R.string.create_slide);
                    break;

                case VIDEO:
                    add.setText(R.string.create_video);
                    break;
            }

            add.setTextColor(getResources().getColor(R.color.head_title_text));
        }
    }
}
