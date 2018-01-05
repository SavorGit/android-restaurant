package com.savor.resturant.service;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.text.TextUtils;

import com.common.api.utils.LogUtils;
import com.common.api.utils.ShowMessage;
import com.google.gson.Gson;
import com.savor.resturant.bean.ContactFormat;
import com.savor.resturant.bean.OperationFailedItem;
import com.savor.resturant.core.ApiRequestListener;
import com.savor.resturant.core.AppApi;
import com.savor.resturant.core.Session;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author hezd created on 2017/12/25
 */
public class ReRequestService extends IntentService {
    /**重新发起已失败的请求操作*/
    private static final String ACTION_UPLOAD_FAILE_LIST = "com.savor.resturant.service.action.request";

    public ReRequestService() {
        super("ReRequestService");
    }

    /**
     * 开启service
     * @see IntentService
     */
    public static void startActionRequest(Context context) {
        Intent intent = new Intent(context, ReRequestService.class);
        intent.setAction(ACTION_UPLOAD_FAILE_LIST);
        context.startService(intent);
    }


    @Override
    protected void onHandleIntent(Intent intent) {
        LogUtils.d("savor:opr onHandleIntent");
        if (intent != null) {
            final String action = intent.getAction();
            LogUtils.d("savor:opr action="+action);
            if (ACTION_UPLOAD_FAILE_LIST.equals(action)) {
                handleActionRequest();
            }
        }
    }

    /**
     * 发起请求
     */
    private void handleActionRequest() {
        final Session session = Session.get(this);
        final CopyOnWriteArrayList<OperationFailedItem> opFailedList = session.getOpFailedList();
        for(final OperationFailedItem item : opFailedList) {
            OperationFailedItem.OpType type = item.getType();
            switch (type) {
                case TYPE_IMPORT_FIRST:
                    // 导入通讯录
                    String importInfo = new Gson().toJson(item.getContactFormat());
                    String invitation = session.getHotelBean().getInvite_id();
                    String tel = session.getHotelBean().getTel();
                    AppApi.importInfoNew(this, importInfo, invitation, tel, new ApiRequestListener() {
                        @Override
                        public void onSuccess(AppApi.Action method, Object obj) {
                            LogUtils.d("savor:opr 通讯录重传导入成功\n "+item.getContactFormat());
                            opFailedList.remove(item);
                        }

                        @Override
                        public void onError(AppApi.Action method, Object obj) {
                            LogUtils.d("savor:opr 通讯录重传导入失败\n "+item.getContactFormat()+"\n；失败原因："+obj);
                        }

                        @Override
                        public void onNetworkFailed(AppApi.Action method) {
                            LogUtils.d("savor:opr 通讯录重传导入失败\n "+item.getContactFormat()+"\n；失败原因：onNetworkFailed");
                        }
                    });
                    break;
                case TYPE_ADD_CUSTOMER:
                    // 添加客户
                    List<ContactFormat> customerList = session.getCustomerList().getCustomerList();
                    if(customerList.contains(item.getContactFormat())) {
                        opFailedList.remove(item);
                    }else {
                        String invite_id = session.getHotelBean().getInvite_id();
                        String mobile = session.getHotelBean().getTel();
                        ContactFormat addCustomerBean = item.getContactFormat().get(0);
                        String usermobile = addCustomerBean.getMobile();
                        String usermobile1 = addCustomerBean.getMobile1();
                        String userm = "";
                        List<String> telList = new ArrayList<>();
                        if(!TextUtils.isEmpty(usermobile)) {
                            telList.add(usermobile);
                        }

                        if(!TextUtils.isEmpty(usermobile1)) {
                            telList.add(usermobile1);
                        }

                        if(telList.size()>0) {
                            userm = new Gson().toJson(telList);
                        }

                        AppApi.addCustomer(this, addCustomerBean.getBill_info(), addCustomerBean.getBirthday(), addCustomerBean.getBirthplace()
                                , addCustomerBean.getConsume_ability(),
                                addCustomerBean.getFace_url(), invite_id, mobile, addCustomerBean.getName(), "", "", userm, new ApiRequestListener() {
                                    @Override
                                    public void onSuccess(AppApi.Action method, Object obj) {
                                        LogUtils.d("savor:opr 通讯录重传添加客户成功\n "+item.getContactFormat());
                                        opFailedList.remove(item);
                                        session.setOpFailedList(opFailedList);
                                    }

                                    @Override
                                    public void onError(AppApi.Action method, Object obj) {
                                        LogUtils.d("savor:opr 通讯录重传添加客户失败\n "+item.getContactFormat()+"\n；失败原因："+obj);
                                    }

                                    @Override
                                    public void onNetworkFailed(AppApi.Action method) {
                                        LogUtils.d("savor:opr 通讯录重传添加客户失败\n "+item.getContactFormat()+"\n；失败原因：onNetworkFailed");
                                    }
                                });
                    }
                    break;
                case TYPE_EDIT_CUSTOMER:
                    // 编辑客户
                    break;
            }
        }
    }

}
