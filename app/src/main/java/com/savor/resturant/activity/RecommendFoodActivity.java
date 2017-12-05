package com.savor.resturant.activity;

import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextPaint;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.common.api.utils.DensityUtil;
import com.savor.resturant.R;
import com.savor.resturant.adapter.RecommendFoodAdapter;
import com.savor.resturant.bean.RecommendFood;
import com.savor.resturant.widget.decoration.SpacesItemDecoration;

import java.util.ArrayList;
import java.util.List;

/**
 * 推荐菜
 * @author hezd
 */
public class RecommendFoodActivity extends BaseActivity implements View.OnClickListener {

    private static final int COLUMN_COUNT = 2;
    private ImageView mBackBtn;
    private TextView mTitleTv;
    private TextView mProBtn;
    private RecyclerView mRecommendFoodsRlv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recommend_food);

        getViews();
        setViews();
        setListeners();
    }

    @Override
    public void getViews() {
        mBackBtn = (ImageView) findViewById(R.id.iv_left);
        mTitleTv = (TextView) findViewById(R.id.tv_center);
        mProBtn = (TextView) findViewById(R.id.tv_pro);
        mRecommendFoodsRlv = (RecyclerView) findViewById(R.id.rlv_foods);
    }

    @Override
    public void setViews() {
        // 测试数据
        List<RecommendFood> dataList = new ArrayList<>();
        for(int i = 0;i<20;i++) {
            RecommendFood food = new RecommendFood();
            food.setChinese_name("红烧佛跳墙");
            food.setOss_path("https://ss0.bdstatic.com/70cFuHSh_Q1YnxGkpoWK1HF6hhy/it/u=2467154800,1064084862&fm=200&gp=0.jpg");
            food.setFood_id("3798");
            food.setFood_name("good taste!");
            food.setId("1");
            food.setMd5("a90600ac734eb705ef1c57fcf42fd39c");
            food.setMd5_type("easyMd5");
            food.setName("ATcxtWnRwb.jpg");
            food.setSuffix("jpg");
            dataList.add(food);
        }

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

        RecommendFoodAdapter mRecommendAdapter = new RecommendFoodAdapter(this);
        mRecommendFoodsRlv.setAdapter(mRecommendAdapter);

        //添加ItemDecoration，item之间的间隔
        int leftRight = DensityUtil.dip2px(this,15);
        int topBottom = DensityUtil.dip2px(this,15);

        mRecommendFoodsRlv.addItemDecoration(new SpacesItemDecoration(leftRight, topBottom, getResources().getColor(R.color.color_eeeeee)));
        mRecommendAdapter.setData(dataList);
    }

    @Override
    public void setListeners() {
        mBackBtn.setOnClickListener(this);
        mTitleTv.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_left:
                finish();
                break;
            case R.id.tv_center:

                break;
            case R.id.tv_pro:

                break;
        }
    }
}
