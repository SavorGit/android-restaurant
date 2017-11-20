package com.savor.resturant.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.common.api.utils.ShowMessage;
import com.savor.resturant.R;
import com.savor.resturant.bean.SmallPlatInfoBySSDP;
import com.savor.resturant.bean.SmallPlatformByGetIp;
import com.savor.resturant.bean.TvBoxInfo;
import com.savor.resturant.bean.TvBoxSSDPInfo;
import com.savor.resturant.core.AppApi;
import com.savor.resturant.core.ResponseErrorMessage;
import com.savor.resturant.utils.RecordUtils;
import com.savor.resturant.utils.WifiUtil;

import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

public class LinkTvActivity extends BaseActivity implements View.OnClickListener{

    public static final String EXRA_TV_BOX = "exra_tv_box";
    private EditText mFirstInputNumEt;
    private EditText mSecondInputNumEt;
    private EditText mThirdNumEt;
    private Context context;
    private TextView tv_center;
    private ImageView iv_left;
    private RelativeLayout relink_la;
    private TextView link_status;
    private TextView relink;
    private String firstNum;
    private String thirdNum ;
    private String seconNum;
    private RelativeLayout loading;
    /**绑定电视*/
    public static final int EXTRA_TV_INFO = 0x111;
    /**校验三位数字接口错误数*/
    private int verifyCodeErrorCount;
    private String erroMsg;
    private int errorMax = 4;
    /**是否已经校验成功*/
    private boolean isVerify;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_link_tv);
        context = this;
        getViews();
        setViews();
        setListeners();
    }

    @Override
    protected void onResume() {
        super.onResume();
        RecordUtils.onPageStart(this, getString(R.string.link_tv_enter));

    }

    @Override
    protected void onPause() {
        super.onPause();
        RecordUtils.onPageEndAndPause(this, this);
    }

    @Override
    public void getViews() {
        mFirstInputNumEt = (EditText) findViewById(R.id.et_first_num);
        mSecondInputNumEt = (EditText) findViewById(R.id.et_sencond_num);
        mThirdNumEt = (EditText) findViewById(R.id.et_third_num);
        tv_center = (TextView) findViewById(R.id.tv_center);
        iv_left = (ImageView) findViewById(R.id.iv_left);
        link_status = (TextView) findViewById(R.id.link_status);
        relink = (TextView) findViewById(R.id.relink);
        relink_la = (RelativeLayout) findViewById(R.id.relink_la);
        loading = (RelativeLayout) findViewById(R.id.loading);
    }

    @Override
    public void setViews() {
        tv_center.setText("连接电视");
        mFirstInputNumEt.setFocusable(true);
        mFirstInputNumEt.setFocusableInTouchMode(true);
        mFirstInputNumEt.requestFocus();
        Timer timer = new Timer();
        timer.schedule(new TimerTask(){
                 public void run() {
                        InputMethodManager inputManager = (InputMethodManager)mFirstInputNumEt.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                        inputManager.showSoftInput(mFirstInputNumEt, 0);

                           }

                       },

                500);
    }

    @Override
    public void setListeners() {
        iv_left.setOnClickListener(this);
        relink.setOnClickListener(this);
        relink_la.setOnClickListener(this);
        mFirstInputNumEt.setOnClickListener(this);
        mSecondInputNumEt.setOnClickListener(this);
        mThirdNumEt.setOnClickListener(this);

        mFirstInputNumEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                seconNum = mSecondInputNumEt.getText().toString();
                thirdNum = mThirdNumEt.getText().toString();
                firstNum = s.toString();
                if (!TextUtils.isEmpty(firstNum)) {

                    int size = firstNum.length();
                    if (size == 2) {
                        String ss = s.toString();
                        String second =  ss.substring(1);
                        String first = ss.substring(0,1);
                        mFirstInputNumEt.setText(first);
                        mSecondInputNumEt.setText(second);
                    }

                    mSecondInputNumEt.setFocusable(true);
                    mSecondInputNumEt.setFocusableInTouchMode(true);
                    mSecondInputNumEt.requestFocus();
                    mSecondInputNumEt.setSelection(seconNum.length());
                }
                link();
            }

            @Override
            public void afterTextChanged(Editable s) {


            }
        });

        mSecondInputNumEt.setOnKeyListener(
                new View.OnKeyListener() {
                    @Override
                    public boolean onKey(View v, int keyCode, KeyEvent event) {
                        if (keyCode == KeyEvent.KEYCODE_DEL) {
                            if (!TextUtils.isEmpty(firstNum)) {
                                if (!TextUtils.isEmpty(seconNum)) {
                                    mFirstInputNumEt.setFocusable(true);
                                    mFirstInputNumEt.setFocusableInTouchMode(true);
                                    mFirstInputNumEt.requestFocus();
                                    mFirstInputNumEt.setSelection(firstNum.length());
                                }else {
                                    mFirstInputNumEt.setFocusable(true);
                                    mFirstInputNumEt.setFocusableInTouchMode(true);
                                    mFirstInputNumEt.requestFocus();
                                    mFirstInputNumEt.setText("");
                                }




                            }
                        }
                        return false;
                    }
                }
        );
        mSecondInputNumEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                firstNum = mFirstInputNumEt.getText().toString();
                thirdNum = mThirdNumEt.getText().toString();
                seconNum = s.toString();
                if (!TextUtils.isEmpty(firstNum)&&!TextUtils.isEmpty(seconNum)) {

                    int size = seconNum.length();
                    if (size == 2) {
                        String ss = s.toString();
                        String second =  ss.substring(1);
                        String first = ss.substring(0,1);
                        mSecondInputNumEt.setText(first);
                        mThirdNumEt.setText(second);
                    }
                    mThirdNumEt.setFocusable(true);
                    mThirdNumEt.setFocusableInTouchMode(true);
                    mThirdNumEt.requestFocus();
                    mThirdNumEt.setSelection(thirdNum.length());
                }else {

                    mFirstInputNumEt.setFocusable(true);
                    mFirstInputNumEt.setFocusableInTouchMode(true);
                    mFirstInputNumEt.requestFocus();
                    mFirstInputNumEt.setSelection(firstNum.length());

                }
                link();
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!TextUtils.isEmpty(firstNum)&&!TextUtils.isEmpty(seconNum)) {
                    mThirdNumEt.setFocusable(true);
                    mThirdNumEt.setFocusableInTouchMode(true);
                    mThirdNumEt.requestFocus();
                }

            }
        });

        mThirdNumEt.setOnKeyListener(
                new View.OnKeyListener() {
                    @Override
                    public boolean onKey(View v, int keyCode, KeyEvent event) {
                        if (keyCode == KeyEvent.KEYCODE_DEL) {
                            if (!TextUtils.isEmpty(seconNum) ) {
                                if (!TextUtils.isEmpty(thirdNum) ) {
                                    mThirdNumEt.setText("");
                                    mSecondInputNumEt.setFocusable(true);
                                    mSecondInputNumEt.setFocusableInTouchMode(true);
                                    mSecondInputNumEt.requestFocus();
                                    mSecondInputNumEt.setSelection(seconNum.length());
                                }else {

                                    mSecondInputNumEt.setFocusable(true);
                                    mSecondInputNumEt.setFocusableInTouchMode(true);
                                    mSecondInputNumEt.requestFocus();
                                    mSecondInputNumEt.setText("");

                                }
                            }else {
                                if (!TextUtils.isEmpty(thirdNum) ) {
                                    mThirdNumEt.setText("");
                                    mSecondInputNumEt.setFocusable(true);
                                    mSecondInputNumEt.setFocusableInTouchMode(true);
                                    mSecondInputNumEt.requestFocus();
                                    mSecondInputNumEt.setSelection(seconNum.length());
                                }else {

                                    mFirstInputNumEt.setFocusable(true);
                                    mFirstInputNumEt.setFocusableInTouchMode(true);
                                    mFirstInputNumEt.requestFocus();
                                    mFirstInputNumEt.setText("");

                                }

                            }
                        }
                        return false;
                    }
                }
        );
        mThirdNumEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                firstNum = mFirstInputNumEt.getText().toString();
                seconNum = mSecondInputNumEt.getText().toString();
                thirdNum = s.toString();

                //1、判断前面的输入框是否输入
                //2、如果输入了则连接
                //3、如果没输入则返回上一个输入框
                if (TextUtils.isEmpty(firstNum)) {
                    mFirstInputNumEt.setFocusable(true);
                    mFirstInputNumEt.setFocusableInTouchMode(true);
                    mFirstInputNumEt.requestFocus();
                }else if (TextUtils.isEmpty(seconNum)) {
                    mSecondInputNumEt.setFocusable(true);
                    mSecondInputNumEt.setFocusableInTouchMode(true);
                    mSecondInputNumEt.requestFocus();
                }else if (TextUtils.isEmpty(thirdNum)) {
                    mSecondInputNumEt.setFocusable(true);
                    mSecondInputNumEt.setFocusableInTouchMode(true);
                    mSecondInputNumEt.requestFocus();
                }

                link();
            }

            @Override
            public void afterTextChanged(Editable s) {


            }
        });
    }

    @Override
    public void onSuccess(AppApi.Action method, Object obj) {
        super.onSuccess(method, obj);
        loading.setVisibility(View.GONE);
        link_status.setVisibility(View.GONE);
        relink_la.setVisibility(View.GONE);
        switch (method) {
            case GET_VERIFY_CODE_BY_BOXIP_JSON:
            case POST_BOX_INFO_JSON:
                if(obj instanceof TvBoxInfo) {
                    if(!isVerify) {
                        isVerify = true;
                        link_status.setVisibility(View.INVISIBLE);
                        relink_la.setVisibility(View.INVISIBLE);
                        TvBoxInfo info = (TvBoxInfo) obj;
                        Intent intent = new Intent();
                        intent.putExtra(EXRA_TV_BOX,info);
                        setResult(EXTRA_TV_INFO,intent);
                        finish();
                    }
                }

            case GET_CALL_CODE_BY_BOXIP_JSON:
                break;
        }
    }

    @Override
    public void onError(AppApi.Action method, Object obj) {
        loading.setVisibility(View.GONE);
        link_status.setVisibility(View.INVISIBLE);
        relink_la.setVisibility(View.INVISIBLE);
        switch (method) {
            case GET_VERIFY_CODE_BY_BOXIP_JSON:
            case POST_BOX_INFO_JSON:
                if(obj instanceof ResponseErrorMessage) {
                        ResponseErrorMessage msg = (ResponseErrorMessage) obj;
                        erroMsg = msg.getMessage();
                    link_status.setVisibility(View.VISIBLE);
                    link_status.setText(erroMsg);
                    relink_la.setVisibility(View.INVISIBLE);
                }else {
                    erroMsg = "绑定失败";
                    link_status.setVisibility(View.VISIBLE);
                    relink_la.setVisibility(View.INVISIBLE);
                }
                verifyCodeErrorCount++;
                if(verifyCodeErrorCount==errorMax) {
                        ShowMessage.showToast(this,erroMsg);
                    loading.setVisibility(View.GONE);
                    link_status.setVisibility(View.INVISIBLE);
                    link_status.setText(erroMsg);
                    relink_la.setVisibility(View.VISIBLE);
                }
                break;
            case GET_CALL_CODE_BY_BOXIP_JSON:
                break;
        }
    }

    @Override
    public void onBackPressed() {
//        setResult(EXTRA_TV_INFO);
        super.onBackPressed();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.iv_left:
                RecordUtils.onEvent(this, getString(R.string.link_tv_back));
                setResult(EXTRA_TV_INFO);
                finish();
                break;
            case R.id.relink_la:
            case R.id.relink:
                link();
                break;
            case R.id.et_first_num:
                mFirstInputNumEt.setFocusable(true);
                mFirstInputNumEt.setFocusableInTouchMode(true);
                mFirstInputNumEt.requestFocus();
                mFirstInputNumEt.setSelection(mFirstInputNumEt.getText().toString().length());
                break;
            case R.id.et_sencond_num:
                mSecondInputNumEt.setFocusable(true);
                mSecondInputNumEt.setFocusableInTouchMode(true);
                mSecondInputNumEt.requestFocus();
                mSecondInputNumEt.setSelection(mSecondInputNumEt.getText().toString().length());
                break;
            case R.id.et_third_num:
                mThirdNumEt.setFocusable(true);
                mThirdNumEt.setFocusableInTouchMode(true);
                mThirdNumEt.requestFocus();
                mThirdNumEt.setSelection(mThirdNumEt.getText().toString().length());
                break;
            default:
                break;
        }
    }

    private void linktype(){
        if(!TextUtils.isEmpty(thirdNum)
                &&!TextUtils.isEmpty(seconNum)
                &&!TextUtils.isEmpty(thirdNum)){

        }else {

        }
    }
    private void link(){
        link_status.setVisibility(View.INVISIBLE);
        relink_la.setVisibility(View.INVISIBLE);
        verifyCodeErrorCount = 0;
        if(!TextUtils.isEmpty(thirdNum)
                &&!TextUtils.isEmpty(seconNum)
                &&!TextUtils.isEmpty(thirdNum)) {
            loading.setVisibility(View.VISIBLE);
            SmallPlatInfoBySSDP smallPlatInfoBySSDP = mSession.getSmallPlatInfoBySSDP();
            TvBoxSSDPInfo tvBoxSSDPInfo = mSession.getTvBoxSSDPInfo();
            SmallPlatformByGetIp smallPlatformByGetIp = mSession.getmSmallPlatInfoByIp();
            if(smallPlatformByGetIp==null&&tvBoxSSDPInfo==null&&smallPlatInfoBySSDP==null) {
                erroMsg = "连接失败，请连接包间wifi";
                loading.setVisibility(View.GONE);
                ShowMessage.showToast(this,erroMsg);
                return;
            }

            // 1.通过机顶盒ssdp返回的小平台地址进行校验
            String number = firstNum+seconNum+thirdNum;
            if(smallPlatInfoBySSDP !=null) {
                //1.小平台ssdp已经获取到直接进行呼玛，errorcount 为1
                String serverIp = smallPlatInfoBySSDP.getServerIp();
                String type = smallPlatInfoBySSDP.getType();
                if(!TextUtils.isEmpty(serverIp)
                        &&!TextUtils.isEmpty(type)) {
                    AppApi.verifyNumBySpSSDP(LinkTvActivity.this,number,LinkTvActivity.this);
                }else {
                    verifyCodeErrorCount++;
                }
            }else {
                verifyCodeErrorCount++;
            }

            // 2.通过getip返回的小平台进行校验
            if(smallPlatformByGetIp!=null) {
                String localIp = smallPlatformByGetIp.getLocalIp();
                if(!TextUtils.isEmpty(localIp)) {
                    AppApi.verifyNumByClound(mContext,number,this);
                }else {
                    verifyCodeErrorCount++;
                }
            }

            // 3.通过机顶盒地址进行校验
            if(tvBoxSSDPInfo!=null) {
                String boxIp = tvBoxSSDPInfo.getBoxIp();
                String type = tvBoxSSDPInfo.getType();
                if (!TextUtils.isEmpty(boxIp)
                        &&!TextUtils.isEmpty(type)) {
                    String boxUrl = "http://" + boxIp + ":8080";
                    AppApi.verifyNumByBoxIp(LinkTvActivity.this,boxUrl,number,this);
                }else {
                    verifyCodeErrorCount++;
                }
            }else {
                verifyCodeErrorCount++;
            }
            // 4.通过机顶盒返回的小平台地址i进行校验
            if(tvBoxSSDPInfo!=null) {
                String serverIp = tvBoxSSDPInfo.getServerIp();
                String type = tvBoxSSDPInfo.getType();
                if(!TextUtils.isEmpty(serverIp)
                        &&!TextUtils.isEmpty(type)) {
                    AppApi.verifyNumByBoxSSDP(mContext,number,this);
                }else {
                    verifyCodeErrorCount++;
                }
            }else {
                verifyCodeErrorCount++;
            }
        }else {
            link_status.setVisibility(View.VISIBLE);
            link_status.setText("请输入电视中的三位数连接电视");
            relink_la.setVisibility(View.INVISIBLE);
        }


    }
}
