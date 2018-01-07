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
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bigkoo.pickerview.TimePickerView;
import com.common.api.utils.FileUtils;
import com.savor.resturant.R;
import com.savor.resturant.SavorApplication;
import com.savor.resturant.adapter.RoomAdapter;
import com.savor.resturant.bean.ContactFormat;
import com.savor.resturant.bean.CustomerLabel;
import com.savor.resturant.bean.HotelBean;
import com.savor.resturant.bean.OrderListBean;
import com.savor.resturant.bean.RoomListBean;
import com.savor.resturant.core.AppApi;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.savor.resturant.activity.AddCustomerActivity.REQUEST_CODE_IMAGE;
import static com.savor.resturant.activity.AddCustomerActivity.TAKE_PHOTO_REQUEST;
import static com.savor.resturant.activity.ContactCustomerListActivity.REQUEST_CODE_SELECT;


/**
 * 包间列表
 */
public class RoomListActivity extends BaseActivity implements View.OnClickListener,AdapterView.OnItemClickListener
        {

    private Context context;
    private ImageView iv_left;
    private TextView tv_center;
    private RoomAdapter roomAdapter;
    private GridView room_list;
    private List<RoomListBean> allList = new ArrayList<RoomListBean>();
    private EditText et_note;
    private TextView add;
    private static final int REQUEST_ADD_ROOM = 208;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room_list_layout);

        context = this;
        getViews();
        setViews();
        setListeners();
        getList();
    }


    @Override
    public void getViews() {
        iv_left = (ImageView) findViewById(R.id.iv_left);
        tv_center = (TextView) findViewById(R.id.tv_center);
        room_list = (GridView) findViewById(R.id.room_list);
        et_note = (EditText) findViewById(R.id.et_note);
        add = (TextView) findViewById(R.id.add);
    }

    @Override
    public void setViews() {
        tv_center.setText("选择包间");
        roomAdapter = new RoomAdapter(context);
        room_list.setAdapter(roomAdapter);
        tv_center.setTextColor(getResources().getColor(R.color.color_f6f2ed));


    }

    @Override
    public void setListeners() {

        iv_left.setOnClickListener(this);
        tv_center.setOnClickListener(this);
        add.setOnClickListener(this);
        room_list.setOnItemClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.iv_left:
                finish();
                break;
            case R.id.add:
                AddRoom();
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
            case POST_ADD_ROOM_JSON:
                if (obj instanceof RoomListBean){
                    RoomListBean room = (RoomListBean) obj;
                   // handleData(mlist);
                    room.setRoom_name(et_note.getText().toString());
                    Intent intent = new Intent();
                    intent.putExtra("room",room);
                    setResult(REQUEST_ADD_ROOM,intent);
                    finish();


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
              allList.clear();
              allList.addAll(mList);
              roomAdapter.setData(allList);

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
        String roomName = et_note.getText().toString();
        if (!TextUtils.isEmpty(roomName)) {
            AppApi.addRoom(mContext,hotelBean.getInvite_id(),hotelBean.getTel(),roomName,this);
        }else {

        }

    }

     private void getList(){
        HotelBean hotelBean = mSession.getHotelBean();
        AppApi.getList(mContext,hotelBean.getInvite_id(),hotelBean.getTel(),this);
    }
     private String getDataTime(Date date) {//可根据需要自行截取数据显示
                SimpleDateFormat format = new SimpleDateFormat("YYYY-MM-dd");
                return format.format(date);
     }
     protected void onActivityResult(int requestCode, int resultCode, Intent data) {
                super.onActivityResult(requestCode, resultCode, data);
                if(requestCode == REQUEST_ADD_ROOM) {

                }
            }

     @Override
     public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
         RoomListBean room = (RoomListBean)roomAdapter.getItem(position);
         Intent intent = new Intent();
         intent.putExtra("room",room);
         setResult(REQUEST_ADD_ROOM,intent);
         finish();
       }
     }

