package com.savor.resturant.activity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.sdk.android.oss.ClientException;
import com.alibaba.sdk.android.oss.OSSClient;
import com.alibaba.sdk.android.oss.ServiceException;
import com.alibaba.sdk.android.oss.callback.OSSCompletedCallback;
import com.alibaba.sdk.android.oss.model.PutObjectRequest;
import com.alibaba.sdk.android.oss.model.PutObjectResult;
import com.bigkoo.pickerview.TimePickerView;
import com.bumptech.glide.Glide;
import com.common.api.utils.DensityUtil;
import com.common.api.utils.FileUtils;
import com.common.api.utils.LogUtils;
import com.common.api.utils.ShowMessage;
import com.google.gson.Gson;
import com.savor.resturant.R;
import com.savor.resturant.SavorApplication;
import com.savor.resturant.bean.ConAbilityList;
import com.savor.resturant.bean.ContactFormat;
import com.savor.resturant.bean.OperationFailedItem;
import com.savor.resturant.core.AppApi;
import com.savor.resturant.core.ResponseErrorMessage;
import com.savor.resturant.utils.ChineseComparator;
import com.savor.resturant.utils.ConstantValues;
import com.savor.resturant.utils.GlideCircleTransform;
import com.savor.resturant.utils.OSSClientUtil;
import com.savor.resturant.widget.ChoosePicDialog;

import net.sourceforge.pinyin4j.PinyinHelper;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 新增客户
 */
public class AddCustomerActivity extends BaseActivity implements View.OnClickListener {
    public static final int TAKE_PHOTO_REQUEST = 0x1;
    public static final int REQUEST_CODE_IMAGE = 0x2;
    private ImageView mBackBtn;
    private EditText mNameEt;
    private EditText mMobileEt;
    private EditText mSecondMobileEt;
    private LinearLayout mUploadHeaderLayout;
    private ImageView mAddBtn;
    private TextView mSaveBtn;
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
    private String faceUrl = "";
    private RadioGroup mSexRG;
    private EditText mBirthPlaceEt;
    private EditText mTicketInfoEt;
    private ContactFormat currentAddCustomer;
    private ChineseComparator pinyinComparator;

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
        mTicketInfoEt = (EditText) findViewById(R.id.et_ticket_info);
        mBirthPlaceEt = (EditText) findViewById(R.id.et_birth_place);
        mSexRG = (RadioGroup) findViewById(R.id.rg_sex);
        mTitleTv = (TextView) findViewById(R.id.tv_center);
        mRightTv = (TextView) findViewById(R.id.tv_right);
        mAbilityTv = (TextView) findViewById(R.id.tv_con_ability);
        mBirthDayTv = (TextView) findViewById(R.id.tv_birthday);
        mBirthDayLayout = (RelativeLayout) findViewById(R.id.rl_birthday);
        mBackBtn = (ImageView) findViewById(R.id.iv_left);
        mNameEt = (EditText) findViewById(R.id.et_name);
        mMobileEt = (EditText) findViewById(R.id.et_phone);
        mSecondMobileEt = (EditText) findViewById(R.id.et_second_mobile);
        mAddBtn = (ImageView) findViewById(R.id.iv_add);
        mUploadHeaderLayout = (LinearLayout) findViewById(R.id.ll_upload_header);
        mSaveBtn = (TextView) findViewById(R.id.tv_save);
        mSeconMobileLayout = (LinearLayout) findViewById(R.id.ll_second_mobile);
        mAbilityLayout = (RelativeLayout) findViewById(R.id.rl_con_ability);
        mHeaderIv = (ImageView) findViewById(R.id.iv_header);
    }

    @Override
    public void setViews() {
        pinyinComparator = new ChineseComparator();
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
                showPhotoDialog();
                break;
            case R.id.tv_right:
                Intent intent = new Intent(this,ContactCustomerListActivity.class);
                intent.putExtra("type", ContactCustomerListActivity.OperationType.CONSTACT_LIST_NOTFIST);
                startActivity(intent);
                break;
            case R.id.rl_birthday:
                showDateDialog();
                break;
            case R.id.rl_con_ability:
                // 消费能力
                showConAbilityDialog();
                break;
            case R.id.tv_save:
                // 新增客户提交
                startSubmit();
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

    private void startSubmit() {
        String name = mNameEt.getText().toString();
        String mobile = mMobileEt.getText().toString();
        String secondMobile = mSecondMobileEt.getText().toString();

        if(TextUtils.isEmpty(name)) {
            ShowMessage.showToast(this,"请输入客户名称");
            return;
        }

        if(TextUtils.isEmpty(mobile)&&TextUtils.isEmpty(secondMobile)) {
            ShowMessage.showToast(this,"请输入手机号");
            return;
        }

        List<ContactFormat> customerList = mSession.getCustomerList();
        if(customerList!=null&&customerList.size()>0) {
            for(ContactFormat contactFormat : customerList) {
                String cMobile = contactFormat.getMobile();
                String cMobile1 = contactFormat.getMobile1();
                if(!TextUtils.isEmpty(cMobile1)) {
                    if(!TextUtils.isEmpty(cMobile)) {
                        if(cMobile.equals(mobile)||cMobile.equals(secondMobile) || cMobile1.equals(mobile)||cMobile1.equals(secondMobile)) {
                            ShowMessage.showToast(this,"已存在相同手机号的客户");
                            return;
                        }
                    }else {
                        if(cMobile1.equals(mobile)||cMobile1.equals(secondMobile)) {
                            ShowMessage.showToast(this,"已存在相同手机号的客户");
                            return;
                        }
                    }

                }else {
                    if(!TextUtils.isEmpty(cMobile)) {
                        if(cMobile.equals(mobile)||cMobile.equals(secondMobile)) {
                            ShowMessage.showToast(this,"已存在相同手机号的客户");
                            return;
                        }
                    }
                }

            }
        }

        if(!TextUtils.isEmpty(currentImagePath)) {
            faceUrl = "";
            String hotel_id = mSession.getHotelBean().getHotel_id();
            File file = new File(currentImagePath);
            final String objectKey = "log/resource/restaurant/mobile/userlogo/"+hotel_id+"/"+file.getName();
            final OSSClient ossClient = OSSClientUtil.getOSSClient(this);
            // 构造上传请求
            PutObjectRequest put = new PutObjectRequest(ConstantValues.BUCKET_NAME,objectKey , currentImagePath);
            ossClient.asyncPutObject(put, new OSSCompletedCallback<PutObjectRequest, PutObjectResult>() {

                @Override
                public void onSuccess(PutObjectRequest putObjectRequest, PutObjectResult putObjectResult) {
                    faceUrl = ossClient.presignPublicObjectURL(ConstantValues.BUCKET_NAME, objectKey);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            submit();
                        }
                    });

                }

                @Override
                public void onFailure(PutObjectRequest putObjectRequest, ClientException e, ServiceException e1) {

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            submit();
                        }
                    });
                }
            });
        }else {
            submit();
        }
    }

    private void submit() {
        String name = mNameEt.getText().toString();
        String mobile = mMobileEt.getText().toString();
        String secondMobile = mSecondMobileEt.getText().toString();
        String birthPlace = mBirthPlaceEt.getText().toString();
        String birthdDay = mBirthDayTv.getText().toString();
        String ticketInfo = mTicketInfoEt.getText().toString();

        String bill_info = getFormatStr(ticketInfo);
        birthdDay = getFormatStr(birthdDay);
        birthPlace = TextUtils.isEmpty(birthPlace)?"":birthPlace;
        String consume_ability = currentConAbility == null?"":currentConAbility.getId()+"";

        String invite_id = mSession.getHotelBean().getInvite_id();
        String tel = mSession.getHotelBean().getTel();
        String remark = "";
        String sex = "";

        List<String> mobiles = new ArrayList<>();
        if(!TextUtils.isEmpty(mobile)) {
            mobiles.add(mobile);
        }
        if(!TextUtils.isEmpty(secondMobile)) {
            mobiles.add(secondMobile);
        }

        String usermobile = new Gson().toJson(mobiles);

        int checkedRadioButtonId = mSexRG.getCheckedRadioButtonId();
        switch (checkedRadioButtonId) {
            case R.id.rb_man:
                sex = "1";
                break;
            case R.id.rb_woman:
                sex = "2";
                break;
        }

        currentAddCustomer = new ContactFormat();
        currentAddCustomer.setUsermobile(usermobile);
        currentAddCustomer.setName(name);
        currentAddCustomer.setMobile(mobile);
        currentAddCustomer.setMobile1(secondMobile);
        currentAddCustomer.setBirthplace(birthPlace);
        currentAddCustomer.setBirthday(birthdDay);
        currentAddCustomer.setBill_info(bill_info);
        currentAddCustomer.setConsume_ability(consume_ability);
        currentAddCustomer.setFace_url(faceUrl);
        currentAddCustomer.setSex(TextUtils.isEmpty(sex)?0:Integer.valueOf(sex));

        StringBuilder sb = new StringBuilder();
        if(!TextUtils.isEmpty(name)) {
            name = name.trim().replaceAll(" ","");
            if(!isNumeric(name)&&!isLetter(name)) {
                for(int i = 0;i<name.length();i++) {
                    String str= removeDigital(String.valueOf(PinyinHelper.toHanyuPinyinStringArray(name.charAt(i))[0]));
                    sb.append(str);
                }
            }else {
                sb.append(name);
            }
        }
        String stuf = "";
        if(isLetter(name)||isNumeric(name)) {
            stuf = "#";
        }

        currentAddCustomer.setKey(stuf+name+"#"+sb.toString().toLowerCase()+"#"+(TextUtils.isEmpty(birthPlace)?"":birthPlace)+"#"+(TextUtils.isEmpty(mobile)?"":mobile));


        List<ContactFormat> customerList = mSession.getCustomerList();
        if(customerList.contains(currentAddCustomer)) {
            ShowMessage.showToast(this,"该客户已存在");
        }else {
            AppApi.addCustomer(this,bill_info,birthdDay,birthPlace,consume_ability,
                    faceUrl,invite_id,tel,name,remark,sex,usermobile,this);
            customerList.add(currentAddCustomer);
            Collections.sort(customerList,pinyinComparator);
            mSession.setCustomerList(customerList);
            ShowMessage.showToast(this,"添加成功");
            finish();
        }
    }

    public String getFormatStr(String str) {
        return TextUtils.isEmpty(str)?"":str;
    }

    private void showConAbilityDialog() {
        ConAbilityList conAbilityList = mSession.getConAbilityList();
        final List<ConAbilityList.ConAbility> list = conAbilityList.getList();
        conAbilityIds = getConAbilityIds(list);
        conAbilityIdsNames = getConAbilityNames(list);
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(mContext);
//        alertBuilder.setTitle("--请选择消费能力--");
        alertBuilder.setItems(conAbilityIdsNames, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int index) {
                currentConAbility = list.get(index);
                mAbilityTv.setText(currentConAbility.getName());
            }
        });
        AlertDialog finalAlertDialog = alertBuilder.create();
        float widthInPx = DensityUtil.getWidthInPx(this);
        float heightInPx = DensityUtil.getHeightInPx(this);
        WindowManager.LayoutParams attributes = finalAlertDialog.getWindow().getAttributes();
        attributes.width = (int) (widthInPx/2);
        attributes.height = (int) (heightInPx/2);
        finalAlertDialog.getWindow().setLayout((int) (widthInPx/2),(int) (heightInPx/2));
        finalAlertDialog.show();
    }

    private void showDateDialog() {
        TimePickerView timePickerView = new TimePickerView.Builder(this, new TimePickerView.OnTimeSelectListener() {
            @Override
            public void onTimeSelect(Date date, View v) {
                String birthDay = getTime(date);
                mBirthDayTv.setText(birthDay);
            }
        }).setType(new boolean[]{true, true, true, false, false, false}).isCenterLabel(false).build();
        timePickerView.show();
    }

    private void showPhotoDialog() {
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
            case POST_ADD_CUS_JSON:
                LogUtils.d("savor:add customer success");
                break;
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

    @Override
    public void onError(AppApi.Action method, Object obj) {
        switch (method) {
            case POST_ADD_CUS_JSON:
                LogUtils.d("savor:add customer failed");
                if(obj instanceof ResponseErrorMessage) {
                    ResponseErrorMessage message = (ResponseErrorMessage) obj;
                    int code = message.getCode();
                    String msg = message.getMessage();
                    if(code == 60105||code == 60106) {
//                        showToast(msg);
                    }else {
                        addOpFailedList();
                    }
                }else {
                    addOpFailedList();
                }
                break;
        }
    }

    private void addOpFailedList() {
        List<ContactFormat> contactFormats = new ArrayList<>();
        contactFormats.add(currentAddCustomer);
        List<OperationFailedItem> opFailedList = mSession.getOpFailedList();
        if(opFailedList == null) {
            opFailedList = new ArrayList<>();
        }
        OperationFailedItem item = new OperationFailedItem();
        item.setType(OperationFailedItem.OpType.TYPE_ADD_CUSTOMER);
        item.setContactFormat(contactFormats);
        opFailedList.add(item);
        mSession.setOpFailedList(opFailedList);
    }


    public boolean isNumeric(String str){
        Pattern pattern = Pattern.compile("[0-9]*");
        Matcher isNum = pattern.matcher(str);
        if( !isNum.matches() ){
            return false;
        }
        return true;
    }

    /**
     * 剔除数字
     * @param value
     */
    public String removeDigital(String value){

        Pattern p = Pattern.compile("[\\d]");
        Matcher matcher = p.matcher(value);
        String result = matcher.replaceAll("");
        return result;
    }

    /*判断字符串中是否仅包含字母数字和汉字
      *各种字符的unicode编码的范围：
     * 汉字：[0x4e00,0x9fa5]（或十进制[19968,40869]）
     * 数字：[0x30,0x39]（或十进制[48, 57]）
     *小写字母：[0x61,0x7a]（或十进制[97, 122]）
     * 大写字母：[0x41,0x5a]（或十进制[65, 90]）
*/
    public static boolean isLetter(String str) {
        String regex = "^[a-zA-Z]+$";
        return str.matches(regex);
    }
}
