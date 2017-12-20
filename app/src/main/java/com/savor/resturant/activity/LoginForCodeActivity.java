package com.savor.resturant.activity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Process;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.common.api.utils.ShowMessage;
import com.savor.resturant.R;
import com.savor.resturant.bean.HotelBean;
import com.savor.resturant.bean.SlideSettingsMediaBean;
import com.savor.resturant.core.ApiRequestListener;
import com.savor.resturant.core.AppApi;
import com.savor.resturant.core.ResponseErrorMessage;
import com.savor.resturant.service.ClearImageCacheService;
import com.savor.resturant.service.LocalJettyService;
import com.savor.resturant.service.SSDPService;
import com.savor.resturant.utils.ActivitiesManager;
import com.savor.resturant.utils.GlideImageLoader;
import com.savor.resturant.widget.LoginDialog;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;



/**
 * 登录页面
 */
public class LoginForCodeActivity extends BaseActivity implements View.OnClickListener,
        ApiRequestListener,
        LoginDialog.OnConfirmListener {

    private Context context;

    private TextView tv_code;
    private ImageView icon;
    private EditText ev_num;
    private EditText ev_code;
    private TextView login_btn;
    private EditText invitation_num;
    private String tel = "";
    private String code = "";
    private String invitation= "";
    private Timer mTimer;
    private TimerTask mTask;
    private long mSeconds = 60;
    private HotelBean hotelBean;
    private LoginDialog loginDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        context = this;
        getViews();
        setViews();
        setListeners();
    }

    @Override
    public void getViews() {
        tv_code = (TextView) findViewById(R.id.tv_code);
        ev_num = (EditText) findViewById(R.id.ev_num);
        ev_code = (EditText) findViewById(R.id.ev_code);
        login_btn = (TextView) findViewById(R.id.login_btn);
        invitation_num = (EditText) findViewById(R.id.invitation_num);
    }

    @Override
    public void setViews() {
        hotelBean = mSession.getHotelBean();
        if (hotelBean != null) {
            tel = hotelBean.getTel();
            invitation = hotelBean.getInvitation();
            if (!TextUtils.isEmpty(tel)) {
                ev_num.setText(tel);
                ev_num.setClickable(false);
            }

            if (!TextUtils.isEmpty(invitation)) {
                invitation_num.setText(invitation);
                invitation_num.setClickable(false);
            }

            if (!TextUtils.isEmpty(invitation)&&!TextUtils.isEmpty(tel)) {
                ev_code.setVisibility(View.GONE);
                tv_code.setVisibility(View.GONE);
                AppApi.doLogin(this,invitation,tel,code,this);
            }
        }
    }

    @Override
    public void setListeners() {
        tv_code.setOnClickListener(this);
        login_btn.setOnClickListener(this);
        ev_num.addTextChangedListener(new TextWatcher(){
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
                setCodeView();
                setLoginView();
            }

        });

        ev_code.addTextChangedListener(new TextWatcher(){
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
                setCodeView();
                setLoginView();
            }

        });

        invitation_num.addTextChangedListener(new TextWatcher(){
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
                setLoginView();
            }

        });

    }

    private void setCodeView(){
        tel = ev_num.getText().toString();
        if (!TextUtils.isEmpty(tel)) {
            tv_code.setClickable(true);
            tv_code.setBackgroundResource(R.drawable.corner_remote_view_click);
            tv_code.setTextColor(context.getResources().getColor(R.color.color_fd7a40));
        }else {
            tv_code.setClickable(false);
            tv_code.setBackgroundResource(R.drawable.corner_remote_view_g);
            tv_code.setTextColor(context.getResources().getColor(R.color.color_b7b6b2));
        }
    }

    private void setLoginView(){
        tel = ev_num.getText().toString();
        code = ev_code.getText().toString();
        invitation = invitation_num.getText().toString();
        if (!TextUtils.isEmpty(tel)&&!TextUtils.isEmpty(code)&&!TextUtils.isEmpty(invitation)) {
            login_btn.setClickable(true);
            login_btn.setBackgroundResource(R.drawable.corner_remote_view_btn);
            login_btn.setTextColor(context.getResources().getColor(R.color.color_fefefe));
        }else {
            login_btn.setClickable(false);
            login_btn.setBackgroundResource(R.drawable.corner_remote_view_btn_normal);
            login_btn.setTextColor(context.getResources().getColor(R.color.color_fefefe));
       }
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_code:
                tv_code.setClickable(true);
                getverifyCode();
                break;
            case R.id.login_btn:
                login();
                break;
            default:
                break;
        }
    }

    private void getverifyCode(){
        String tel = ev_num.getText().toString();
        if (!TextUtils.isEmpty(tel)) {
            AppApi.getverifyCode(this,tel,this);
        }
    }

    private void login(){
        invitation = invitation_num.getText().toString();
        if (!TextUtils.isEmpty(invitation)) {
            loginDialog = new LoginDialog(context,
                    "您正在使用"+invitation+"的邀请码\n"+"确认无误，我们将为您的手机号与此酒楼进行绑定！",this);
            loginDialog.show();
        }

    }

    @Override
    public void onSuccess(AppApi.Action method, Object obj) {
        switch (method) {
            case POST_VERIFY_CODE_JSON:
                //String tel = ev_num.getText().toString();
                countTime();
                break;
            case POST_LOGIN_JSON:
                if (obj instanceof HotelBean) {
                    HotelBean hotelBean = (HotelBean)obj;
                    if (hotelBean != null) {
                        hotelBean.setInvitation(invitation);
                        hotelBean.setTel(tel);
                        mSession.setHotelBean(hotelBean);



                        // 启动跳转到首页
                        Intent homeIntent = new Intent(LoginForCodeActivity.this, SavorMainActivity.class);
                        Intent intent = getIntent();
                        if(intent!=null&&("application/pdf").equals(intent.getType())) {
                            Uri data = getIntent().getData();
                            homeIntent.setDataAndType(data,intent.getType());
                        }
                        startActivity(homeIntent);
                       // finish();
                        finish();
                    }
                }
//                UserBean userBean = new UserBean();
//                userBean.setUserNum(ev_num.getText().toString());
//                mSession.setUserBean(userBean);
//
//                PropertyBean property = mSession.getProperty();
//                if(property!=null) {
//                    property.setUploadPro(true);
//                    mSession.setProperty(property);
//                }

                finish();
                break;
        }
    }

    @Override
    public void onError(AppApi.Action method, Object obj) {

        if(obj instanceof ResponseErrorMessage) {
            ResponseErrorMessage message = (ResponseErrorMessage) obj;
            int code = message.getCode();
            ShowMessage.showToast(LoginForCodeActivity.this,message.getMessage());
        }
        switch (method) {
            case POST_LOGIN_JSON:
//                if (hotelBean != null) {
//                    tel = hotelBean.getTel();
//                    invitation = hotelBean.getInvitation();
//                    if (!TextUtils.isEmpty(tel)) {
//                        ev_num.setText(tel);
//                        ev_num.setClickable(false);
//                    }
//
//                    if (!TextUtils.isEmpty(invitation)) {
//                        invitation_num.setText(invitation);
//                        invitation_num.setClickable(false);
//                    }
//
//                    if (!TextUtils.isEmpty(invitation)&&!TextUtils.isEmpty(tel)) {
//                        ev_code.setVisibility(View.GONE);
//                        tv_code.setVisibility(View.GONE);
//                    }
//                }else {
                    ev_num.setClickable(true);
                    invitation_num.setClickable(true);
                    ev_code.setVisibility(View.VISIBLE);
                    tv_code.setVisibility(View.VISIBLE);
                    ev_num.setVisibility(View.VISIBLE);
                    invitation_num.setVisibility(View.VISIBLE);
//                }
                login_btn.setClickable(true);
                login_btn.setBackgroundResource(R.drawable.corner_remote_view_btn);
                login_btn.setTextColor(context.getResources().getColor(R.color.color_fefefe));
                break;

        }

    }

    /**
     * 倒计时
     */
    private void countTime() {
        mTimer = new Timer();
        mTask = new TimerTask() {

            @Override
            public void run() {

                mSeconds = mSeconds - 1;
                Message msg = Message.obtain();
                msg.what = 1;
                msg.obj = mSeconds;
                mTimeHandler.sendMessage(msg);
            }
        };
        mTimer.schedule(mTask, 0, 1000);
    }


    private Handler mTimeHandler = new Handler() {
        public void handleMessage(Message msg) {

            switch (msg.what) {
                case 1:
                    long time = (Long) msg.obj;
                    if (time >= 0 ) {
                        tv_code.setText(time+"S");
                        tv_code.setClickable(false);
                    }else {
                        if (mTimer != null) {
                            mTimer.cancel();
                            mTimer = null;
                        }
                        if (mTask != null) {
                            mTask.cancel();
                            mTask = null;
                        }
                        tv_code.setText("获取验证码");
                        tv_code.setClickable(true);
                    }

                    break;
            }
        };
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 清除倒计时。
        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
        }
        if (mTask != null) {
            mTask.cancel();
            mTask = null;
        }

    }


    @Override
    public void onConfirm() {
        tel = ev_num.getText().toString();
        code = ev_code.getText().toString();
        invitation = invitation_num.getText().toString();
//        String ptype = mSession.getProperty().getProperty()+"";
        if (!TextUtils.isEmpty(tel)&&!TextUtils.isEmpty(code) ) {
            AppApi.doLogin(this,invitation,tel,code,this);
        }
    }

    private void exitApp() {
        finish();
        Process.killProcess(android.os.Process.myPid());

    }

    @Override
    public void onBackPressed() {
        ActivitiesManager.getInstance().popAllActivities();
        exitApp();
    }
}

