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
import android.view.TextureView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.common.api.okhttp.OkHttpUtils;
import com.common.api.utils.DensityUtil;
import com.common.api.utils.ShowMessage;
import com.savor.resturant.R;
import com.savor.resturant.adapter.RoomServiceAdapter;
import com.savor.resturant.bean.RoomInfo;
import com.savor.resturant.bean.SmallPlatInfoBySSDP;
import com.savor.resturant.bean.SmallPlatformByGetIp;
import com.savor.resturant.bean.TvBoxSSDPInfo;
import com.savor.resturant.core.AppApi;
import com.savor.resturant.core.ResponseErrorMessage;
import com.savor.resturant.utils.ConstantValues;
import com.savor.resturant.widget.LoadingDialog;
import com.savor.resturant.widget.decoration.SpacesItemDecoration;

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
    private RoomInfo currentRoom;
    /**延迟5分钟播放推荐菜*/
    private PendingIntent recommendPlayDelayedIntent;
    /**推荐菜播放完毕修改刷新播放状态*/
    private PendingIntent stopProDelayedIntent;

    public enum ProState {
        STATE_PLAY,
        STATE_STOP,
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resturant_service);

        getViews();
        setViews();
        setListeners();
        initProBroadcast();
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

        List<RoomInfo> roomList = mSession.getRoomList();
        roomServiceAdapter.setData(roomList);
    }

    @Override
    public void setListeners() {
        mBackBtn.setOnClickListener(this);
        roomServiceAdapter.setOnItemClickListener(this);
        roomServiceAdapter.setOnWelBtnClickListener(this);
        roomServiceAdapter.setOnRecommendBtnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_left:
                finish();
                break;
        }
    }

    @Override
    public void onItemClick(RoomInfo roomInfo, RoomServiceAdapter.ProType type) {
        switch (type) {
            case TYPE_WELCOM:

                break;
            case TYPE_RECOMMEND:
                Intent intent = new Intent(this,Recommend4ServiceActivity.class);
                intent.putExtra("box",roomInfo);
                intent.putExtra("type",TYPE_RECOMMEND_FOODS);
                startActivity(intent);
                break;
        }
    }

    @Override
    public void onWelBtnClick(RoomInfo roomInfo, RoomServiceAdapter.ProType type) {
        currentRoom = roomInfo;
        resetErrorSettings();
        String templateId = "1";
        String keyWord = "欢迎老六作为年会主唱";
        // 1.通过getIp获取的小平台地址进行投屏
        SmallPlatInfoBySSDP smallPlatInfoBySSDP = mSession.getSmallPlatInfoBySSDP();
        SmallPlatformByGetIp smallPlatformByGetIp = mSession.getmSmallPlatInfoByIp();
        TvBoxSSDPInfo tvBoxSSDPInfo = mSession.getTvBoxSSDPInfo();
        if(smallPlatformByGetIp!=null&&!TextUtils.isEmpty(smallPlatformByGetIp.getLocalIp())) {
            String localIp = smallPlatformByGetIp.getLocalIp();
            String url = "http://"+localIp+":8080";
            showLoadingLayout();
            AppApi.welRecommendPro(this,url,roomInfo.getBox_mac(),templateId,keyWord,this);
        }else {
            erroCount++;
        }

        // 2.通过小平台ssdp获取小平台地址进行投屏
        if(smallPlatInfoBySSDP!=null&&!TextUtils.isEmpty(smallPlatInfoBySSDP.getServerIp())) {
            String serverIp = smallPlatInfoBySSDP.getServerIp();
            String url = "http://"+serverIp+":8080";
            showLoadingLayout();
            AppApi.welRecommendPro(this,url,roomInfo.getBox_mac(),templateId,keyWord,this);
        }else {
            erroCount++;
        }

        // 3.通过盒子ssdp获取小平台地址进行投屏
        if(tvBoxSSDPInfo!=null&&!TextUtils.isEmpty(tvBoxSSDPInfo.getServerIp())) {
            String serverIp = tvBoxSSDPInfo.getServerIp();
            String url = "http://"+serverIp+":8080";
            showLoadingLayout();
            AppApi.welRecommendPro(this,url,roomInfo.getBox_mac(),templateId,keyWord,this);
        }else {
            erroCount++;
        }

        if(erroCount == 3) {
            hideLoadingLayout();
            ShowMessage.showToast(this,"投屏失败");
            resetErrorSettings();
        }
    }

    private void resetErrorSettings() {
        erroCount = 0;
        errorMsg = null;
    }

    @Override
    public void onRecommendBtnClick(RoomInfo roomInfo, RoomServiceAdapter.ProType type) {

    }

    @Override
    public void onSuccess(AppApi.Action method, Object obj) {
        super.onSuccess(method, obj);
        switch (method) {
            case GET_WEL_RECOMMEND_JSON:
                hideLoadingLayout();
                ShowMessage.showToast(this,"投屏成功");
                OkHttpUtils.getInstance().getOkHttpClient().dispatcher().cancelAll();
                currentRoom.setRecommendPlay(false);
                currentRoom.setWelPlay(true);
                roomServiceAdapter.notifyDataSetChanged();

                recommendPlayDelayed();
                break;
        }
    }

    private void recommendPlayDelayed() {
        /************5分钟以后开始播放推荐菜***********/
        //创建Intent对象，action为ELITOR_CLOCK，附加信息为字符串“你该打酱油了”
        Intent intent = new Intent(ConstantValues.ACTION_RECOMMEND_PLAY_DELAYED_5MIN);
        intent.putExtra("box", currentRoom);

        //AlarmManager对象,注意这里并不是new一个对象，Alarmmanager为系统级服务
        AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
        if(recommendPlayDelayedIntent!=null)
            am.cancel(recommendPlayDelayedIntent);

        //定义一个PendingIntent对象，PendingIntent.getBroadcast包含了sendBroadcast的动作。
        //也就是发送了action 为"ELITOR_CLOCK"的intent
        recommendPlayDelayedIntent = PendingIntent.getBroadcast(this, ConstantValues.REQUEST_CODE_RECOMEMND_PLAY_DELAYED, intent, 0);

        //设置闹钟从当前时间开始，每隔5s执行一次PendingIntent对象pi，注意第一个参数与第二个参数的关系
        // 5秒后通过PendingIntent pi对象发送广播
        am.set(AlarmManager.RTC_WAKEUP,  20  * 1000, recommendPlayDelayedIntent);
    }

    @Override
    public void onError(AppApi.Action method, Object obj) {
        switch (method) {
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
                    showToast("包间电视连接失败，请检查是否开机");
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
            RoomInfo roomInfo = (RoomInfo) intent.getSerializableExtra("box");
            switch (action) {
                case ConstantValues.ACTION_RECOMMEND_PLAY_DELAYED_5MIN:
                    roomInfo.setRecommendPlay(true);
                    roomInfo.setWelPlay(false);
                    roomServiceAdapter.notifyDataSetChanged();
                    stopProDelayed(roomInfo);
                    break;
                case ConstantValues.ACTION_REFRESH_PRO_STATE_DELAYED:
                    roomInfo.setRecommendPlay(false);
                    roomInfo.setWelPlay(false);
                    roomServiceAdapter.notifyDataSetChanged();
                    break;
            }
        }
    }

    /**
     * 延迟停止播放（当推荐菜播放完毕以后停止）
     */
    private void stopProDelayed(RoomInfo roomInfo) {
    /************推荐菜播放完毕后刷新播放状态***********/
        //创建Intent对象，action为ELITOR_CLOCK，附加信息为字符串“你该打酱油了”
        Intent intent = new Intent(ConstantValues.ACTION_REFRESH_PRO_STATE_DELAYED);
        intent.putExtra("box", roomInfo);
        intent.putExtra("pro_state", ProState.STATE_PLAY);

        //AlarmManager对象,注意这里并不是new一个对象，Alarmmanager为系统级服务
        AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
        if(stopProDelayedIntent!=null)
            am.cancel(stopProDelayedIntent);

        //定义一个PendingIntent对象，PendingIntent.getBroadcast包含了sendBroadcast的动作。
        //也就是发送了action 为"ELITOR_CLOCK"的intent
        stopProDelayedIntent = PendingIntent.getBroadcast(this, ConstantValues.REQUEST_CODE_REFRESH_PRO_STATE_DELAYED, intent, 0);

        //设置闹钟从当前时间开始，每隔5s执行一次PendingIntent对象pi，注意第一个参数与第二个参数的关系
        // 5秒后通过PendingIntent pi对象发送广播
        am.set(AlarmManager.RTC_WAKEUP,  20 * 1000, stopProDelayedIntent);
    }
}
