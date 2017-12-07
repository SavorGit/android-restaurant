package com.savor.resturant.activity;

import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextPaint;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.common.api.okhttp.OkHttpUtils;
import com.common.api.utils.DensityUtil;
import com.common.api.utils.ShowMessage;
import com.savor.resturant.R;
import com.savor.resturant.adapter.RecommendFoodAdapter;
import com.savor.resturant.adapter.RoomListAdapter;
import com.savor.resturant.bean.RecommendFoodAdvert;
import com.savor.resturant.bean.RoomInfo;
import com.savor.resturant.core.AppApi;
import com.savor.resturant.widget.decoration.SpacesItemDecoration;

import java.io.Serializable;
import java.util.List;

/**
 * 推荐菜和宣传片共用页面
 * @author hezd
 */
public class RecommendFoodActivity extends BaseActivity implements View.OnClickListener, RecommendFoodAdapter.OnCheckStateChangeListener, RecommendFoodAdapter.OnSingleProBtnClickListener, RoomListAdapter.OnRoomItemClicklistener {
    /**单张投屏*/
    private static final int TYPE_PRO_SINGLE = 1;
    /**多张投屏*/
    private static final int TYPE_PRO_MULTI = 2;
    /**当前投屏类型*/
    private int currentProType = 0;
    private static final int COLUMN_COUNT = 2;
    private ImageView mBackBtn;
    private TextView mTitleTv;
    private TextView mProBtn;
    private RecyclerView mRecommendFoodsRlv;
    /**当前是否属于选择包间状态*/
    private boolean isSelectRommState;
    private RecyclerView mRoomListView;
    private RecommendFoodAdapter mRecommendAdapter;
    private OperationType currentType;
    private RoomListAdapter roomListAdapter;
    private RoomInfo currentRoom;
    /**单个投屏的条目*/
    private RecommendFoodAdvert currentFoodAdvert;


    /**
     * 操作类型，宣传片或者推荐菜
     */
    public enum OperationType implements Serializable{
        /**宣传片*/
        TYPE_ADVERT,
        /**推荐菜*/
        TYPE_RECOMMEND_FOODS,
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recommend_food);

        handleIntent();
        getViews();
        setViews();
        setListeners();

        getData();
    }

    private void handleIntent() {
        currentType = (OperationType) getIntent().getSerializableExtra("type");
    }

    private void getData() {
//        HotelBean hotelBean = mSession.getHotelBean();
//        String hotel_id = hotelBean.getHotel_id();
        switch (currentType) {
            case TYPE_ADVERT:
                AppApi.getAdvertList(this,"60",this);
                break;
            case TYPE_RECOMMEND_FOODS:
                AppApi.getRecommendFoods(this,"60",this);
                break;
        }

    }

    @Override
    public void getViews() {
        mBackBtn = (ImageView) findViewById(R.id.iv_left);
        mTitleTv = (TextView) findViewById(R.id.tv_center);
        mProBtn = (TextView) findViewById(R.id.tv_pro);
        mRecommendFoodsRlv = (RecyclerView) findViewById(R.id.rlv_foods);
        mRoomListView = (RecyclerView) findViewById(R.id.rlv_room);
    }

    @Override
    public void setViews() {
        // 初始化标题栏
        initTitleBar();
        // 初始化内容列表
        initContentList();
        // 初始化房间列表
        initRoomList();
    }

    private void initTitleBar() {
        mTitleTv.setText("请选择投屏包间");
        TextPaint tp = mTitleTv.getPaint();
        tp.setFakeBoldText(true);
        mTitleTv.setCompoundDrawablesWithIntrinsicBounds(null,null,getResources().getDrawable(R.drawable.ico_arrow_down),null);
        mTitleTv.setCompoundDrawablePadding(DensityUtil.dip2px(this,10));
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) mBackBtn.getLayoutParams();
        layoutParams.setMargins(DensityUtil.dip2px(this,15),0,0,0);
    }

    private void initContentList() {
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this,COLUMN_COUNT);
        gridLayoutManager.setOrientation(GridLayoutManager.VERTICAL);
        mRecommendFoodsRlv.setLayoutManager(gridLayoutManager);

        mRecommendAdapter = new RecommendFoodAdapter(this);
        mRecommendFoodsRlv.setAdapter(mRecommendAdapter);

        //添加ItemDecoration，item之间的间隔
        int leftRight = DensityUtil.dip2px(this,15);
        int topBottom = DensityUtil.dip2px(this,15);

        mRecommendFoodsRlv.addItemDecoration(new SpacesItemDecoration(leftRight, topBottom, getResources().getColor(R.color.color_eeeeee)));
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
    public void setListeners() {
        mProBtn.setOnClickListener(this);
        mBackBtn.setOnClickListener(this);
        mTitleTv.setOnClickListener(this);
        mRecommendAdapter.setOnCheckStateChangeListener(this);
        mRecommendAdapter.setOnSingleProBtnClickListener(this);
        roomListAdapter.setOnRoomItemClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_left:
                if(isSelectRommState) {
                    hideRommList();
                }else {
                    finish();
                }
                break;
            case R.id.tv_center:
                if(!isSelectRommState) {
                    showRoomList();

                }

                break;
            case R.id.tv_pro:
                currentProType = TYPE_PRO_MULTI;
                startMultiPro();
                break;
        }
    }

    /**多选投屏*/
    private void startMultiPro() {
        // 1.判断是否选择包间
        if(currentRoom == null) {
            initRoomNotSelected();
            return;
        }
        // 2.开始投屏
        String vid = getSelectedList(mRecommendAdapter.getData());
        switch (currentType) {
            case TYPE_ADVERT:
                AppApi.adverPro(this,"",currentRoom.getBox_mac(),vid,this);
                break;
            case TYPE_RECOMMEND_FOODS:
                AppApi.recommendPro(this,"",currentRoom.getBox_mac(),1000*30+"",vid,this);
                break;
        }

    }

    private String getSelectedList(List<RecommendFoodAdvert> data) {
       StringBuilder sb = new StringBuilder();
       for(int i = 0;i<data.size();i++) {
           RecommendFoodAdvert foodAdvert = data.get(i);
           if(i==data.size()-1) {
               switch (currentType) {
                   case TYPE_RECOMMEND_FOODS:
                       sb.append(foodAdvert.getFood_id());
                       break;
                   case TYPE_ADVERT:
                       sb.append(foodAdvert.getId());
                       break;
               }

           }else {
               switch (currentType) {
                   case TYPE_RECOMMEND_FOODS:
                       sb.append(foodAdvert.getFood_id()+",");
                       break;
                   case TYPE_ADVERT:
                       sb.append(foodAdvert.getId()+",");
                       break;
               }
           }
       }

        return sb.toString();
    }

    private void hideRommList() {
        mTitleTv.setCompoundDrawablesWithIntrinsicBounds(null,null,getResources().getDrawable(R.drawable.ico_arrow_down),null);
        mBackBtn.setImageResource(R.drawable.back);

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
    }

    private void showRoomList() {
        mRoomListView.setVisibility(View.VISIBLE);
        mTitleTv.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
        mBackBtn.setImageResource(R.drawable.ico_close);
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

    @Override
    public void onCheckStateChange() {
        List<RecommendFoodAdvert> data = mRecommendAdapter.getData();
        if (data != null) {
            if (isSomeOneSelected(data)) {
                mProBtn.setEnabled(true);
            } else {
                mProBtn.setEnabled(false);
            }
        }
    }


    @Override
    public void onSingleProBtnClick(RecommendFoodAdvert recommendFoodAdvert) {
        currentProType = TYPE_PRO_SINGLE;
        currentFoodAdvert = recommendFoodAdvert;
        startSinglePro();
    }

    /**
     * 单个投屏
     */
    private void startSinglePro() {
        if (currentRoom == null) {
            initRoomNotSelected();
            return;
        }
        switch (currentType) {
            case TYPE_RECOMMEND_FOODS:
                AppApi.recommendPro(this, "", currentRoom.getBox_mac(), 1000 * 60 * 2 + "", currentFoodAdvert.getFood_id(), this);
                break;
            case TYPE_ADVERT:
                AppApi.adverPro(this, "", currentRoom.getBox_mac(), currentFoodAdvert.getId(), this);
                break;
        }
    }

    private void initRoomNotSelected() {
        ShowMessage.showToast(this, "请选择包间");
        showRoomList();
    }

    private boolean isSomeOneSelected(List<RecommendFoodAdvert> data) {
        for (RecommendFoodAdvert food : data) {
            if (food.isSelected())
                return true;
        }
        return false;
    }

    @Override
    public void onRoomItemClick(RoomInfo roomInfo) {
        hideRommList();
        currentRoom = roomInfo;
        switch (currentProType) {
            case TYPE_PRO_SINGLE:
                startSinglePro();
                break;
            case TYPE_PRO_MULTI:
                startMultiPro();
                break;
        }
    }

    @Override
    public void onSuccess(AppApi.Action method, Object obj) {
        switch (method) {
            case GET_RECOMMEND_PRO_JSON:
            case GET_ADVERT_PRO_JSON:
                ShowMessage.showToast(this,"投屏成功！");
                break;
            case GET_ADVERT_JSON:
            case GET_RECOMMEND_FOODS_JSON:
                if(obj instanceof List) {
                    List<RecommendFoodAdvert> recommendFoodAdvertList = (List<RecommendFoodAdvert>) obj;
                    mRecommendAdapter.setData(recommendFoodAdvertList,currentType);
                }
                break;
        }
    }

    @Override
    public void onError(AppApi.Action method, Object obj) {
        super.onError(method,obj);
    }


    @Override
    protected void onDestroy() {
        // 清楚房间选择记录
        resetRoomList();
        OkHttpUtils.getInstance().getOkHttpClient().dispatcher().cancelAll();
        super.onDestroy();
    }

    private void resetRoomList() {
        List<RoomInfo> roomList = mSession.getRoomList();
        if(roomList!=null) {
            for(RoomInfo info:roomList) {
                info.setSelected(false);
            }
        }
    }
}
