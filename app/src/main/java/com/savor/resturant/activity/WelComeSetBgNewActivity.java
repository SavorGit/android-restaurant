package com.savor.resturant.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextPaint;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.common.api.okhttp.OkHttpUtils;
import com.common.api.utils.DensityUtil;
import com.common.api.utils.ShowMessage;
import com.savor.resturant.R;
import com.savor.resturant.adapter.RoomListAdapter;
import com.savor.resturant.bean.HotelBean;
import com.savor.resturant.bean.KeyWordBean;
import com.savor.resturant.bean.RoomInfo;
import com.savor.resturant.bean.RoomService;
import com.savor.resturant.bean.SmallPlatInfoBySSDP;
import com.savor.resturant.bean.SmallPlatformByGetIp;
import com.savor.resturant.bean.TvBoxSSDPInfo;
import com.savor.resturant.core.AppApi;
import com.savor.resturant.core.ResponseErrorMessage;
import com.savor.resturant.utils.ActivitiesManager;
import com.savor.resturant.widget.LoadingDialog;

import java.util.List;


/**
 * 欢迎词文背景置页
 */
public class WelComeSetBgNewActivity extends BaseActivity implements View.OnClickListener{

    private Context context;
    private ImageView iv_left;
    private TextView tv_center;
    private TextView t1,t2,t3,t4,t5,t6,t7,t8;
    private RelativeLayout bg_l1,bg_l2,bg_l3,bg_l4,bg_l5,bg_l6,bg_l7,bg_l8;
    private ImageView h1,h2,h3,h4,h5,h6,h7,h8;
    private String keyWord;
//    private RecyclerView mRoomListView;
//    private RoomListAdapter roomListAdapter;
    //private RoomInfo currentRoom;
    private boolean isSelectRommState;
    private int erroCount;
    private LoadingDialog mLoadingDialog;
    private String CurrentTemplateId = "1";
    private String erroMsg1;
    private String box_mac;
    private String is_default;
    private TextView tv_right;
    private RoomInfo roomInfo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.welcome_set_bg_new_layout);
        getWord();
        context = this;
        getViews();
        setViews();
        setListeners();
    }

    private void getWord(){
        Intent intent = getIntent();
        if (intent != null) {
            keyWord = intent.getStringExtra("keyWord");
            roomInfo = (RoomInfo)intent.getSerializableExtra("box");
            is_default = intent.getStringExtra("is_default");
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
        t6 = (TextView) findViewById(R.id.t6);
        t7 = (TextView) findViewById(R.id.t7);
        t8 = (TextView) findViewById(R.id.t8);
        tv_right = (TextView) findViewById(R.id.tv_right);
        bg_l1 = (RelativeLayout) findViewById(R.id.bg_l1);
        bg_l2 = (RelativeLayout) findViewById(R.id.bg_l2);
        bg_l3 = (RelativeLayout) findViewById(R.id.bg_l3);
        bg_l4 = (RelativeLayout) findViewById(R.id.bg_l4);
        bg_l5 = (RelativeLayout) findViewById(R.id.bg_l5);
        bg_l6 = (RelativeLayout) findViewById(R.id.bg_l6);
        bg_l7 = (RelativeLayout) findViewById(R.id.bg_l7);
        bg_l8 = (RelativeLayout) findViewById(R.id.bg_l8);
        h1 = (ImageView) findViewById(R.id.h1);
        h2 = (ImageView) findViewById(R.id.h2);
        h3 = (ImageView) findViewById(R.id.h4);
        h4 = (ImageView) findViewById(R.id.h4);
        h5 = (ImageView) findViewById(R.id.h5);
        h6 = (ImageView) findViewById(R.id.h6);
        h7 = (ImageView) findViewById(R.id.h7);
        h8 = (ImageView) findViewById(R.id.h8);


    }

    @Override
    public void setViews() {
//        initTitleBar();
//        initRoomList();
        tv_right.setVisibility(View.VISIBLE);
        tv_right.setText("完成");
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
        tv_center.setOnClickListener(this);
        bg_l1.setOnClickListener(this);
        bg_l2.setOnClickListener(this);
        bg_l3.setOnClickListener(this);
        bg_l4.setOnClickListener(this);
        bg_l5.setOnClickListener(this);
        bg_l6.setOnClickListener(this);
        bg_l7.setOnClickListener(this);
        bg_l8.setOnClickListener(this);
        tv_right.setOnClickListener(this);
       // roomListAdapter.setOnRoomItemClickListener(this);

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.iv_left:
//                if(isSelectRommState) {
//                    hideRommList();
//                }else {
//                    finish();
//                }
                finish();
                break;
            case R.id.bg_l1:
                setPro("1");
                break;
            case R.id.bg_l2:
                setPro("2");
                break;
            case R.id.bg_l3:
                setPro("3");
                break;
            case R.id.bg_l4:
                setPro("4");
                break;
            case R.id.bg_l5:
                setPro("5");
                break;
            case R.id.bg_l6:
                setPro("6");
                break;
            case R.id.bg_l7:
                setPro("7");
                break;
            case R.id.bg_l8:
                setPro("8");
                break;
            case R.id.tv_right:
                toWord();
                break;
            case R.id.tv_center:
//                if(!isSelectRommState) {
//                    showRoomList();
//                }

                break;
            default:
                break;
        }
    }


    private void setPro(String templateId){
        CurrentTemplateId = templateId;
        if ("1".equals(templateId)) {
            h1.setImageResource(R.drawable.hb_1c);
            h2.setImageResource(R.drawable.hb_2);
            h3.setImageResource(R.drawable.hb_3);
            h4.setImageResource(R.drawable.hb_4);
            h5.setImageResource(R.drawable.hb_5);
            h6.setImageResource(R.drawable.hb_6);
            h7.setImageResource(R.drawable.hb_7);
            h8.setImageResource(R.drawable.hb_8);

        }else if("2".equals(templateId)){
            h1.setImageResource(R.drawable.hb_1);
            h2.setImageResource(R.drawable.hb_2c);
            h3.setImageResource(R.drawable.hb_3);
            h4.setImageResource(R.drawable.hb_4);
            h5.setImageResource(R.drawable.hb_5);
            h6.setImageResource(R.drawable.hb_6);
            h7.setImageResource(R.drawable.hb_7);
            h8.setImageResource(R.drawable.hb_8);
        }else if("3".equals(templateId)){
            h1.setImageResource(R.drawable.hb_1);
            h2.setImageResource(R.drawable.hb_2);
            h3.setImageResource(R.drawable.hb_3c);
            h4.setImageResource(R.drawable.hb_4);
            h5.setImageResource(R.drawable.hb_5);
            h6.setImageResource(R.drawable.hb_6);
            h7.setImageResource(R.drawable.hb_7);
            h8.setImageResource(R.drawable.hb_8);
        }else if("4".equals(templateId)){
            h1.setImageResource(R.drawable.hb_1);
            h2.setImageResource(R.drawable.hb_2);
            h3.setImageResource(R.drawable.hb_3);
            h4.setImageResource(R.drawable.hb_4c);
            h5.setImageResource(R.drawable.hb_5);
            h6.setImageResource(R.drawable.hb_6);
            h7.setImageResource(R.drawable.hb_7);
            h8.setImageResource(R.drawable.hb_8);
        }else if("5".equals(templateId)){
            h1.setImageResource(R.drawable.hb_1);
            h2.setImageResource(R.drawable.hb_2);
            h3.setImageResource(R.drawable.hb_3);
            h4.setImageResource(R.drawable.hb_4);
            h5.setImageResource(R.drawable.hb_5);
            h6.setImageResource(R.drawable.hb_6);
            h7.setImageResource(R.drawable.hb_7);
            h8.setImageResource(R.drawable.hb_8);
        }else if("6".equals(templateId)){
            h1.setImageResource(R.drawable.hb_1);
            h2.setImageResource(R.drawable.hb_2);
            h3.setImageResource(R.drawable.hb_3);
            h4.setImageResource(R.drawable.hb_4);
            h5.setImageResource(R.drawable.hb_5);
            h6.setImageResource(R.drawable.hb_6c);
            h7.setImageResource(R.drawable.hb_7);
            h8.setImageResource(R.drawable.hb_8);
        }else if("7".equals(templateId)){
            h1.setImageResource(R.drawable.hb_1);
            h2.setImageResource(R.drawable.hb_2);
            h3.setImageResource(R.drawable.hb_3);
            h4.setImageResource(R.drawable.hb_4);
            h5.setImageResource(R.drawable.hb_5);
            h6.setImageResource(R.drawable.hb_6);
            h7.setImageResource(R.drawable.hb_7c);
            h8.setImageResource(R.drawable.hb_8);
        }else if("8".equals(templateId)){
            h1.setImageResource(R.drawable.hb_1);
            h2.setImageResource(R.drawable.hb_2);
            h3.setImageResource(R.drawable.hb_3);
            h4.setImageResource(R.drawable.hb_4);
            h5.setImageResource(R.drawable.hb_5);
            h6.setImageResource(R.drawable.hb_6);
            h7.setImageResource(R.drawable.hb_7);
            h8.setImageResource(R.drawable.hb_8c);
        }

    }

    
    private void proWord(String templateId,SmallPlatformByGetIp smallPlatformByGetIp, SmallPlatInfoBySSDP smallPlatInfoBySSDP, TvBoxSSDPInfo tvBoxSSDPInfo){
        erroCount = 0;
        // 1.通过getIp获取的小平台地址进行投屏
        if(smallPlatformByGetIp!=null&&!TextUtils.isEmpty(smallPlatformByGetIp.getLocalIp())) {
            String localIp = smallPlatformByGetIp.getLocalIp();
            String url = "http://"+localIp+":8080";
            showLoadingLayout();
            AppApi.wordPro(this,url,roomInfo.getBox_mac(),templateId,keyWord,this);
        }else {
            erroCount++;
        }

        // 2.通过小平台ssdp获取小平台地址进行投屏
        if(smallPlatInfoBySSDP!=null&&!TextUtils.isEmpty(smallPlatInfoBySSDP.getServerIp())) {
            String serverIp = smallPlatInfoBySSDP.getServerIp();
            String url = "http://"+serverIp+":8080";
            showLoadingLayout();
            AppApi.wordPro(this,url,roomInfo.getBox_mac(),templateId,keyWord,this);
        }else {
            erroCount++;
        }

        // 3.通过盒子ssdp获取小平台地址进行投屏
        if(tvBoxSSDPInfo!=null&&!TextUtils.isEmpty(tvBoxSSDPInfo.getServerIp())) {
            String serverIp = tvBoxSSDPInfo.getServerIp();
            String url = "http://"+serverIp+":8080";
            showLoadingLayout();
            AppApi.wordPro(this,url,roomInfo.getBox_mac(),templateId,keyWord,this);
        }else {
            erroCount++;
        }
    }
    private void initTitleBar() {
       // tv_center.setText("请选择投屏包间");
        TextPaint tp = tv_center.getPaint();
        tp.setFakeBoldText(true);
//        tv_center.setCompoundDrawablesWithIntrinsicBounds(null,null,getResources().getDrawable(R.drawable.ico_arrow_down),null);
//        tv_center.setCompoundDrawablePadding(DensityUtil.dip2px(this,10));
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) iv_left.getLayoutParams();
        layoutParams.setMargins(DensityUtil.dip2px(this,15),0,0,0);

//        RoomInfo bindRoom = mSession.getBindRoom();
//        if(bindRoom!=null&&!TextUtils.isEmpty(bindRoom.getBox_name())) {
//            tv_center.setText(bindRoom.getBox_name());
//        }
//        currentRoom = bindRoom;
    }


    private void toWord(){
        if ("1".equals(is_default)) {// 设置默认欢迎词，刷新列表
            KeyWordBean keyWordBean = new KeyWordBean();
            keyWordBean.setDefault(true);
            keyWordBean.setKeyWord(keyWord);
            keyWordBean.setTemplateId(CurrentTemplateId);
            mSession.setkeyWordBean(keyWordBean);

            RoomService roomService = new RoomService();
            roomService.setRoomInfo(roomInfo);

            List<RoomService> roomServiceList = mSession.getRoomServiceList();
            if(roomServiceList!=null&&roomServiceList.contains(roomService)) {
                int i = roomServiceList.indexOf(roomService);
                RoomService currentService = roomServiceList.get(i);
                currentService.refresh(this);
            }
        }else {// 没设置默认欢迎词，只针对当前包间，判断是否正在播放
            KeyWordBean keyWordBean = mSession.getKeyWordBean();
            if(keyWordBean!=null) {
                keyWordBean.setDefault(false);
            }
            RoomService roomService = new RoomService();
            roomService.setRoomInfo(roomInfo);

            List<RoomService> roomServiceList = mSession.getRoomServiceList();
            if(roomServiceList!=null&&roomServiceList.contains(roomService)) {
                int i = roomServiceList.indexOf(roomService);
                RoomService currentService = roomServiceList.get(i);
                boolean welPlay = currentService.getRoomInfo().isWelPlay();
                currentService.getRoomInfo().setWord(keyWord);
                currentService.getRoomInfo().setTemplateId(CurrentTemplateId);
                if(!welPlay) {
                    currentService.refresh(this);
                }
            }
        }
        ActivitiesManager.getInstance().popSpecialActivity(WelComeSetTextNewActivity.class);
        finish();
    }

    @Override
    public void onError(AppApi.Action method, Object obj) {
        hideLoadingLayout();
        switch (method) {
            case GET_RECOMMEND_PRO_JSON:

                if(obj instanceof ResponseErrorMessage) {

                    ResponseErrorMessage message = (ResponseErrorMessage) obj;
                    int code = message.getCode();
                    String msg = message.getMessage();
                    erroMsg1 = msg;
                    //showToast(msg);
                    errorLog();

                }else if(obj == AppApi.ERROR_TIMEOUT) {
                    //showToast("网络超时，请重试");
                   errorLog();
                }else if(obj == AppApi.ERROR_NETWORK_FAILED) {
                    //showToast("网络已断开，请检查");
                    errorLog();
                }
                erroCount++;
                if(erroCount<3)
                    return;

                if (!TextUtils.isEmpty(erroMsg1)) {
                    showToast(erroMsg1);
                }else {
                    showToast( "网络超时，请重试");

                }
                break;
                default:
                    //super.onError(method,obj);
                    break;
        }
    }

    private void resetRoomList() {
        List<RoomInfo> roomList = mSession.getRoomList();
        if(roomList!=null) {
            for(RoomInfo info:roomList) {
                info.setSelected(false);
            }
        }
    }

    @Override
    protected void onDestroy() {
        // 清楚房间选择记录
        resetRoomList();
        OkHttpUtils.getInstance().getOkHttpClient().dispatcher().cancelAll();
        super.onDestroy();
    }

    private void initRoomNotSelected() {
        ShowMessage.showToast(this, "请选择包间");

    }

    @Override
    public void showLoadingLayout() {
        if(mLoadingDialog == null) {
            mLoadingDialog = new LoadingDialog(this);
        }
        mLoadingDialog.show();
    }

    @Override
    public void hideLoadingLayout() {
        if(mLoadingDialog!=null&&mLoadingDialog.isShowing()&&!isFinishing()) {
            mLoadingDialog.dismiss();
        }
    }

    private void errorLog(){
        HotelBean hotel = mSession.getHotelBean();
        AppApi.reportLog(context,
                hotel.getHotel_id()+"",
                "",hotel.getInvite_id(),
                hotel.getTel(),
                box_mac,
                "1",
                "0",
                "120",
                "5",
                CurrentTemplateId,
                keyWord,
                this
        );
    }

}

