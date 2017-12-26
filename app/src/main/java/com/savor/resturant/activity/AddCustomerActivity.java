package com.savor.resturant.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.savor.resturant.R;
import com.savor.resturant.bean.ConAbilityList;
import com.savor.resturant.core.AppApi;

import java.util.List;

/**
 * 新增客户
 */
public class AddCustomerActivity extends BaseActivity implements View.OnClickListener {

    private ImageView mBackBtn;
    private EditText mNameEt;
    private EditText mPhontEt;
    private EditText mSecondMobileEt;
    private LinearLayout mUploadHeaderLayout;
    private ImageView mAddBtn;
    private Button mSaveBtn;
    private LinearLayout mSeconMobileLayout;

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
        mBackBtn = (ImageView) findViewById(R.id.iv_left);
        mNameEt = (EditText) findViewById(R.id.et_name);
        mPhontEt = (EditText) findViewById(R.id.et_phone);
        mSecondMobileEt = (EditText) findViewById(R.id.et_second_mobile);
        mAddBtn = (ImageView) findViewById(R.id.iv_add);
        mUploadHeaderLayout = (LinearLayout) findViewById(R.id.ll_upload_header);
        mSaveBtn = (Button) findViewById(R.id.btn_save);
        mSeconMobileLayout = (LinearLayout) findViewById(R.id.ll_second_mobile);
    }

    @Override
    public void setViews() {

    }

    @Override
    public void setListeners() {
        mBackBtn.setOnClickListener(this);
        mAddBtn.setOnClickListener(this);
        mSaveBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_save:
                // 新增客户

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

    @Override
    public void onSuccess(AppApi.Action method, Object obj) {
        super.onSuccess(method, obj);
        switch (method) {
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
}
