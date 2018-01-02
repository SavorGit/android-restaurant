package com.savor.resturant.activity;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.sdk.android.oss.ClientException;
import com.alibaba.sdk.android.oss.OSSClient;
import com.alibaba.sdk.android.oss.ServiceException;
import com.alibaba.sdk.android.oss.callback.OSSCompletedCallback;
import com.alibaba.sdk.android.oss.model.PutObjectRequest;
import com.alibaba.sdk.android.oss.model.PutObjectResult;
import com.bumptech.glide.Glide;
import com.common.api.utils.AppUtils;
import com.common.api.utils.FileUtils;
import com.common.api.utils.ShowMessage;
import com.google.gson.Gson;
import com.savor.resturant.R;
import com.savor.resturant.SavorApplication;
import com.savor.resturant.bean.AddSpendTicketNoBookInfo;
import com.savor.resturant.bean.ContactFormat;
import com.savor.resturant.bean.Customer;
import com.savor.resturant.bean.CustomerBean;
import com.savor.resturant.bean.CustomerLabel;
import com.savor.resturant.bean.CustomerLabelList;
import com.savor.resturant.core.AppApi;
import com.savor.resturant.utils.ChineseComparator;
import com.savor.resturant.utils.ConstantValues;
import com.savor.resturant.utils.OSSClientUtil;
import com.savor.resturant.widget.ChoosePicDialog;
import com.zhy.view.flowlayout.FlowLayout;
import com.zhy.view.flowlayout.TagAdapter;
import com.zhy.view.flowlayout.TagFlowLayout;

import net.sourceforge.pinyin4j.PinyinHelper;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.savor.resturant.activity.AddCustomerActivity.REQUEST_CODE_IMAGE;
import static com.savor.resturant.activity.AddCustomerActivity.TAKE_PHOTO_REQUEST;
import static com.savor.resturant.activity.ContactCustomerListActivity.REQUEST_CODE_SELECT;

public class SpendHistoryAddActivity extends BaseActivity implements View.OnClickListener {

    private static final int REQUEST_CODE_LABEL = 100;
    private TagFlowLayout mLabelsRlv;
    private TextView mLabelHint;
    private ImageView mBackBtn;
    private TextView mTitleTv;
    private EditText mNameEt;
    private EditText mMobileEt;
//    private FlowAdapter mLabelAdapter;
    private TextView mEditLabelTv;
    private String customer_id;
    private LinearLayout mSelectCusLayout;
    private List<CustomerLabel> labelList = new ArrayList<>();;
    private TagAdapter mTagAdapter = new  TagAdapter(labelList) {
        @Override
        public View getView(FlowLayout parent, int position, Object o) {
            TextView tv = (TextView) getLayoutInflater().inflate(R.layout.flow_item,
                    mLabelsRlv, false);
            tv.setBackgroundResource(R.drawable.label_bg);
            tv.setTextColor(mContext.getResources().getColor(R.color.color_label));
            tv.setText(labelList.get(position).getLabel_name());
            return tv;
        }
    };
    private String currentImagePath;
    private ImageView mSpendHistoryIv;
    private TextView mTicketTv;
    private TextView mSaveTv;
    private String ticketOssUrl;
    private ContactFormat existContact;
    private ChineseComparator pinyinComparator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spend_history_add);

        getViews();
        setViews();
        setListeners();
    }

    @Override
    public void getViews() {
        pinyinComparator = new ChineseComparator();

        mSaveTv = (TextView) findViewById(R.id.tv_save);
        mTicketTv = (TextView) findViewById(R.id.tv_add_ticket);
        mSpendHistoryIv = (ImageView) findViewById(R.id.iv_spend_history);
        mSelectCusLayout = (LinearLayout) findViewById(R.id.ll_customer_select);
        mEditLabelTv = (TextView) findViewById(R.id.tv_edit_label);
        mNameEt = (EditText) findViewById(R.id.et_name);
        mMobileEt = (EditText) findViewById(R.id.et_phone);
        mBackBtn = (ImageView) findViewById(R.id.iv_left);
        mLabelHint = (TextView) findViewById(R.id.tv_label_hint);
        mTitleTv = (TextView) findViewById(R.id.tv_center);

        mLabelsRlv = (TagFlowLayout) findViewById(R.id.rlv_labels);

    }

    @Override
    public void setViews() {
        mSaveTv.setOnClickListener(this);
        mTicketTv.setOnClickListener(this);
        mTitleTv.setTextColor(getResources().getColor(R.color.white));
        mTitleTv.setText("添加消费记录");

//        FlowLayoutManager flowLayoutManager = new FlowLayoutManager();
//        mLabelsRlv.addItemDecoration(new SpaceItemDecoration(DensityUtil.dip2px(this,5),DensityUtil.dip2px(this,10)));
//        mLabelsRlv.setLayoutManager(flowLayoutManager);
        mLabelsRlv.setAdapter(mTagAdapter);

//        testLabel();

    }

    private void testLabel() {
        showLabel();
        List<CustomerLabel> labelList = new ArrayList<>();

        final CustomerLabel label = new CustomerLabel();
        label.setLabel_id("1");
        label.setLabel_name("口味偏辣");
        label.setLight(0);
        labelList.add(label);

        final CustomerLabel label2 = new CustomerLabel();
        label2.setLabel_id("2");
        label2.setLabel_name("爱吃海鲜");
        label2.setLight(0);
        labelList.add(label2);

        final CustomerLabel label3 = new CustomerLabel();
        label3.setLabel_id("3");
        label3.setLabel_name("喜欢热闹");
        label3.setLight(0);
        labelList.add(label3);

        CustomerLabel label4 = new CustomerLabel();
        label4.setLabel_id("4");
        label4.setLabel_name("不要香菜");
        label4.setLight(0);
        labelList.add(label4);

        CustomerLabel label5 = new CustomerLabel();
        label5.setLabel_id("5");
        label5.setLabel_name("好面子");
        label5.setLight(0);
        labelList.add(label5);

        CustomerLabel label6 = new CustomerLabel();
        label6.setLabel_id("6");
        label6.setLabel_name("喜欢推荐菜");
        label6.setLight(0);
        labelList.add(label6);
        this.labelList.addAll(labelList);

        mTagAdapter.notifyDataChanged();

//        mLabelsRlv.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                List<CustomerLabel> labelList = new ArrayList<>();
//                labelList.add(label);
//                labelList.add(label2);
//                labelList.add(label3);
//                mLabelAdapter.setData(labelList);
//            }
//        },2000);
    }

    @Override
    public void setListeners() {
        mSelectCusLayout.setOnClickListener(this);
        mEditLabelTv.setOnClickListener(this);
        mBackBtn.setOnClickListener(this);

        mMobileEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String content = mMobileEt.getText().toString();
                if(!TextUtils.isEmpty(content)&&content.length()==11) {
                    String regex = "^1[34578]\\d{9}$";
                    boolean matches = content.matches(regex);
                    if(matches) {
                        // 1.从客户列表查找是否存在这个用户如果存在，判断customerid是否为空
                        existContact = getMobileInCustomerList(content);
                        String invite_id = mSession.getHotelBean().getInvite_id();
                        customer_id = "";
                        if(existContact !=null) {// 已存在
                            String customerId = existContact.getCustomer_id();
                            String name = existContact.getName();
                            mNameEt.setText(name);
                            if(!TextUtils.isEmpty(customerId)) {
                                customer_id = customerId;
                                AppApi.getCustomerLabelList(SpendHistoryAddActivity.this, customer_id,invite_id,mSession.getHotelBean().getTel(),SpendHistoryAddActivity.this);
                            }
                        }

                    }else {
                        mMobileEt.setText("");
                        ShowMessage.showToast(SpendHistoryAddActivity.this,"请输入正确的手机号");
                    }
                }
            }
        });
    }

    private ContactFormat getMobileInCustomerList(String content) {
        List<ContactFormat> customerList = mSession.getCustomerList();
        if(customerList!=null&&customerList.size()>0) {
            for(ContactFormat contactFormat:customerList) {
                if(content.equals(contactFormat.getMobile())||content.equals(contactFormat.getMobile1())) {
                    return contactFormat;
                }
            }
        }
        return null;
    }

    public void showLabel() {
        mLabelHint.setVisibility(View.GONE);
        mLabelsRlv.setVisibility(View.VISIBLE);
    }

    public void hideLabel() {
        mLabelsRlv.setVisibility(View.GONE);
        mLabelHint.setVisibility(View.VISIBLE);
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
                SpendHistoryAddActivity.this.startActivityForResult(intent, TAKE_PHOTO_REQUEST);
            }
        },
                new ChoosePicDialog.OnAlbumBtnClickListener() {
                    @Override
                    public void onAlbumBtnClick() {
                        Intent intent = new Intent(Intent.ACTION_PICK,
                                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        SpendHistoryAddActivity.this.startActivityForResult(intent, REQUEST_CODE_IMAGE);
                    }
                }
        ).show();
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.tv_save:
                submit();
                break;
            case R.id.tv_add_ticket:
                showPhotoDialog();
                break;
            case R.id.ll_customer_select:
                intent = new Intent(this,ContactCustomerListActivity.class);
                intent.putExtra("type", ContactCustomerListActivity.OperationType.CONSTACT_LIST_SELECT);
                startActivityForResult(intent,REQUEST_CODE_SELECT);
                break;
            case R.id.tv_edit_label:
                String mobile = mMobileEt.getText().toString();
                if(TextUtils.isEmpty(mobile)) {
                    ShowMessage.showToast(this,"请输入客户手机号");
                }else {
                    intent = new Intent(this,LabelAddActivity.class);
                    intent.putExtra("customer_id",customer_id);
                    startActivityForResult(intent,REQUEST_CODE_LABEL);
                }
                break;
            case R.id.iv_left:
                finish();
                break;
        }
    }

    private void submit() {

        final String usermobile = mMobileEt.getText().toString();

        if(TextUtils.isEmpty(mNameEt.getText().toString())) {
            ShowMessage.showToast(this,"请输入客户姓名");
            return;
        }

        if(TextUtils.isEmpty(usermobile)) {
            ShowMessage.showToast(this,"请输入客户手机号");
            return;
        }

        File file = new File(currentImagePath);
        String hotel_id = mSession.getHotelBean().getHotel_id();
        final String objectKey = "log/resource/restaurant/mobile/userlogo/"+hotel_id+"/"+file.getName();
        final OSSClient ossClient = OSSClientUtil.getOSSClient(this);
        // 构造上传请求
        PutObjectRequest put = new PutObjectRequest(ConstantValues.BUCKET_NAME,objectKey , currentImagePath);
        ossClient.asyncPutObject(put, new OSSCompletedCallback<PutObjectRequest, PutObjectResult>() {

            @Override
            public void onSuccess(PutObjectRequest putObjectRequest, PutObjectResult putObjectResult) {
                ticketOssUrl = ossClient.presignPublicObjectURL(ConstantValues.BUCKET_NAME, objectKey);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        String name = mNameEt.getText().toString();
                        String invite_id = mSession.getHotelBean().getInvite_id();
                        String mobile = mSession.getHotelBean().getTel();
                        String recipt = "";
                        List<String> urlList = new ArrayList<>();
                        urlList.add(ticketOssUrl);

                        recipt = new Gson().toJson(urlList);

                        if(TextUtils.isEmpty(customer_id)) {
                            // 如果客户id为空需要传客户信息bill_info，birthday，birthplace，consume_ability
                            String bill_info = "";
                            String birthday = "";
                            String birthplace = "";
                            String consume_ability = "";
                            String face_url = "";
                            String lable_id_str = "";
                            String remark = "";
                            String sex = "";
                            if(existContact!=null) {
                                bill_info = existContact.getBill_info();
                                birthday = existContact.getBirthday();
                                birthplace = existContact.getBirthplace();
                                consume_ability = existContact.getConsume_ability();
                                face_url = existContact.getFace_url();
                                sex = String.valueOf(existContact.getSex());
                            }else {
                                List<ContactFormat> customerList = mSession.getCustomerList();
                                if(customerList == null) {
                                    customerList = new ArrayList<>();
                                }
                                ContactFormat contactFormat = new ContactFormat();
                                contactFormat.setMobile(usermobile);
                                contactFormat.setName(name);


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
                                customerList.add(contactFormat);
                                contactFormat.setKey(stuf+name+"#"+sb.toString().toLowerCase()+"#"+(TextUtils.isEmpty(birthplace)?"":birthplace)+"#"+(TextUtils.isEmpty(mobile)?"":mobile));
                                Collections.sort(customerList,pinyinComparator);
                                mSession.setCustomerList(customerList);
                            }

                            List<String> labeIds = new ArrayList<>();
                            if(labelList.size()>0) {
                                for(int i = 0;i<labelList.size();i++) {
                                    CustomerLabel label = labelList.get(i);
                                    String label_id = label.getLabel_id();
                                    labeIds.add(label_id);
                                }
                                lable_id_str = new Gson().toJson(labeIds);
                            }


                            AppApi.addSignleConsumeRecord(SpendHistoryAddActivity.this,
                                    bill_info,birthday,birthplace,consume_ability,"",
                                    face_url,invite_id,lable_id_str,mobile,name, recipt,usermobile,
                                    "",sex,SpendHistoryAddActivity.this);
                        }else {
                            String lable_id_str = "";
                            List<String> labeIds = new ArrayList<>();
                            if(labelList.size()>0) {
                                for(int i = 0;i<labelList.size();i++) {
                                    CustomerLabel label = labelList.get(i);
                                    String label_id = label.getLabel_id();
                                    labeIds.add(label_id);
                                }
                                lable_id_str = new Gson().toJson(labeIds);
                            }
                            // 如果客户id不为空 不需要传客户信息
                            AppApi.addSignleConsumeRecord(SpendHistoryAddActivity.this,
                                    "","","","",customer_id,
                                    "",invite_id,lable_id_str,mobile,name, recipt,usermobile,
                                    "","",SpendHistoryAddActivity.this);
                        }
                    }
                });

            }

            @Override
            public void onFailure(PutObjectRequest putObjectRequest, ClientException e, ServiceException e1) {

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ShowMessage.showToast(SpendHistoryAddActivity.this,"小票上传失败");
                    }
                });
            }
        });


    }

    @Override
    public void onSuccess(AppApi.Action method, Object obj) {
        super.onSuccess(method, obj);
        AppUtils.hideSoftKeybord(this);
        switch (method) {
            case POST_ADD_SIGNLE_CONSUME_RECORD_JSON:
                if(obj instanceof AddSpendTicketNoBookInfo) {
                    AddSpendTicketNoBookInfo spendTicketNoBookInfo = (AddSpendTicketNoBookInfo) obj;
                    AddSpendTicketNoBookInfo.ListBean list = spendTicketNoBookInfo.getList();
                    if(list!=null) {
                        String customer_id = list.getCustomer_id();
                        String mobile = mMobileEt.getText().toString();
                        ContactFormat mobileInCustomerList = getMobileInCustomerList(mobile);
                        if(!TextUtils.isEmpty(customer_id)&&existContact == null) {
                            mobileInCustomerList.setCustomer_id(customer_id);
                            List<ContactFormat> customerList = mSession.getCustomerList();
                            mSession.setCustomerList(customerList);
                        }
                    }

                    ShowMessage.showToast(this,"添加成功");
                    finish();
                }
                break;
            case POST_CUSTOMER_LABELS_JSON:
                if(obj instanceof CustomerLabelList) {
                    CustomerLabelList customerBean = (CustomerLabelList) obj;

                    if(customerBean!=null) {
                        List<CustomerLabel> list = customerBean.getList();
                            if(list!=null&&list.size()>0) {
                                labelList.clear();
                                labelList.addAll(list);
                                mTagAdapter.notifyDataChanged();
                                showLabel();
                        }

                    }
                }
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_CODE_SELECT) {
            if(data!=null) {
                ContactFormat contactFormat = (ContactFormat) data.getSerializableExtra("customer");
                mMobileEt.setText(contactFormat.getMobile());
            }
        }else if(requestCode == REQUEST_CODE_LABEL) {
            if(data!=null) {
                ArrayList<CustomerLabel> labelList = (ArrayList<CustomerLabel>) data.getSerializableExtra("selecteLabels");
                if(labelList!=null&&labelList.size()>0) {
                    this.labelList.clear();
                    this.labelList.addAll(labelList);
                    mTagAdapter.notifyDataChanged();
                    showLabel();
                }else {
                    hideLabel();
                }
            }
        }else if (requestCode == TAKE_PHOTO_REQUEST && resultCode == Activity.RESULT_OK) {
            // 拍照
            Glide.with(this).load(currentImagePath).placeholder(R.drawable.empty_slide).into(mSpendHistoryIv);
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

            Glide.with(this).load(currentImagePath).placeholder(R.drawable.empty_slide).into(mSpendHistoryIv);
        }
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
