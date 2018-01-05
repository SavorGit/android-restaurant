package com.savor.resturant.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bigkoo.pickerview.TimePickerView;
import com.bumptech.glide.Glide;
import com.common.api.widget.pulltorefresh.library.PullToRefreshListView;
import com.savor.resturant.R;
import com.savor.resturant.bean.ContactFormat;
import com.savor.resturant.bean.Customer;
import com.savor.resturant.bean.CustomerBean;
import com.savor.resturant.bean.CustomerLabel;
import com.savor.resturant.bean.HotelBean;
import com.savor.resturant.bean.OrderListBean;
import com.savor.resturant.core.AppApi;
import com.savor.resturant.utils.GlideCircleTransform;
import com.zhy.view.flowlayout.FlowLayout;
import com.zhy.view.flowlayout.TagAdapter;
import com.zhy.view.flowlayout.TagFlowLayout;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.savor.resturant.activity.ContactCustomerListActivity.REQUEST_CODE_SELECT;


/**
 * 客户信息
 */
public class UserInfoActivity extends BaseActivity implements View.OnClickListener
        {

    private Context context;
    private ImageView iv_left;
    private TextView tv_center;
    private ImageView iv_header;
    private ImageView sex;
    private TextView name;
    private TextView tel;
    private TextView consume_ability;
    private TextView birthday;
    private TextView birthplace;
    private TextView tv_edit_label;
    private TagFlowLayout rlv_labels;
    private TextView tv_label_hint;
    private TextView edit_label;
    private TextView remark;
    private TextView edit_label_remark;
    private TextView tv_add_ticket;
    private String customer_id;
    private CustomerBean customerBean;
    private String usernameStr;
    private String usermobileStr;
    private String usermobile1Str;
    private String sexStr;
    private String birthdayStr;
    private String birthplaceStr;
    private String face_urlStr;
    private String consume_abilityStr;
    private String remarkStr;
    private List<CustomerLabel> labelList = new ArrayList<>();;
    private TagAdapter mTagAdapter = new  TagAdapter(labelList) {
                @Override
                public View getView(FlowLayout parent, int position, Object o) {
                    TextView tv = (TextView) getLayoutInflater().inflate(R.layout.flow_item,
                            rlv_labels, false);
                    tv.setBackgroundResource(R.drawable.label_bg);
                    tv.setTextColor(mContext.getResources().getColor(R.color.color_label));
                    tv.setText(labelList.get(position).getLabel_name());
                    return tv;
                }
            };

    private static final int REQUEST_CODE_LABEL = 100;
            private PullToRefreshListView refreshListView;

            @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info_layout);
        getDate();
        context = this;
        getViews();
        setViews();
        setListeners();
        getCustomerBaseInfo();
    }

    private void getDate(){
        Intent intent = getIntent();
        if (intent != null) {
            customer_id = intent.getStringExtra("customerID");

        }
    }

    @Override
    public void getViews() {
        iv_left = (ImageView) findViewById(R.id.iv_left);
        tv_center = (TextView) findViewById(R.id.tv_center);

        refreshListView = (PullToRefreshListView) findViewById(R.id.listview);

        initHeaderView();

    }

    private void initHeaderView() {
        View headerView = View.inflate(this,R.layout.header_view_user_info,null);
        iv_header = (ImageView) headerView.findViewById(R.id.iv_header);

        name = (TextView) headerView.findViewById(R.id.name);
        consume_ability = (TextView) headerView.findViewById(R.id.consume_ability);
        birthday = (TextView) headerView.findViewById(R.id.birthday);
        birthplace = (TextView) headerView.findViewById(R.id.birthplace);
        tv_edit_label = (TextView) headerView.findViewById(R.id.tv_edit_label);
        tv_label_hint = (TextView) headerView.findViewById(R.id.tv_label_hint);
        edit_label = (TextView) headerView.findViewById(R.id.edit_label);
        remark = (TextView) headerView.findViewById(R.id.remark);
        edit_label_remark = (TextView) headerView.findViewById(R.id.edit_label_remark);
        tv_add_ticket = (TextView) headerView.findViewById(R.id.tv_add_ticket);
        sex = (ImageView) headerView.findViewById(R.id.sex);
        tel = (TextView) headerView.findViewById(R.id.tel);
        rlv_labels = (TagFlowLayout) headerView.findViewById(R.id.rlv_labels);

        refreshListView.getRefreshableView().addHeaderView(headerView);
    }

    @Override
    public void setViews() {
        tv_center.setText("详细资料");
        tv_center.setTextColor(getResources().getColor(R.color.color_f6f2ed));

    }

    @Override
    public void setListeners() {

        iv_left.setOnClickListener(this);
        tv_center.setOnClickListener(this);
        edit_label.setOnClickListener(this);


    }


    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()){
            case R.id.iv_left:
                finish();
                break;
            case R.id.edit_label:
                intent = new Intent(this,LabelAddActivity.class);
                intent.putExtra("customer_id",customer_id);
                startActivityForResult(intent,REQUEST_CODE_LABEL);
                break;


            default:
                break;
        }
    }

    @Override
    public void onSuccess(AppApi.Action method, Object obj) {
        hideLoadingLayout();
        switch (method) {
            case POST_CUSTOMER_INFO_JSON:
                if (obj instanceof CustomerBean){
                    customerBean = (CustomerBean)obj;
                    handleData();
                }
                break;

        }
    }

    @Override
    public void onError(AppApi.Action method, Object obj) {
        hideLoadingLayout();
        switch (method) {
            case POST_CUSTOMER_INFO_JSON:

                default:
                    super.onError(method,obj);
                    break;
        }
    }

     private void handleData( ){

          if (customerBean != null){
              Customer customer = customerBean.getList();
              if (customer != null){
                  usernameStr = customer.getUsername();
                  if (!TextUtils.isEmpty(usernameStr)) {
                      name.setText(usernameStr);
                  }else{
                      name.setText("");
                  }
                  
                  
                  usermobileStr = customer.getUsermobile();
                  if (!TextUtils.isEmpty(usermobileStr)) {
                      tel.setText(usermobileStr);
                  }else{
                      tel.setText("");
                  }
                 
                  sexStr = customer.getSex();
                  if (!TextUtils.isEmpty(sexStr)) {
                      sex.setVisibility(View.VISIBLE);
                      if ("男".equals(sexStr)) {
                          sex.setImageResource(R.drawable.nan);
                      }else if ("女".equals(sexStr)){
                          sex.setImageResource(R.drawable.nv);
                      }else {
                          sex.setVisibility(View.GONE);
                      }
                  }else{
                      sex.setVisibility(View.GONE);
                  }

                  birthdayStr = customer.getBirthday();
                  if (!TextUtils.isEmpty(birthdayStr)) {
                      birthday.setText(birthdayStr);
                  }else{
                      birthday.setText("");
                  }

                  birthplaceStr = customer.getBirthplace();
                  if (!TextUtils.isEmpty(birthplaceStr)) {
                      birthplace.setText(birthplaceStr);
                  }else{
                      birthplace.setText("");
                  }
                  face_urlStr = customer.getFace_url();
                  if (!TextUtils.isEmpty(face_urlStr)) {
                     // Glide.with(mContext).
                      Glide.with(context).load(face_urlStr).centerCrop().transform(new GlideCircleTransform(context)).into(iv_header);
                  }else{
                      birthplace.setText("");
                  }
                  consume_abilityStr = customer.getConsume_ability();
                  if (!TextUtils.isEmpty(birthplaceStr)) {
                      consume_ability.setText(birthplaceStr);
                  }else{
                      consume_ability.setText("");
                  }
                  remarkStr = customer.getRemark();
                  if (!TextUtils.isEmpty(remarkStr)) {
                      remark.setText(remarkStr);
                  }else{
                      remark.setText("");
                  }

                  labelList = customer.getLabel();
                  if (labelList != null && labelList.size()>0) {
                      showLabel();
                      rlv_labels.setAdapter(mTagAdapter);
                      mTagAdapter.notifyDataChanged();
                  }else {
                      hideLabel();
                  }
              }else {
                  hideLabel();
              }
          }else {


                }

            }



    @Override
    protected void onDestroy() {
        // 清楚房间选择记录

        super.onDestroy();
    }



    private void getCustomerBaseInfo(){
        HotelBean hotelBean = mSession.getHotelBean();
        AppApi.getCustomerBaseInfo(mContext,customer_id,hotelBean.getInvite_id(),hotelBean.getTel(),this);
    }



            @Override
            protected void onActivityResult(int requestCode, int resultCode, Intent data) {
                super.onActivityResult(requestCode, resultCode, data);
                if(requestCode == REQUEST_CODE_LABEL) {
                    if(data!=null) {
                        ArrayList<CustomerLabel> labelList = (ArrayList<CustomerLabel>) data.getSerializableExtra("selecteLabels");
                        if(labelList!=null&&labelList.size()>0) {
                            this.labelList.clear();
                            this.labelList.addAll(labelList);
                            mTagAdapter.notifyDataChanged();
                            showLabel();
                        }else {
                            hideLabel();
                        }
                    }
                }
            }


    public void showLabel() {
        tv_label_hint.setVisibility(View.GONE);
        rlv_labels.setVisibility(View.VISIBLE);
     }

    public void hideLabel() {
        tv_label_hint.setVisibility(View.GONE);
        rlv_labels.setVisibility(View.VISIBLE);
     }
}

