package com.savor.resturant.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.savor.resturant.R;

public class SpendHistoryAddActivity extends BaseActivity {

    private RecyclerView mLabelsRlv;
    private TextView mLabelHint;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spend_history_add);

        getViews();
        setViews();
        setListeners();
    }

    @Override
    public void getViews() {
        mLabelsRlv = (RecyclerView) findViewById(R.id.rlv_labels);
        mLabelHint = (TextView) findViewById(R.id.tv_label_hint);

    }

    @Override
    public void setViews() {

    }

    @Override
    public void setListeners() {

    }

    public void showLabel() {
        mLabelHint.setVisibility(View.GONE);
        mLabelsRlv.setVisibility(View.VISIBLE);
    }

    public void hideLabel() {
        mLabelsRlv.setVisibility(View.GONE);
        mLabelHint.setVisibility(View.VISIBLE);
    }
}
