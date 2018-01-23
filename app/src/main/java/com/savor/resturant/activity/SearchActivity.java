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
import android.widget.LinearLayout;
import android.widget.TextView;

import com.common.api.utils.AppUtils;
import com.common.api.utils.ShowMessage;
import com.google.gson.Gson;
import com.savor.resturant.R;
import com.savor.resturant.adapter.contact.MyContactAdapter;
import com.savor.resturant.bean.ContactFormat;
import com.savor.resturant.bean.CustomerListBean;
import com.savor.resturant.bean.ImportInfoResponse;
import com.savor.resturant.bean.OperationFailedItem;
import com.savor.resturant.core.AppApi;
import com.savor.resturant.core.ResponseErrorMessage;
import com.savor.resturant.utils.ChineseComparator;
import com.savor.resturant.widget.contact.DividerDecoration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.savor.resturant.activity.ContactCustomerListActivity.RESULT_CODE_SELECT;

public class SearchActivity extends BaseActivity implements MyContactAdapter.OnAddBtnClickListener, MyContactAdapter.OnItemClickListener, View.OnClickListener {

    private EditText mSearchEt;
    private TextView mCancelBtn;
    private RecyclerView mContactListView;
    private ContactCustomerListActivity.OperationType operationType;
    private MyContactAdapter mAdapter;
    private List<ContactFormat> contactList = new ArrayList<>();
    private ChineseComparator pinyinComparator;
    private int currentAddPosition;
    private LinearLayout mEmptyHintLayout;
    private TextView mAddCustomerTv;

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
        mEmptyHintLayout = (LinearLayout) findViewById(R.id.ll_empty_hint);
        mAddCustomerTv = (TextView) findViewById(R.id.tv_add_customer);
        mSearchEt = (EditText) findViewById(R.id.et_search);
        mCancelBtn = (TextView) findViewById(R.id.tv_cancel_btn);
        mContactListView = (RecyclerView) findViewById(R.id.contact_member);
    }

    @Override
    public void setViews() {
        pinyinComparator = new ChineseComparator();
        switch (operationType) {
            case CONSTACT_LIST_NOTFIST:
            case CONSTACT_LIST_FIRST:
                contactList = mSession.getContactList();
                break;
            case CUSTOMER_LIST_SELECT:
            case CUSTOMER_LIST:
                contactList = mSession.getCustomerList().getCustomerList();
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
        mAddCustomerTv.setOnClickListener(this);
        if(operationType == ContactCustomerListActivity.OperationType.CONSTACT_LIST_FIRST||operationType == ContactCustomerListActivity.OperationType.CONSTACT_LIST_NOTFIST) {
            mAdapter.setOnAddBtnClickListener(this);
            mAdapter.setOnItemClickListener(null);
        }else if(operationType == ContactCustomerListActivity.OperationType.CUSTOMER_LIST){
            mAdapter.setOnItemClickListener(this);
        }else if(operationType == ContactCustomerListActivity.OperationType.CUSTOMER_LIST_SELECT) {
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
                        switch (operationType) {
                            case CUSTOMER_LIST_SELECT:
                            case CUSTOMER_LIST:
                                if(contactFormatLike==null||contactFormatLike.size()==0) {
                                    showEmptyHintLayout();
                                }else {
                                    hideEmptyHintLayout();
                                }
                                break;
                        }

                        mAdapter.setData(contactFormatLike);
                    }else {
                        showEmptyHintLayout();
                    }
                }else {
                    List<ContactFormat> emptyList = new ArrayList<>();
                    mAdapter.setData(emptyList);
                }
            }
        });

        mCancelBtn.setOnClickListener(this);
    }

    private void hideEmptyHintLayout() {
        mEmptyHintLayout.setVisibility(View.GONE);
    }

    private void showEmptyHintLayout() {
        mEmptyHintLayout.setVisibility(View.VISIBLE);
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

        List<ContactFormat> customerList = mSession.getCustomerList().getCustomerList();
        if(customerList == null) {
            customerList = new ArrayList<>();
        }

        String mobile1 = contactFormat.getMobile1();
        String mobile = contactFormat.getMobile();
        if(TextUtils.isEmpty(mobile)&&TextUtils.isEmpty(mobile1)) {
            ShowMessage.showToast(this,"该客户没有手机号，请手动添加");
            return;
        }

        for(ContactFormat cacheContact : customerList){

            String cacheMobile = cacheContact.getMobile();
            String cacheMobile1 = cacheContact.getMobile1();
            String name = cacheContact.getName();
            if(TextUtils.isEmpty(mobile1)) {
                if(mobile.equals(cacheMobile)||mobile.equals(cacheMobile1)) {
                    ShowMessage.showToast(this,"与"+name+"的手机号码重复，添加失败");
                    return;
                }
            }else {
                if(mobile.equals(cacheMobile)||mobile.equals(cacheMobile1)||mobile1.equals(cacheMobile)||mobile1.equals(cacheMobile1)) {
                    ShowMessage.showToast(this,"与"+name+"的手机号码重复，添加失败");
                    return;
                }
            }
        }

        contactFormat.setAdded(true);
        customerList.add(contactFormat);
        Collections.sort(customerList,pinyinComparator);
        CustomerListBean cacheListBean = mSession.getCustomerList();
        cacheListBean.setCustomerList(customerList);
        mSession.setCustomerList(cacheListBean);
        contactFormat.setAdded(true);
        mAdapter.notifyDataSetChanged();

        AppApi.importInfoNew(this,importInfo,invitation,tel,this);

    }

    @Override
    public void onItemClick(int position, ContactFormat contactFormat) {
        if(operationType == ContactCustomerListActivity.OperationType.CUSTOMER_LIST_SELECT) {
            Intent intent = new Intent();
            intent.putExtra("customer",contactFormat);
            setResult(RESULT_CODE_SELECT,intent);
        }else {
            Intent intent = new Intent(this,UserInfoActivity.class);
            intent.putExtra("customer",contactFormat);
            intent.putExtra("customerID",contactFormat.getCustomer_id());
            startActivity(intent);
//            ShowMessage.showToast(this,"打开客户信息列表");
        }
        AppUtils.hideSoftKeybord(this);
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
        Intent intent;
        switch (v.getId()) {
            case R.id.tv_cancel_btn:
                AppUtils.hideSoftKeybord(this);
                finish();
                break;
            case R.id.tv_add_customer:
                intent = new Intent(this, AddCustomerActivity.class);
                intent.putExtra("type", AddCustomerActivity.CustomerOpType.TYPE_ADD);
                startActivity(intent);
                finish();
                break;
        }
    }

    @Override
    public void onSuccess(AppApi.Action method, Object obj) {
        switch (method) {
            case POST_IMPORT_INFO_NEW_JSON:
                ShowMessage.showToast(this,"导入成功");
                contactList.get(currentAddPosition).setAdded(true);
                if(obj instanceof ImportInfoResponse) {
                    ImportInfoResponse importInfoResponse = (ImportInfoResponse) obj;
                    List<ContactFormat> customer_list = importInfoResponse.getCustomer_list();
                    List<ContactFormat> cacheList = mSession.getCustomerList().getCustomerList();
                    if(customer_list!=null&&customer_list.size()>0) {
                        if(customer_list!=null) {
                            for(ContactFormat contactFormat:customer_list) {
                                int i = cacheList.indexOf(contactFormat);
                                if(i!=-1) {
                                    ContactFormat cacheCustomer = cacheList.get(i);
                                    cacheCustomer.setCustomer_id(contactFormat.getCustomer_id());
                                    cacheCustomer.setAdded(true);
                                }else {
                                    contactFormat.setAdded(true);
                                    cacheList.add(contactFormat);
                                }

                                List<ContactFormat> data = mAdapter.getData();
                                int indexOf = data.indexOf(contactFormat);
                                if(indexOf!=-1) {
                                    data.get(indexOf).setAdded(true);
                                }
                            }


                            CustomerListBean cacheListBean = mSession.getCustomerList();
                            Collections.sort(cacheList, pinyinComparator);
                            cacheListBean.setCustomerList(cacheList);
                            mSession.setCustomerList(cacheListBean);
                        }
                    }
                }
                hideLoadingLayout();
                mAdapter.notifyDataSetChanged();
                break;
        }
    }

    @Override
    public void onError(AppApi.Action method, Object obj) {
//        super.onError(method, obj);
        switch (method) {
            case POST_IMPORT_INFO_NEW_JSON:
                hideLoadingLayout();
                if(obj instanceof ResponseErrorMessage) {
                    ResponseErrorMessage message = (ResponseErrorMessage) obj;
                    String msg = message.getMessage();
                    int code = message.getCode();
                    if(code == 60016) {
                        ShowMessage.showToast(this,msg);
                    }else {

                    }
                }else  {

                }
                break;
        }
    }
}
