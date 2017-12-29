package com.savor.resturant.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.common.api.utils.AppUtils;
import com.common.api.utils.DensityUtil;
import com.common.api.utils.ShowMessage;
import com.savor.resturant.R;
import com.savor.resturant.adapter.FlowAdapter;
import com.savor.resturant.bean.ContactFormat;
import com.savor.resturant.bean.Customer;
import com.savor.resturant.bean.CustomerBean;
import com.savor.resturant.bean.CustomerLabel;
import com.savor.resturant.bean.CustomerLabelList;
import com.savor.resturant.core.AppApi;
import com.savor.resturant.widget.flowlayout.FlowLayoutManager;
import com.savor.resturant.widget.flowlayout.SpaceItemDecoration;
import com.zhy.view.flowlayout.FlowLayout;
import com.zhy.view.flowlayout.TagAdapter;
import com.zhy.view.flowlayout.TagFlowLayout;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.savor.resturant.activity.ContactCustomerListActivity.REQUEST_CODE_SELECT;

public class SpendHistoryAddActivity extends BaseActivity implements View.OnClickListener {

    private static final int REQUEST_CODE_LABEL = 100;
    private TagFlowLayout mLabelsRlv;
    private TextView mLabelHint;
    private ImageView mBackBtn;
    private TextView mTitleTv;
    private EditText mNameEt;
    private EditText mMobileEt;
    private FlowAdapter mLabelAdapter;
    private TextView mEditLabelTv;
    private String customer_id;
    private LinearLayout mSelectCusLayout;
    private List<CustomerLabel> labelList = new ArrayList<>();;
    private TagAdapter mTagAdapter = new  TagAdapter(labelList) {
        @Override
        public View getView(FlowLayout parent, int position, Object o) {
            TextView tv = (TextView) getLayoutInflater().inflate(R.layout.flow_item,
                    mLabelsRlv, false);
            tv.setBackgroundResource(R.drawable.label_bg);
            tv.setTextColor(mContext.getResources().getColor(R.color.color_label));
            tv.setText(labelList.get(position).getLabel_name());
            return tv;
        }
    };

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
        mSelectCusLayout = (LinearLayout) findViewById(R.id.ll_customer_select);
        mEditLabelTv = (TextView) findViewById(R.id.tv_edit_label);
        mNameEt = (EditText) findViewById(R.id.et_name);
        mMobileEt = (EditText) findViewById(R.id.et_phone);
        mBackBtn = (ImageView) findViewById(R.id.iv_left);
        mLabelHint = (TextView) findViewById(R.id.tv_label_hint);
        mTitleTv = (TextView) findViewById(R.id.tv_center);

        mLabelsRlv = (TagFlowLayout) findViewById(R.id.rlv_labels);
        mLabelAdapter = new FlowAdapter(this);

    }

    @Override
    public void setViews() {
        mTitleTv.setTextColor(getResources().getColor(R.color.white));
        mTitleTv.setText("添加消费记录");

//        FlowLayoutManager flowLayoutManager = new FlowLayoutManager();
//        mLabelsRlv.addItemDecoration(new SpaceItemDecoration(DensityUtil.dip2px(this,5),DensityUtil.dip2px(this,10)));
//        mLabelsRlv.setLayoutManager(flowLayoutManager);
        mLabelsRlv.setAdapter(mTagAdapter);

        testLabel();

    }

    private void testLabel() {
        showLabel();
        List<CustomerLabel> labelList = new ArrayList<>();

        final CustomerLabel label = new CustomerLabel();
        label.setLabel_id("1");
        label.setLabel_name("口味偏辣");
        label.setLight(0);
        labelList.add(label);

        final CustomerLabel label2 = new CustomerLabel();
        label2.setLabel_id("2");
        label2.setLabel_name("爱吃海鲜");
        label2.setLight(0);
        labelList.add(label2);

        final CustomerLabel label3 = new CustomerLabel();
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
        this.labelList.addAll(labelList);

        mTagAdapter.notifyDataChanged();

//        mLabelsRlv.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                List<CustomerLabel> labelList = new ArrayList<>();
//                labelList.add(label);
//                labelList.add(label2);
//                labelList.add(label3);
//                mLabelAdapter.setData(labelList);
//            }
//        },2000);
    }

    @Override
    public void setListeners() {
        mSelectCusLayout.setOnClickListener(this);
        mEditLabelTv.setOnClickListener(this);
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
                        customer_id = "";
                        if(existContact!=null) {// 已存在
                            String customerId = existContact.getCustomer_id();
                            String name = existContact.getName();
                            mNameEt.setText(name);
                            if(!TextUtils.isEmpty(customerId)) {
                                customer_id = customerId;
//                                AppApi.getCustomerBaseInfo(SpendHistoryAddActivity.this, customer_id,invite_id,mSession.getHotelBean().getTel(),SpendHistoryAddActivity.this);
                            }
                        }

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
        Intent intent;
        switch (v.getId()) {
            case R.id.ll_customer_select:
                intent = new Intent(this,ContactCustomerListActivity.class);
                intent.putExtra("type", ContactCustomerListActivity.OperationType.CONSTACT_LIST_SELECT);
                startActivityForResult(intent,REQUEST_CODE_SELECT);
                break;
            case R.id.tv_edit_label:
                String mobile = mMobileEt.getText().toString();
                if(TextUtils.isEmpty(mobile)) {
                    ShowMessage.showToast(this,"请输入客户手机号");
                }else {
                    intent = new Intent(this,LabelAddActivity.class);
                    intent.putExtra("customer_id",customer_id);
                    startActivityForResult(intent,REQUEST_CODE_LABEL);
                }
                break;
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
            case POST_CUSTOMER_INFO_JSON:
                if(obj instanceof CustomerBean) {
                    CustomerBean customerBean = (CustomerBean) obj;

                    if(customerBean!=null) {
                        Customer list = customerBean.getList();
                        if(list!=null) {
                            List<CustomerLabel> label = list.getLabel();
                            if(label!=null&&label.size()>0) {
                                mLabelAdapter.setData(label);
                                showLabel();
                            }
                        }

                    }
                }
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_CODE_SELECT) {
            if(data!=null) {
                ContactFormat contactFormat = (ContactFormat) data.getSerializableExtra("customer");
                mMobileEt.setText(contactFormat.getMobile());
            }
        }else if(requestCode == REQUEST_CODE_LABEL) {
            if(data!=null) {
                ArrayList<CustomerLabel> labelList = (ArrayList<CustomerLabel>) data.getSerializableExtra("selecteLabels");
                if(labelList!=null&&labelList.size()>0) {
                    mLabelAdapter.setData(labelList);
                    showLabel();
                }else {
                    hideLabel();
                }
            }
        }
    }
}
