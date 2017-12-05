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

import com.savor.resturant.R;


/**
 * 欢迎词文背景置页
 */
public class WelComeSetBgActivity extends BaseActivity implements View.OnClickListener{

    private Context context;
    private ImageView iv_left;
    private TextView tv_center;
    private TextView t1,t2,t3,t4,t5,t6,t7,t8;
    private RelativeLayout bg_l1,bg_l2,bg_l3,bg_l4,bg_l5,bg_l6,bg_l7,bg_l8;
    private String keyWord;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.welcome_set_bg_layout);
        context = this;
        getViews();
        setViews();
        setListeners();
    }

    private void getWord(){
        Intent intent = getIntent();
        if (intent != null) {
            keyWord = intent.getStringExtra("keyWord");

        }
    }

    @Override
    public void getViews() {
        iv_left = (ImageView) findViewById(R.id.iv_left);
        tv_center = (TextView) findViewById(R.id.tv_center);
        t1 = (TextView) findViewById(R.id.t1);
        t2 = (TextView) findViewById(R.id.t2);
        t3 = (TextView) findViewById(R.id.t3);
        t4 = (TextView) findViewById(R.id.t4);
        t5 = (TextView) findViewById(R.id.t5);
        t6 = (TextView) findViewById(R.id.t7);
        t7 = (TextView) findViewById(R.id.t7);
        t8 = (TextView) findViewById(R.id.t8);
        bg_l1 = (RelativeLayout) findViewById(R.id.bg_l1);
        bg_l2 = (RelativeLayout) findViewById(R.id.bg_l2);
        bg_l3 = (RelativeLayout) findViewById(R.id.bg_l3);
        bg_l4 = (RelativeLayout) findViewById(R.id.bg_l4);
        bg_l5 = (RelativeLayout) findViewById(R.id.bg_l5);
        bg_l6 = (RelativeLayout) findViewById(R.id.bg_l6);
        bg_l7 = (RelativeLayout) findViewById(R.id.bg_l7);
        bg_l8 = (RelativeLayout) findViewById(R.id.bg_l8);
    }

    @Override
    public void setViews() {
        tv_center.setText("请选择背景");
        if (!TextUtils.isEmpty(keyWord)) {
            t1.setText(keyWord);
            t2.setText(keyWord);
            t3.setText(keyWord);
            t4.setText(keyWord);
            t5.setText(keyWord);
            t6.setText(keyWord);
            t7.setText(keyWord);
            t8.setText(keyWord);
        }
    }

    @Override
    public void setListeners() {

        iv_left.setOnClickListener(this);
        bg_l1.setOnClickListener(this);
        bg_l2.setOnClickListener(this);
        bg_l3.setOnClickListener(this);
        bg_l4.setOnClickListener(this);
        bg_l5.setOnClickListener(this);
        bg_l6.setOnClickListener(this);
        bg_l7.setOnClickListener(this);
        bg_l8.setOnClickListener(this);

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.iv_left:
                finish();
                break;
            case R.id.bg_l1:
                setText();
                break;
            case R.id.bg_l2:
                setText();
                break;
            case R.id.bg_l3:
                setText();
                break;
            case R.id.bg_l4:
                setText();
                break;
            case R.id.bg_l5:
                setText();
                break;
            case R.id.bg_l6:
                setText();
                break;
            case R.id.bg_l7:
                setText();
                break;
            case R.id.bg_l8:
                setText();
                break;
            default:
                break;
        }
    }


    private void setText(){

    }

}

