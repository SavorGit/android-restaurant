package com.savor.resturant.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.savor.resturant.R;
import com.savor.resturant.activity.ContactCustomerListActivity;
import com.savor.resturant.activity.SearchActivity;

/**
 * 客户管理
 * @author hezd 2019/09/19
 */
public class CustomerFragment extends Fragment implements View.OnClickListener {


    private TextView mTitleTv;
    private ImageView mRightIv;
    private TextView mSearchTv;

    public CustomerFragment() {
        // Required empty public constructor
    }

    /**
     */
    public static CustomerFragment newInstance() {
        CustomerFragment fragment = new CustomerFragment();
//        Bundle args = new Bundle();
//        args.putString(ARG_PARAM1, param1);
//        args.putString(ARG_PARAM2, param2);
//        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View parentLayout = inflater.inflate(R.layout.fragment_customer, container, false);
        initViews(parentLayout);
        setViews();
        setListeners();
        return parentLayout;
    }

    private void setListeners() {
        mSearchTv.setOnClickListener(this);
        mRightIv.setOnClickListener(this);
    }

    private void setViews() {
        mTitleTv.setText("客户管理");
        mTitleTv.setTextColor(getResources().getColor(R.color.white));
        mRightIv.setVisibility(View.VISIBLE);
        mRightIv.setImageResource(R.drawable.ico_slide_video);
    }

    private void initViews(View parentLayout) {
        parentLayout.findViewById(R.id.iv_left).setVisibility(View.GONE);
        mTitleTv = (TextView) parentLayout.findViewById(R.id.tv_center);
        mRightIv = (ImageView) parentLayout.findViewById(R.id.iv_right);
        mSearchTv = (TextView) parentLayout.findViewById(R.id.tv_search);
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.tv_search:
                intent = new Intent(getActivity(), SearchActivity.class);
                intent.putExtra("type", ContactCustomerListActivity.OperationType.CUSTOMER_LIST);
                startActivity(intent);
                break;
            case R.id.iv_right:
                intent = new Intent(getActivity(),ContactCustomerListActivity.class);
                intent.putExtra("type", ContactCustomerListActivity.OperationType.CUSTOMER_LIST);
                startActivity(intent);
                break;
        }
    }
}
