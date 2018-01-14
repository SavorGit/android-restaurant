package com.savor.resturant.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.savor.resturant.R;
import com.savor.resturant.bean.HotelBean;
import com.savor.resturant.bean.OrderListBean;
import com.savor.resturant.core.AppApi;
import com.savor.resturant.utils.GlideCircleTransform;

/**
 * 预定详情
 * Created by bushlee on 2018/1/7.
 */

public class BookInfoActivity extends BaseActivity implements View.OnClickListener{

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
    private String is_welcome;
    private String is_recfood;
    private String customer_id;
    private String room_id;
    private String room_type;
    private String face_url;
    private String moment_str;
    private String remarkStr;
    private String order_id;
    private HotelBean hotelBean;
    private static final int REQUEST_ADD_BOOK = 308;
    private String ticket_url;
    private String OrderServiceType = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_info_layout);
        getDate();
        context = this;
        getViews();
        setViews();
        setListeners();
        init();
    }


    private void getDate(){
        Intent intent = getIntent();
        if (intent != null) {
            orderListBean = (OrderListBean)intent.getSerializableExtra("orderListBean");

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
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()){
            case R.id.iv_left:
                finish();
                break;
            case R.id.wel_type:
                ticket_url = "";
                OrderServiceType = "1";
                upateOrderService();
                break;
            case R.id.tjc_type:
                ticket_url = "";
                OrderServiceType = "2";
                upateOrderService();
                break;
            case R.id.xp_type:
                OrderServiceType = "3";
                upateOrderService();
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
                intent.putExtra("customerID",customer_id);
                startActivityForResult(intent, REQUEST_ADD_BOOK );
                break;


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



            if (!TextUtils.isEmpty(room_nameStr)) {
                room_name.setText(room_nameStr);
            }else{
                room_name.setText("");
            }

            if (!TextUtils.isEmpty(person_nums)) {
                nums.setText("就餐人数："+person_nums);
            }else{
                nums.setText("");
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
                time.setText("订餐时间:"+time_str);
            }else{
                time.setText("");
            }

            if (!TextUtils.isEmpty(remarkStr)) {
                remark.setText("订餐时间:"+remarkStr);
            }else{
                remark.setText("");
            }

            if (is_expense == 1) {
               // holder.is_xfjl.setVisibility(View.VISIBLE);
                xp_type.setBackgroundResource(R.drawable.corner_remote_book_btn);
                xp_type.setTextColor(context.getResources().getColor(R.color.color_14b2fc));
                xp_type.setClickable(false);
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
                    wel_type.setClickable(false);
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
                    tjc_type.setClickable(false);
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

        AppApi.deleteOrder(context,hotelBean.getInvite_id(),hotelBean.getTel(),order_id,this);
    }

    private void upateOrderService(){
        AppApi.upateOrderService(context,hotelBean.getInvite_id(),hotelBean.getTel(),order_id,ticket_url,OrderServiceType,this);
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
                if ("1".equals(OrderServiceType)) {
                    wel_type.setBackgroundResource(R.drawable.corner_remote_book_btn);
                    wel_type.setTextColor(context.getResources().getColor(R.color.color_14b2fc));
                    wel_type.setClickable(false);
                    wel_type.setText("已完成");
                }else if ("2".equals(OrderServiceType)) {
                    tjc_type.setBackgroundResource(R.drawable.corner_remote_book_btn);
                    tjc_type.setTextColor(context.getResources().getColor(R.color.color_14b2fc));
                    tjc_type.setClickable(false);
                    tjc_type.setText("已完成");

                }else if ("3".equals(OrderServiceType)) {
                    xp_type.setBackgroundResource(R.drawable.corner_remote_book_btn);
                    xp_type.setTextColor(context.getResources().getColor(R.color.color_14b2fc));
                    xp_type.setClickable(false);
                    xp_type.setText("已完成");
                }
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

}
