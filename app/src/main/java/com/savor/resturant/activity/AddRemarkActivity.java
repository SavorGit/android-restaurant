package com.savor.resturant.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.savor.resturant.R;
import com.savor.resturant.bean.CustomerBean;
import com.savor.resturant.bean.CustomerLabel;
import com.savor.resturant.bean.HotelBean;
import com.savor.resturant.core.AppApi;

import java.util.ArrayList;
import java.util.List;

/**
 * 添加备注
 * Created by bushlee on 2018/1/7.
 */

public class AddRemarkActivity extends BaseActivity implements View.OnClickListener{

    private Context context;
    private ImageView iv_left;
    private TextView tv_center;
    private String customer_id;
    private String remark;
    private TextView tv_save;
    private EditText et_remark;
    private static final int REQUEST_CODE_REMARK = 108;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_remark_layout);
        getDate();
        context = this;
        getViews();
        setViews();
        setListeners();
    }

    private void getDate(){
        Intent intent = getIntent();
        if (intent != null) {
            customer_id = intent.getStringExtra("customer_id");
            remark = intent.getStringExtra("remark");
        }
    }

    @Override
    public void getViews() {
        et_remark = (EditText) findViewById(R.id.et_remark);
        tv_save = (TextView) findViewById(R.id.tv_save);
        iv_left = (ImageView) findViewById(R.id.iv_left);
        tv_center = (TextView) findViewById(R.id.tv_center);
    }

    @Override
    public void setViews() {
        tv_center.setText("维修");
        tv_center.setTextColor(getResources().getColor(R.color.color_f6f2ed));
        if (!TextUtils.isEmpty(remark)) {
            et_remark.setText(remark);
        }
    }

    @Override
    public void setListeners() {
        iv_left.setOnClickListener(this);
        tv_save.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()){
            case R.id.iv_left:
                finish();
                break;
            case R.id.tv_save:
                SaveRemark();
//                intent = new Intent(this,LabelAddActivity.class);
//                intent.putExtra("customer_id",customer_id);
//                intent.putExtra("type",12);
//                startActivityForResult(intent,REQUEST_CODE_LABEL);
                break;


            default:
                break;
        }
    }

    private void SaveRemark(){
        HotelBean hotelBean = mSession.getHotelBean();
        remark = et_remark.getText().toString();
        if (!TextUtils.isEmpty(remark)) {
            AppApi.editRemark(context,customer_id,hotelBean.getInvite_id(),hotelBean.getTel(),remark,this);
        }
    }

    @Override
    public void onSuccess(AppApi.Action method, Object obj) {
        hideLoadingLayout();
        switch (method) {
            case POST_CUSTOMER_EDIT_REMARK_JSON:
//                if (obj instanceof CustomerBean){
//                    customerBean = (CustomerBean)obj;
//                    handleData();
//                }
                onBackPressed();
                break;

        }
    }

    @Override
    public void onError(AppApi.Action method, Object obj) {
        hideLoadingLayout();
        switch (method) {
            case POST_CUSTOMER_EDIT_REMARK_JSON:

            default:
               // super.onError(method,obj);
                break;
        }
    }

    @Override
    public void onBackPressed() {

        Intent intent = new Intent();
        intent.putExtra("remark",remark);
        setResult(REQUEST_CODE_REMARK,intent);
        finish();
    }
}
