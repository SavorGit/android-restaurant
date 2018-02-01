package com.savor.resturant.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.common.api.utils.DensityUtil;
import com.savor.resturant.R;
import com.savor.resturant.adapter.RoomServiceAdapter;
import com.savor.resturant.bean.RoomInfo;
import com.savor.resturant.widget.decoration.SpacesItemDecoration;

import java.util.List;

import static com.savor.resturant.activity.RecommendFoodActivity.OperationType.TYPE_RECOMMEND_FOODS;

/**
 * 餐厅服务
 * @author hezd 2018/01/30
 */
public class ResturantServiceActivity extends BaseActivity implements View.OnClickListener, RoomServiceAdapter.OnItemClickListener {

    private RecyclerView mRoomListRlv;
    private ImageView mBackBtn;
    private TextView mTitleTv;
    private TextView mRightTv;
    private RoomServiceAdapter roomServiceAdapter;

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
}
