package com.savor.resturant.activity;

import android.content.Intent;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resturant_service);

        getViews();
        setViews();
        setListeners();
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
                break;
        }
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
                    ShowMessage.showToast(this,errorMsg);
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
}
