package com.savor.resturant.fragment;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bigkoo.pickerview.TimePickerView;
import com.common.api.widget.pulltorefresh.library.PullToRefreshListView;
import com.savor.resturant.R;
import com.savor.resturant.adapter.BookAdapter;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 *@author hezd 2017
 */
public class BookFragment extends BaseFragment implements View.OnClickListener{

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
    private ImageView ia_4;

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
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.iv_right:
                showDateDialog();
                break;
            case R.id.yesterday_la:
                break;
            case R.id.today_la:
                break;
            case R.id.tomorrow_la:
                break;
            case R.id.after_tomorrow_la:
                break;

        }
    }

    private void showDateDialog() {
        TimePickerView timePickerView = new TimePickerView.Builder(mContext, new TimePickerView.OnTimeSelectListener() {
            @Override
            public void onTimeSelect(Date date, View v) {

                //setTime(date);

            }
        }).setType(new boolean[]{true, true, true, false, false, false}).isCenterLabel(false).build();
        timePickerView.show();
    }

    private String getTime(Date date) {//可根据需要自行截取数据显示
        SimpleDateFormat format = new SimpleDateFormat("MM-dd");
        return format.format(date);
    }

    private void setTime(){

        Date curDate = new Date(System.currentTimeMillis());//获取当前时间
        String today = getTime(curDate);
        today_month.setText(today);
        yesterday_month.setText(getTime(datePlus(curDate,-1)));
        tomorrow_month.setText(getTime(datePlus(curDate,1)));
        after_tomorrow_month.setText(getTime(datePlus(curDate,2)));
    }

    public static Date datePlus(Date base, int days) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(base);
        cal.add(Calendar.DATE, days);
        return cal.getTime();
    }
}
