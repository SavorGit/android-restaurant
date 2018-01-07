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

import com.bigkoo.pickerview.TimePickerView;
import com.bumptech.glide.Glide;
import com.common.api.utils.FileUtils;
import com.common.api.widget.pulltorefresh.library.PullToRefreshBase;
import com.common.api.widget.pulltorefresh.library.PullToRefreshListView;
import com.savor.resturant.R;
import com.savor.resturant.SavorApplication;
import com.savor.resturant.adapter.BookAdapter;
import com.savor.resturant.bean.ContactFormat;
import com.savor.resturant.bean.CustomerLabel;
import com.savor.resturant.bean.HotelBean;
import com.savor.resturant.bean.OrderListBean;
import com.savor.resturant.bean.RoomListBean;
import com.savor.resturant.core.AppApi;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static com.savor.resturant.activity.AddCustomerActivity.REQUEST_CODE_IMAGE;
import static com.savor.resturant.activity.AddCustomerActivity.TAKE_PHOTO_REQUEST;
import static com.savor.resturant.activity.ContactCustomerListActivity.REQUEST_CODE_SELECT;


/**
 * 添加预定
 */
public class AddBookActivity extends BaseActivity implements View.OnClickListener
        {

    private Context context;
    private ImageView iv_left;
    private TextView tv_center;
    private String curDate;
    private EditText et_name;
    private EditText et_phone;
    private EditText et_dining_num;
    private RelativeLayout dining_time_la;
    private TextView tv_dining_time;
    private RelativeLayout dining_room_la;
    private TextView tv_dining_room;
    private EditText et_note;
    private ImageView iv_header;
    private String order_mobile;
    private String order_name;
    private String order_time;
    private String person_nums;
    private String room_id;
    private String room_type;
    private static final int REQUEST_ADD_ROOM = 208;
    private RoomListBean room;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_book_layout);
        getDate();
        context = this;
        getViews();
        setViews();
        setListeners();
    }

    private void getDate(){
        Intent intent = getIntent();
        if (intent != null) {
            curDate = intent.getStringExtra("Date");

        }
    }

    @Override
    public void getViews() {
        iv_left = (ImageView) findViewById(R.id.iv_left);
        tv_center = (TextView) findViewById(R.id.tv_center);
        et_name = (EditText) findViewById(R.id.et_name);
        et_phone = (EditText) findViewById(R.id.et_phone);
        et_dining_num = (EditText) findViewById(R.id.et_dining_num);
        dining_time_la = (RelativeLayout) findViewById(R.id.dining_time_la);
        tv_dining_time = (TextView) findViewById(R.id.tv_dining_time);
        dining_room_la = (RelativeLayout) findViewById(R.id.dining_room_la);
        tv_dining_room = (TextView) findViewById(R.id.tv_dining_room);
        et_note = (EditText) findViewById(R.id.et_note);
        iv_header = (ImageView) findViewById(R.id.iv_header);
    }

    @Override
    public void setViews() {
        tv_center.setText("添加预定");


    }

    @Override
    public void setListeners() {
        dining_time_la.setOnClickListener(this);
        iv_left.setOnClickListener(this);
        tv_center.setOnClickListener(this);
        iv_header.setOnClickListener(this);
        dining_room_la.setOnClickListener(this);

    }

      private void showDateDialog() {
                TimePickerView timePickerView = new TimePickerView.Builder(mContext, new TimePickerView.OnTimeSelectListener() {
                    @Override
                    public void onTimeSelect(Date date, View v) {
                        curDate = getDataTime(date);
                        //setTime(date);
                        tv_dining_time.setText(curDate);


                    }
                }).setType(new boolean[]{true, true, true, false, false, false}).isCenterLabel(false).build();
                timePickerView.show();
      }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.iv_left:
                finish();
                break;
            case R.id.dining_time_la:
                showDateDialog();
                break;
            case R.id.iv_header:
                //showDateDialog();
                Intent intent;
                intent = new Intent(this,ContactCustomerListActivity.class);
                intent.putExtra("type", ContactCustomerListActivity.OperationType.CUSTOMER_LIST_SELECT);
                startActivityForResult(intent,REQUEST_CODE_SELECT);
                break;
            case R.id.dining_room_la:
                intent = new Intent(this,RoomListActivity.class);
                intent.putExtra("room",room);
                //intent.putExtra("remark",remarkStr);
                startActivityForResult(intent,REQUEST_ADD_ROOM);
                break;



            default:
                break;
        }
    }








    @Override
    public void onSuccess(AppApi.Action method, Object obj) {
        hideLoadingLayout();
        switch (method) {
            case POST_ORDER_LIST_JSON:

                if (obj instanceof List<?>){
                    List<OrderListBean> mlist = (List<OrderListBean>) obj;
                    handleData(mlist);

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

     private void handleData(List<OrderListBean> mList){

          if (mList != null && mList.size() > 0){

          }else {


                }

            }



    @Override
    protected void onDestroy() {
        // 清楚房间选择记录

        super.onDestroy();
    }

    private void AddBook(){
      order_mobile = et_phone.getText().toString();
      order_name = et_phone.getText().toString();
      order_time = tv_dining_time.getText().toString();
      person_nums = et_dining_num.getText().toString();
      room_id = et_phone.getText().toString();
      room_type = et_phone.getText().toString();

        if (TextUtils.isEmpty(order_mobile)) {

            return;
        }

        if (TextUtils.isEmpty(person_nums)) {

            return;
        }
        if (TextUtils.isEmpty(order_time)) {

            return;
        }
        AddOrderList();
    }

    private void AddOrderList(){
        HotelBean hotelBean = mSession.getHotelBean();
        AppApi.addOrder(mContext,hotelBean.getInvite_id(),hotelBean.getTel(),order_mobile,order_name,order_time,person_nums,room_id,room_type,this);
    }

     private String getDataTime(Date date) {//可根据需要自行截取数据显示
                SimpleDateFormat format = new SimpleDateFormat("YYYY-MM-dd");
                return format.format(date);
     }

            @Override
            protected void onActivityResult(int requestCode, int resultCode, Intent data) {
                super.onActivityResult(requestCode, resultCode, data);
                if(requestCode == REQUEST_CODE_SELECT) {
                    if(data!=null) {
                        ContactFormat contactFormat = (ContactFormat) data.getSerializableExtra("customer");
                        et_phone.setText(contactFormat.getMobile());
                        et_name.setText(contactFormat.getName());
                    }
                }else if (requestCode == REQUEST_ADD_ROOM) {
                    if(data!=null) {
                        room = (RoomListBean) data.getSerializableExtra("room");
                        tv_dining_room.setText(room.getName());
//                        et_phone.setText(contactFormat.getMobile());
//                        et_name.setText(contactFormat.getName());
                    }

                }
            }
}

