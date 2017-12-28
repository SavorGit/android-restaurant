package com.savor.resturant.activity;

import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.common.api.utils.AppUtils;
import com.common.api.utils.DensityUtil;
import com.common.api.utils.ShowMessage;
import com.savor.resturant.R;
import com.savor.resturant.adapter.FlowAdapter;
import com.savor.resturant.bean.ContactFormat;
import com.savor.resturant.bean.CustomerLabel;
import com.savor.resturant.bean.CustomerLabelList;
import com.savor.resturant.core.AppApi;
import com.savor.resturant.widget.flowlayout.FlowLayoutManager;
import com.savor.resturant.widget.flowlayout.SpaceItemDecoration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SpendHistoryAddActivity extends BaseActivity implements View.OnClickListener {

    private RecyclerView mLabelsRlv;
    private TextView mLabelHint;
    private ImageView mBackBtn;
    private TextView mTitleTv;
    private EditText mNameEt;
    private EditText mMobileEt;
    private FlowAdapter mLabelAdapter;

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
        mNameEt = (EditText) findViewById(R.id.et_name);
        mMobileEt = (EditText) findViewById(R.id.et_phone);
        mBackBtn = (ImageView) findViewById(R.id.iv_left);
        mLabelHint = (TextView) findViewById(R.id.tv_label_hint);
        mTitleTv = (TextView) findViewById(R.id.tv_center);

        mLabelsRlv = (RecyclerView) findViewById(R.id.rlv_labels);
        mLabelAdapter = new FlowAdapter(this);

    }

    @Override
    public void setViews() {
        mTitleTv.setTextColor(getResources().getColor(R.color.white));
        mTitleTv.setText("添加消费记录");

        FlowLayoutManager flowLayoutManager = new FlowLayoutManager();
        mLabelsRlv.addItemDecoration(new SpaceItemDecoration(DensityUtil.dip2px(this,5),DensityUtil.dip2px(this,10)));
        mLabelsRlv.setLayoutManager(flowLayoutManager);
        mLabelsRlv.setAdapter(mLabelAdapter);

//        testLabel();

    }

    private void testLabel() {
        showLabel();
        List<CustomerLabel> labelList = new ArrayList<>();

        CustomerLabel label = new CustomerLabel();
        label.setLabel_id("1");
        label.setLabel_name("口味偏辣");
        label.setLight(0);
        labelList.add(label);

        CustomerLabel label2 = new CustomerLabel();
        label2.setLabel_id("2");
        label2.setLabel_name("爱吃海鲜");
        label2.setLight(0);
        labelList.add(label2);

        CustomerLabel label3 = new CustomerLabel();
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

        mLabelAdapter.setData(labelList);
    }

    @Override
    public void setListeners() {
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
                        ContactFormat existContact = getMobileInCustomerList(content);
                        String invite_id = mSession.getHotelBean().getInvite_id();
                        String customer_id = "";
                        if(existContact!=null) {// 已存在
                            String customerId = existContact.getCustomer_id();
                            if(!TextUtils.isEmpty(customerId)) {
                                customer_id = customerId;
                            }
                        }
                        AppApi.getCustomerLabelList(SpendHistoryAddActivity.this,customer_id,invite_id,mSession.getHotelBean().getTel(),SpendHistoryAddActivity.this);
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


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_left:
                finish();
                break;
        }
    }

    @Override
    public void onSuccess(AppApi.Action method, Object obj) {
        super.onSuccess(method, obj);
        AppUtils.hideSoftKeybord(this);
        switch (method) {
            case POST_CUSTOMER_LABELS_JSON:
                if(obj instanceof CustomerLabelList) {
                    CustomerLabelList customerLabelList = (CustomerLabelList) obj;
                    List<CustomerLabel> list = customerLabelList.getList();
                    if(list!=null&&list.size()>0) {
                        mLabelAdapter.setData(list);
                        showLabel();
                    }
                }
                break;
        }
    }
}
