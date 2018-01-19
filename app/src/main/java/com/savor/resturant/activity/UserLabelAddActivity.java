package com.savor.resturant.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.common.api.utils.AppUtils;
import com.common.api.utils.DensityUtil;
import com.common.api.utils.ShowMessage;
import com.savor.resturant.R;
import com.savor.resturant.adapter.LabelAdapter;
import com.savor.resturant.bean.CustomerLabel;
import com.savor.resturant.bean.CustomerLabelList;
import com.savor.resturant.bean.LabelAddRessponse;
import com.savor.resturant.core.AppApi;
import com.savor.resturant.widget.flowlayout.FlowLayoutManager;
import com.savor.resturant.widget.flowlayout.SpaceItemDecoration;

import java.util.ArrayList;
import java.util.List;

/**
 * 添加标签页面
 * @author bushlee created on 2017/12/28
 */
public class UserLabelAddActivity extends BaseActivity implements View.OnClickListener {

    public static final int RESULT_CODE_LABEL = 10002;
    private ImageView mBackBtn;
    private TextView mTitleTv;
    private TextView mAddTv;
    private RecyclerView mLabelListRlv;
    private LabelAdapter mLabelAdapter;
    private EditText mLabelEt;
    private String customerId;
    private String invite_id;
    private String tel;
    private List<CustomerLabel> list;
    private int typeActivity = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_label_add);

        handleIntent();
        getViews();
        setViews();
        setListeners();
    }

    private void handleIntent() {
        invite_id = mSession.getHotelBean().getInvite_id();
        tel = mSession.getHotelBean().getTel();
        customerId = getIntent().getStringExtra("customer_id");
        typeActivity = getIntent().getIntExtra("type",0);
        AppApi.getLabelList(this, customerId, invite_id, tel,this);
    }

    @Override
    public void getViews() {
        mBackBtn = (ImageView) findViewById(R.id.iv_left);
        mTitleTv = (TextView) findViewById(R.id.tv_center);

        mAddTv = (TextView) findViewById(R.id.tv_add);
        mLabelListRlv = (RecyclerView) findViewById(R.id.rlv_labels);
        mLabelEt = (EditText) findViewById(R.id.et_intpu_label);
    }

    @Override
    public void setViews() {
        mTitleTv.setTextColor(getResources().getColor(R.color.white));
        mTitleTv.setText("选择标签");


        mLabelAdapter = new LabelAdapter(this);
        FlowLayoutManager flowLayoutManager = new FlowLayoutManager();
        mLabelListRlv.addItemDecoration(new SpaceItemDecoration(DensityUtil.dip2px(this,5),DensityUtil.dip2px(this,10)));
        mLabelListRlv.setLayoutManager(flowLayoutManager);
        mLabelListRlv.setAdapter(mLabelAdapter);
    }

    @Override
    public void setListeners() {
        mBackBtn.setOnClickListener(this);
        mAddTv.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_add:
                String inputLabel = mLabelEt.getText().toString();
                if(TextUtils.isEmpty(inputLabel)) {
                    ShowMessage.showToast(this,"请输入标签");
                }else {
                    AppApi.addLabel(this,customerId,invite_id,inputLabel,tel,this);
                }
                break;
            case R.id.iv_left:
                onBackPressed();
                AppUtils.hideSoftKeybord(this);
                break;
        }
    }

    @Override
    public void onBackPressed() {
        List<CustomerLabel> data = mLabelAdapter.getData();
        ArrayList<CustomerLabel> selectedList = new ArrayList<>();
        if(data!=null&&data.size()>0) {
            for(CustomerLabel label: data) {
                if(label.getLight() == 1) {
                    selectedList.add(label);
                    if (12 == typeActivity) {
                        AppApi.addLabel(this,customerId,invite_id,label.getLabel_name(),tel,this);
                    }
                }
            }
        }
        Intent intent = new Intent();
        intent.putExtra("selecteLabels",selectedList);
        setResult(RESULT_CODE_LABEL,intent);
        super.onBackPressed();
    }

    @Override
    public void onSuccess(AppApi.Action method, Object obj) {
        super.onSuccess(method, obj);
        switch (method) {
            case POST_ADD_LABEL_JSON:
                if(obj instanceof LabelAddRessponse) {
                    LabelAddRessponse labelAddRessponse = (LabelAddRessponse) obj;
                    CustomerLabel label = labelAddRessponse.getList();
                    if(label!=null) {

                        if(this.list == null) {
                            this.list = new ArrayList<>();
                        }
                        this.list.add(label);
                        mLabelAdapter.setData(this.list);

                        mLabelEt.setText("");
                    }
                }

                break;
            case POST_LABEL_List_JSON:
                if(obj instanceof CustomerLabelList) {
                    CustomerLabelList customerLabelList = (CustomerLabelList) obj;
                    list = customerLabelList.getList();
                    if(list !=null&& list.size()>0) {
                        mLabelAdapter.setData(list);
                    }
                }
                break;
        }
    }
}
