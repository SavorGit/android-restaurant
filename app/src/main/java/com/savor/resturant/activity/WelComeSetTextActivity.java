package com.savor.resturant.activity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.common.api.utils.ShowMessage;
import com.savor.resturant.R;
import com.savor.resturant.bean.HotelBean;
import com.savor.resturant.core.ApiRequestListener;
import com.savor.resturant.core.AppApi;
import com.savor.resturant.core.ResponseErrorMessage;

import java.util.Timer;
import java.util.TimerTask;


/**
 * 欢迎词文本设置页
 */
public class WelComeSetTextActivity extends BaseActivity implements View.OnClickListener{

    private Context context;
    private ImageView iv_left;
    private TextView tv_center;
    private TextView tv_right;
    private EditText greeting;
    private TextView t1,t2,t3,t4,t5;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.welcome_set_text_layout);
        context = this;
        getViews();
        setViews();
        setListeners();
    }

    @Override
    public void getViews() {
        iv_left = (ImageView) findViewById(R.id.iv_left);
        tv_center = (TextView) findViewById(R.id.tv_center);
        tv_right = (TextView) findViewById(R.id.tv_right);
        t1 = (TextView) findViewById(R.id.t1);
        t2 = (TextView) findViewById(R.id.t2);
        t3 = (TextView) findViewById(R.id.t3);
        t4 = (TextView) findViewById(R.id.t4);
        t5 = (TextView) findViewById(R.id.t5);
        greeting = (EditText) findViewById(R.id.greeting);
    }

    @Override
    public void setViews() {
        tv_center.setText("欢迎词");
        tv_right.setText("下一步");
        tv_right.setVisibility(View.VISIBLE);
        tv_right.setTextColor(context.getResources().getColor(R.color.color_3e000000));
    }

    @Override
    public void setListeners() {
        tv_right.setOnClickListener(this);
        iv_left.setOnClickListener(this);
        t1.setOnClickListener(this);
        t2.setOnClickListener(this);
        t3.setOnClickListener(this);
        t4.setOnClickListener(this);
        t5.setOnClickListener(this);
        greeting.addTextChangedListener(new TextWatcher(){
            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub
                //s:变化后的所有字符

            }
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                // TODO Auto-generated method stub
                //s:变化前的所有字符； start:字符开始的位置； count:变化前的总字节数；after:变化后的字节数
                //Toast.makeText(getApplicationContext(), "变化前:"+s+";"+start+";"+count+";"+after, Toast.LENGTH_SHORT).show();
            }
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                // TODO Auto-generated method stub
                //S：变化后的所有字符；start：字符起始的位置；before: 变化之前的总字节数；count:变化后的字节数
                //Toast.makeText(getApplicationContext(), "变化后:"+s+";"+start+";"+before+";"+count, Toast.LENGTH_SHORT).show();
                //setCodeView();
                checkNextBut();
            }

        });


    }


    private void checkNextBut(){
        String word = greeting.getText().toString();
        if (!TextUtils.isEmpty(word)) {
            tv_right.setTextColor(context.getResources().getColor(R.color.color_ff783e));
            tv_right.setClickable(true);
        }else {
            tv_right.setTextColor(context.getResources().getColor(R.color.color_3e000000));
            tv_right.setClickable(false);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_right:
                toSetBg();
                break;
            case R.id.iv_left:
                finish();
                break;
            case R.id.t1:
                setText(t1);
                break;
            case R.id.t2:
                setText(t2);
                break;
            case R.id.t3:
                setText(t3);
                break;
            case R.id.t4:
                setText(t4);
                break;
            case R.id.t5:
                setText(t5);
                break;
            default:
                break;
        }
    }


    private void setText(TextView t){
        greeting.setText(t.getText().toString());
        int size = t.getText().toString().length();
        if (size >18) {
            greeting.setSelection(18);
        }else {
            greeting.setSelection(size);
        }


    }

    private void toSetBg(){
        String word = greeting.getText().toString();
        if (!TextUtils.isEmpty(word)) {
            Intent intent = new Intent();
            intent.putExtra("keyWord",word);
            intent.setClass(WelComeSetTextActivity.this,WelComeSetBgActivity.class);
            startActivity(intent);
        }

    }
}

