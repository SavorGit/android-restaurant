package com.savor.resturant.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.common.api.utils.ShowMessage;
import com.github.tamir7.contacts.Address;
import com.github.tamir7.contacts.Contact;
import com.github.tamir7.contacts.Contacts;
import com.github.tamir7.contacts.Event;
import com.github.tamir7.contacts.PhoneNumber;
import com.github.tamir7.contacts.Query;
import com.google.gson.Gson;
import com.savor.resturant.R;
import com.savor.resturant.adapter.contact.MyContactAdapter;
import com.savor.resturant.bean.ContactFormat;
import com.savor.resturant.bean.ImportInfoResponse;
import com.savor.resturant.bean.OperationFailedItem;
import com.savor.resturant.core.AppApi;
import com.savor.resturant.core.ResponseErrorMessage;
import com.savor.resturant.utils.ChineseComparator;
import com.savor.resturant.widget.contact.DividerDecoration;
import com.savor.resturant.widget.contact.SideBar;

import net.sourceforge.pinyin4j.PinyinHelper;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 客户列表，通讯录列表
 * @author hezd
 */
public class ContactCustomerListActivity extends BaseActivity implements View.OnClickListener, MyContactAdapter.OnAddBtnClickListener, MyContactAdapter.OnCheckStateChangeListener, MyContactAdapter.OnItemClickListener {
    public static final int RESULT_CODE_SELECT = 1000;
    public static final int REQUEST_CODE_SELECT = 1001;
    private List<ContactFormat> contactFormats;
    private ChineseComparator pinyinComparator;
    private MyContactAdapter adapter;
    private TextView contactDialog;
    private SideBar sideBar;
    private RecyclerView recyclerView;
//    private EditText mSearchEt;
    private ProgressBar mLoadingPb;
    private OperationType operationType;
    private TextView mTitleTv;
    private TextView mRightTv;
    /**是否是多选状态*/
    private boolean isMultiSelectMode;

    private List<ContactFormat> selectedLsit = new ArrayList<>();
    private CheckBox mSelectAllCb;
    private TextView mImportTv;
    private LinearLayout mImportLayout;
    private int currentAddPosition;
    private ImageView mBackBtn;
    private TextView mSearchTv;
    private ImageView mRightBtn;

    public enum OperationType implements Serializable{
        /**客户列表*/
        CUSTOMER_LIST,
        /**通讯录列表*/
        CONSTACT_LIST_FIRST,
        /**通讯录列表非首次导入*/
        CONSTACT_LIST_NOTFIST,
        /**选择客户*/
        CONSTACT_LIST_SELECT,
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_and_customer_list);

        handleIntent();
        getViews();
        setViews();
        setListeners();
        initData();
    }

    private void handleIntent() {
        operationType = (OperationType) getIntent().getSerializableExtra("type");
    }

    public void initData() {
//        List<ContactFormat> contactFormats = Contacts.getQuery().find();
        showLoadingLayout();
        new Thread(){
            @Override
            public void run() {
//                contactFormats = ContactUtil.getInstance().getAllContact(ContactCustomerListActivity.this);
                switch (operationType) {
                    case CONSTACT_LIST_SELECT:
                    case CUSTOMER_LIST:
                        contactFormats = mSession.getCustomerList();
                        break;
                    case CONSTACT_LIST_NOTFIST:
                    case CONSTACT_LIST_FIRST:
                        Query query = Contacts.getQuery();
                        List<Contact> contacts = query.find();
                        contactFormats = getFormatContactList(contacts);
                        break;
                }

                Collections.sort(contactFormats, pinyinComparator);

                // 通讯录保存全局
                mSession.setContactList(contactFormats);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        adapter = new MyContactAdapter(ContactCustomerListActivity.this, contactFormats,operationType);
                        int orientation = LinearLayoutManager.VERTICAL;
                        final LinearLayoutManager layoutManager = new LinearLayoutManager(ContactCustomerListActivity.this, orientation, false);
                        recyclerView.setLayoutManager(layoutManager);

                        recyclerView.setAdapter(adapter);
                        recyclerView.addItemDecoration(new DividerDecoration(ContactCustomerListActivity.this));

                        adapter.setOnCheckStateChangeListener(ContactCustomerListActivity.this);
                        adapter.setOnAddBtnClickListener(ContactCustomerListActivity.this);
                        adapter.setOnItemClickListener(ContactCustomerListActivity.this);
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
            Long id = contact.getId();
            format.setContactId(String.valueOf(id));

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
            String stuf = "";
            if(isLetter(displayName)||isNumeric(displayName)) {
                stuf = "#";
            }

            format.setKey(stuf+displayName+"#"+sb.toString().toLowerCase()+"#"+(TextUtils.isEmpty(format.getBirthplace())?"":format.getBirthplace())+"#"+(TextUtils.isEmpty(mobile)?"":mobile));

            tempList.add(format);
        }

        return tempList;
    }

    public void getViews() {
        mBackBtn = (ImageView) findViewById(R.id.iv_left);
        mImportLayout = (LinearLayout) findViewById(R.id.ll_import_layout);
        mSelectAllCb = (CheckBox) findViewById(R.id.cb_select_all);
        mImportTv = (TextView) findViewById(R.id.tv_import);

        mTitleTv = (TextView) findViewById(R.id.tv_center);
        mLoadingPb = (ProgressBar) findViewById(R.id.pb_loading);
        contactDialog = (TextView) findViewById(R.id.contact_dialog);
        sideBar = (SideBar) findViewById(R.id.contact_sidebar);
        recyclerView = (RecyclerView) findViewById(R.id.contact_member);
        mSearchTv = (TextView) findViewById(R.id.tv_search);

        mRightTv = (TextView) findViewById(R.id.tv_right);
        mRightBtn = (ImageView) findViewById(R.id.iv_right);

    }

    public void setViews() {
        mTitleTv.setTextColor(getResources().getColor(R.color.white));
        sideBar.setTextView(contactDialog);
        pinyinComparator = new ChineseComparator();

        switch (operationType) {
            case CONSTACT_LIST_NOTFIST:
            case CONSTACT_LIST_FIRST:
                mRightBtn.setVisibility(View.GONE);
                mRightTv.setVisibility(View.VISIBLE);
                mRightTv.setText("多选");
                mTitleTv.setText("通讯录");
                break;
            case CUSTOMER_LIST:
                mRightBtn.setVisibility(View.VISIBLE);
                mRightBtn.setImageResource(R.drawable.ico_customer_list_add);
                mRightTv.setVisibility(View.GONE);
                mTitleTv.setText("客户列表");
                break;
            case CONSTACT_LIST_SELECT:
                mRightBtn.setVisibility(View.GONE);
                break;
        }
    }

    public void setListeners() {
        mRightBtn.setOnClickListener(this);
        mSearchTv.setOnClickListener(this);

        mBackBtn.setOnClickListener(this);

        mImportTv.setOnClickListener(this);

        mSelectAllCb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean checked = mSelectAllCb.isChecked();
                if(checked) {
                    for(ContactFormat contactFormat:contactFormats) {
                        contactFormat.setSelected(true);
                    }
                }else {
                    resetList();
                }
                adapter.notifyDataSetChanged();
            }
        });


        mRightTv.setOnClickListener(this);

        sideBar.setOnTouchingLetterChangedListener(new SideBar.OnTouchingLetterChangedListener() {
            @Override
            public void onTouchingLetterChanged(String s) {
                hideSoftKeybord(ContactCustomerListActivity.this);
                int position = adapter.getPositionForSection(s.charAt(0));
                if (position != -1) {
                    recyclerView.getLayoutManager().scrollToPosition(position);
                }
            }
        });



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

    @Override
    public void onAddBtnClick(int position, ContactFormat contactFormat) {
        currentAddPosition = position;
        List<ContactFormat> contactFormats = new ArrayList<>();
        contactFormats.add(contactFormat);
        String importInfo = new Gson().toJson(contactFormats);
        String invitation = mSession.getHotelBean().getInvite_id();
        String tel = mSession.getHotelBean().getTel();

        List<ContactFormat> customerList = mSession.getCustomerList();
        if(customerList == null) {
            customerList = new ArrayList<>();
        }

        String mobile1 = contactFormat.getMobile1();
        String mobile = contactFormat.getMobile();
        if(TextUtils.isEmpty(mobile)&&TextUtils.isEmpty(mobile1)) {
            ShowMessage.showToast(this,"不能添加手机号为空的客户");
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
            mSession.setCustomerList(customerList);

            switch (operationType) {
                case CONSTACT_LIST_NOTFIST:
                    AppApi.importInfoNew(this,importInfo,invitation,tel,this);
                    break;
                case CONSTACT_LIST_FIRST:
                    AppApi.importInfoFirst(this,importInfo,invitation,tel,this);
                    break;
            }

    }

    @Override
    public void onCheckStateChange(boolean isChecked, ContactFormat contactFormat) {
        if(isChecked) {
//            selectedLsit.add(contactFormat);
        }else {
//            selectedLsit.remove(contactFormat);
            mSelectAllCb.setChecked(false);
        }
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.iv_right:
                intent = new Intent(this,AddCustomerActivity.class);
                startActivity(intent);
                break;
            case R.id.tv_search:
                if(operationType == OperationType.CONSTACT_LIST_SELECT) {
                    intent = new Intent(this,SearchActivity.class);
                    intent.putExtra("type",operationType);
                    startActivityForResult(intent,REQUEST_CODE_SELECT);
                }else {
                    intent = new Intent(this,SearchActivity.class);
                    intent.putExtra("type",operationType);
                    startActivity(intent);
                }
                break;
            case R.id.iv_left:
                finish();
                break;
            case R.id.tv_import:
                selectedLsit.clear();
                List<ContactFormat> data = adapter.getData();
                if(data.size()==0) {
                    ShowMessage.showToast(this,"请选择要导入联系人");
                    return;
                }
                boolean checked = mSelectAllCb.isChecked();
                if(checked) {
                    // 获取为添加的集合
                    List<ContactFormat> enableList = getEnableList(data);
                    selectedLsit.addAll(enableList);
                }else {
                    for(ContactFormat contactFormat:data) {
                        if(contactFormat.isSelected()) {
                            selectedLsit.add(contactFormat);
                        }
                    }
                }

                if(selectedLsit.size()==0) {
                    ShowMessage.showToast(this,"请选择未添加的客户");
                }else {
                    String importInfo = new Gson().toJson(selectedLsit);
                    String invitation = mSession.getHotelBean().getInvite_id();
                    String tel = mSession.getHotelBean().getTel();
                    if (operationType == OperationType.CONSTACT_LIST_FIRST) {
                        AppApi.importInfoFirst(this, importInfo, invitation, tel, this);
                    } else if (operationType == OperationType.CONSTACT_LIST_NOTFIST) {
                        AppApi.importInfoNew(this, importInfo, invitation, tel, this);
                    }
                }

                break;
            case R.id.tv_right:
                if (isMultiSelectMode) {
                    mImportLayout.setVisibility(View.GONE);
                    mSelectAllCb.setChecked(false);
                    mRightTv.setText("多选");
                    resetList();
                    adapter.setSelectMode(false);
                    selectedLsit.clear();
                } else {
                    mImportLayout.setVisibility(View.VISIBLE);
                    mRightTv.setText("取消");
                    adapter.setSelectMode(true);
                }
                isMultiSelectMode = !isMultiSelectMode;
                break;
        }
    }

    private List<ContactFormat> getEnableList(List<ContactFormat> data) {
        List<ContactFormat> enableList = new ArrayList<>();
        for(ContactFormat contactFormat :data ) {
            if(!contactFormat.isAdded()) {
                enableList.add(contactFormat);
            }
        }
        return enableList;
    }

    private void resetList() {
        for(ContactFormat contactFormat: contactFormats) {
            contactFormat.setSelected(false);
        }
    }

    @Override
    public void onSuccess(AppApi.Action method, Object obj) {
        super.onSuccess(method, obj);
        switch (method) {
            case POST_IMPORT_INFO_NEW_JSON:
//                ShowMessage.showToast(this,"导入成功");
                contactFormats.get(currentAddPosition).setAdded(true);
                adapter.notifyDataSetChanged();
//                break;
            case POST_IMPORT_INFO_JSON:
                if(obj instanceof ImportInfoResponse) {
                    ImportInfoResponse importInfoResponse = (ImportInfoResponse) obj;
                    List<ContactFormat> customer_list = importInfoResponse.getCustomer_list();
                    List<ContactFormat> cacheList = mSession.getCustomerList();
                    if(customer_list!=null&&customer_list.size()>0) {
                        if(customer_list!=null) {
                            for(ContactFormat contactFormat:customer_list) {
                                int i = cacheList.indexOf(contactFormat);
                                if(i!=-1) {
                                    ContactFormat cacheCustomer = cacheList.get(i);
                                    cacheCustomer.setCustomer_id(contactFormat.getCustomer_id());
                                }
                            }
                            mSession.setCustomerList(cacheList);
                        }
                    }
                }
                break;
        }
    }

    @Override
    public void onError(AppApi.Action method, Object obj) {
//        super.onError(method, obj);
        switch (method) {
            case POST_IMPORT_INFO_NEW_JSON:
                if(obj instanceof ResponseErrorMessage) {
                    ResponseErrorMessage message = (ResponseErrorMessage) obj;
                    String msg = message.getMessage();
                    int code = message.getCode();
                    if(code == 60105||code == 60106) {

                    }else {
                        addOpFailedList(selectedLsit, OperationFailedItem.OpType.TYPE_IMPORT_NEW);
                    }
                }else  {
                    addOpFailedList(selectedLsit, OperationFailedItem.OpType.TYPE_IMPORT_NEW);
                }
                break;
            case POST_IMPORT_INFO_JSON:
                if(obj instanceof ResponseErrorMessage) {
                    ResponseErrorMessage message = (ResponseErrorMessage) obj;
                    String msg = message.getMessage();
                    showToast(msg);
                }else  {
                    addOpFailedList(selectedLsit, OperationFailedItem.OpType.TYPE_IMPORT_FIRST);
                }
                break;
        }
    }

    private void addOpFailedList(List<ContactFormat> selectedLsit, OperationFailedItem.OpType typeImportNew) {
        List<OperationFailedItem> failedItemList = mSession.getOpFailedList();
        OperationFailedItem item = new OperationFailedItem();
        if (isMultiSelectMode) {
            if (failedItemList == null) {
                failedItemList = new ArrayList<>();
            }
            item.setContactFormat(selectedLsit);
            item.setType(typeImportNew);
        } else {
            List<ContactFormat> list = new ArrayList<>();
            list.add(contactFormats.get(currentAddPosition));
            item.setContactFormat(list);
            item.setType(typeImportNew);
        }
        failedItemList.add(item);
        mSession.setOpFailedList(failedItemList);
    }

    @Override
    public void onItemClick(int position, ContactFormat contactFormat) {
        switch (operationType) {
            case CONSTACT_LIST_SELECT:
                Intent intent = new Intent();
                intent.putExtra("customer",contactFormat);
                setResult(RESULT_CODE_SELECT,intent);
                finish();
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_CODE_SELECT || resultCode == RESULT_CODE_SELECT) {
            if(data!=null) {
                ContactFormat contactFormat = (ContactFormat) data.getSerializableExtra("customer");
                Intent intent = new Intent();
                intent.putExtra("customer",contactFormat);
                setResult(RESULT_CODE_SELECT,intent);
            }
        }
    }
}
