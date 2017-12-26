package com.savor.resturant.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.common.api.utils.AppUtils;
import com.common.api.utils.ShowMessage;
import com.google.gson.Gson;
import com.savor.resturant.R;
import com.savor.resturant.adapter.contact.MyContactAdapter;
import com.savor.resturant.bean.ContactFormat;
import com.savor.resturant.core.AppApi;
import com.savor.resturant.utils.ChineseComparator;
import com.savor.resturant.widget.contact.DividerDecoration;

import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends BaseActivity implements MyContactAdapter.OnAddBtnClickListener, MyContactAdapter.OnItemClickListener, View.OnClickListener {

    private EditText mSearchEt;
    private TextView mCancelBtn;
    private RecyclerView mContactListView;
    private ContactCustomerListActivity.OperationType operationType;
    private MyContactAdapter mAdapter;
    private List<ContactFormat> contactList = new ArrayList<>();
    private ChineseComparator pinyinComparator;
    private int currentAddPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        handleIntent();
        getViews();
        setViews();
        setListeners();
    }

    private void handleIntent() {
        operationType = (ContactCustomerListActivity.OperationType) getIntent().getSerializableExtra("type");
    }

    @Override
    public void getViews() {
        mSearchEt = (EditText) findViewById(R.id.et_search);
        mCancelBtn = (TextView) findViewById(R.id.tv_cancel_btn);
        mContactListView = (RecyclerView) findViewById(R.id.contact_member);
    }

    @Override
    public void setViews() {
        pinyinComparator = new ChineseComparator();
        switch (operationType) {
            case CONSTACT_LIST:
                contactList = mSession.getContactList();
                break;
            case CUSTOMER_LIST:
                contactList = mSession.getCustomerList();
                break;
        }

        mAdapter = new MyContactAdapter(this,null,operationType);
        int orientation = LinearLayoutManager.VERTICAL;
        final LinearLayoutManager layoutManager = new LinearLayoutManager(this, orientation, false);
        mContactListView.setLayoutManager(layoutManager);

        mContactListView.setAdapter(mAdapter);
        mContactListView.addItemDecoration(new DividerDecoration(this));


    }

    @Override
    public void setListeners() {

        if(operationType == ContactCustomerListActivity.OperationType.CONSTACT_LIST) {
            mAdapter.setOnAddBtnClickListener(this);
            mAdapter.setOnItemClickListener(null);
        }else {
            mAdapter.setOnItemClickListener(this);
        }

        mSearchEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String content = mSearchEt.getText().toString();
                if(!TextUtils.isEmpty(content)) {
                    if(contactList !=null&& contactList.size()>0) {
                        List<ContactFormat> contactFormatLike = getLikeList(contactList,content);
                        mAdapter.setData(contactFormatLike);
                    }
                }else {
                    List<ContactFormat> emptyList = new ArrayList<>();
                    mAdapter.setData(emptyList);
                }
            }
        });

        mCancelBtn.setOnClickListener(this);
    }

    @Override
    public void onAddBtnClick(int position, ContactFormat contactFormat) {
        AppUtils.hideSoftKeybord(this);
        currentAddPosition = position;
        List<ContactFormat> contactFormats = new ArrayList<>();
        contactFormats.add(contactFormat);
        String importInfo = new Gson().toJson(contactFormats);
        String invitation = mSession.getHotelBean().getInvite_id();
        String tel = mSession.getHotelBean().getTel();
        AppApi.importInfo(this,importInfo,invitation,tel,this);
    }

    @Override
    public void onItemClick(int position, ContactFormat contactFormat) {
        AppUtils.hideSoftKeybord(this);
        ShowMessage.showToast(this,"打开客户信息列表");
        finish();
    }

    private List<ContactFormat> getLikeList(List<ContactFormat> contactFormats, String content) {
        List<ContactFormat> tempList = new ArrayList<>();
        for(ContactFormat contactFormat : contactFormats) {
            if(contactFormat.getKey().trim().contains(content)) {
                tempList.add(contactFormat);
            }
        }
        return tempList;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_cancel_btn:
                AppUtils.hideSoftKeybord(this);
                finish();
                break;
        }
    }

    @Override
    public void onSuccess(AppApi.Action method, Object obj) {
        switch (method) {
            case POST_IMPORT_INFO_JSON:
                ShowMessage.showToast(this,"导入成功");
                contactList.get(currentAddPosition).setAdded(true);
                mAdapter.notifyDataSetChanged();
                break;
        }
    }
}
