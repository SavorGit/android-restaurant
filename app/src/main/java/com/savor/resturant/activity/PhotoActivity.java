package com.savor.resturant.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.PersistableBundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.savor.resturant.R;
import com.savor.resturant.adapter.PhotoAdapter;
import com.savor.resturant.bean.MediaInfo;
import com.savor.resturant.bean.PhotoInfo;
import com.savor.resturant.bean.SlideSetInfo;
import com.savor.resturant.interfaces.AsyncCallBack;
import com.savor.resturant.utils.AsyncTaskUtil;
import com.savor.resturant.utils.IntentUtil;
import com.savor.resturant.utils.MediaUtils;
import com.savor.resturant.utils.ProjectionManager;
import com.savor.resturant.utils.RecordUtils;
import com.savor.resturant.utils.SlideManager;
import com.savor.resturant.utils.ToastUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.savor.resturant.utils.IntentUtil.KEY_SLIDE;
import static com.savor.resturant.utils.IntentUtil.KEY_TYPE;
import static com.savor.resturant.utils.IntentUtil.MEDIA_TYPE;

/**
 * Created by hezd on 2016/12/10.
 */

//本地图片页面
public class PhotoActivity extends BaseActivity implements View.OnClickListener{

    private ImageView back;
    private TextView title;
    private ListView photoList;
    private List<PhotoInfo> list = new ArrayList<>();
    private HashMap<String, ArrayList<MediaInfo>> photoMap = new HashMap<>();
    private int type;
    private SlideSetInfo slideInfo;
    private SlideManager.SlideType slideType;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_list);
        Intent intent = getIntent();
        type = intent.getIntExtra(KEY_TYPE, IntentUtil.TYPE_PHOTO);
        slideInfo = (SlideSetInfo) intent.getSerializableExtra(KEY_SLIDE);
        slideType = (SlideManager.SlideType) intent.getSerializableExtra(IntentUtil.MEDIA_TYPE);
        getViews();
        setViews();
        initPhotoList();
        setListeners();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable("type",slideType);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        slideType = (SlideManager.SlideType) savedInstanceState.getSerializable("type");
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onRestoreInstanceState(savedInstanceState, persistentState);
        slideType = (SlideManager.SlideType) savedInstanceState.getSerializable("type");
    }

    @Override
    public void onBackPressed() {
//        setResult(HotspotMainActivity.FROM_APP_BACK);
        super.onBackPressed();
    }

    //点击事件
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_left:
                onBackPressed();
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        RecordUtils.onPageStart(this, getString(R.string.slide_to_screen_photo_list));
        RecordUtils.onPageStart(this, getString(R.string.picture_to_screen_photo_list));
    }

    @Override
    protected void onPause() {
        super.onPause();
        RecordUtils.onPageEndAndPause(this, this);
    }

    /**
     * 关联控件
     */
    @Override
    public void getViews() {
        back = (ImageView) findViewById(R.id.iv_left);
        title = (TextView) findViewById(R.id.tv_center);
        photoList = (ListView) findViewById(R.id.lv_photo_list);
    }

    /**
     * 初始化控件
     */
    @Override
    public void setViews() {
        switch (slideType) {
            case IMAGE:
                title.setText("照片列表");
                break;
            case VIDEO:
                title.setText("视频列表");
                break;
        }
    }

    /**
     * 设置监听事件
     */
    @Override
    public void setListeners() {
        back.setOnClickListener(this);
        photoList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (!photoMap.isEmpty()) {
                    ArrayList<MediaInfo> childList = photoMap.get(list.get(i).getFolderName());
                    if (childList.size() <= 0) {
                        ToastUtil.showToastSavor(PhotoActivity.this, "没有发现照片哦");
                        return;
                    }
                    switch (type) {
                        case IntentUtil.TYPE_PHOTO:
                            ProjectionManager.getInstance().setImgList(childList);
                            break;
                        case IntentUtil.TYPE_SLIDE_BY_LIST:
                            RecordUtils.onEvent(mContext, getString(R.string.slide_to_screen_click_album));
                            ProjectionManager.getInstance().setImgList(childList);

                            Intent intent = new Intent(PhotoActivity.this, PhotoSelectActivity.class);
                            intent.putExtra(KEY_TYPE, type);
                            intent.putExtra(KEY_SLIDE, slideInfo);
                            intent.putExtra(MEDIA_TYPE,slideType);
                            startActivity(intent);
                            break;
                        case IntentUtil.TYPE_SLIDE_BY_DETAIL:
                            ProjectionManager.getInstance().setImgList(childList);

                            Intent dintent = new Intent(PhotoActivity.this, PhotoSelectActivity.class);
                            dintent.putExtra(KEY_TYPE, type);
                            dintent.putExtra(KEY_SLIDE, slideInfo);
                            dintent.putExtra(IntentUtil.MEDIA_TYPE,slideType);
//        intent.putStringArrayListExtra(KEY_PHOTO_LIST, picList);
                            startActivity(dintent);
                            break;
                    }

                }
            }
        });
    }

    /**
     * 获取手机内所有图片路径
     */
    private void initPhotoList() {

        AsyncTaskUtil.doAsync(new AsyncCallBack() {

            ProgressDialog progressDialog;

            @Override
            public void onPreExecute() {
                super.onPreExecute();
                if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                    ToastUtil.showToastSavor(PhotoActivity.this, "没有发现存储设备");
                    return;
                }
                progressDialog = ProgressDialog.show(PhotoActivity.this, null, "正在加载...");
            }

            @Override
            public void doInBackground() {
                switch (slideType) {
                    case IMAGE:
                        MediaUtils.getImgInfo(PhotoActivity.this, photoMap);
                        break;
                    case VIDEO:
                        MediaUtils.getVideoInfo(PhotoActivity.this,photoMap);
                        break;
                }

            }

            @Override
            public void onPostExecute() {
                progressDialog.dismiss();
                list = MediaUtils.subGroupOfImage(photoMap);
                photoList.setAdapter(new PhotoAdapter(PhotoActivity.this, list));
            }
        });
    }

}
