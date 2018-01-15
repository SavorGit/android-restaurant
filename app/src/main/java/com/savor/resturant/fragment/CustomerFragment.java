package com.savor.resturant.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.savor.resturant.R;
import com.savor.resturant.activity.AddCustomerActivity;
import com.savor.resturant.activity.ContactCustomerListActivity;
import com.savor.resturant.activity.SearchActivity;
import com.savor.resturant.activity.SpendHistoryAddActivity;
import com.savor.resturant.activity.UserInfoActivity;
import com.savor.resturant.adapter.CustomerOpHistoryAdapter;
import com.savor.resturant.bean.CustomerHistory;
import com.savor.resturant.bean.CustomerHistoryBean;
import com.savor.resturant.core.AppApi;
import com.savor.resturant.widget.contact.DividerDecoration;

import java.util.List;

/**
 * 客户管理
 * @author hezd 2019/09/19
 */
public class CustomerFragment extends BaseFragment implements View.OnClickListener, CustomerOpHistoryAdapter.OnItemClickListener {


    private TextView mTitleTv;
    private ImageView mRightIv;
    private TextView mSearchTv;
    private TextView mAddCustomerTv;
    private TextView mAddHistoryTv;
    private RecyclerView mHistoryRlv;
    private CustomerOpHistoryAdapter mHistoryAdapter;
    private TextView mHistoryHintTv;

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

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getCustomerHistory();
    }

    @Override
    public String getFragmentName() {
        return this.getClass().getSimpleName();
    }

    private void getCustomerHistory() {
        String invite_id = mSession.getHotelBean().getInvite_id();
        String tel = mSession.getHotelBean().getTel();
        AppApi.getCustomerHistory(getActivity(),"",invite_id,tel,this);
    }

    public void setListeners() {
        mAddCustomerTv.setOnClickListener(this);
        mAddHistoryTv.setOnClickListener(this);
        mSearchTv.setOnClickListener(this);
        mRightIv.setOnClickListener(this);
        mHistoryAdapter.setOnItemClickListener(this);
    }

    public void setViews() {
        mTitleTv.setText("客户管理");
        mTitleTv.setTextColor(getResources().getColor(R.color.white));
        mRightIv.setVisibility(View.VISIBLE);
        mRightIv.setImageResource(R.drawable.ico_cutomer_list);

        int orientation = LinearLayoutManager.VERTICAL;
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), orientation, false);
        mHistoryRlv.setLayoutManager(layoutManager);

        mHistoryAdapter = new CustomerOpHistoryAdapter(getContext());
        mHistoryRlv.setAdapter(mHistoryAdapter);
        mHistoryRlv.addItemDecoration(new DividerDecoration(getContext()));
    }

    private void initViews(View parentLayout) {
        parentLayout.findViewById(R.id.iv_left).setVisibility(View.GONE);
        mTitleTv = (TextView) parentLayout.findViewById(R.id.tv_center);
        mRightIv = (ImageView) parentLayout.findViewById(R.id.iv_right);
        mSearchTv = (TextView) parentLayout.findViewById(R.id.tv_search);
        mAddCustomerTv = (TextView) parentLayout.findViewById(R.id.tv_add_customer);
        mAddHistoryTv = (TextView) parentLayout.findViewById(R.id.tv_add_history);

        mHistoryRlv = (RecyclerView) parentLayout.findViewById(R.id.rlv_op_list);
        mHistoryHintTv = (TextView) parentLayout.findViewById(R.id.tv_history_empty);
    }

    @Override
    public void onResume() {
        super.onResume();
        getCustomerHistory();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
//        if(!hidden) {
//            getCustomerHistory();
//        }
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.tv_add_customer:
                intent = new Intent(getActivity(), AddCustomerActivity.class);
                intent.putExtra("type", AddCustomerActivity.CustomerOpType.TYPE_ADD);
                startActivity(intent);
                break;
            case R.id.tv_add_history:
                intent = new Intent(getActivity(), SpendHistoryAddActivity.class);
                startActivity(intent);
                break;
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

    @Override
    public void onSuccess(AppApi.Action method, Object obj) {
        super.onSuccess(method, obj);
        switch (method) {
            case POST_CUS_HISTORY_JSON:
                if(obj instanceof CustomerHistory) {
                    CustomerHistory customerHistory = (CustomerHistory) obj;
                    List<CustomerHistoryBean> list = customerHistory.getList();
                    if(list!=null&&list.size()>0) {
                        mHistoryAdapter.setData(list);
                        showHistoryLayout();
                    }else {
                        hideHistoryLayout();
                    }
                }
                break;
        }
    }

    private void showHistoryLayout() {
        mHistoryRlv.setVisibility(View.VISIBLE);
        mHistoryHintTv.setVisibility(View.GONE);
    }

    public void hideHistoryLayout() {
        mHistoryRlv.setVisibility(View.GONE);
        mHistoryHintTv.setVisibility(View.VISIBLE);
    }

    @Override
    public void onItemClick(CustomerHistoryBean historyBean) {
        Intent intent = new Intent(getContext(), UserInfoActivity.class);
        intent.putExtra("customerID",historyBean.getCustomer_id());
        startActivity(intent);
    }
}
