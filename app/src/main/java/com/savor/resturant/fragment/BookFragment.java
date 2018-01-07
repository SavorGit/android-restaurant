package com.savor.resturant.fragment;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bigkoo.pickerview.TimePickerView;
import com.common.api.utils.LogUtils;
import com.common.api.widget.pulltorefresh.library.PullToRefreshBase;
import com.common.api.widget.pulltorefresh.library.PullToRefreshListView;
import com.savor.resturant.R;
import com.savor.resturant.activity.AddBookActivity;
import com.savor.resturant.activity.AddRemarkActivity;
import com.savor.resturant.activity.BookListByDateActivity;
import com.savor.resturant.adapter.BookAdapter;
import com.savor.resturant.bean.BookListResult;
import com.savor.resturant.bean.ConAbilityList;
import com.savor.resturant.bean.CustomerLabel;
import com.savor.resturant.bean.HotelBean;
import com.savor.resturant.bean.OrderListBean;
import com.savor.resturant.core.ApiRequestListener;
import com.savor.resturant.core.AppApi;
import com.savor.resturant.core.ResponseErrorMessage;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 *@author hezd 2017
 */
public class BookFragment extends BaseFragment implements View.OnClickListener,ApiRequestListener {

    private TextView tv_center;
    private ImageView iv_right;
    private ImageView iv_left;
    private PullToRefreshListView listview;
    private BookAdapter bookAdapter;
    private View mParentLayout;
    private Context mContext;
    private RelativeLayout yesterday_la;
    private TextView yesterday;
    private TextView yesterday_month;
    private ImageView yesterday_ia;

    private RelativeLayout today_la;
    private TextView today;
    private TextView today_month;
    private ImageView ia_2;

    private RelativeLayout tomorrow_la;
    private TextView tomorrow;
    private TextView tomorrow_month;
    private ImageView tomorrow_ia;

    private RelativeLayout after_tomorrow_la;
    private TextView after_tomorrow;
    private TextView after_tomorrow_month;
    private TextView tv_add;
    private ImageView ia_4;
    private int page_num = 1;

    private String yesterdayStr = "";
    private String todayStr = "";
    private String tomorrowStr = "";
    private String after_tomorrowStr = "";
    private String currentDate = "";
    private boolean isUp = false;
    private String yesterday_order_nums;
    private String today_order_nums;
    private String tomorrow_order_nums;
    private String after_tomorrow_order_nums;
    private List<OrderListBean> listItems = new ArrayList<>();
    private static final int REQUEST_ADD_BOOK = 308;
    public BookFragment() {
        // Required empty public constructor
    }

    /**
     */
    public static BookFragment newInstance() {
        BookFragment fragment = new BookFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mParentLayout =  inflater.inflate(R.layout.fragment_book, container, false);
        mContext = getContext();
        initViews(mParentLayout);
        setViews();
        setListeners();
        getOrderList();
        return mParentLayout;
    }

    @Override
    public String getFragmentName() {
        return null;
    }

    public void initViews(View parent){
        tv_center = (TextView) parent.findViewById(R.id.tv_center);
        iv_right = (ImageView) parent.findViewById(R.id.iv_right);
        iv_left = (ImageView) parent.findViewById(R.id.iv_left);
        listview = (PullToRefreshListView) parent.findViewById(R.id.listview);

        yesterday_la = (RelativeLayout) parent.findViewById(R.id.yesterday_la);
        yesterday = (TextView) parent.findViewById(R.id.yesterday);
        yesterday_month = (TextView) parent.findViewById(R.id.yesterday_month);
        yesterday_ia = (ImageView) parent.findViewById(R.id.yesterday_ia);

        today_la = (RelativeLayout) parent.findViewById(R.id.today_la);
        today = (TextView) parent.findViewById(R.id.today);
        today_month = (TextView) parent.findViewById(R.id.today_month);
        ia_2 = (ImageView) parent.findViewById(R.id.ia_2);

        tomorrow_la = (RelativeLayout) parent.findViewById(R.id.tomorrow_la);
        tomorrow = (TextView) parent.findViewById(R.id.tomorrow);
        tomorrow_month = (TextView) parent.findViewById(R.id.tomorrow_month);
        tomorrow_ia = (ImageView) parent.findViewById(R.id.tomorrow_ia);

        after_tomorrow_la = (RelativeLayout) parent.findViewById(R.id.after_tomorrow_la);
        after_tomorrow = (TextView) parent.findViewById(R.id.after_tomorrow);
        after_tomorrow_month = (TextView) parent.findViewById(R.id.after_tomorrow_month);
        ia_4 = (ImageView) parent.findViewById(R.id.ia_4);

        tv_add = (TextView) parent.findViewById(R.id.tv_add);

    }
    @Override
    public void setViews() {
        bookAdapter = new BookAdapter(mContext);
        listview.setAdapter(bookAdapter);
        iv_left.setVisibility(View.GONE);
        iv_right.setVisibility(View.VISIBLE);
        iv_right.setBackgroundResource(R.drawable.rili);
        setTime();
    }

    @Override
    public void setListeners() {
        iv_right.setOnClickListener(this);
        yesterday_la.setOnClickListener(this);
        today_la.setOnClickListener(this);
        tomorrow_la.setOnClickListener(this);
        after_tomorrow_la.setOnClickListener(this);
        tv_add.setOnClickListener(this);
        listview.setOnRefreshListener(onRefreshListener);
        listview.setOnLastItemVisibleListener(onLastItemVisibleListener);
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()){
            case R.id.iv_right:
                showDateDialog();
                break;
            case R.id.yesterday_la:
                InitBtnData(0);
                break;
            case R.id.today_la:
                InitBtnData(1);
                break;
            case R.id.tomorrow_la:
                InitBtnData(2);
                break;
            case R.id.after_tomorrow_la:
                InitBtnData(3);
                break;
            case R.id.tv_add:
                intent = new Intent(mContext,AddBookActivity.class);
                startActivityForResult(intent,REQUEST_ADD_BOOK);
                break;

        }
    }

    private void showDateDialog() {
        TimePickerView timePickerView = new TimePickerView.Builder(mContext, new TimePickerView.OnTimeSelectListener() {
            @Override
            public void onTimeSelect(Date date, View v) {
            String time = getDataTime(date);
            Intent intent = new Intent();
            intent.putExtra("Date",time);
            intent.setClass(mContext,BookListByDateActivity.class);
            startActivity(intent);
                //setTime(date);

            }
        }).setType(new boolean[]{true, true, true, false, false, false}).isCenterLabel(false).build();
        timePickerView.show();
    }

    private String getTime(Date date) {//可根据需要自行截取数据显示
        SimpleDateFormat format = new SimpleDateFormat("MM-dd");
        return format.format(date);
    }

    private String getDataTime(Date date) {//可根据需要自行截取数据显示
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        return format.format(date);
    }
    private void setTime(){

        Date curDate = new Date(System.currentTimeMillis());//获取当前时间
        String today = getTime(curDate);
        today_month.setText(today);
        yesterday_month.setText(getTime(datePlus(curDate,-1)));
        tomorrow_month.setText(getTime(datePlus(curDate,1)));
        after_tomorrow_month.setText(getTime(datePlus(curDate,2)));

        yesterdayStr = getDataTime(datePlus(curDate,-1));
        todayStr = getDataTime(curDate);
        tomorrowStr = getDataTime(datePlus(curDate,1));
        after_tomorrowStr = getDataTime(datePlus(curDate,2));
        currentDate = todayStr;
    }

    public static Date datePlus(Date base, int days) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(base);
        cal.add(Calendar.DATE, days);
        return cal.getTime();
    }

    private void setListTime(Date date){
        String day = getTime(date);

    }
    @Override
    public void onSuccess(AppApi.Action method, Object obj) {
        super.onSuccess(method, obj);
        switch (method) {
            case POST_ORDER_LIST_JSON:
                listview.onRefreshComplete();
                if (obj instanceof BookListResult){
                    BookListResult bookListResult = (BookListResult) obj;
                    if (bookListResult != null) {
                        List<OrderListBean> mList = bookListResult.getOrder_list();
                        handleData(mList);
                        initDataNum( bookListResult);
                    }


                }

                break;

        }
    }

    @Override
    public void onError(AppApi.Action method, Object obj) {
        switch (method) {
            case POST_ORDER_LIST_JSON:
                listview.onRefreshComplete();
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
    private void getOrderList(){
        HotelBean hotelBean = mSession.getHotelBean();
        AppApi.getOrderList(mContext,hotelBean.getInvite_id(),hotelBean.getTel(),currentDate,page_num+"",this);
    }

    private void InitBtnData(int pos){
        switch (pos){
            case 0:
                initYesterday(true);
                initToday(false);
                initTomorrow(false);
                initAfterTomorrow(false);
                currentDate = yesterdayStr;
                break;
            case 1:
                initYesterday(false);
                initToday(true);
                initTomorrow(false);
                initAfterTomorrow(false);
                currentDate = todayStr;
                break;
            case 2:
                initYesterday(false);
                initToday(false);
                initTomorrow(true);
                initAfterTomorrow(false);
                currentDate = tomorrowStr;
                break;
            case 3:
                initYesterday(false);
                initToday(false);
                initTomorrow(false);
                initAfterTomorrow(true);
                currentDate = after_tomorrowStr;
                break;

        }

        listItems.clear();
        bookAdapter.setData(listItems);
        page_num = 1;
        isUp = true;
        getOrderList();
    }
    private void initYesterday(boolean flag){
        if (flag) {
            yesterday.setTextColor(mContext.getResources().getColor(R.color.app_red_color));
            yesterday_month.setTextColor(mContext.getResources().getColor(R.color.app_red_color));
            yesterday_ia.setVisibility(View.VISIBLE);
        }else{
            yesterday.setTextColor(mContext.getResources().getColor(R.color.color_text_black));
            yesterday_month.setTextColor(mContext.getResources().getColor(R.color.color_label));
            yesterday_ia.setVisibility(View.INVISIBLE);
        }
    }

    private void initToday(boolean flag){
        if (flag) {
            today.setTextColor(mContext.getResources().getColor(R.color.app_red_color));
            today_month.setTextColor(mContext.getResources().getColor(R.color.app_red_color));
            ia_2.setVisibility(View.VISIBLE);
        }else{
            today.setTextColor(mContext.getResources().getColor(R.color.color_text_black));
            today_month.setTextColor(mContext.getResources().getColor(R.color.color_label));
            ia_2.setVisibility(View.INVISIBLE);
        }
    }

    private void initTomorrow(boolean flag){
        if (flag) {
            tomorrow.setTextColor(mContext.getResources().getColor(R.color.app_red_color));
            tomorrow_month.setTextColor(mContext.getResources().getColor(R.color.app_red_color));
            tomorrow_ia.setVisibility(View.VISIBLE);
        }else{
            tomorrow.setTextColor(mContext.getResources().getColor(R.color.color_text_black));
            tomorrow_month.setTextColor(mContext.getResources().getColor(R.color.color_label));
            tomorrow_ia.setVisibility(View.INVISIBLE);
        }
    }

    private void initAfterTomorrow(boolean flag){
        if (flag) {
            after_tomorrow.setTextColor(mContext.getResources().getColor(R.color.app_red_color));
            after_tomorrow_month.setTextColor(mContext.getResources().getColor(R.color.app_red_color));
            ia_4.setVisibility(View.VISIBLE);
        }else{
            after_tomorrow.setTextColor(mContext.getResources().getColor(R.color.color_text_black));
            after_tomorrow_month.setTextColor(mContext.getResources().getColor(R.color.color_label));
            ia_4.setVisibility(View.INVISIBLE);
        }
    }

    private void initDataNum(BookListResult bookListResult){
        yesterday_order_nums = bookListResult.getYesterday_order_nums();
        today_order_nums = bookListResult.getToday_order_nums();
        tomorrow_order_nums = bookListResult.getTomorrow_order_nums();
        after_tomorrow_order_nums = bookListResult.getAfter_tomorrow_order_nums();

        if (!TextUtils.isEmpty(yesterday_order_nums)) {
            yesterday.setText("昨天("+yesterday_order_nums+")");
        }

        if (!TextUtils.isEmpty(today_order_nums)) {
            today.setText("今天("+today_order_nums+")");
        }

        if (!TextUtils.isEmpty(tomorrow_order_nums)) {
            tomorrow.setText("明天("+tomorrow_order_nums+")");
        }

        if (!TextUtils.isEmpty(after_tomorrow_order_nums)) {
            after_tomorrow.setText("后天("+after_tomorrow_order_nums+")");
        }
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == REQUEST_ADD_BOOK) {
            listItems.clear();
            bookAdapter.setData(listItems);
            page_num = 1;
            isUp = true;
            getOrderList();
//            if(data!=null) {
//                ArrayList<CustomerLabel> labelList = (ArrayList<CustomerLabel>) data.getSerializableExtra("selecteLabels");
//                if(labelList!=null&&labelList.size()>0) {
//                    this.labelList.clear();
//                    this.labelList.addAll(labelList);
//                    mTagAdapter.notifyDataChanged();
//                    showLabel();
//                }else {
//                    hideLabel();
//                }
//            }

        }
    }
}
