package com.savor.resturant.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bigkoo.pickerview.TimePickerView;
import com.common.api.widget.pulltorefresh.library.PullToRefreshBase;
import com.common.api.widget.pulltorefresh.library.PullToRefreshListView;
import com.savor.resturant.R;
import com.savor.resturant.adapter.BookAdapter;
import com.savor.resturant.bean.HotelBean;
import com.savor.resturant.bean.OrderListBean;
import com.savor.resturant.core.AppApi;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.savor.resturant.activity.ContactCustomerListActivity.REQUEST_CODE_SELECT;


/**
 * 欢迎词文背景置页
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
        dining_room_la = (RelativeLayout) findViewById(R.id.dining_time_la);
        tv_dining_room = (TextView) findViewById(R.id.tv_dining_time);
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
                intent.putExtra("type", ContactCustomerListActivity.OperationType.CONSTACT_LIST_SELECT);
                startActivityForResult(intent,REQUEST_CODE_SELECT);
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


    private void getOrderList(){
        HotelBean hotelBean = mSession.getHotelBean();
        //AppApi.getOrderList(mContext,hotelBean.getInvite_id(),hotelBean.getTel(),curDate,page_num+"",this);
    }

     private String getDataTime(Date date) {//可根据需要自行截取数据显示
                SimpleDateFormat format = new SimpleDateFormat("YYYY-MM-dd");
                return format.format(date);
     }
}

