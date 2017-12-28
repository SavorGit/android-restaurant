package com.savor.resturant.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.savor.resturant.R;
import com.savor.resturant.core.AppApi;

/**
 * 添加标签页面
 * @author hezd created on 2017/12/28
 */
public class LabelAddActivity extends BaseActivity implements View.OnClickListener {

    private ImageView mBackBtn;
    private TextView mTitleTv;
    private TextView mAddTv;
    private RecyclerView mLabelListRlv;

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
        String invite_id = mSession.getHotelBean().getInvite_id();
        String tel = mSession.getHotelBean().getTel();
        String customerId = getIntent().getStringExtra("customer_id");
        AppApi.getCustomerLabelList(this,customerId,invite_id,tel,this);
    }

    @Override
    public void getViews() {
        mBackBtn = (ImageView) findViewById(R.id.iv_left);
        mTitleTv = (TextView) findViewById(R.id.tv_center);

        mAddTv = (TextView) findViewById(R.id.tv_add);
        mLabelListRlv = (RecyclerView) findViewById(R.id.rlv_labels);
    }

    @Override
    public void setViews() {
        mTitleTv.setTextColor(getResources().getColor(R.color.white));
        mTitleTv.setText("选择标签");
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
            case POST_CUSTOMER_LABELS_JSON:
                
                break;
        }
    }
}
