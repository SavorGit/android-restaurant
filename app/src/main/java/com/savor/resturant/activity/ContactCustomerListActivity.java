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

import com.common.api.utils.AppUtils;
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
import com.savor.resturant.bean.CustomerListBean;
import com.savor.resturant.bean.ImportInfoResponse;
import com.savor.resturant.bean.OperationFailedItem;
import com.savor.resturant.core.AppApi;
import com.savor.resturant.core.ResponseErrorMessage;
import com.savor.resturant.utils.ChineseComparator;
import com.savor.resturant.widget.LoadingDialog;
import com.savor.resturant.widget.contact.DividerDecoration;
import com.savor.resturant.widget.contact.SideBar;

import net.sourceforge.pinyin4j.PinyinHelper;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 客户列表，通讯录列表
 * @author hezd
 */
public class ContactCustomerListActivity extends BaseActivity implements View.OnClickListener, MyContactAdapter.OnAddBtnClickListener, MyContactAdapter.OnCheckStateChangeListener, MyContactAdapter.OnItemClickListener {
    public static final int RESULT_CODE_SELECT = 1000;
    public static final int REQUEST_CODE_SELECT = 1001;
    public static final int REQUEST_CODE_ADD = 1002;
    public static final int RESULT_CODE_ADD = 1003;
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
    private LinearLayout mCustomerEmptyHintLayout;
    private TextView mAddCustomerTv;
    private LoadingDialog loadingDialog;

    public enum OperationType implements Serializable{
        /**客户列表*/
        CUSTOMER_LIST,
        /**通讯录列表*/
        CONSTACT_LIST_FIRST,
        /**通讯录列表非首次导入*/
        CONSTACT_LIST_NOTFIST,
        /**选择客户*/
        CUSTOMER_LIST_SELECT,
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

    @Override
    protected void onRestart() {
        super.onRestart();
        switch (operationType) {
            case CUSTOMER_LIST_SELECT:
            case CUSTOMER_LIST:
                if(contactFormats != null&&contactFormats.size()>0) {
                    hideLoadingLayout();
                    hideEmptyCustomerHintLayout();
                }
                break;
        }

        initData();
        refreshSelectStatus();
    }

    public void initData() {
        showLoadingLayout();
        switch (operationType) {
            case CUSTOMER_LIST_SELECT:
            case CUSTOMER_LIST:
                contactFormats = mSession.getCustomerList().getCustomerList();
                if(contactFormats == null||contactFormats.size()==0) {
                    hideLoadingLayout();
                    showEmptyCustomerHintLayout();
                    return;
                }
                break;
            case CONSTACT_LIST_NOTFIST:
            case CONSTACT_LIST_FIRST:
                Query query = Contacts.getQuery();
                List<Contact> contacts = query.find();
                contactFormats = getFormatContactList(contacts);
                break;
        }
        new Thread(){
            @Override
            public void run() {
                Collections.sort(contactFormats, pinyinComparator);

                CustomerListBean customerList = mSession.getCustomerList();
                if(customerList!=null&&customerList.getCustomerList()!=null&&customerList.getCustomerList().size()>0) {
                    List<ContactFormat> customers = customerList.getCustomerList();
                    for(ContactFormat contactFormat:contactFormats) {
                        if(customers.contains(contactFormat)) {
                            contactFormat.setAdded(true);
                        }else {
                            outer:
                            for(ContactFormat customer : customers) {
                                String mobile = customer.getMobile();
                                String mobile1 = customer.getMobile1();
                                if(!TextUtils.isEmpty(mobile)) {
                                    if(mobile.equals(contactFormat.getMobile())||mobile.equals(contactFormat.getMobile1())) {
                                        contactFormat.setAdded(true);
                                        break outer;
                                    }
                                }else if(!TextUtils.isEmpty(mobile1)) {
                                    if(mobile1.equals(contactFormat.getMobile())||mobile.equals(contactFormat.getMobile1())) {
                                        contactFormat.setAdded(true);
                                        break outer;
                                    }
                                }
                            }
                        }
                    }
                }
                // 通讯录保存全局
                mSession.setContactList(contactFormats);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        adapter = new MyContactAdapter(ContactCustomerListActivity.this, contactFormats,operationType);
                        int orientation = LinearLayoutManager.VERTICAL;
                        final LinearLayoutManager layoutManager = new LinearLayoutManager(ContactCustomerListActivity.this, orientation, false);
                        recyclerView.setLayoutManager(layoutManager);

                        adapter.setSelectMode(isMultiSelectMode);
                        recyclerView.setAdapter(adapter);
//                        final StickyRecyclerHeadersDecoration headersDecor = new StickyRecyclerHeadersDecoration(adapter);
//                        recyclerView.addItemDecoration(headersDecor);
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
                if(phoneNumber!=null&&!TextUtils.isEmpty(phoneNumber.getNumber())) {
                    format.setMobile(phoneNumber.getNumber().replaceAll(" ",""));
                }
                if(phoneNumbers.size()>=2) {
                    PhoneNumber phoneNumber1 = phoneNumbers.get(1);
                    if(phoneNumber1!=null&&!TextUtils.isEmpty(phoneNumber1.getNumber())) {
                        format.setMobile1(phoneNumber1.getNumber().replaceAll(" ",""));
                    }
                }
            }else {
                continue;
            }

            StringBuilder sb = new StringBuilder();
            if(!TextUtils.isEmpty(displayName)) {
                displayName = displayName.trim().replaceAll(" ","");
                if(!isNumeric(displayName)&&!isLetter(displayName)) {
                    for(int i = 0;i<displayName.length();i++) {
                        String[] pinyinStringArray = PinyinHelper.toHanyuPinyinStringArray(displayName.charAt(i));
                        String appendStr = "";
                        if(pinyinStringArray == null || pinyinStringArray.length == 0) {
                            appendStr = String.valueOf(displayName.charAt(i));
                        }else {
                            appendStr= removeDigital(String.valueOf(PinyinHelper.toHanyuPinyinStringArray(displayName.charAt(i))[0]));
                        }

                        sb.append(appendStr);
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

        mCustomerEmptyHintLayout = (LinearLayout) findViewById(R.id.ll_empty_hint);
        mAddCustomerTv = (TextView) findViewById(R.id.tv_add_customer);
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
            case CUSTOMER_LIST_SELECT:
                mRightBtn.setVisibility(View.GONE);
                break;
        }
    }

    public void setListeners() {
        mAddCustomerTv.setOnClickListener(this);
        mRightBtn.setOnClickListener(this);
        mSearchTv.setOnClickListener(this);

        mBackBtn.setOnClickListener(this);

        mImportTv.setOnClickListener(this);

        mSelectAllCb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                refreshSelectStatus();
            }
        });


        mRightTv.setOnClickListener(this);

        sideBar.setOnTouchingLetterChangedListener(new SideBar.OnTouchingLetterChangedListener() {
            @Override
            public void onTouchingLetterChanged(String s) {
                hideSoftKeybord(ContactCustomerListActivity.this);
                int position = adapter.getPositionForSection(s.charAt(0));
                if (position != -1) {
                    LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                    int firstVisibleItem = layoutManager.findFirstVisibleItemPosition();
                    int lastVisibleItem = layoutManager.findLastVisibleItemPosition();
                    if(position<=firstVisibleItem) {
                        recyclerView.getLayoutManager().scrollToPosition(position);
                    }else if(position<=lastVisibleItem) {
                        int top = recyclerView.getChildAt(position - firstVisibleItem).getTop();
                        recyclerView.scrollBy(0,top);
                    }else {
                        recyclerView.getLayoutManager().scrollToPosition(position);
                    }
                }
            }
        });



    }

    private void refreshSelectStatus() {
        boolean checked = mSelectAllCb.isChecked();
        if(checked) {
            for(ContactFormat contactFormat:contactFormats) {
                contactFormat.setSelected(true);
            }
        }else {
            resetList();
        }
        if(adapter!=null) {
            adapter.notifyDataSetChanged();
        }
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
        if(loadingDialog == null)
            loadingDialog = new LoadingDialog(this);
        loadingDialog.show();
    }

    @Override
    public void hideLoadingLayout() {
        if(loadingDialog!=null&&loadingDialog.isShowing()&&!isFinishing())
        loadingDialog.dismiss();
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
            adapter.notifyDataSetChanged();

            switch (operationType) {
                case CONSTACT_LIST_NOTFIST:
                case CONSTACT_LIST_FIRST:
                    AppApi.importInfoNew(this,importInfo,invitation,tel,this);
                    break;
            }

    }

    @Override
    public void onCheckStateChange(boolean isChecked, ContactFormat contactFormat) {
        if(isChecked) {
            List<ContactFormat> data = adapter.getData();
            int count = 0;
            for(ContactFormat contat:data) {
                if(contat.isSelected()||contat.isAdded()) {
                    count++;
                }
            }
            if(count == data.size()) {
                mSelectAllCb.setChecked(true);
            }
//            selectedLsit.add(contactFormat);
        }else {
//            selectedLsit.remove(contactFormat);
            mSelectAllCb.setChecked(false);
        }
    }

    public void showEmptyCustomerHintLayout() {
        mCustomerEmptyHintLayout.setVisibility(View.VISIBLE);
    }

    public void hideEmptyCustomerHintLayout() {
        mCustomerEmptyHintLayout.setVisibility(View.GONE);
    }



    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.tv_add_customer:
                intent = new Intent(this, AddCustomerActivity.class);
                intent.putExtra("type", AddCustomerActivity.CustomerOpType.TYPE_ADD);
                startActivityForResult(intent,REQUEST_CODE_ADD);
                break;
            case R.id.iv_right:
                intent = new Intent(this,AddCustomerActivity.class);
                intent.putExtra("type", AddCustomerActivity.CustomerOpType.TYPE_ADD);
                startActivity(intent);
                break;
            case R.id.tv_search:
                if(operationType == OperationType.CUSTOMER_LIST_SELECT) {
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
                mImportTv.setEnabled(false);
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
                    showLoadingLayout();
                    AppApi.importInfoNew(this, importInfo, invitation, tel, this);
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
                ShowMessage.showToast(this,"导入成功");
                if(!isMultiSelectMode) {
                    contactFormats.get(currentAddPosition).setAdded(true);
                }
//                break;
//            case POST_IMPORT_INFO_JSON:

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

                                List<ContactFormat> data = adapter.getData();
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
                mImportTv.setEnabled(true);
                adapter.notifyDataSetChanged();
                break;
        }
    }

    @Override
    public void onError(AppApi.Action method, Object obj) {
//        super.onError(method, obj);
        switch (method) {
            case POST_IMPORT_INFO_NEW_JSON:
                mImportTv.setEnabled(true);
                hideLoadingLayout();
                if(obj instanceof ResponseErrorMessage) {
                    ResponseErrorMessage message = (ResponseErrorMessage) obj;
                    String msg = message.getMessage();
                    int code = message.getCode();
                    if(code == 60016) {
                        ShowMessage.showToast(this,msg);
                    }else {
                        addLocal();
                        addOpFailedList(selectedLsit, OperationFailedItem.OpType.TYPE_IMPORT_NEW);
                    }
                }else  {
                    addLocal();
                    addOpFailedList(selectedLsit, OperationFailedItem.OpType.TYPE_IMPORT_NEW);
                }
                break;
        }
    }

    private void addLocal() {
        CustomerListBean customerListBean = mSession.getCustomerList();
        if(customerListBean==null) {
            customerListBean = new CustomerListBean();

        }
        List<ContactFormat> customerList = customerListBean.getCustomerList();
        if(customerList==null) {
            customerList = new ArrayList<>();
        }
        customerList.addAll(selectedLsit);
        Collections.sort(customerList,pinyinComparator);
        customerListBean.setCustomerList(customerList);
        mSession.setCustomerList(customerListBean);

        for(ContactFormat customer : selectedLsit) {
            customer.setAdded(true);
        }
        adapter.notifyDataSetChanged();
        ShowMessage.showToast(this,"导入成功!");
    }

    private void addOpFailedList(List<ContactFormat> selectedLsit, OperationFailedItem.OpType typeImportNew) {
        CopyOnWriteArrayList<OperationFailedItem> failedItemList = mSession.getOpFailedList();
        OperationFailedItem item = new OperationFailedItem();
        if (failedItemList == null) {
            failedItemList = new CopyOnWriteArrayList<>();
        }
        if (isMultiSelectMode) {
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
        if(AppUtils.isFastDoubleClick(1))
            return;
        Intent intent;
        switch (operationType) {
            case CUSTOMER_LIST:
                intent = new Intent(this,UserInfoActivity.class);
                intent.putExtra("customer",contactFormat);
                intent.putExtra("customerID",contactFormat.getCustomer_id());
                startActivity(intent);
                break;
            case CUSTOMER_LIST_SELECT:
                intent = new Intent();
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
                finish();
            }
        }else if(requestCode == REQUEST_CODE_ADD&&resultCode == RESULT_CODE_ADD) {
            initData();
        }
    }
}
