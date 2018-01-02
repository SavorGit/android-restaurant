package com.savor.resturant.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.common.api.utils.LogUtils;
import com.common.api.widget.customTab.MyTabWidget;
import com.savor.resturant.R;
import com.savor.resturant.bean.ContactFormat;
import com.savor.resturant.fragment.BookFragment;
import com.savor.resturant.fragment.CustomerFragment;
import com.savor.resturant.fragment.MyFragment;
import com.savor.resturant.fragment.ProjectionFragment;
import com.savor.resturant.presenter.SensePresenter;
import com.savor.resturant.utils.ConstantValues;
import com.savor.resturant.widget.ImportDialog;

import java.util.List;

public class SavorMainActivity extends BaseFragmentActivity implements MyTabWidget.OnTabSelectedListener {
    public static final String SMALL_PLATFORM = "small_platform";
    private FragmentManager supportFragmentManager;
    private MyTabWidget tabWidget;
    private int currentIndex;
    private BookFragment bookFragment;
    private CustomerFragment customerFragment;
    private ProjectionFragment projectionFragment;
    private MyFragment myFragment;
    private SmallPlatformReciver smallPlatformReciver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_savor_main);


        supportFragmentManager = getSupportFragmentManager();
        getViews();
        setViews();
        setListeners();
        showImportDialog();
//        checkShouldShowImportDialog();

        regitsterSmallPlatformReciever();
    }

    private void checkShouldShowImportDialog() {
        List<ContactFormat> customerList = mSession.getCustomerList();
        String is_import_customer = mSession.getHotelBean().getIs_import_customer();
        if(!"1".equals(is_import_customer)&&(customerList==null||customerList.size() == 0)) {
            showImportDialog();
        }
    }

    /**
     * 注册小平台发现广播
     */
    public void regitsterSmallPlatformReciever() {
        smallPlatformReciver = new SmallPlatformReciver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(SMALL_PLATFORM);
        mContext.registerReceiver(smallPlatformReciver,filter);
    }

    private void showImportDialog() {
        new ImportDialog(this, new ImportDialog.OnImportBtnClickListener() {
            @Override
            public void onImportBtnClick() {
                Intent intent = new Intent(SavorMainActivity.this,ContactCustomerListActivity.class);
                intent.putExtra("type", ContactCustomerListActivity.OperationType.CONSTACT_LIST_FIRST);
                startActivity(intent);
            }
        }).show();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt("tab_index", currentIndex);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        currentIndex = savedInstanceState.getInt("tab_index");

    }

    @Override
    public void getViews() {
        tabWidget = (MyTabWidget) findViewById(R.id.tabwidget);
    }

    @Override
    public void setViews() {
        onTabSelected(currentIndex);
        tabWidget.setTabsDisplay(currentIndex);
    }

    @Override
    public void setListeners() {
        tabWidget.setOnTabSelectedListener(this);
    }

    @Override
    public void onTabSelected(int index) {
        FragmentTransaction fragmentTransaction = supportFragmentManager.beginTransaction();
        hideFragments(fragmentTransaction);
        switch (index) {
            case ConstantValues.HomeTabIndex.TAB_INDEX_BOOK:
                if(bookFragment == null) {
                    bookFragment = BookFragment.newInstance();
                    fragmentTransaction.add(R.id.center_layout,bookFragment,"book_fragment");
                }else {
                    fragmentTransaction.show(bookFragment);
                }
                break;
                case ConstantValues.HomeTabIndex.TAB_INDEX_CUSTOMER:
                    if(customerFragment == null) {
                        customerFragment = CustomerFragment.newInstance();
                        fragmentTransaction.add(R.id.center_layout,customerFragment,"customer_fragment");
                    }else {
                        fragmentTransaction.show(customerFragment);
                    }
                    break;
                case ConstantValues.HomeTabIndex.TAB_INDEX_SERVICE:
                    if(projectionFragment == null) {
                        projectionFragment = ProjectionFragment.newInstance();
                        fragmentTransaction.add(R.id.center_layout, projectionFragment,"service_fragment");
                    }else {
                        fragmentTransaction.show(projectionFragment);
                    }
                    break;
                case ConstantValues.HomeTabIndex.TAB_INDEX_MY:
                    if(myFragment == null) {
                        myFragment = MyFragment.newInstance();
                        fragmentTransaction.add(R.id.center_layout,myFragment,"my_fragment");
                    }else {
                        fragmentTransaction.show(myFragment);
                    }
                    break;
        }

        currentIndex = index;
        fragmentTransaction.commitNowAllowingStateLoss();

    }

    private void hideFragments(FragmentTransaction fragmentTransaction) {
        if(bookFragment!=null) {
            fragmentTransaction.hide(bookFragment);
        }

        if(customerFragment!=null) {
            fragmentTransaction.hide(customerFragment);
        }

        if(projectionFragment !=null) {
            fragmentTransaction.hide(projectionFragment);
        }

        if(myFragment!=null) {
            fragmentTransaction.hide(myFragment);
        }

    }


    public class SmallPlatformReciver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction().equals(SensePresenter.SMALL_PLATFORM)) {
                LogUtils.d("savor:ssdp 收到小平台接受广播");
                if(projectionFragment!=null) {
                    projectionFragment.initWIfiHint();
                }
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(smallPlatformReciver!=null) {
            unregisterReceiver(smallPlatformReciver);
        }
    }
}
