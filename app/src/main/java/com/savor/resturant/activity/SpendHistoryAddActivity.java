package com.savor.resturant.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.common.api.utils.ShowMessage;
import com.savor.resturant.R;

public class SpendHistoryAddActivity extends BaseActivity implements View.OnClickListener {

    private RecyclerView mLabelsRlv;
    private TextView mLabelHint;
    private ImageView mBackBtn;
    private TextView mTitleTv;
    private EditText mNameEt;
    private EditText mMobileEt;

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
        mLabelsRlv = (RecyclerView) findViewById(R.id.rlv_labels);
        mLabelHint = (TextView) findViewById(R.id.tv_label_hint);
        mTitleTv = (TextView) findViewById(R.id.tv_center);
    }

    @Override
    public void setViews() {
        mTitleTv.setTextColor(getResources().getColor(R.color.white));
        mTitleTv.setText("添加消费记录");



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
                        // 1.从客户列表查找是否存在这个用户如果存在将客户名称自动填充
                        // 2.获取标签列表，判断customer_id是否为空如果为空，需要将全量信息传给接口，
                        // 接口会创建新客户并否则不需要生日籍贯等字段
                    }else {
                        ShowMessage.showToast(SpendHistoryAddActivity.this,"请输入正确的手机号");
                    }
                }
            }
        });
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
}
