package com.savor.resturant.activity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.savor.resturant.R;
import com.savor.resturant.adapter.contact.MyContactAdapter;
import com.savor.resturant.bean.MyContact;
import com.savor.resturant.utils.ChineseComparator;
import com.savor.resturant.utils.ContactUtil;
import com.savor.resturant.widget.contact.DividerDecoration;
import com.savor.resturant.widget.contact.SideBar;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 客户列表，通讯录列表
 * @author hezd
 */
public class ContactAndCustomerListActivity extends BaseActivity {
    private List<MyContact> contacts;
    private ChineseComparator pinyinComparator;
    private MyContactAdapter adapter;
    private TextView contactDialog;
    private SideBar sideBar;
    private RecyclerView recyclerView;
    private EditText mSearchEt;
    private ProgressBar mLoadingPb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_and_customer_list);

        getViews();
        setViews();
        setListeners();
        initData();
    }

    public void initData() {
//        List<Contact> contacts = Contacts.getQuery().find();
        showLoadingLayout();
        recyclerView.postDelayed(new Runnable() {
            @Override
            public void run() {


                contacts = ContactUtil.getInstance().getAllContact(ContactAndCustomerListActivity.this);
                Collections.sort(contacts, pinyinComparator);
                adapter = new MyContactAdapter(ContactAndCustomerListActivity.this, contacts);
                int orientation = LinearLayoutManager.VERTICAL;
                final LinearLayoutManager layoutManager = new LinearLayoutManager(ContactAndCustomerListActivity.this, orientation, false);
                recyclerView.setLayoutManager(layoutManager);

                recyclerView.setAdapter(adapter);
                recyclerView.addItemDecoration(new DividerDecoration(ContactAndCustomerListActivity.this));

                hideLoadingLayout();
            }
        },100);

    }

    public void getViews() {
        mLoadingPb = (ProgressBar) findViewById(R.id.pb_loading);
        contactDialog = (TextView) findViewById(R.id.contact_dialog);
        sideBar = (SideBar) findViewById(R.id.contact_sidebar);
        recyclerView = (RecyclerView) findViewById(R.id.contact_member);
        mSearchEt = (EditText) findViewById(R.id.et_search);
    }

    public void setViews() {

        sideBar.setTextView(contactDialog);

        pinyinComparator = new ChineseComparator();
    }

    public void setListeners() {
        sideBar.setOnTouchingLetterChangedListener(new SideBar.OnTouchingLetterChangedListener() {
            @Override
            public void onTouchingLetterChanged(String s) {
                hideSoftKeybord(ContactAndCustomerListActivity.this);
                int position = adapter.getPositionForSection(s.charAt(0));
                if (position != -1) {
                    recyclerView.getLayoutManager().scrollToPosition(position);
                }
            }
        });

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
                    if(contacts!=null&&contacts.size()>0) {
                        List<MyContact> contactLike = getLikeList(contacts,content);
                        adapter.setData(contactLike);
                    }
                }else {
                    if(contacts == null) {
                        contacts = ContactUtil.getInstance().getAllContact(ContactAndCustomerListActivity.this);
                    }
                    adapter.setData(contacts);
                }
            }
        });
    }

    private List<MyContact> getLikeList(List<MyContact> contacts, String content) {
        List<MyContact> tempList = new ArrayList<>();
        for(MyContact contact : contacts) {
            if(contact.getKey().trim().contains(content)) {
                tempList.add(contact);
            }
        }
        return tempList;
    }

    public void hideSoftKeybord(Activity activity) {

        if (null == activity) {
            return;
        }
        try {
            final View v = activity.getWindow().peekDecorView();
            if (v != null && v.getWindowToken() != null) {
                InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
            }
        } catch (Exception e) {

        }
    }

    @Override
    public void showLoadingLayout() {
        mLoadingPb.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideLoadingLayout() {
        mLoadingPb.setVisibility(View.GONE);
    }
}
