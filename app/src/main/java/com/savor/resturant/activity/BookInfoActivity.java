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
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.sdk.android.oss.ClientException;
import com.alibaba.sdk.android.oss.OSSClient;
import com.alibaba.sdk.android.oss.ServiceException;
import com.alibaba.sdk.android.oss.callback.OSSCompletedCallback;
import com.alibaba.sdk.android.oss.model.PutObjectRequest;
import com.alibaba.sdk.android.oss.model.PutObjectResult;
import com.bumptech.glide.Glide;
import com.common.api.utils.FileUtils;
import com.common.api.utils.ShowMessage;
import com.google.gson.Gson;
import com.savor.resturant.R;
import com.savor.resturant.SavorApplication;
import com.savor.resturant.bean.BookListResult;
import com.savor.resturant.bean.ConRecBean;
import com.savor.resturant.bean.ContactFormat;
import com.savor.resturant.bean.CustomerLabel;
import com.savor.resturant.bean.HotelBean;
import com.savor.resturant.bean.OrderListBean;
import com.savor.resturant.core.AppApi;
import com.savor.resturant.utils.ConstantValues;
import com.savor.resturant.utils.GlideCircleTransform;
import com.savor.resturant.utils.OSSClientUtil;
import com.savor.resturant.widget.ChoosePicDialog;
import com.savor.resturant.widget.CommonDialog;
import com.savor.resturant.widget.CommonDialog2;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static com.savor.resturant.activity.AddCustomerActivity.REQUEST_CODE_IMAGE;
import static com.savor.resturant.activity.AddCustomerActivity.TAKE_PHOTO_REQUEST;

/**
 * 预定详情
 * Created by bushlee on 2018/1/7.
 */

public class BookInfoActivity extends BaseActivity implements View.OnClickListener,CommonDialog2.OnConfirmListener,CommonDialog2.OnCancelListener {

    private Context context;
    private ImageView iv_left;
    private TextView tv_center;
    private OrderListBean orderListBean;
    private TextView room_name;
    private TextView time;
    private TextView nums;
    private TextView remark;
    private TextView user_name;
    private TextView user_mobile;
    private TextView del;
    private TextView update;
    private TextView wel_type;
    private TextView tjc_type;
    private TextView xp_type;
    private TextView to_user_info;
    private ImageView iv_header;
    private String person_nums;
    private String order_name;
    private String order_mobile;
    private String room_nameStr;
    private String time_str;
    private int is_expense;
    private String moment_str;
    private String is_welcome;
    private String is_recfood;
    private String customer_id;
    private String room_id;
    private String room_type;
    private String face_url;
    private String remarkStr;
    private String order_id;
    private HotelBean hotelBean;
    private static final int REQUEST_ADD_BOOK = 308;
    private static final int REQUEST_ADD_USER = 408;
    private String ticket_url;
    private String OrderServiceType = "";
    private String currentImagePath;
    private String ticketOssUrl;
    private CommonDialog2 dialog;
    private TextView wel_lab;
    private TextView tjc_lab;
    private TextView xp_lab;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_info_layout);
        getDate();
        context = this;
        getViews();
        setViews();
        setListeners();
       // init();
        getOrderDetail();
    }


    private void getDate(){
        Intent intent = getIntent();
        if (intent != null) {
            orderListBean = (OrderListBean)intent.getSerializableExtra("orderListBean");
            customer_id = orderListBean.getCustomer_id();

        }
    }

    @Override
    public void getViews() {
        tv_center = (TextView) findViewById(R.id.tv_center);
        room_name = (TextView) findViewById(R.id.room_name);
        time = (TextView) findViewById(R.id.time);
        nums = (TextView) findViewById(R.id.nums);
        remark = (TextView) findViewById(R.id.remark);
        user_name = (TextView) findViewById(R.id.user_name);
        user_mobile = (TextView) findViewById(R.id.user_mobile);
        del = (TextView) findViewById(R.id.del);
        update = (TextView) findViewById(R.id.update);
        wel_type = (TextView) findViewById(R.id.wel_type);
        tjc_type = (TextView) findViewById(R.id.tjc_type);
        xp_type = (TextView) findViewById(R.id.xp_type);
        to_user_info= (TextView) findViewById(R.id.to_user_info);
        iv_header= (ImageView) findViewById(R.id.iv_header);
        iv_left= (ImageView) findViewById(R.id.iv_left);

        wel_lab = (TextView) findViewById(R.id.wel_lab);
        tjc_lab = (TextView) findViewById(R.id.tjc_lab);
        xp_lab = (TextView) findViewById(R.id.xp_lab);


    }

    @Override
    public void setViews() {
        hotelBean = mSession.getHotelBean();
        order_id = orderListBean.getOrder_id();
        tv_center.setText("预定信息");
    }

    @Override
    public void setListeners() {
        wel_type.setOnClickListener(this);
        tjc_type.setOnClickListener(this);
        xp_type.setOnClickListener(this);
        del.setOnClickListener(this);
        update.setOnClickListener(this);
        iv_left.setOnClickListener(this);
        to_user_info.setOnClickListener(this);
        wel_lab.setOnClickListener(this);
        tjc_lab.setOnClickListener(this);
        xp_lab.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()){
            case R.id.iv_left:
                finish();
                break;
            case R.id.wel_type:
            case R.id.wel_lab:
                if (!"1".equals(is_welcome)) {
                    ticket_url = "";
                    OrderServiceType = "1";
                    upateOrderService();
                }else {
                    intent = new Intent(mContext, WelComeSetTextActivity.class);
                    mContext.startActivity(intent);
                }

                break;
            case R.id.tjc_type:
            case R.id.tjc_lab:
                if (!"1".equals(is_recfood)) {
                    ticket_url = "";
                    OrderServiceType = "2";
                    upateOrderService();
                }else {
                    intent = new Intent(mContext, RecommendFoodActivity.class);
                    intent.putExtra("type", RecommendFoodActivity.OperationType.TYPE_RECOMMEND_FOODS);
                    mContext.startActivity(intent);
                }

                break;
            case R.id.xp_type:
            case R.id.xp_lab:
                OrderServiceType = "3";
                showPhotoDialog();
               // upateOrderService();
                break;
            case R.id.del:
                Del();
                break;
            case R.id.update:
                intent = new Intent(this,UpDateBookActivity.class);
                intent.putExtra("orderListBean",orderListBean);
                startActivityForResult(intent, REQUEST_ADD_BOOK );
                break;
            case R.id.to_user_info:
               intent = new Intent(this,UserInfoActivity.class);
                ContactFormat contactFormat = new ContactFormat();
                contactFormat.setName(order_name);
                contactFormat.setMobile(order_mobile);
                intent.putExtra("customer",contactFormat);
               intent.putExtra("customerID",customer_id);
               startActivityForResult(intent, REQUEST_ADD_BOOK );



                break;

            //REQUEST_ADD_USER

            default:
                break;
        }
    }

    private void init(){
        if (orderListBean != null) {
            person_nums = orderListBean.getPerson_nums();
            order_name = orderListBean.getOrder_name();
            order_mobile = orderListBean.getOrder_mobile();
            room_nameStr = orderListBean.getRoom_name();
            time_str = orderListBean.getTime_str();
            is_expense = orderListBean.getIs_expense();
            is_welcome = orderListBean.getIs_welcome();
            is_recfood = orderListBean.getIs_recfood();
            customer_id = orderListBean.getCustomer_id();
            room_id = orderListBean.getRoom_id();
            room_type = orderListBean.getRoom_type();
            face_url = orderListBean.getFace_url();
            moment_str = orderListBean.getMoment_str();
            remarkStr = orderListBean.getRemark();
            moment_str = orderListBean.getMoment_str();



            if (!TextUtils.isEmpty(room_nameStr)) {
                room_name.setText(room_nameStr);
            }else{
                room_name.setText("");
            }

            if (!TextUtils.isEmpty(person_nums)) {
                nums.setText("就餐人数："+person_nums);
            }else{
                nums.setText("就餐人数：未填写");
            }

            if (!TextUtils.isEmpty(order_name)) {
                user_name.setText(order_name);
            }else{
                user_name.setText("");
            }


            if (!TextUtils.isEmpty(order_mobile)) {
                user_mobile.setText(order_mobile);
            }else{
                user_mobile.setText("");
            }

            if (!TextUtils.isEmpty(time_str)) {
                time.setText("预定时间:"+time_str+" "+moment_str);
            }else{
                time.setText("");
            }

            if (!TextUtils.isEmpty(remarkStr)) {
                remark.setText("备注:"+remarkStr);
            }else{
                remark.setText("备注:未填写");
            }

            if (is_expense == 1) {
               // holder.is_xfjl.setVisibility(View.VISIBLE);
                xp_type.setBackgroundResource(R.drawable.corner_remote_book_btn);
                xp_type.setTextColor(context.getResources().getColor(R.color.color_14b2fc));
                xp_type.setClickable(true);
                xp_type.setText("已完成");

            }else {
                xp_type.setBackgroundResource(R.drawable.corner_remote_room_item);
                xp_type.setTextColor(context.getResources().getColor(R.color.app_red_color));
                xp_type.setClickable(true);
                xp_type.setText("未完成");
            }


            if (!TextUtils.isEmpty(is_welcome)) {
                if ("1".equals(is_welcome)) {

                    wel_type.setBackgroundResource(R.drawable.corner_remote_book_btn);
                    wel_type.setTextColor(context.getResources().getColor(R.color.color_14b2fc));
                    wel_type.setClickable(true);
                    wel_type.setText("已完成");
                }else {
                    wel_type.setBackgroundResource(R.drawable.corner_remote_room_item);
                    wel_type.setTextColor(context.getResources().getColor(R.color.app_red_color));
                    wel_type.setClickable(true);
                    wel_type.setText("未完成");
                }
            }else {
                wel_type.setBackgroundResource(R.drawable.corner_remote_room_item);
                wel_type.setTextColor(context.getResources().getColor(R.color.app_red_color));
                wel_type.setClickable(true);
                wel_type.setText("未完成");
            }


            if (!TextUtils.isEmpty(is_recfood)) {
                if ("1".equals(is_recfood)) {
                    tjc_type.setBackgroundResource(R.drawable.corner_remote_book_btn);
                    tjc_type.setTextColor(context.getResources().getColor(R.color.color_14b2fc));
                    tjc_type.setClickable(true);
                    tjc_type.setText("已完成");
                }else {
                    tjc_type.setBackgroundResource(R.drawable.corner_remote_room_item);
                    tjc_type.setTextColor(context.getResources().getColor(R.color.app_red_color));
                    tjc_type.setClickable(true);
                    tjc_type.setText("未完成");
                }
            }else {
                tjc_type.setBackgroundResource(R.drawable.corner_remote_room_item);
                tjc_type.setTextColor(context.getResources().getColor(R.color.app_red_color));
                tjc_type.setClickable(true);
                tjc_type.setText("未完成");
            }

           // face_urlStr = customer.getFace_url();
            if (!TextUtils.isEmpty(face_url)) {
                // Glide.with(mContext).
                Glide.with(context).load(face_url).centerCrop().transform(new GlideCircleTransform(context)).into(iv_header);
            }else{
                // birthplace.setText("");
            }
        }
    }

    private void Del(){

        if (dialog != null) {
            dialog.show();
        }else {
            dialog = new CommonDialog2(context,"是否删除",this,this);
            dialog.show();
        }


    }

    private void upateOrderService(){

        AppApi.upateOrderService(context,hotelBean.getInvite_id(),hotelBean.getTel(),order_id,ticketOssUrl,OrderServiceType,this);
    }
    @Override
    public void onSuccess(AppApi.Action method, Object obj) {
        hideLoadingLayout();
        switch (method) {
            case POST_DELETE_ORDER_JSON:
                Intent intent = new Intent();
                setResult(REQUEST_ADD_BOOK,intent);
                finish();
                break;
            case POST_UPDATE_ORDER_SERVICE_JSON:
                Intent intent1 = new Intent();
                if ("1".equals(OrderServiceType)) {
                    intent = new Intent(mContext, WelComeSetTextActivity.class);
                    mContext.startActivity(intent);
                    is_welcome = "1";
                    wel_type.setBackgroundResource(R.drawable.corner_remote_book_btn);
                    wel_type.setTextColor(context.getResources().getColor(R.color.color_14b2fc));
                    wel_type.setClickable(true);
                    wel_type.setText("已完成");
                }else if ("2".equals(OrderServiceType)) {
                    intent = new Intent(mContext, RecommendFoodActivity.class);
                    intent.putExtra("type", RecommendFoodActivity.OperationType.TYPE_RECOMMEND_FOODS);
                    mContext.startActivity(intent);
                    is_recfood = "1";
                    tjc_type.setBackgroundResource(R.drawable.corner_remote_book_btn);
                    tjc_type.setTextColor(context.getResources().getColor(R.color.color_14b2fc));
                    tjc_type.setClickable(true);
                    tjc_type.setText("已完成");

                }else if ("3".equals(OrderServiceType)) {
                    xp_type.setBackgroundResource(R.drawable.corner_remote_book_btn);
                    xp_type.setTextColor(context.getResources().getColor(R.color.color_14b2fc));
                    xp_type.setClickable(true);
                    xp_type.setText("已完成");
                }
                break;
            case POST_ADD_SIGNLE_CONSUME_RECORD_JSON:
                upateOrderService();
                // finish();
                break;
            case POST_ORDER_DETAIL_JSON:

                if (obj instanceof OrderListBean){
                    orderListBean = (OrderListBean) obj;
                    init();
                }


                // finish();
                break;


        }
    }

    @Override
    public void onError(AppApi.Action method, Object obj) {
        hideLoadingLayout();
        switch (method) {
            case POST_ORDER_LIST_JSON:

            default:
                super.onError(method,obj);
                break;
        }
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
                BookInfoActivity.this.startActivityForResult(intent, TAKE_PHOTO_REQUEST);
            }
        },
                new ChoosePicDialog.OnAlbumBtnClickListener() {
                    @Override
                    public void onAlbumBtnClick() {
                        Intent intent = new Intent(Intent.ACTION_PICK,
                                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        BookInfoActivity.this.startActivityForResult(intent, REQUEST_CODE_IMAGE);
                    }
                }
        ).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == TAKE_PHOTO_REQUEST && resultCode == Activity.RESULT_OK) {
            // 拍照
            // Glide.with(this).load(currentImagePath).placeholder(R.drawable.empty_slide).into(mSpendHistoryIv);
            submit();
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
            submit();
            //Glide.with(this).load(currentImagePath).placeholder(R.drawable.empty_slide).into(mSpendHistoryIv);
        }else if (requestCode == REQUEST_ADD_BOOK){
            getOrderDetail();
        }

        //
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

                        //String name = mNameEt.getText().toString();
                        String invite_id = mSession.getHotelBean().getInvite_id();
                        String mobile = mSession.getHotelBean().getTel();
                        String recipt = "";
                        List<String> urlList = new ArrayList<>();
                        urlList.add(ticketOssUrl);

                        recipt = new Gson().toJson(urlList);

                        if(TextUtils.isEmpty(customer_id)) {
                            // 如果客户id为空需要传客户信息bill_info，birthday，birthplace，consume_ability

//                                    String bill_info = "";
//                                    AppApi.addSignleConsumeRecord(UserInfoActivity.this,
//                                            bill_info,birthday,birthplace,consume_ability,"",
//                                            "",invite_id,"",mobile,name, recipt,usermobile,
//                                            "",sex,UserInfoActivity.this);
                        }else {
//                            String lable_id_str = "";
//                            List<String> labeIds = new ArrayList<>();
//                            if(labelList.size()>0) {
//                                for(int i = 0;i<labelList.size();i++) {
//                                    CustomerLabel label = labelList.get(i);
//                                    String label_id = label.getLabel_id();
//                                    labeIds.add(label_id);
//                                }
//                                lable_id_str = new Gson().toJson(labeIds);
//                            }
                            // 如果客户id不为空 不需要传客户信息
                                    AppApi.addSignleConsumeRecord(BookInfoActivity.this,
                                            "","","","",customer_id,
                                            "",invite_id,"",mobile,orderListBean.getOrder_name(), recipt,orderListBean.getOrder_mobile(),
                                            "","",BookInfoActivity.this);

//                            AppApi.addSignleConsumeRecord(BookInfoActivity.this,
//                                    "","","","",customer_id,
//                                    "",invite_id,lable_id_str,mobile,"李丛", recipt,"15555555555",
//                                    "","",BookInfoActivity.this);
                        }
                    }
                });
//                ConRecBean conRecBean = new ConRecBean();
//                conRecBean.setRecipt(ticketOssUrl);
//                imageList.add(conRecBean);
//                ticketAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(PutObjectRequest putObjectRequest, ClientException e, ServiceException e1) {

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ShowMessage.showToast(BookInfoActivity.this,"小票上传失败");
                    }
                });
            }
        });


    }

    @Override
    public void onConfirm() {
        AppApi.deleteOrder(context,hotelBean.getInvite_id(),hotelBean.getTel(),order_id,this);
    }

    @Override
    public void onCancel() {

    }


    private void getOrderDetail(){
        AppApi.getOrderDetail(context,hotelBean.getInvite_id(),hotelBean.getTel(),order_id,this);
    }
}
