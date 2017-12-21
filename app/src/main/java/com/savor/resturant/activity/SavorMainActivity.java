package com.savor.resturant.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.common.api.utils.ShowMessage;
import com.common.api.widget.customTab.MyTabWidget;
import com.savor.resturant.R;
import com.savor.resturant.fragment.BookFragment;
import com.savor.resturant.fragment.CustomerFragment;
import com.savor.resturant.fragment.MyFragment;
import com.savor.resturant.fragment.ServiceFragment;
import com.savor.resturant.utils.ConstantValues;
import com.savor.resturant.widget.ImportDialog;

public class SavorMainActivity extends BaseFragmentActivity implements MyTabWidget.OnTabSelectedListener {

    private FragmentManager supportFragmentManager;
    private MyTabWidget tabWidget;
    private int currentIndex;
    private BookFragment bookFragment;
    private CustomerFragment customerFragment;
    private ServiceFragment serviceFragment;
    private MyFragment myFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_savor_main);


        supportFragmentManager = getSupportFragmentManager();
        getViews();
        setViews();
        setListeners();
        testDialog();
    }

    private void testDialog() {
        new ImportDialog(this, new ImportDialog.OnImportBtnClickListener() {
            @Override
            public void onImportBtnClick() {
                Intent intent = new Intent(SavorMainActivity.this,ContactAndCustomerListActivity.class);
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
                    if(serviceFragment == null) {
                        serviceFragment = ServiceFragment.newInstance();
                        fragmentTransaction.add(R.id.center_layout,serviceFragment,"service_fragment");
                    }else {
                        fragmentTransaction.show(serviceFragment);
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

        if(serviceFragment!=null) {
            fragmentTransaction.hide(serviceFragment);
        }

        if(myFragment!=null) {
            fragmentTransaction.hide(myFragment);
        }

    }
}
