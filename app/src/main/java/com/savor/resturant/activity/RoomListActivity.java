package com.savor.resturant.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bigkoo.pickerview.TimePickerView;
import com.savor.resturant.R;
import com.savor.resturant.bean.ContactFormat;
import com.savor.resturant.bean.HotelBean;
import com.savor.resturant.bean.OrderListBean;
import com.savor.resturant.bean.RoomListBean;
import com.savor.resturant.core.AppApi;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static com.savor.resturant.activity.ContactCustomerListActivity.REQUEST_CODE_SELECT;


/**
 * 包间列表
 */
public class RoomListActivity extends BaseActivity implements View.OnClickListener
        {

    private Context context;
    private ImageView iv_left;
    private TextView tv_center;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_book_layout);

        context = this;
        getViews();
        setViews();
        setListeners();
    }


    @Override
    public void getViews() {
        iv_left = (ImageView) findViewById(R.id.iv_left);
        tv_center = (TextView) findViewById(R.id.tv_center);

    }

    @Override
    public void setViews() {
        tv_center.setText("添加预定");


    }

    @Override
    public void setListeners() {

        iv_left.setOnClickListener(this);
        tv_center.setOnClickListener(this);

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.iv_left:
                finish();
                break;



            default:
                break;
        }
    }








    @Override
    public void onSuccess(AppApi.Action method, Object obj) {
        hideLoadingLayout();
        switch (method) {
            case POST_ROOM_LIST_JSON:

                if (obj instanceof List<?>){
                    List<RoomListBean> mlist = (List<RoomListBean>) obj;
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

     private void handleData(List<RoomListBean> mList){

          if (mList != null && mList.size() > 0){

          }else {


                }

            }



    @Override
    protected void onDestroy() {
        // 清楚房间选择记录

        super.onDestroy();
    }



    private void AddRoom(){
        HotelBean hotelBean = mSession.getHotelBean();
        AppApi.addRoom(mContext,hotelBean.getInvite_id(),hotelBean.getTel(),"",this);
    }

     private void getList(){
        HotelBean hotelBean = mSession.getHotelBean();
        AppApi.getList(mContext,hotelBean.getInvite_id(),hotelBean.getTel(),this);
    }
     private String getDataTime(Date date) {//可根据需要自行截取数据显示
                SimpleDateFormat format = new SimpleDateFormat("YYYY-MM-dd");
                return format.format(date);
     }


}

