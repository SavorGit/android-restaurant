package com.savor.resturant.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextPaint;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.common.api.okhttp.OkHttpUtils;
import com.common.api.utils.DensityUtil;
import com.common.api.utils.ShowMessage;
import com.savor.resturant.R;
import com.savor.resturant.adapter.RecommendFoodAdapter;
import com.savor.resturant.adapter.RoomListAdapter;
import com.savor.resturant.bean.HotelBean;
import com.savor.resturant.bean.RecommendFoodAdvert;
import com.savor.resturant.bean.RoomInfo;
import com.savor.resturant.bean.SmallPlatInfoBySSDP;
import com.savor.resturant.bean.SmallPlatformByGetIp;
import com.savor.resturant.bean.TvBoxSSDPInfo;
import com.savor.resturant.core.AppApi;
import com.savor.resturant.core.ResponseErrorMessage;
import com.savor.resturant.widget.LoadingDialog;
import com.savor.resturant.widget.decoration.SpacesItemDecoration;

import java.util.List;


/**
 * 欢迎词文背景置页
 */
public class WelComeSetBgActivity extends BaseActivity implements View.OnClickListener,
        RoomListAdapter.OnRoomItemClicklistener {

    private Context context;
    private ImageView iv_left;
    private TextView tv_center;
    private TextView t1,t2,t3,t4,t5,t6,t7,t8;
    private RelativeLayout bg_l1,bg_l2,bg_l3,bg_l4,bg_l5,bg_l6,bg_l7,bg_l8;
    private String keyWord;
    private RecyclerView mRoomListView;
    private RoomListAdapter roomListAdapter;
    private RoomInfo currentRoom;
    private boolean isSelectRommState;
    private int erroCount;
    private LoadingDialog mLoadingDialog;
    private String CurrentTemplateId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.welcome_set_bg_layout);
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

        }
    }

    @Override
    public void getViews() {
        iv_left = (ImageView) findViewById(R.id.iv_left);
        tv_center = (TextView) findViewById(R.id.tv_center);
        mRoomListView = (RecyclerView) findViewById(R.id.rlv_room);
        t1 = (TextView) findViewById(R.id.t1);
        t2 = (TextView) findViewById(R.id.t2);
        t3 = (TextView) findViewById(R.id.t3);
        t4 = (TextView) findViewById(R.id.t4);
        t5 = (TextView) findViewById(R.id.t5);
        t6 = (TextView) findViewById(R.id.t6);
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
        initTitleBar();
        initRoomList();
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
        roomListAdapter.setOnRoomItemClickListener(this);

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.iv_left:
                if(isSelectRommState) {
                    hideRommList();
                }else {
                    finish();
                }
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
//        if(currentRoom == null) {
//            initRoomNotSelected();
//            return;
//        }
        CurrentTemplateId = templateId;
        if(!isSelectRommState) {
            showRoomList();
        }

    }

    private void proWord(String templateId,SmallPlatformByGetIp smallPlatformByGetIp, SmallPlatInfoBySSDP smallPlatInfoBySSDP, TvBoxSSDPInfo tvBoxSSDPInfo){
        erroCount = 0;
        // 1.通过getIp获取的小平台地址进行投屏
        if(smallPlatformByGetIp!=null&&!TextUtils.isEmpty(smallPlatformByGetIp.getLocalIp())) {
            String localIp = smallPlatformByGetIp.getLocalIp();
            String url = "http://"+localIp+":8080";
            showLoadingLayout();
            AppApi.wordPro(this,url,currentRoom.getBox_mac(),templateId,keyWord,this);
        }else {
            erroCount++;
        }

        // 2.通过小平台ssdp获取小平台地址进行投屏
        if(smallPlatInfoBySSDP!=null&&!TextUtils.isEmpty(smallPlatInfoBySSDP.getServerIp())) {
            String serverIp = smallPlatInfoBySSDP.getServerIp();
            String url = "http://"+serverIp+":8080";
            showLoadingLayout();
            AppApi.wordPro(this,url,currentRoom.getBox_mac(),templateId,keyWord,this);
        }else {
            erroCount++;
        }

        // 3.通过盒子ssdp获取小平台地址进行投屏
        if(tvBoxSSDPInfo!=null&&!TextUtils.isEmpty(tvBoxSSDPInfo.getServerIp())) {
            String serverIp = tvBoxSSDPInfo.getServerIp();
            String url = "http://"+serverIp+":8080";
            showLoadingLayout();
            AppApi.wordPro(this,url,currentRoom.getBox_mac(),templateId,keyWord,this);
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

    private void showRoomList() {
//        if(currentRoom!=null) {
//            tv_center.setText(currentRoom.getBox_name());
//        }else {
//            tv_center.setText("请选择投屏包间");
//        }
        mRoomListView.setVisibility(View.VISIBLE);
        tv_center.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
        iv_left.setImageResource(R.drawable.ico_close);
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.actionsheet_dialog_in);
        mRoomListView.startAnimation(animation);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                mRoomListView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animation animation) {

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        isSelectRommState  = true;
    }
    private void initRoomList() {
        //添加ItemDecoration，item之间的间隔
        int leftRight = DensityUtil.dip2px(this,15);
        int topBottom = DensityUtil.dip2px(this,15);
        GridLayoutManager roomLayoutManager = new GridLayoutManager(this,3);
        roomLayoutManager.setOrientation(GridLayoutManager.VERTICAL);
        mRoomListView.setLayoutManager(roomLayoutManager);
        roomListAdapter = new RoomListAdapter(this);
        mRoomListView.setAdapter(roomListAdapter);
        List<RoomInfo> roomList = mSession.getRoomList();
        mRoomListView.addItemDecoration(new SpacesItemDecoration(leftRight, topBottom, getResources().getColor(R.color.white)));
        if(roomList!=null && roomList.size()>0) {
            roomListAdapter.setData(roomList);
        }
    }

    @Override
    public void onRoomItemClick(RoomInfo roomInfo) {
        currentRoom = roomInfo;
        SmallPlatformByGetIp smallPlatformByGetIp = mSession.getmSmallPlatInfoByIp();
        SmallPlatInfoBySSDP smallPlatInfoBySSDP = mSession.getSmallPlatInfoBySSDP();
        TvBoxSSDPInfo tvBoxSSDPInfo = mSession.getTvBoxSSDPInfo();
        proWord(CurrentTemplateId,smallPlatformByGetIp,smallPlatInfoBySSDP,tvBoxSSDPInfo);
        hideRommList();

    }

    private void hideRommList() {

//        if(currentRoom!=null) {
//            tv_center.setText(currentRoom.getBox_name());
//        }else {
//            tv_center.setText("请选择投屏包间");
//        }
//        tv_center.setCompoundDrawablesWithIntrinsicBounds(null,null,getResources().getDrawable(R.drawable.ico_arrow_down),null);
        iv_left.setImageResource(R.drawable.back);

        Animation animation = AnimationUtils.loadAnimation(this, R.anim.actionsheet_dialog_out);
        mRoomListView.startAnimation(animation);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mRoomListView.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        isSelectRommState = false;
        resetRoomList();
    }

    @Override
    public void onSuccess(AppApi.Action method, Object obj) {
        hideLoadingLayout();
        switch (method) {
            case GET_WORD_PRO_JSON:
                HotelBean hotel = mSession.getHotelBean();
                ShowMessage.showToast(this,"投屏成功！");
                AppApi.reportLog(context,
                        hotel.getHotel_id()+"",
                        "",hotel.getInvitation(),
                        hotel.getTel(),
                        currentRoom.getRoom_id(),
                        "1",
                        "1",
                        "120",
                        "5",
                        CurrentTemplateId,
                        keyWord,
                        this
                        );
                break;

        }
    }

    @Override
    public void onError(AppApi.Action method, Object obj) {
        hideLoadingLayout();
        switch (method) {
            case GET_RECOMMEND_PRO_JSON:
                erroCount++;
                if(erroCount<3)
                    return;
                if(obj instanceof ResponseErrorMessage) {

                    ResponseErrorMessage message = (ResponseErrorMessage) obj;
                    int code = message.getCode();
                    String msg = message.getMessage();
                    showToast(msg);
                    errorLog();

                }else if(obj == AppApi.ERROR_TIMEOUT) {
                    //showToast("网络超时，请重试");
                    errorLog();
                }else if(obj == AppApi.ERROR_NETWORK_FAILED) {
                    //showToast("网络已断开，请检查");
                    errorLog();
                }
                break;
                default:
                    super.onError(method,obj);
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
        showRoomList();
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
        if(mLoadingDialog!=null) {
            mLoadingDialog.dismiss();
        }
    }

    private void errorLog(){
        HotelBean hotel = mSession.getHotelBean();
        AppApi.reportLog(context,
                hotel.getHotel_id()+"",
                "",hotel.getInvitation(),
                hotel.getTel(),
                currentRoom.getRoom_id(),
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

