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

import com.common.api.utils.DensityUtil;
import com.savor.resturant.R;
import com.savor.resturant.adapter.RecommendFoodAdapter;
import com.savor.resturant.adapter.RoomListAdapter;
import com.savor.resturant.bean.RecommendFoodAdvert;
import com.savor.resturant.bean.RoomInfo;
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
        initTitleBar();
    }

    @Override
    public void setListeners() {

        iv_left.setOnClickListener(this);
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
                finish();
                break;
            case R.id.bg_l1:
                setText();
                break;
            case R.id.bg_l2:
                setText();
                break;
            case R.id.bg_l3:
                setText();
                break;
            case R.id.bg_l4:
                setText();
                break;
            case R.id.bg_l5:
                setText();
                break;
            case R.id.bg_l6:
                setText();
                break;
            case R.id.bg_l7:
                setText();
                break;
            case R.id.bg_l8:
                setText();
                break;
            default:
                break;
        }
    }


    private void setText(){

    }

    private void initTitleBar() {
        tv_center.setText("请选择投屏包间");
        TextPaint tp = tv_center.getPaint();
        tp.setFakeBoldText(true);
        tv_center.setCompoundDrawablesWithIntrinsicBounds(null,null,getResources().getDrawable(R.drawable.ico_arrow_down),null);
        tv_center.setCompoundDrawablePadding(DensityUtil.dip2px(this,10));
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) iv_left.getLayoutParams();
        layoutParams.setMargins(DensityUtil.dip2px(this,15),0,0,0);
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
        hideRommList();
//        currentRoom = roomInfo;
//        switch (currentProType) {
//            case TYPE_PRO_SINGLE:
//                startSinglePro();
//                break;
//            case TYPE_PRO_MULTI:
//                startMultiPro();
//                break;
//        }
    }

    private void hideRommList() {
        tv_center.setCompoundDrawablesWithIntrinsicBounds(null,null,getResources().getDrawable(R.drawable.ico_arrow_down),null);
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
        //isSelectRommState = false;
    }

}

