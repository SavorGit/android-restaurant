package com.savor.resturant.activity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bigkoo.pickerview.TimePickerView;
import com.bumptech.glide.Glide;
import com.common.api.utils.FileUtils;
import com.savor.resturant.R;
import com.savor.resturant.SavorApplication;
import com.savor.resturant.bean.ConAbilityList;
import com.savor.resturant.core.AppApi;
import com.savor.resturant.utils.GlideCircleTransform;
import com.savor.resturant.widget.ChoosePicDialog;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * 新增客户
 */
public class AddCustomerActivity extends BaseActivity implements View.OnClickListener {
    public static final int TAKE_PHOTO_REQUEST = 0x1;
    public static final int REQUEST_CODE_IMAGE = 0x2;
    private ImageView mBackBtn;
    private EditText mNameEt;
    private EditText mPhontEt;
    private EditText mSecondMobileEt;
    private LinearLayout mUploadHeaderLayout;
    private ImageView mAddBtn;
    private Button mSaveBtn;
    private LinearLayout mSeconMobileLayout;
    private RelativeLayout mAbilityLayout;
    /**消费能力Id列表*/
    private String[] conAbilityIds;
    private String[] conAbilityIdsNames;
    /**当前选择的消费能力*/
    private ConAbilityList.ConAbility currentConAbility;
    private RelativeLayout mBirthDayLayout;
    private TextView mBirthDayTv;
    private TextView mAbilityTv;
    private TextView mTitleTv;
    private TextView mRightTv;
    /**当前选择要上传的头像*/
    private String currentImagePath;
    private ImageView mHeaderIv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_customer);

        getViews();
        setViews();
        setListeners();
        getConAbility();
    }

    /**
     * 获取消费能力列表
     */
    private void getConAbility() {
        AppApi.getConAbilityList(this,this);
    }

    @Override
    public void getViews() {
        mTitleTv = (TextView) findViewById(R.id.tv_center);
        mRightTv = (TextView) findViewById(R.id.tv_right);
        mAbilityTv = (TextView) findViewById(R.id.tv_con_ability);
        mBirthDayTv = (TextView) findViewById(R.id.tv_birthday);
        mBirthDayLayout = (RelativeLayout) findViewById(R.id.rl_birthday);
        mBackBtn = (ImageView) findViewById(R.id.iv_left);
        mNameEt = (EditText) findViewById(R.id.et_name);
        mPhontEt = (EditText) findViewById(R.id.et_phone);
        mSecondMobileEt = (EditText) findViewById(R.id.et_second_mobile);
        mAddBtn = (ImageView) findViewById(R.id.iv_add);
        mUploadHeaderLayout = (LinearLayout) findViewById(R.id.ll_upload_header);
        mSaveBtn = (Button) findViewById(R.id.btn_save);
        mSeconMobileLayout = (LinearLayout) findViewById(R.id.ll_second_mobile);
        mAbilityLayout = (RelativeLayout) findViewById(R.id.rl_con_ability);
        mHeaderIv = (ImageView) findViewById(R.id.iv_header);
    }

    @Override
    public void setViews() {
        mTitleTv.setText("新增客户");
        mTitleTv.setTextColor(getResources().getColor(R.color.white));
        mRightTv.setVisibility(View.VISIBLE);
        mRightTv.setText("导入通讯录");
    }

    @Override
    public void setListeners() {
        mRightTv.setOnClickListener(this);
        mBirthDayLayout.setOnClickListener(this);
        mAbilityLayout.setOnClickListener(this);
        mBackBtn.setOnClickListener(this);
        mAddBtn.setOnClickListener(this);
        mUploadHeaderLayout.setOnClickListener(this);
        mSaveBtn.setOnClickListener(this);
    }

    private String getTime(Date date) {//可根据需要自行截取数据显示
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        return format.format(date);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_upload_header:
                final String tel = mSession.getHotelBean().getTel();
                new ChoosePicDialog(this, new ChoosePicDialog.OnTakePhotoBtnClickListener() {
                    @Override
                    public void onTakePhotoClick() {

                        String cacheDir = ((SavorApplication) getApplication()).imagePath;
                        File cachePath = new File(cacheDir);
                        if(!cachePath.exists()) {
                            cachePath.mkdirs();
                        }
                        currentImagePath = cacheDir+ File.separator+tel+"_"+System.currentTimeMillis()+".jpg";
                        File file = new File(currentImagePath);
                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        Uri imageUri = Uri.fromFile(file);
                        intent.putExtra(MediaStore.EXTRA_OUTPUT,imageUri);
                        AddCustomerActivity.this.startActivityForResult(intent, TAKE_PHOTO_REQUEST);
                    }
                },
                        new ChoosePicDialog.OnAlbumBtnClickListener() {
                            @Override
                            public void onAlbumBtnClick() {
                                Intent intent = new Intent(Intent.ACTION_PICK,
                                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                                AddCustomerActivity.this.startActivityForResult(intent, REQUEST_CODE_IMAGE);
                            }
                        }
                ).show();
                break;
            case R.id.tv_right:
                Intent intent = new Intent(this,ContactCustomerListActivity.class);
                intent.putExtra("type", ContactCustomerListActivity.OperationType.CONSTACT_LIST_NOTFIST);
                startActivity(intent);
                break;
            case R.id.rl_birthday:
                TimePickerView timePickerView = new TimePickerView.Builder(this, new TimePickerView.OnTimeSelectListener() {
                    @Override
                    public void onTimeSelect(Date date, View v) {
                        String birthDay = getTime(date);
                        mBirthDayTv.setText(birthDay);
                    }
                }).setType(new boolean[]{true, true, true, false, false, false}).isCenterLabel(false).build();
                timePickerView.show();
                break;
            case R.id.rl_con_ability:
                // 消费能力
                ConAbilityList conAbilityList = mSession.getConAbilityList();
                final List<ConAbilityList.ConAbility> list = conAbilityList.getList();
                conAbilityIds = getConAbilityIds(list);
                conAbilityIdsNames = getConAbilityNames(list);
                AlertDialog.Builder alertBuilder = new AlertDialog.Builder(mContext);
                alertBuilder.setTitle("--请选择消费能力--");
                alertBuilder.setItems(conAbilityIdsNames, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int index) {
                        currentConAbility = list.get(index);
                        mAbilityTv.setText(currentConAbility.getName());
                    }
                });
                AlertDialog finalAlertDialog = alertBuilder.create();
                finalAlertDialog.show();
                break;
            case R.id.btn_save:
                // 新增客户提交

                break;
            case R.id.iv_add:
                mAddBtn.setVisibility(View.GONE);
                mSeconMobileLayout.setVisibility(View.VISIBLE);
                break;
            case R.id.iv_left:
                finish();
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == TAKE_PHOTO_REQUEST && resultCode == Activity.RESULT_OK) {
            // 拍照
            Glide.with(this).load(currentImagePath).transform(new GlideCircleTransform(this)).into(mHeaderIv);
        }else   if (requestCode == REQUEST_CODE_IMAGE&&resultCode == Activity.RESULT_OK && data != null) {
            // 从相册选择
            Uri selectedImage = data.getData();
            String[] filePathColumns = {MediaStore.Images.Media.DATA};
            Cursor c = getContentResolver().query(selectedImage, filePathColumns, null, null, null);
            c.moveToFirst();
            int columnIndex = c.getColumnIndex(filePathColumns[0]);
            String imagePath = c.getString(columnIndex);

            String cachePath = ((SavorApplication)mContext.getApplication()).imagePath;
            File dir = new File(cachePath);
            if(!dir.exists()) {
                dir.mkdirs();
            }

            long timeMillis = System.currentTimeMillis();
            String tel = mSession.getHotelBean().getTel();
            String key = tel+"_"+timeMillis+".jpg";
            String copyPath = dir.getAbsolutePath()+File.separator+key;

            File sFile = new File(imagePath);
            FileUtils.copyFile(sFile, copyPath);

            currentImagePath = copyPath;

            Glide.with(this).load(currentImagePath).transform(new GlideCircleTransform(this)).into(mHeaderIv);
        }
    }

    private String[] getConAbilityNames(List<ConAbilityList.ConAbility> list) {
        String[] names = new String[list.size()];
        for(int i = 0;i<list.size();i++) {
            ConAbilityList.ConAbility conAbility = list.get(i);
            names[i] = conAbility.getName();
        }
        return names;
    }

    private String[] getConAbilityIds(List<ConAbilityList.ConAbility> list) {
        String[] ids = new String[list.size()];
        for(int i = 0;i<list.size();i++) {
            ConAbilityList.ConAbility conAbility = list.get(i);
            ids[i] = conAbility.getId()+"";
        }
        return ids;
    }

    @Override
    public void onSuccess(AppApi.Action method, Object obj) {
        super.onSuccess(method, obj);
        switch (method) {
            case POST_CON_ABILITY_JSON:
                if(obj instanceof ConAbilityList) {
                    ConAbilityList conAbilityList = (ConAbilityList) obj;
                    List<ConAbilityList.ConAbility> list = conAbilityList.getList();
                    if(list!=null&&list.size()>0) {
                        mSession.setConAbilityList(conAbilityList);
                    }
                }
                break;
        }
    }
}
