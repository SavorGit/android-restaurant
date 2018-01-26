package com.savor.resturant.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
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
import com.common.api.utils.FileUtils;
import com.common.api.utils.ShowMessage;
import com.common.api.widget.pulltorefresh.library.PullToRefreshBase;
import com.common.api.widget.pulltorefresh.library.PullToRefreshListView;
import com.google.gson.Gson;
import com.savor.resturant.R;
import com.savor.resturant.SavorApplication;
import com.savor.resturant.adapter.TicketAdapter;
import com.savor.resturant.bean.AddCustomerResponse;
import com.savor.resturant.bean.ConRecBean;
import com.savor.resturant.bean.ContactFormat;
import com.savor.resturant.bean.Customer;
import com.savor.resturant.bean.CustomerBean;
import com.savor.resturant.bean.CustomerLabel;
import com.savor.resturant.bean.CustomerListBean;
import com.savor.resturant.bean.HotelBean;
import com.savor.resturant.bean.OrderListBean;
import com.savor.resturant.bean.RecTopList;
import com.savor.resturant.core.AppApi;
import com.savor.resturant.core.ResponseErrorMessage;
import com.savor.resturant.interfaces.SetLabel;
import com.savor.resturant.utils.ChineseComparator;
import com.savor.resturant.utils.ConstantValues;
import com.savor.resturant.utils.GlideCircleTransform;
import com.savor.resturant.utils.OSSClientUtil;
import com.savor.resturant.widget.ChoosePicDialog;
import com.savor.resturant.widget.LoadingDialog;
import com.zhy.view.flowlayout.FlowLayout;
import com.zhy.view.flowlayout.TagAdapter;
import com.zhy.view.flowlayout.TagFlowLayout;

import net.sourceforge.pinyin4j.PinyinHelper;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static com.savor.resturant.activity.AddCustomerActivity.REQUEST_CODE_IMAGE;
import static com.savor.resturant.activity.AddCustomerActivity.TAKE_PHOTO_REQUEST;
import static com.savor.resturant.activity.ContactCustomerListActivity.REQUEST_CODE_SELECT;


/**
 * 客户信息
 */
public class UserInfoActivity extends BaseActivity implements View.OnClickListener {

    private Context context;
    private ImageView iv_left;
    private TextView tv_center;
    private ImageView iv_header;
    private ImageView sex;
    private TextView name;
    private TextView tel;
    private TextView consume_ability;
    private TextView birthday;
    private TextView birthplace;
    private TextView tv_edit_label;
    private TagFlowLayout rlv_labels;
    private TextView tv_label_hint;
    private TextView edit_label;
    private TextView remark;
    private TextView edit_label_remark;
    private TextView tv_add_ticket;
    private TextView la_a;
    private TextView invoice;
    private String customer_id;
    private CustomerBean customerBean;
    private String usernameStr;
    private String usermobileStr;
    private String usermobile1Str;
    private String sexStr;
    private String birthdayStr;
    private String birthplaceStr;
    private String face_urlStr;
    private String consume_abilityStr;
    private String remarkStr;
    private List<CustomerLabel> labelList = new ArrayList<>();
    ;
    private String currentImagePath;
    private String ticketOssUrl;
    private TagAdapter mTagAdapter = new TagAdapter(labelList) {
        @Override
        public View getView(FlowLayout parent, int position, Object o) {
            TextView tv = (TextView) getLayoutInflater().inflate(R.layout.flow_item,
                    rlv_labels, false);
            tv.setBackgroundResource(R.drawable.label_bg);
            tv.setTextColor(mContext.getResources().getColor(R.color.color_label));
            tv.setText(labelList.get(position).getLabel_name());
            return tv;
        }
    };

    private static final int REQUEST_CODE_LABEL = 100;
    private static final int REQUEST_CODE_REMARK = 108;
    private static final int REQUEST_CODE_USERINFO = 109;
    private PullToRefreshListView refreshListView;
    private TicketAdapter ticketAdapter;
    final List<ConRecBean> imageList = new ArrayList<>();
    private String max_id = "0";
    private String min_id = "0";
    private String Rectype = "1";
    private RecTopList recTopList;
    private List<ConRecBean> TopList = new ArrayList<ConRecBean>();
    private ContactFormat contactFormat;
    private ChineseComparator pinyinComparator;
    private LoadingDialog loadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info_layout);
        getDate();
        context = this;
        getViews();
        setViews();
        setListeners();
        getData();

    }

    private void getData() {
        if(!TextUtils.isEmpty(customer_id)) {
            getCustomerBaseInfo();
            getConRecTopList();
        }else if(contactFormat!=null){
            String mobile = contactFormat.getMobile();
            ContactFormat cusInMemory = getCusInMemory(mobile);
            if(cusInMemory!=null&&!TextUtils.isEmpty(cusInMemory.getCustomer_id())) {
                contactFormat.setCustomer_id(cusInMemory.getCustomer_id());
                CustomerListBean customerListBean = mSession.getCustomerList();
                if(customerListBean!=null) {
                    List<ContactFormat> customerList = customerListBean.getCustomerList();
                    if(customerList!=null) {
                        int indexOf = customerList.indexOf(contactFormat);
                        if(indexOf!=-1) {
                            ContactFormat contactFormat = customerList.get(indexOf);
                            contactFormat.setCustomer_id(cusInMemory.getCustomer_id());
                            mSession.setCustomerList(customerListBean);
                        }
                    }
                }
                customer_id = cusInMemory.getCustomer_id();
                getCustomerBaseInfo();
                getConRecTopList();
            }else {
                String invite_id = mSession.getHotelBean().getInvite_id();
                String tel = mSession.getHotelBean().getTel();
                String name = contactFormat.getName();
                List<String> mobiles = new ArrayList<>();
                mobiles.add(mobile);
                String phone = new Gson().toJson(mobiles);
                AppApi.addCustomer(this,"","","","","",invite_id,tel,name,""
                        ,"",phone,this);
            }


        }
    }

    private ContactFormat getCusInMemory(String mobile) {
        CustomerListBean cusListInMemory = mSession.getCusListInMemory();
        if(cusListInMemory!=null) {
            List<ContactFormat> customerList = cusListInMemory.getCustomerList();
            if(customerList!=null) {
                for(ContactFormat contactFormat :customerList) {
                    if(mobile.equals(contactFormat.getMobile1())||mobile.equals(contactFormat.getMobile())) {
                        return contactFormat;
                    }
                }
            }
        }

        return null;
    }

    private void getDate() {
        Intent intent = getIntent();
        if (intent != null) {
            customer_id = intent.getStringExtra("customerID");
            contactFormat = (ContactFormat) intent.getSerializableExtra("customer");
        }
    }

    @Override
    public void getViews() {
        pinyinComparator = new ChineseComparator();

        iv_left = (ImageView) findViewById(R.id.iv_left);
        tv_center = (TextView) findViewById(R.id.tv_center);

        refreshListView = (PullToRefreshListView) findViewById(R.id.listview);

        initHeaderView();

    }

    private void initHeaderView() {
        View headerView = View.inflate(this, R.layout.header_view_user_info, null);
        iv_header = (ImageView) headerView.findViewById(R.id.iv_header);

        name = (TextView) headerView.findViewById(R.id.name);
        consume_ability = (TextView) headerView.findViewById(R.id.consume_ability);
        birthday = (TextView) headerView.findViewById(R.id.birthday);
        birthplace = (TextView) headerView.findViewById(R.id.birthplace);
        tv_edit_label = (TextView) headerView.findViewById(R.id.tv_edit_label);
        tv_label_hint = (TextView) headerView.findViewById(R.id.tv_label_hint);
        edit_label = (TextView) headerView.findViewById(R.id.edit_label);
        remark = (TextView) headerView.findViewById(R.id.remark);
        invoice = (TextView) headerView.findViewById(R.id.invoice);
        edit_label_remark = (TextView) headerView.findViewById(R.id.edit_label_remark);
        tv_add_ticket = (TextView) headerView.findViewById(R.id.tv_add_ticket);
        sex = (ImageView) headerView.findViewById(R.id.sex);
        tel = (TextView) headerView.findViewById(R.id.tel);
        la_a = (TextView) headerView.findViewById(R.id.la_a);
        rlv_labels = (TagFlowLayout) headerView.findViewById(R.id.rlv_labels);
        refreshListView.getRefreshableView().addHeaderView(headerView);
        ticketAdapter = new TicketAdapter(this);
        refreshListView.setAdapter(ticketAdapter);
    }

    @Override
    public void setViews() {
        tv_center.setText("详细资料");
        tv_center.setTextColor(getResources().getColor(R.color.color_f6f2ed));
        rlv_labels.setAdapter(mTagAdapter);

//        for(int i = 0;i<5;i++) {
//            imageList.add("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1515744348&di=df7a8a21f2dc6dca3b840939a1a9da98&imgtype=jpg&er=1&src=http%3A%2F%2Ff.hiphotos.baidu.com%2Fzhidao%2Fpic%2Fitem%2Fa50f4bfbfbedab64fe858d27f536afc378311e66.jpg");
//        }

        ticketAdapter.setData(imageList);
        refreshListView.onLoadComplete(true);
        refreshListView.postDelayed(new Runnable() {
            @Override
            public void run() {
                refreshListView.onLoadComplete(false);
//                for(int i = 0;i<5;i++) {
//                    imageList.add("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1515744348&di=df7a8a21f2dc6dca3b840939a1a9da98&imgtype=jpg&er=1&src=http%3A%2F%2Ff.hiphotos.baidu.com%2Fzhidao%2Fpic%2Fitem%2Fa50f4bfbfbedab64fe858d27f536afc378311e66.jpg");
//                }
                ticketAdapter.notifyDataSetChanged();
            }
        }, 3000);
    }

    @Override
    public void setListeners() {

        iv_left.setOnClickListener(this);
        tv_center.setOnClickListener(this);
        edit_label.setOnClickListener(this);
        edit_label_remark.setOnClickListener(this);
        tv_add_ticket.setOnClickListener(this);
        tv_edit_label.setOnClickListener(this);
        refreshListView.setOnLastItemVisibleListener(onLastItemVisibleListener);

    }


    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.iv_left:
                finish();
                break;
            case R.id.edit_label:
                intent = new Intent(this, UserLabelAddActivity.class);
                intent.putExtra("customer_id", customer_id);
                intent.putExtra("type", 12);
                startActivityForResult(intent, REQUEST_CODE_LABEL);
                break;
            case R.id.edit_label_remark:
                intent = new Intent(this, AddRemarkActivity.class);
                intent.putExtra("customer_id", customer_id);
                intent.putExtra("remark", remarkStr);
                startActivityForResult(intent, REQUEST_CODE_REMARK);
                break;
            case R.id.tv_add_ticket:
                showPhotoDialog();
                break;
            case R.id.tv_edit_label:
                intent = new Intent(this, AddCustomerActivity.class);
                intent.putExtra("type", AddCustomerActivity.CustomerOpType.TYPE_EDIT);
                intent.putExtra("customer", customerBean);
                startActivityForResult(intent, REQUEST_CODE_USERINFO);
                break;

            default:
                break;
        }
    }

    @Override
    public void onSuccess(AppApi.Action method, Object obj) {
        hideLoadingLayout();
        switch (method) {
            case POST_ADD_CUS_JSON:
                if(obj instanceof AddCustomerResponse) {
                    AddCustomerResponse addCustomerResponse = (AddCustomerResponse) obj;
                    customer_id = addCustomerResponse.getList().getCustomer_id();
                    getCustomerBaseInfo();
                    getConRecTopList();
                    if(!isCustomerInLocalList(contactFormat.getMobile())) {
                        contactFormat.setCustomer_id(customer_id);
                        CustomerListBean customerListBean = mSession.getCustomerList();
                        if(customerListBean!=null) {
                            List<ContactFormat> customerList = customerListBean.getCustomerList();
                            if(customerList==null) {
                                customerList = new ArrayList<>();
                            }
                            customerList.add(contactFormat);
                            Collections.sort(customerList,pinyinComparator);
                            customerListBean.setCustomerList(customerList);
                            mSession.setCustomerList(customerListBean);
                        }else {
                            customerListBean = new CustomerListBean();
                            List<ContactFormat> customerList = new ArrayList<>();

                            customerList.add(contactFormat);
                            Collections.sort(customerList,pinyinComparator);
                            customerListBean.setCustomerList(customerList);
                            mSession.setCustomerList(customerListBean);
                        }
                    }
                }
                break;
            case POST_CUSTOMER_INFO_JSON:
                if (obj instanceof CustomerBean) {
                    customerBean = (CustomerBean) obj;
                    handleData();
                }
                break;
            case POST_TOP_LIST_JSON:
                if (obj instanceof RecTopList) {
                    recTopList = (RecTopList) obj;
                    handleTopList();
                }
                break;
            case POST_ADD_SIGNLE_CONSUME_RECORD_JSON:
                hideLoadingLayout();
                ConRecBean conRecBean = new ConRecBean();
                conRecBean.setRecipt(ticketOssUrl);
                imageList.add(conRecBean);
                if (imageList != null && imageList.size() > 0) {
                    la_a.setVisibility(View.GONE);
                } else {
                    la_a.setVisibility(View.VISIBLE);
                }
                ticketAdapter.notifyDataSetChanged();
                // finish();
                break;

        }
    }

    private boolean isCustomerInLocalList(String mobile) {
        CustomerListBean customerListBean = mSession.getCustomerList();
        if(customerListBean!=null) {
            List<ContactFormat> customerList = customerListBean.getCustomerList();
            if(customerList!=null&&customerList.size()>0) {
                for(ContactFormat contactFormat: customerList) {
                    if(mobile.equals(contactFormat.getMobile())||mobile.equals(contactFormat.getMobile1()))
                        return true;
                }
            }
        }

        return false;
    }

    @Override
    public void onError(AppApi.Action method, Object obj) {
        hideLoadingLayout();
        switch (method) {
            case POST_TOP_LIST_JSON:
                if (obj instanceof ResponseErrorMessage) {
                    ResponseErrorMessage message = (ResponseErrorMessage) obj;
                    int code = message.getCode();
                    String msg = message.getMessage();
                    ShowMessage.showToast(UserInfoActivity.this, msg);
                }
                if (imageList != null && imageList.size() > 0) {
                    la_a.setVisibility(View.GONE);
                } else {
                    la_a.setVisibility(View.VISIBLE);
                }
            case POST_ADD_SIGNLE_CONSUME_RECORD_JSON:
                hideLoadingLayout();
                break;
            default:
                super.onError(method, obj);
                break;
        }
    }

    private void handleData() {

        if (customerBean != null) {
            Customer customer = customerBean.getList();
            if (customer != null) {
                usernameStr = customer.getName();
                if (!TextUtils.isEmpty(usernameStr)) {
                    name.setText(usernameStr);
                } else {
                    name.setText("");
                }


                usermobileStr = customer.getMobile();
                String usermobileStr2 = customer.getMobile1();
                if (TextUtils.isEmpty(usermobileStr2)) {
                    usermobileStr2 = "";
                } else {
                    usermobileStr2 = "/" + usermobileStr2;
                }

                if (!TextUtils.isEmpty(usermobileStr)) {
                    tel.setVisibility(View.VISIBLE);
                    tel.setText("电话：" + usermobileStr + usermobileStr2);
                } else {
                    tel.setVisibility(View.GONE);
                }

                sexStr = customer.getSex();
                if (!TextUtils.isEmpty(sexStr)) {
                    sex.setVisibility(View.VISIBLE);
                    if ("1".equals(sexStr)) {
                        sex.setImageResource(R.drawable.nan);
                    } else if ("2".equals(sexStr)) {
                        sex.setImageResource(R.drawable.nv);
                    } else {
                        sex.setVisibility(View.GONE);
                    }
                } else {
                    sex.setVisibility(View.GONE);
                }

                birthdayStr = customer.getBirthday();
                if (!TextUtils.isEmpty(birthdayStr)) {
                    birthday.setVisibility(View.VISIBLE);
                    birthday.setText("生日：" + birthdayStr);
                } else {
                    birthday.setVisibility(View.GONE);
                }

                birthplaceStr = customer.getBirthplace();
                if (!TextUtils.isEmpty(birthplaceStr)) {
                    birthplace.setVisibility(View.VISIBLE);
                    birthplace.setText("籍贯：" + birthplaceStr);
                } else {
                    birthplace.setVisibility(View.GONE);
                }
                face_urlStr = customer.getFace_url();
                if (!TextUtils.isEmpty(face_urlStr)) {
                    // Glide.with(mContext).
                    Glide.with(context).load(face_urlStr).centerCrop().transform(new GlideCircleTransform(context)).into(iv_header);
                } else {
                    // birthplace.setText("");
                }
                consume_abilityStr = customer.getConsume_ability();
                if (!TextUtils.isEmpty(consume_abilityStr)) {
                    consume_ability.setVisibility(View.VISIBLE);
                    consume_ability.setText("消费能力：" + consume_abilityStr);
                } else {
                    consume_ability.setVisibility(View.GONE);
                }
                remarkStr = customer.getRemark();
                if (!TextUtils.isEmpty(remarkStr)) {
                    remark.setText(remarkStr);
                } else {
                    // remark.setText("");
                }

                String bill_info = customer.getBill_info();
                if (!TextUtils.isEmpty(bill_info)) {
                    invoice.setVisibility(View.VISIBLE);
                    invoice.setText("发票信息："+bill_info);
                }else {
                    invoice.setVisibility(View.GONE);
                }

                List<CustomerLabel> labelListl = customer.getLabel();
                if (labelListl != null && labelListl.size() > 0) {

                    this.labelList.clear();
                    this.labelList.addAll(labelListl);
                    mTagAdapter.notifyDataChanged();
                    showLabel();
                } else {
                    hideLabel();
                }
            } else {
                hideLabel();
            }
        } else {


        }

    }

    private void handleTopList() {

        if (recTopList != null) {
            List<ConRecBean> list = recTopList.getList();
            if (list != null && list.size() > 0) {
                la_a.setVisibility(View.GONE);
                imageList.clear();
                imageList.addAll(list);
                ticketAdapter.notifyDataSetChanged();

                if (list != null && list.size() < 10) {
                    refreshListView.onLoadComplete(false, false);
                } else {
                    refreshListView.onLoadComplete(true, false);
                }

            } else {
                if (imageList != null && imageList.size() > 0) {
                    la_a.setVisibility(View.GONE);
                } else {
                    la_a.setVisibility(View.VISIBLE);
                }
                refreshListView.onLoadComplete(false, false);
            }
            max_id = recTopList.getMax_id();
            min_id = recTopList.getMin_id();
        } else {
            if (imageList != null && imageList.size() > 0) {
                la_a.setVisibility(View.GONE);
            } else {
                la_a.setVisibility(View.VISIBLE);
            }
            refreshListView.onLoadComplete(false, false);
        }

    }

    @Override
    protected void onDestroy() {
        // 清楚房间选择记录

        super.onDestroy();
    }


    private void getCustomerBaseInfo() {
        HotelBean hotelBean = mSession.getHotelBean();
        AppApi.getCustomerBaseInfo(mContext, customer_id, hotelBean.getInvite_id(), hotelBean.getTel(), this);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_LABEL) {
            if (data != null) {
                ArrayList<CustomerLabel> labelList = (ArrayList<CustomerLabel>) data.getSerializableExtra("selecteLabels");
                if (labelList != null && labelList.size() > 0) {
                    this.labelList.clear();
                    this.labelList.addAll(labelList);
                    mTagAdapter.notifyDataChanged();
                    showLabel();
                } else {
                    hideLabel();
                }
            }
        } else if (requestCode == REQUEST_CODE_REMARK) {
            if (data != null) {
                String remarkS = (String) data.getStringExtra("remark");
                // ArrayList<CustomerLabel> labelList = (ArrayList<CustomerLabel>) data.getSerializableExtra("selecteLabels");
                remark.setText(remarkS);
            } else {
                hideLabel();
            }
        } else if (requestCode == TAKE_PHOTO_REQUEST && resultCode == Activity.RESULT_OK) {
            // 拍照
            // Glide.with(this).load(currentImagePath).placeholder(R.drawable.empty_slide).into(mSpendHistoryIv);
            submit();
        } else if (requestCode == REQUEST_CODE_IMAGE && resultCode == Activity.RESULT_OK && data != null) {
            // 从相册选择
            Uri selectedImage = data.getData();
            String[] filePathColumns = {MediaStore.Images.Media.DATA};
            Cursor c = getContentResolver().query(selectedImage, filePathColumns, null, null, null);
            c.moveToFirst();
            int columnIndex = c.getColumnIndex(filePathColumns[0]);
            String imagePath = c.getString(columnIndex);

            String cachePath = ((SavorApplication) mContext.getApplication()).imagePath;
            File dir = new File(cachePath);
            if (!dir.exists()) {
                dir.mkdirs();
            }

            long timeMillis = System.currentTimeMillis();
            String tel = mSession.getHotelBean().getTel();
            String key = tel + "_" + timeMillis + ".jpg";
            String copyPath = dir.getAbsolutePath() + File.separator + key;

            File sFile = new File(imagePath);
            FileUtils.copyFile(sFile, copyPath);

            currentImagePath = copyPath;
            submit();
            //Glide.with(this).load(currentImagePath).placeholder(R.drawable.empty_slide).into(mSpendHistoryIv);
        } else if (requestCode == REQUEST_CODE_USERINFO) {
            getCustomerBaseInfo();
        }


    }

    private void submit() {

//                final String usermobile = mMobileEt.getText().toString();
//
//                if(TextUtils.isEmpty(mNameEt.getText().toString())) {
//                    ShowMessage.showToast(this,"请输入客户姓名");
//                    return;
//                }
//
//                if(TextUtils.isEmpty(usermobile)) {
//                    ShowMessage.showToast(this,"请输入客户手机号");
//                    return;
//                }

        File file = new File(currentImagePath);
        String hotel_id = mSession.getHotelBean().getHotel_id();
        final String objectKey = "log/resource/restaurant/mobile/userlogo/" + hotel_id + "/" + file.getName();
        final OSSClient ossClient = OSSClientUtil.getOSSClient(this);
        showLoadingLayout();
        // 构造上传请求
        PutObjectRequest put = new PutObjectRequest(ConstantValues.BUCKET_NAME, objectKey, currentImagePath);
        ossClient.asyncPutObject(put, new OSSCompletedCallback<PutObjectRequest, PutObjectResult>() {

            @Override
            public void onSuccess(PutObjectRequest putObjectRequest, PutObjectResult putObjectResult) {
                ticketOssUrl = ossClient.presignPublicObjectURL(ConstantValues.BUCKET_NAME, objectKey);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        //String name = mNameEt.getText().toString();
                        String invite_id = mSession.getHotelBean().getInvite_id();
                        String mobile = mSession.getHotelBean().getTel();
                        String recipt = "";
                        List<String> urlList = new ArrayList<>();
                        urlList.add(ticketOssUrl);

                        recipt = new Gson().toJson(urlList);

                        if (TextUtils.isEmpty(customer_id)) {
                            // 如果客户id为空需要传客户信息bill_info，birthday，birthplace，consume_ability

//                                    String bill_info = "";
//                                    AppApi.addSignleConsumeRecord(UserInfoActivity.this,
//                                            bill_info,birthday,birthplace,consume_ability,"",
//                                            "",invite_id,"",mobile,name, recipt,usermobile,
//                                            "",sex,UserInfoActivity.this);
                        } else {
                            String lable_id_str = "";
                            List<String> labeIds = new ArrayList<>();
                            if (labelList.size() > 0) {
                                for (int i = 0; i < labelList.size(); i++) {
                                    CustomerLabel label = labelList.get(i);
                                    String label_id = label.getLabel_id();
                                    labeIds.add(label_id);
                                }
                                lable_id_str = new Gson().toJson(labeIds);
                            }
                            // 如果客户id不为空 不需要传客户信息
                            AppApi.addSignleConsumeRecord(UserInfoActivity.this,
                                    "", "", "", "", customer_id,
                                    "", invite_id, lable_id_str, mobile, usernameStr, recipt, usermobileStr,
                                    "", "", UserInfoActivity.this);

//                                    AppApi.addSignleConsumeRecord(UserInfoActivity.this,
//                                            "","","","",customer_id,
//                                            "",invite_id,lable_id_str,mobile,"李丛", recipt,"15555555555",
//                                            "","",UserInfoActivity.this);
                        }
                    }
                });

            }

            @Override
            public void onFailure(PutObjectRequest putObjectRequest, ClientException e, ServiceException e1) {

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        hideLoadingLayout();
                        ShowMessage.showToast(UserInfoActivity.this, "小票上传失败");
                    }
                });
            }
        });


    }

    public void showLabel() {
        tv_label_hint.setVisibility(View.GONE);
        rlv_labels.setVisibility(View.VISIBLE);
    }

    public void hideLabel() {
        tv_label_hint.setVisibility(View.VISIBLE);
        rlv_labels.setVisibility(View.GONE);
    }

    private void showPhotoDialog() {
        final String tel = mSession.getHotelBean().getTel();
        new ChoosePicDialog(this, new ChoosePicDialog.OnTakePhotoBtnClickListener() {
            @Override
            public void onTakePhotoClick() {

                String cacheDir = ((SavorApplication) getApplication()).imagePath;
                File cachePath = new File(cacheDir);
                if (!cachePath.exists()) {
                    cachePath.mkdirs();
                }
                currentImagePath = cacheDir + File.separator + tel + "_" + System.currentTimeMillis() + ".jpg";
                File file = new File(currentImagePath);
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                Uri imageUri = Uri.fromFile(file);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                UserInfoActivity.this.startActivityForResult(intent, TAKE_PHOTO_REQUEST);
            }
        },
                new ChoosePicDialog.OnAlbumBtnClickListener() {
                    @Override
                    public void onAlbumBtnClick() {
                        Intent intent = new Intent(Intent.ACTION_PICK,
                                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        UserInfoActivity.this.startActivityForResult(intent, REQUEST_CODE_IMAGE);
                    }
                }
        ).show();
    }

    private void getConRecTopList() {
        HotelBean hotelBean = mSession.getHotelBean();
        AppApi.getConRecTopList(context, customer_id, hotelBean.getInvite_id(), hotelBean.getTel(), max_id, min_id, Rectype, this);
    }

    PullToRefreshBase.OnLastItemVisibleListener onLastItemVisibleListener = new PullToRefreshBase.OnLastItemVisibleListener() {
        @Override
        public void onLastItemVisible() {
            getConRecTopList();
        }
    };

    @Override
    public void showLoadingLayout() {
        if(loadingDialog==null)
            loadingDialog = new LoadingDialog(this);
        loadingDialog.show();
    }

    @Override
    public void hideLoadingLayout() {
        if (loadingDialog!=null&&loadingDialog.isShowing()&&!isFinishing())
            loadingDialog.dismiss();
    }
}

