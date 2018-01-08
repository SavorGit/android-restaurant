package com.savor.resturant.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextPaint;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bigkoo.pickerview.TimePickerView;
import com.common.api.okhttp.OkHttpUtils;
import com.common.api.utils.DensityUtil;
import com.common.api.utils.ShowMessage;
import com.common.api.widget.pulltorefresh.library.PullToRefreshBase;
import com.common.api.widget.pulltorefresh.library.PullToRefreshListView;
import com.savor.resturant.R;
import com.savor.resturant.adapter.BookAdapter;
import com.savor.resturant.adapter.RoomListAdapter;
import com.savor.resturant.bean.BookListResult;
import com.savor.resturant.bean.HotelBean;
import com.savor.resturant.bean.OrderListBean;
import com.savor.resturant.bean.RoomInfo;
import com.savor.resturant.bean.SmallPlatInfoBySSDP;
import com.savor.resturant.bean.SmallPlatformByGetIp;
import com.savor.resturant.bean.TvBoxSSDPInfo;
import com.savor.resturant.core.AppApi;
import com.savor.resturant.core.ResponseErrorMessage;
import com.savor.resturant.widget.LoadingDialog;
import com.savor.resturant.widget.decoration.SpacesItemDecoration;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/**
 * 欢迎词文背景置页
 */
public class BookListByDateActivity extends BaseActivity implements View.OnClickListener,AdapterView.OnItemClickListener
{

    private Context context;
    private ImageView iv_left;
    private TextView tv_center;
    private ImageView iv_right;
    private String curDate;
    private PullToRefreshListView listview;
    private boolean isUp = false;
    private BookAdapter bookAdapter;
    private int page_num = 1;
            private String yesterday_order_nums;
            private String today_order_nums;
            private String tomorrow_order_nums;
            private String after_tomorrow_order_nums;
            private List<OrderListBean> listItems = new ArrayList<>();
    private static final int REQUEST_ADD_BOOK = 308;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activityt_book);
        getDate();
        context = this;
        getViews();
        setViews();
        setListeners();
    }

    private void getDate(){
        Intent intent = getIntent();
        if (intent != null) {
            curDate = intent.getStringExtra("Date");

        }
    }

    @Override
    public void getViews() {
        iv_left = (ImageView) findViewById(R.id.iv_left);
        tv_center = (TextView) findViewById(R.id.tv_center);
        iv_right = (ImageView) findViewById(R.id.iv_right);
        listview = (PullToRefreshListView) findViewById(R.id.listview);
    }

    @Override
    public void setViews() {
        tv_center.setText(curDate);
        bookAdapter = new BookAdapter(mContext);
        listview.setAdapter(bookAdapter);
        iv_right.setVisibility(View.VISIBLE);
        iv_right.setBackgroundResource(R.drawable.rili);

    }

    @Override
    public void setListeners() {

        iv_left.setOnClickListener(this);
        tv_center.setOnClickListener(this);
        iv_right.setOnClickListener(this);
        listview.setOnRefreshListener(onRefreshListener);
        listview.setOnLastItemVisibleListener(onLastItemVisibleListener);
        listview.setOnItemClickListener(this);

    }

      private void showDateDialog() {
                TimePickerView timePickerView = new TimePickerView.Builder(mContext, new TimePickerView.OnTimeSelectListener() {
                    @Override
                    public void onTimeSelect(java.util.Date date, View v) {
                        curDate = getDataTime(date);
                        //setTime(date);
                        tv_center.setText(curDate);
                        page_num = 1;
                        isUp = true;
                        getOrderList();

                    }
                }).setType(new boolean[]{true, true, true, false, false, false}).isCenterLabel(false).build();
                timePickerView.show();
      }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.iv_left:
                finish();
                break;
            case R.id.iv_right:
                showDateDialog();
                break;


            default:
                break;
        }
    }








    @Override
    public void onSuccess(AppApi.Action method, Object obj) {
        hideLoadingLayout();
        switch (method) {
            case POST_ORDER_LIST_JSON:
                listview.onRefreshComplete();
                listview.onRefreshComplete();
                if (obj instanceof BookListResult){
                    BookListResult bookListResult = (BookListResult) obj;
                    if (bookListResult != null) {
                        List<OrderListBean> mList = bookListResult.getOrder_list();
                        handleData(mList);
                       // initDataNum( bookListResult);
                    }


                }
                break;

        }
    }

    @Override
    public void onError(AppApi.Action method, Object obj) {
        hideLoadingLayout();
        switch (method) {
            case POST_ORDER_LIST_JSON:
                listview.onRefreshComplete();
                default:
                    super.onError(method,obj);
                    break;
        }
    }

     private void handleData(List<OrderListBean> mList){

          if (mList != null && mList.size() > 0) {
              page_num++;
               listview.setVisibility(View.VISIBLE);
                if (isUp) {
                   listItems.clear();
                   bookAdapter.clear();
                   listview.onLoadComplete(true,false);

                 }else {
                        listview.onLoadComplete(true,false);
                 }

                    listItems.addAll(mList);
                    bookAdapter.setData(listItems);

                    if (mList!=null && mList.size()<15) {
                        listview.onLoadComplete(false,true);
                    }else {
                        listview.onLoadComplete(true,false);
                    }
                }else {

                    listview.onLoadComplete(false,true);
                }

            }

            PullToRefreshBase.OnRefreshListener onRefreshListener = new PullToRefreshBase.OnRefreshListener() {
                @Override
                public void onRefresh(PullToRefreshBase refreshView) {
                    page_num = 1;
                    isUp = true;
                    getOrderList();
                }
            };

            PullToRefreshBase.OnLastItemVisibleListener onLastItemVisibleListener = new PullToRefreshBase.OnLastItemVisibleListener() {
                @Override
                public void onLastItemVisible() {
                    isUp = false;
                    getOrderList();
                }
            };

    @Override
    protected void onDestroy() {
        // 清楚房间选择记录

        super.onDestroy();
    }


    private void getOrderList(){
        HotelBean hotelBean = mSession.getHotelBean();
        AppApi.getOrderList(mContext,hotelBean.getInvite_id(),hotelBean.getTel(),curDate,page_num+"",this);
    }

     private String getDataTime(Date date) {//可根据需要自行截取数据显示
                SimpleDateFormat format = new SimpleDateFormat("YYYY-MM-dd");
                return format.format(date);
     }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        OrderListBean orderListBean = (OrderListBean)bookAdapter.getItem(position-1);
        Intent intent;
        intent = new Intent(mContext,BookInfoActivity.class);
        intent.putExtra("type", ContactCustomerListActivity.OperationType.CUSTOMER_LIST_SELECT);
        intent.putExtra("orderListBean",orderListBean);
        startActivityForResult(intent,REQUEST_ADD_BOOK);
    }
}

