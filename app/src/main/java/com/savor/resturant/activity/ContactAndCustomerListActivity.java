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

import com.github.tamir7.contacts.Address;
import com.github.tamir7.contacts.Contact;
import com.github.tamir7.contacts.Contacts;
import com.github.tamir7.contacts.Event;
import com.github.tamir7.contacts.PhoneNumber;
import com.github.tamir7.contacts.Query;
import com.savor.resturant.R;
import com.savor.resturant.adapter.contact.MyContactAdapter;
import com.savor.resturant.bean.ContactFormat;
import com.savor.resturant.utils.ChineseComparator;
import com.savor.resturant.utils.ContactUtil;
import com.savor.resturant.widget.contact.DividerDecoration;
import com.savor.resturant.widget.contact.SideBar;

import net.sourceforge.pinyin4j.PinyinHelper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 客户列表，通讯录列表
 * @author hezd
 */
public class ContactAndCustomerListActivity extends BaseActivity {
    private List<ContactFormat> contactFormats;
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
//        List<ContactFormat> contactFormats = Contacts.getQuery().find();
        showLoadingLayout();
        new Thread(){
            @Override
            public void run() {
//                contactFormats = ContactUtil.getInstance().getAllContact(ContactAndCustomerListActivity.this);
                Query query = Contacts.getQuery();
                List<Contact> contacts = query.find();
                contactFormats = getFormatContactList(contacts);
                Collections.sort(contactFormats, pinyinComparator);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        adapter = new MyContactAdapter(ContactAndCustomerListActivity.this, contactFormats);
                        int orientation = LinearLayoutManager.VERTICAL;
                        final LinearLayoutManager layoutManager = new LinearLayoutManager(ContactAndCustomerListActivity.this, orientation, false);
                        recyclerView.setLayoutManager(layoutManager);

                        recyclerView.setAdapter(adapter);
                        recyclerView.addItemDecoration(new DividerDecoration(ContactAndCustomerListActivity.this));

                        hideLoadingLayout();
                    }
                });
            }
        }.start();

    }

    private List<ContactFormat> getFormatContactList(List<Contact> contacts) {
        List<ContactFormat> tempList = new ArrayList<>();
        for(Contact contact : contacts) {
            ContactFormat format = new ContactFormat();
            String displayName = contact.getDisplayName();
            List<Address> addresses = contact.getAddresses();
            Event birthday = contact.getBirthday();
            List<PhoneNumber> phoneNumbers = contact.getPhoneNumbers();

            if(birthday!=null&&!TextUtils.isEmpty(birthday.getStartDate())) {
                format.setBirthday(birthday.getStartDate());
            }

            if(addresses!=null&&addresses.size()>0) {
                Address address = addresses.get(0);
                if(address!=null&&!TextUtils.isEmpty(address.getFormattedAddress())) {
                    format.setBirthplace(address.getFormattedAddress());
                }
            }

            format.setName(displayName);

            if(phoneNumbers!=null&&phoneNumbers.size()>0) {
                PhoneNumber phoneNumber = phoneNumbers.get(0);
                if(phoneNumber!=null&&!TextUtils.isEmpty(phoneNumber.getNormalizedNumber())) {
                    format.setMobile(phoneNumber.getNormalizedNumber());
                }
                if(phoneNumbers.size()>=2) {
                    PhoneNumber phoneNumber1 = phoneNumbers.get(1);
                    if(phoneNumber1!=null&&!TextUtils.isEmpty(phoneNumber1.getNormalizedNumber())) {
                        format.setMobile1(phoneNumber1.getNormalizedNumber());
                    }
                }
            }

            StringBuilder sb = new StringBuilder();
            if(!TextUtils.isEmpty(displayName)) {
                displayName = displayName.trim().replaceAll(" ","");
                if(!isNumeric(displayName)&&!isLetter(displayName)) {
                    for(int i = 0;i<displayName.length();i++) {
                        String str= removeDigital(String.valueOf(PinyinHelper.toHanyuPinyinStringArray(displayName.charAt(i))[0]));
                        sb.append(str);
                    }
                }else {
                    sb.append(displayName);
                }
            }
            String mobile = format.getMobile();
            format.setKey(displayName+"#"+sb.toString().toLowerCase()+"#"+format.getBirthplace()+"#"+(TextUtils.isEmpty(mobile)?"":mobile));

            tempList.add(format);
        }

        return tempList;
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
                    if(contactFormats !=null&& contactFormats.size()>0) {
                        List<ContactFormat> contactFormatLike = getLikeList(contactFormats,content);
                        adapter.setData(contactFormatLike);
                    }
                }else {
                    if(contactFormats == null) {
                        contactFormats = ContactUtil.getInstance().getAllContact(ContactAndCustomerListActivity.this);
                    }
                    adapter.setData(contactFormats);
                }
            }
        });
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

    public boolean isNumeric(String str){
        Pattern pattern = Pattern.compile("[0-9]*");
        Matcher isNum = pattern.matcher(str);
        if( !isNum.matches() ){
            return false;
        }
        return true;
    }

    /**
     * 剔除数字
     * @param value
     */
    public String removeDigital(String value){

        Pattern p = Pattern.compile("[\\d]");
        Matcher matcher = p.matcher(value);
        String result = matcher.replaceAll("");
        return result;
    }

    /*判断字符串中是否仅包含字母数字和汉字
      *各种字符的unicode编码的范围：
     * 汉字：[0x4e00,0x9fa5]（或十进制[19968,40869]）
     * 数字：[0x30,0x39]（或十进制[48, 57]）
     *小写字母：[0x61,0x7a]（或十进制[97, 122]）
     * 大写字母：[0x41,0x5a]（或十进制[65, 90]）
*/
    public static boolean isLetter(String str) {
        String regex = "^[a-zA-Z]+$";
        return str.matches(regex);
    }
}
