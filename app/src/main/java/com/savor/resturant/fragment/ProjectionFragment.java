package com.savor.resturant.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.common.api.utils.DensityUtil;
import com.savor.resturant.R;
import com.savor.resturant.adapter.FunctionAdapter;
import com.savor.resturant.bean.FunctionItem;
import com.savor.resturant.bean.HotelBean;
import com.savor.resturant.widget.decoration.SpacesItemDecoration;

import java.util.ArrayList;
import java.util.List;

/**
 * 餐厅服务
 * @author hezd 2017/09/19
 */
public class ProjectionFragment extends BaseFragment implements FunctionAdapter.OnNoHotelClickListener {

    private TextView mHintTv;
    private ImageView iv_left;
    private TextView tv_center;
    private RecyclerView listView;
    private List<FunctionItem> mList = new ArrayList<>();
    public ProjectionFragment() {
        // Required empty public constructor
    }

    /**
     *
     */
    public static ProjectionFragment newInstance() {
        ProjectionFragment fragment = new ProjectionFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View parentlayout = inflater.inflate(R.layout.fragment_service, container, false);
        initViews(parentlayout);
        setViews();
        setListeners();
        return parentlayout;
    }

    private void initViews(View parentlayout) {
        mHintTv = (TextView) parentlayout.findViewById(R.id.tv_wifi_hint);
        iv_left = (ImageView) parentlayout.findViewById(R.id.iv_left);
        tv_center = (TextView)parentlayout.findViewById(R.id.tv_center);
        listView = (RecyclerView) parentlayout.findViewById(R.id.category_list);
    }

    @Override
    public String getFragmentName() {
        return null;
    }

    @Override
    public void setViews() {
        iv_left.setVisibility(View.GONE);
        String hotel_name = mSession.getHotelBean().getHotel_name();
        tv_center.setText(hotel_name);
        tv_center.setTextColor(getResources().getColor(R.color.white));

        mList.clear();

        FunctionItem recommandItem = new FunctionItem();
        recommandItem.setContent("推荐菜");
        recommandItem.setResId(R.drawable.ico_recommand);
        recommandItem.setType(FunctionItem.FunctionType.TYPE_RECOMMAND_FOODS);
        mList.add(recommandItem);

        FunctionItem advertItem = new FunctionItem();
        advertItem.setContent("宣传片");
        advertItem.setResId(R.drawable.ico_xcp);
        advertItem.setType(FunctionItem.FunctionType.TYPE_ADVERT);
        mList.add(advertItem);

        FunctionItem welcomeItem = new FunctionItem();
        welcomeItem.setContent("欢迎词");
        welcomeItem.setResId(R.drawable.ico_welcom_word);
        welcomeItem.setType(FunctionItem.FunctionType.TYPE_WELCOME_WORD);
        mList.add(welcomeItem);

        FunctionItem picItem = new FunctionItem();
        picItem.setContent("照片");
        picItem.setResId(R.drawable.ico_pic);
        picItem.setType(FunctionItem.FunctionType.TYPE_PIC);
        mList.add(picItem);

        FunctionItem videoItem = new FunctionItem();
        videoItem.setContent("视频");
        videoItem.setResId(R.drawable.ico_video);
        videoItem.setType(FunctionItem.FunctionType.TYPE_VIDEO);
        mList.add(videoItem);

        FunctionAdapter mFunctionAdapter = new FunctionAdapter(getContext());
        GridLayoutManager layoutManager = new GridLayoutManager(getContext(),2);
        layoutManager.setOrientation(GridLayoutManager.VERTICAL);
        listView.setLayoutManager(layoutManager);
        mFunctionAdapter.setData(mList);
        listView.setAdapter(mFunctionAdapter);
        mFunctionAdapter.setOnNoHotelClickListener(this);

        //添加ItemDecoration，item之间的间隔
        int leftRight = DensityUtil.dip2px(getContext(),5);
        int topBottom = DensityUtil.dip2px(getContext(),15);

        listView.addItemDecoration(new SpacesItemDecoration(leftRight, topBottom, getResources().getColor(R.color.color_eeeeee)));

        initWIfiHint();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if(!hidden) {
            initWIfiHint();
        }
    }

    @Override
    public void setListeners() {

    }

    @Override
    public void onNoHotelClick() {
        Animation shake = AnimationUtils.loadAnimation(getContext(), R.anim.shake);
        mHintTv.startAnimation(shake);
    }

    public void initWIfiHint() {
        // 判断当前是否是酒店环境
        int hotelid = mSession.getHotelid();
        HotelBean loginResponse = mSession.getHotelBean();
        if(loginResponse!=null) {
            String hid = loginResponse.getHotel_id();
            if(String.valueOf(hotelid).equals(hid)) {
                mHintTv.setText("当前连接酒楼\""+loginResponse.getHotel_name()+"\"");
                mHintTv.setTextColor(getResources().getColor(R.color.color_0da606));
                mHintTv.setCompoundDrawables(null,null,null,null);
            }else {
                mHintTv.setText("请链接“"+loginResponse.getHotel_name()+"”的wifi后继续操作");
                mHintTv.setTextColor(getResources().getColor(R.color.color_e43018));
                mHintTv.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.ico_exe_hint),null,null,null);
            }
        }

    }
}
