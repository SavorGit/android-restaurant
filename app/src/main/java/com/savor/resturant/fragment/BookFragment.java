package com.savor.resturant.fragment;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.common.api.widget.pulltorefresh.library.PullToRefreshListView;
import com.savor.resturant.R;
import com.savor.resturant.adapter.BookAdapter;

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
    }
    @Override
    public void setViews() {
        bookAdapter = new BookAdapter(mContext);
        listview.setAdapter(bookAdapter);
    }

    @Override
    public void setListeners() {

    }

    @Override
    public void onClick(View v) {

    }
}
