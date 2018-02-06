package com.savor.resturant.activity;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.common.api.okhttp.OkHttpUtils;
import com.common.api.utils.AppUtils;
import com.common.api.utils.DensityUtil;
import com.common.api.utils.LogUtils;
import com.common.api.utils.ShowMessage;
import com.savor.resturant.R;
import com.savor.resturant.adapter.RoomServiceAdapter;
import com.savor.resturant.bean.KeyWordBean;
import com.savor.resturant.bean.ProResponse;
import com.savor.resturant.bean.RoomInfo;
import com.savor.resturant.bean.RoomService;
import com.savor.resturant.bean.SmallPlatInfoBySSDP;
import com.savor.resturant.bean.SmallPlatformByGetIp;
import com.savor.resturant.bean.TvBoxSSDPInfo;
import com.savor.resturant.core.AppApi;
import com.savor.resturant.core.ResponseErrorMessage;
import com.savor.resturant.utils.ActivitiesManager;
import com.savor.resturant.utils.ConstantValues;
import com.savor.resturant.widget.CommonDialog;
import com.savor.resturant.widget.LoadingDialog;
import com.savor.resturant.widget.decoration.SpacesItemDecoration;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import static com.savor.resturant.activity.RecommendFoodActivity.OperationType.TYPE_RECOMMEND_FOODS;

/**
 * 餐厅服务
 * @author hezd 2018/01/30
 */
public class ResturantServiceActivity extends BaseActivity implements View.OnClickListener, RoomServiceAdapter.OnItemClickListener, RoomServiceAdapter.OnWelBtnClickListener, RoomServiceAdapter.OnRecommendBtnClickListener {

    private RecyclerView mRoomListRlv;
    private ImageView mBackBtn;
    private TextView mTitleTv;
    private TextView mRightTv;
    private RoomServiceAdapter roomServiceAdapter;
    private int erroCount;
    private LoadingDialog loadingDialog;
    private String errorMsg;
    private RoomService currentRoom;
    private ArrayList<RoomInfo> roomList;

    public enum ProState {
        STATE_PLAY,
        STATE_STOP,
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resturant_service);
        LogUtils.d("savor:pro onCreate");
        getViews();
        setViews();
        setListeners();
        initProBroadcast();
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        LogUtils.d("savor:pro onRestoreInstanceState");
        restartService();
        roomList = (ArrayList<RoomInfo>) savedInstanceState.getSerializable("room_list");
        mSession.setRoomList(roomList);
        if(roomList !=null) {
            List<RoomService> roomServiceList = new ArrayList<>();
            for(int i = 0; i< roomList.size(); i++) {
                RoomService roomService = new RoomService();
                roomService.setRoomInfo(roomList.get(i));
                roomServiceList.add(roomService);
            }
            mSession.setRoomServiceList(roomServiceList);
            roomServiceAdapter.setData(roomServiceList);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable("room_list",roomList);
    }

    private void initProBroadcast() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ConstantValues.ACTION_RECOMMEND_PLAY_DELAYED_5MIN);
        intentFilter.addAction(ConstantValues.ACTION_REFRESH_PRO_STATE_DELAYED);
        RoomListRefreshReceiver refreshReceiver = new RoomListRefreshReceiver();
        registerReceiver(refreshReceiver,intentFilter);
    }

    @Override
    public void getViews() {
        mBackBtn = (ImageView) findViewById(R.id.iv_left);
        mTitleTv = (TextView) findViewById(R.id.tv_center);
        mRightTv = (TextView) findViewById(R.id.tv_right);
        mRoomListRlv = (RecyclerView) findViewById(R.id.rlv_room_list);
    }

    @Override
    public void setViews() {
        mTitleTv.setText("餐厅服务");
        mRightTv.setVisibility(View.VISIBLE);
        mRightTv.setText("恢复默认");

        roomServiceAdapter = new RoomServiceAdapter(this);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRoomListRlv.setLayoutManager(linearLayoutManager);
        mRoomListRlv.setAdapter(roomServiceAdapter);

        //添加ItemDecoration，item之间的间隔
        int leftRight = DensityUtil.dip2px(this,0);
        int topBottom = DensityUtil.dip2px(this,10);

        mRoomListRlv.addItemDecoration(new SpacesItemDecoration(leftRight, topBottom, getResources().getColor(R.color.color_ece6de)));

        roomList = mSession.getRoomList();
        if(roomList !=null) {
            List<RoomService> roomServiceList = new ArrayList<>();
            for(int i = 0; i< roomList.size(); i++) {
                RoomService roomService = new RoomService();
                roomService.setRoomInfo(roomList.get(i));
                roomServiceList.add(roomService);
            }
            mSession.setRoomServiceList(roomServiceList);
            roomServiceAdapter.setData(roomServiceList);
        }

    }

    @Override
    public void setListeners() {
        mRightTv.setOnClickListener(this);
        mBackBtn.setOnClickListener(this);
        roomServiceAdapter.setOnItemClickListener(this);
        roomServiceAdapter.setOnWelBtnClickListener(this);
        roomServiceAdapter.setOnRecommendBtnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_right:
                new CommonDialog(this, "所有包间的欢迎词将回复默认状态：\n欢迎光临，祝您用餐愉快！",
                        new CommonDialog.OnConfirmListener() {
                            @Override
                            public void onConfirm() {
                                KeyWordBean keyWordBean = mSession.getKeyWordBean();
                                if(keyWordBean!=null) {
                                    keyWordBean.setKeyWord("欢迎光临，祝您用餐愉快！");
                                    mSession.setkeyWordBean(keyWordBean);
                                }
                            }
                        },
                        new CommonDialog.OnCancelListener() {
                            @Override
                            public void onCancel() {

                            }
                        }).show();
                break;
            case R.id.iv_left:
                finish();
                break;
        }
    }

    @Override
    public void onItemClick(RoomService roomInfo, RoomServiceAdapter.ProType type) {
        Intent intent;
        switch (type) {
            case TYPE_WELCOM:
                intent = new Intent(this,WelComeSetTextNewActivity.class);
                intent.putExtra("box",roomInfo.getRoomInfo());
                startActivity(intent);
                break;
            case TYPE_RECOMMEND:
                intent = new Intent(this,Recommend4ServiceActivity.class);
                intent.putExtra("box",roomInfo.getRoomInfo());
                intent.putExtra("type",TYPE_RECOMMEND_FOODS);
                startActivity(intent);
                break;
        }
    }

    @Override
    public void onWelBtnClick(RoomService roomInfo, RoomServiceAdapter.ProType type) {
        currentRoom = roomInfo;
        resetErrorSettings();
        RoomInfo info = currentRoom.getRoomInfo();
        boolean welPlay = info.isWelPlay();
        SmallPlatformByGetIp smallPlatformByGetIp = mSession.getmSmallPlatInfoByIp();
        SmallPlatInfoBySSDP smallPlatInfoBySSDP = mSession.getSmallPlatInfoBySSDP();
        TvBoxSSDPInfo tvBoxSSDPInfo = mSession.getTvBoxSSDPInfo();

        if(welPlay) {
            ShowMessage.showToast(this,"退出成功");
            currentRoom.getRoomInfo().setWelPlay(false);
            currentRoom.getRoomInfo().setRecommendPlay(false);
            roomServiceAdapter.notifyDataSetChanged();
            stopPro(info, smallPlatformByGetIp, smallPlatInfoBySSDP, tvBoxSSDPInfo);
        }else {
//            ShowMessage.showToast(this,"投屏成功");
//            OkHttpUtils.getInstance().getOkHttpClient().dispatcher().cancelAll();
//            currentRoom.getRoomInfo().setRecommendPlay(false);
//            currentRoom.getRoomInfo().setWelPlay(true);
//            roomServiceAdapter.notifyDataSetChanged();
//
//            recommendPlayDelayed();
            showLoadingLayout();
            erroCount = 0;
            // 1.通过getIp获取的小平台地址进行投屏
            if(smallPlatformByGetIp!=null&&!TextUtils.isEmpty(smallPlatformByGetIp.getLocalIp())) {
                String localIp = smallPlatformByGetIp.getLocalIp();
                String url = "http://"+localIp+":8080";
                KeyWordBean keyWordBean = mSession.getKeyWordBean();
                String templateId = "1";
                String keyword = "欢迎光临祝您用餐愉快";
                if(keyWordBean!=null&&!TextUtils.isEmpty(keyWordBean.getTemplateId())&&!TextUtils.isEmpty(keyWordBean.getKeyWord())) {
                    templateId = keyWordBean.getTemplateId();
                    keyword = keyWordBean.getKeyWord();
                }
                AppApi.welRecommendPro(this,url,info.getBox_mac(),templateId,keyword,this);
            }else {
                erroCount++;
            }

            // 2.通过小平台ssdp获取小平台地址进行投屏
            if(smallPlatInfoBySSDP!=null&&!TextUtils.isEmpty(smallPlatInfoBySSDP.getServerIp())) {
                String serverIp = smallPlatInfoBySSDP.getServerIp();
                String url = "http://"+serverIp+":8080";
                KeyWordBean keyWordBean = mSession.getKeyWordBean();
                String templateId = "1";
                String keyword = "欢迎光临祝您用餐愉快";
                if(keyWordBean!=null&&!TextUtils.isEmpty(keyWordBean.getTemplateId())&&!TextUtils.isEmpty(keyWordBean.getKeyWord())) {
                    templateId = keyWordBean.getTemplateId();
                    keyword = keyWordBean.getKeyWord();
                }
                AppApi.welRecommendPro(this,url,info.getBox_mac(),templateId,keyword,this);
            }else {
                erroCount++;
            }

            // 3.通过盒子ssdp获取小平台地址进行投屏
            if(tvBoxSSDPInfo!=null&&!TextUtils.isEmpty(tvBoxSSDPInfo.getServerIp())) {
                String serverIp = tvBoxSSDPInfo.getServerIp();
                String url = "http://"+serverIp+":8080";
                KeyWordBean keyWordBean = mSession.getKeyWordBean();
                String templateId = "1";
                String keyword = "欢迎光临祝您用餐愉快";
                if(keyWordBean!=null&&!TextUtils.isEmpty(keyWordBean.getTemplateId())&&!TextUtils.isEmpty(keyWordBean.getKeyWord())) {
                    templateId = keyWordBean.getTemplateId();
                    keyword = keyWordBean.getKeyWord();
                }
                AppApi.welRecommendPro(this,url,info.getBox_mac(),templateId,keyword,this);
            }else {
                erroCount++;
                if(erroCount>=3) {
                    hideLoadingLayout();
                    if(AppUtils.isNetworkAvailable(this)) {
                        showToast("网络超时，请重试");
                    }else {
                        showToast("网络已断开，请检查");
                    }

                }
            }
        }

    }

    private void stopPro(RoomInfo info, SmallPlatformByGetIp smallPlatformByGetIp, SmallPlatInfoBySSDP smallPlatInfoBySSDP, TvBoxSSDPInfo tvBoxSSDPInfo) {
        erroCount = 0;
        // 1.通过getIp获取的小平台地址进行投屏
        if(smallPlatformByGetIp!=null&&!TextUtils.isEmpty(smallPlatformByGetIp.getLocalIp())) {
            String localIp = smallPlatformByGetIp.getLocalIp();
            String url = "http://"+localIp+":8080";
            AppApi.stopBySmall(this,url,info.getBox_mac(),this);
        }else {
            erroCount++;
        }

        // 2.通过小平台ssdp获取小平台地址进行投屏
        if(smallPlatInfoBySSDP!=null&&!TextUtils.isEmpty(smallPlatInfoBySSDP.getServerIp())) {
            String serverIp = smallPlatInfoBySSDP.getServerIp();
            String url = "http://"+serverIp+":8080";
            AppApi.stopBySmall(this,url,info.getBox_mac(),this);
        }else {
            erroCount++;
        }

        // 3.通过盒子ssdp获取小平台地址进行投屏
        if(tvBoxSSDPInfo!=null&&!TextUtils.isEmpty(tvBoxSSDPInfo.getServerIp())) {
            String serverIp = tvBoxSSDPInfo.getServerIp();
            String url = "http://"+serverIp+":8080";
            AppApi.stopBySmall(this,url,info.getBox_mac(),this);
        }else {
            erroCount++;
//            if(erroCount>=3) {
//                hideLoadingLayout();
//                if(AppUtils.isNetworkAvailable(this)) {
//                    showToast("网络超时，请重试");
//                }else {
//                    showToast("网络已断开，请检查");
//                }
//
//            }
        }
    }

    private void resetErrorSettings() {
        erroCount = 0;
        errorMsg = null;
    }

    @Override
    public void onRecommendBtnClick(RoomService roomInfo, RoomServiceAdapter.ProType type) {
//        roomInfo.startRecommendTimer(getApplicationContext(),10);
        currentRoom = roomInfo;
        RoomInfo info = roomInfo.getRoomInfo();
        SmallPlatformByGetIp smallPlatformByGetIp = mSession.getmSmallPlatInfoByIp();
        SmallPlatInfoBySSDP smallPlatInfoBySSDP = mSession.getSmallPlatInfoBySSDP();
        TvBoxSSDPInfo tvBoxSSDPInfo = mSession.getTvBoxSSDPInfo();

        if(info.isRecommendPlay()) {
            ShowMessage.showToast(this,"退出成功");
            roomInfo.getRoomInfo().setWelPlay(false);
            roomInfo.getRoomInfo().setRecommendPlay(false);
            roomServiceAdapter.notifyDataSetChanged();
            stopPro(info, smallPlatformByGetIp, smallPlatInfoBySSDP, tvBoxSSDPInfo);
        }else {
            showLoadingLayout();
            erroCount = 0;
            // 1.通过getIp获取的小平台地址进行投屏
            if(smallPlatformByGetIp!=null&&!TextUtils.isEmpty(smallPlatformByGetIp.getLocalIp())) {
                String localIp = smallPlatformByGetIp.getLocalIp();
                String url = "http://"+localIp+":8080";
                AppApi.recommendPro(this,url,info.getBox_mac(),10+"","-1",this);
            }else {
                erroCount++;
            }

            // 2.通过小平台ssdp获取小平台地址进行投屏
            if(smallPlatInfoBySSDP!=null&&!TextUtils.isEmpty(smallPlatInfoBySSDP.getServerIp())) {
                String serverIp = smallPlatInfoBySSDP.getServerIp();
                String url = "http://"+serverIp+":8080";
                AppApi.recommendPro(this,url,info.getBox_mac(),10+"","-1",this);
            }else {
                erroCount++;
            }

            // 3.通过盒子ssdp获取小平台地址进行投屏
            if(tvBoxSSDPInfo!=null&&!TextUtils.isEmpty(tvBoxSSDPInfo.getServerIp())) {
                String serverIp = tvBoxSSDPInfo.getServerIp();
                String url = "http://"+serverIp+":8080";
                AppApi.recommendPro(this,url,info.getBox_mac(),10+"","-1",this);
            }else {
                erroCount++;
                if(erroCount>=3) {
                    hideLoadingLayout();
                    if(AppUtils.isNetworkAvailable(this)) {
                        showToast("网络超时，请重试");
                    }else {
                        showToast("网络已断开，请检查");
                    }

                }
            }
        }

    }

    @Override
    public void onSuccess(AppApi.Action method, Object obj) {
        super.onSuccess(method, obj);
        switch (method) {
            case GET_RECOMMEND_PRO_JSON:
                hideLoadingLayout();
                if(obj instanceof ProResponse) {
                    ProResponse proResponse = (ProResponse) obj;
                    String founded_count = proResponse.getFounded_count();
                    int count = 1;
                    try {
                        count = Integer.valueOf(founded_count);
                    }catch (Exception e) {}


                    int sec = 1;
                    if(count == 1) {
                        sec = count*20;
                    }else {
                        sec = count*10;
                    }
                    currentRoom.startRecommendTimer(this,sec);
                }
                break;
            case GET_STOP_BY_SMALL_JSON:
                currentRoom.getRoomInfo().setRecommendPlay(false);
                currentRoom.getRoomInfo().setWelPlay(false);
                roomServiceAdapter.notifyDataSetChanged();
                break;
            case GET_WEL_RECOMMEND_JSON:
                hideLoadingLayout();
                ShowMessage.showToast(this,"投屏成功");
                OkHttpUtils.getInstance().getOkHttpClient().dispatcher().cancelAll();
                currentRoom.getRoomInfo().setRecommendPlay(false);
                currentRoom.getRoomInfo().setWelPlay(true);
                roomServiceAdapter.notifyDataSetChanged();

                recommendPlayDelayed(obj);
                break;
        }
    }

    private void recommendPlayDelayed(Object object) {
        /************5分钟以后开始播放推荐菜***********/
        ProResponse proResponse = (ProResponse) object;
        String founded_count = proResponse.getFounded_count();
        int count = 1;
        try {
            count = Integer.valueOf(founded_count);
        }catch (Exception e) {}


        int sec = 1;
        if(count == 1) {
            sec = count*20;
        }else {
            sec = count*10;
        }
        currentRoom.startWelcomeAndRecommendTimer(getApplicationContext(),60*5,sec);
//        ProjectionService.startActionRecommend(this,currentRoom);
//        int requestCode = 0;
//        try {
//            requestCode = Integer.valueOf(currentRoom.getRoom_id());
//        }catch (Exception e){}
//
//        //创建Intent对象，action为ELITOR_CLOCK，附加信息为字符串“你该打酱油了”
//        Intent intent = new Intent(ConstantValues.ACTION_RECOMMEND_PLAY_DELAYED_5MIN);
//        intent.putExtra("box", currentRoom);
//
//        //AlarmManager对象,注意这里并不是new一个对象，Alarmmanager为系统级服务
//        AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
////        if(recommendPlayDelayedIntent!=null)
////            am.cancel(recommendPlayDelayedIntent);
//
//        //定义一个PendingIntent对象，PendingIntent.getBroadcast包含了sendBroadcast的动作。
//        //也就是发送了action 为"ELITOR_CLOCK"的intent
//        PendingIntent recommendPlayDelayedIntent = PendingIntent.getBroadcast(this,requestCode, intent, 0);
//
//        //设置闹钟从当前时间开始，每隔5s执行一次PendingIntent对象pi，注意第一个参数与第二个参数的关系
//        // 5秒后通过PendingIntent pi对象发送广播
//        am.set(AlarmManager.RTC_WAKEUP,  System.currentTimeMillis()+10  * 1000, recommendPlayDelayedIntent);
    }

    @Override
    public void onError(AppApi.Action method, Object obj) {
        switch (method) {
            case GET_STOP_BY_SMALL_JSON:
                hideLoadingLayout();
                break;
            case GET_RECOMMEND_PRO_JSON:
            case GET_WEL_RECOMMEND_JSON:
                erroCount++;
                if(obj instanceof ResponseErrorMessage) {
                    ResponseErrorMessage message = (ResponseErrorMessage) obj;
                    String msg = message.getMessage();
                    if(!TextUtils.isEmpty(msg)) {
                        errorMsg = msg;
                    }
                }
                if(erroCount <3)
                    return;
                hideLoadingLayout();
                if(!TextUtils.isEmpty(errorMsg)) {
                    showToast(errorMsg);
                    resetErrorSettings();
                }else {
                    showToast("电视未开机\n请打开电视后操作");
                }
                break;
        }
    }

    @Override
    public void showLoadingLayout() {
        if(loadingDialog == null)
            loadingDialog = new LoadingDialog(this);
        loadingDialog.show();
    }

    @Override
    public void hideLoadingLayout() {
        if(loadingDialog!=null&&loadingDialog.isShowing()&&!isFinishing()) {
            loadingDialog.dismiss();
        }
    }

    public class RoomListRefreshReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            switch (action) {
                case ConstantValues.ACTION_RECOMMEND_PLAY_DELAYED_5MIN:
                    LogUtils.d("savor:service 延迟10秒播放推荐菜 ");
                    break;
                case ConstantValues.ACTION_REFRESH_PRO_STATE_DELAYED:
                    LogUtils.d("savor:service 延迟10秒停止 boxId");
                    break;
            }
            roomServiceAdapter.notifyDataSetChanged();
        }
    }

    /**
     * 延迟停止播放（当推荐菜播放完毕以后停止）
     */
    private void stopProDelayed(RoomInfo roomInfo) {
        int requestCode = 0;
        try {
            requestCode = Integer.valueOf(roomInfo.getRoom_id());
        }catch (Exception e){}
    /************推荐菜播放完毕后刷新播放状态***********/
        //创建Intent对象，action为ELITOR_CLOCK，附加信息为字符串“你该打酱油了”
        Intent intent = new Intent(ConstantValues.ACTION_REFRESH_PRO_STATE_DELAYED);
        intent.putExtra("box", roomInfo);

        //AlarmManager对象,注意这里并不是new一个对象，Alarmmanager为系统级服务
        AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
//        if(stopProDelayedIntent!=null)
//            am.cancel(stopProDelayedIntent);

        //定义一个PendingIntent对象，PendingIntent.getBroadcast包含了sendBroadcast的动作。
        //也就是发送了action 为"ELITOR_CLOCK"的intent
        PendingIntent stopProDelayedIntent = PendingIntent.getBroadcast(this, requestCode, intent, 0);

        //设置闹钟从当前时间开始，每隔5s执行一次PendingIntent对象pi，注意第一个参数与第二个参数的关系
        // 5秒后通过PendingIntent pi对象发送广播
        am.set(AlarmManager.RTC_WAKEUP,  System.currentTimeMillis()+10 * 1000, stopProDelayedIntent);
    }
}
