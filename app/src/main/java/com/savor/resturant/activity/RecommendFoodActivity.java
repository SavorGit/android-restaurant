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

import com.common.api.utils.DensityUtil;
import com.savor.resturant.R;
import com.savor.resturant.adapter.RecommendFoodAdapter;
import com.savor.resturant.bean.HotelBean;
import com.savor.resturant.bean.RecommendFood;
import com.savor.resturant.core.AppApi;
import com.savor.resturant.widget.decoration.SpacesItemDecoration;

import java.util.ArrayList;
import java.util.List;

/**
 * 推荐菜
 * @author hezd
 */
public class RecommendFoodActivity extends BaseActivity implements View.OnClickListener, RecommendFoodAdapter.OnCheckStateChangeListener, RecommendFoodAdapter.OnSingleProBtnClickListener {

    private static final int COLUMN_COUNT = 2;
    private ImageView mBackBtn;
    private TextView mTitleTv;
    private TextView mProBtn;
    private RecyclerView mRecommendFoodsRlv;
    /**当前是否属于选择包间状态*/
    private boolean isSelectRommState;
    private RecyclerView mRoomListView;
    private RecommendFoodAdapter mRecommendAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recommend_food);

        getViews();
        setViews();
        setListeners();

        getData();
    }

    private void getData() {
//        HotelBean hotelBean = mSession.getHotelBean();
//        String hotel_id = hotelBean.getHotel_id();
        AppApi.getRecommendFoods(this,"60",this);
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
        // 测试数据
//        List<RecommendFood> dataList = new ArrayList<>();
//        for(int i = 0;i<20;i++) {
//            RecommendFood food = new RecommendFood();
//            food.setChinese_name("红烧佛跳墙");
//            food.setOss_path("https://ss0.bdstatic.com/70cFuHSh_Q1YnxGkpoWK1HF6hhy/it/u=2467154800,1064084862&fm=200&gp=0.jpg");
//            food.setFood_id("3798");
//            food.setFood_name("good taste!");
//            food.setId("1");
//            food.setMd5("a90600ac734eb705ef1c57fcf42fd39c");
//            food.setMd5_type("easyMd5");
//            food.setName("ATcxtWnRwb.jpg");
//            food.setSuffix("jpg");
//            dataList.add(food);
//        }

        mTitleTv.setText("请选择投屏包间");
        TextPaint tp = mTitleTv.getPaint();
        tp.setFakeBoldText(true);
        mTitleTv.setCompoundDrawablesWithIntrinsicBounds(null,null,getResources().getDrawable(R.drawable.ico_arrow_down),null);
        mTitleTv.setCompoundDrawablePadding(DensityUtil.dip2px(this,10));
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) mBackBtn.getLayoutParams();
        layoutParams.setMargins(DensityUtil.dip2px(this,15),0,0,0);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(this,COLUMN_COUNT);
        gridLayoutManager.setOrientation(GridLayoutManager.VERTICAL);
        mRecommendFoodsRlv.setLayoutManager(gridLayoutManager);

        mRecommendAdapter = new RecommendFoodAdapter(this);
        mRecommendFoodsRlv.setAdapter(mRecommendAdapter);

        //添加ItemDecoration，item之间的间隔
        int leftRight = DensityUtil.dip2px(this,15);
        int topBottom = DensityUtil.dip2px(this,15);

        mRecommendFoodsRlv.addItemDecoration(new SpacesItemDecoration(leftRight, topBottom, getResources().getColor(R.color.color_eeeeee)));
//        mRecommendAdapter.setData(dataList);
    }

    @Override
    public void setListeners() {
        mBackBtn.setOnClickListener(this);
        mTitleTv.setOnClickListener(this);
        mRecommendAdapter.setOnCheckStateChangeListener(this);
        mRecommendAdapter.setOnSingleProBtnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_left:
                if(isSelectRommState) {
                    hideRommList();
                    isSelectRommState = false;
                }else {
                    finish();
                }
                break;
            case R.id.tv_center:
                if(!isSelectRommState) {
                    showRoomList();
                    isSelectRommState  = true;
                }

                break;
            case R.id.tv_pro:

                break;
        }
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
    }

    private void showRoomList() {
        mRoomListView.setVisibility(View.VISIBLE);
        mTitleTv.setCompoundDrawablesWithIntrinsicBounds(null,null,null,null);
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
    }


    @Override
    public void onSuccess(AppApi.Action method, Object obj) {
        switch (method) {
            case GET_RECOMMEND_FOODS_JSON:
                if(obj instanceof List) {
                    List<RecommendFood> recommendFoodList = (List<RecommendFood>) obj;
                    mRecommendAdapter.setData(recommendFoodList);
                }
                break;
        }
    }

    @Override
    public void onCheckStateChange() {
        List<RecommendFood> data = mRecommendAdapter.getData();
        if(data!=null) {
            if(isSomeOneSelected(data)) {
                mProBtn.setEnabled(true);
            }else {
                mProBtn.setEnabled(false);
            }
        }
    }


    @Override
    public void onSingleProBtnClick(RecommendFood recommendFood) {

    }

    private boolean isSomeOneSelected(List<RecommendFood> data) {
        for(RecommendFood food : data) {
            if(food.isSelected())
                return true;
        }
        return false;
    }

}
