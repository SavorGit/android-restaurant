package com.savor.resturant.service;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;

import com.common.api.utils.LogUtils;
import com.google.gson.Gson;
import com.savor.resturant.bean.ContactFormat;
import com.savor.resturant.bean.OperationFailedItem;
import com.savor.resturant.core.ApiRequestListener;
import com.savor.resturant.core.AppApi;
import com.savor.resturant.core.Session;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author hezd created on 2017/12/25
 */
public class ReRequestService extends IntentService {
    /**重新发起已失败的请求操作*/
    private static final String ACTION_UPLOAD_FAILE_LIST = "com.savor.resturant.service.action.request";
    private int requestCount;
    private int successCount;
    private int failedCount;
    private int netfailedCount;

    private static final int MSG_SUCCESS = 0x1;
    private static final int MSG_FAILED = 0x2;
    private static final int MSG_RESET = 0x3;
    private static final int MSG_FAILED_NETWORK = 0x4;

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_SUCCESS:
                    successCount++;
                    if(successCount+failedCount+netfailedCount==requestCount) {
                        LogUtils.d("savor:opr 当前请求完毕 成功 "+successCount+";失败 "+failedCount+",网络请求失败："+netfailedCount);
                        mHandler.sendEmptyMessage(MSG_RESET);
                    }
                    break;
                case MSG_FAILED:
                    failedCount++;
                    if((successCount+failedCount+netfailedCount==requestCount && successCount>0)||failedCount==requestCount) {
                        LogUtils.d("savor:opr 当前请求完毕 成功 "+successCount+";失败 "+failedCount+",网络请求失败："+netfailedCount);
                        mHandler.sendEmptyMessage(MSG_RESET);
                    }
                    break;
                case MSG_FAILED_NETWORK:
                    netfailedCount++;
                    if(successCount+failedCount+netfailedCount==requestCount && successCount>0) {
                        LogUtils.d("savor:opr 当前请求完毕 成功 "+successCount+";失败 "+failedCount+",网络请求失败："+netfailedCount);
                        mHandler.sendEmptyMessage(MSG_RESET);
                    }
                    break;
                case MSG_RESET:
                    mHandler.removeMessages(MSG_SUCCESS);
                    mHandler.removeMessages(MSG_FAILED);
                    mHandler.removeMessages(MSG_RESET);
                    LogUtils.d("savor:opr 清空失败记录");
                    Session session = Session.get(ReRequestService.this);
                    session.setOpFailedList(null);
                    break;
            }
        }
    };

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
        if(opFailedList==null)
            return;
        requestCount = opFailedList.size();
        for(final OperationFailedItem item : opFailedList) {
            OperationFailedItem.OpType type = item.getType();
            switch (type) {
                case TYPE_IMPORT_NEW:
                case TYPE_IMPORT_FIRST:
                    // 导入通讯录
                    String importInfo = new Gson().toJson(item.getContactFormat());
                    String invitation = session.getHotelBean().getInvite_id();
                    String tel = session.getHotelBean().getTel();
                    AppApi.importInfoNew(this, importInfo, invitation, tel, new ApiRequestListener() {
                        @Override
                        public void onSuccess(AppApi.Action method, Object obj) {
                            LogUtils.d("savor:opr 通讯录重传导入成功\n "+item.getContactFormat());
                            mHandler.sendEmptyMessage(MSG_SUCCESS);
//                            opFailedList.remove(item);
                        }

                        @Override
                        public void onError(AppApi.Action method, Object obj) {
                            LogUtils.d("savor:opr 通讯录重传导入失败\n "+item.getContactFormat()+"\n；失败原因："+obj);
                            mHandler.sendEmptyMessage(MSG_FAILED);
                        }

                        @Override
                        public void onNetworkFailed(AppApi.Action method) {
                            LogUtils.d("savor:opr 通讯录重传导入失败\n "+item.getContactFormat()+"\n；失败原因：onNetworkFailed");
                            mHandler.sendEmptyMessage(MSG_FAILED);
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
                                        mHandler.sendEmptyMessage(MSG_SUCCESS);
//                                        opFailedList.remove(item);
//                                        session.setOpFailedList(opFailedList);
                                    }

                                    @Override
                                    public void onError(AppApi.Action method, Object obj) {
                                        LogUtils.d("savor:opr 通讯录重传添加客户失败\n "+item.getContactFormat()+"\n；失败原因："+obj);
                                        mHandler.sendEmptyMessage(MSG_FAILED);
                                    }

                                    @Override
                                    public void onNetworkFailed(AppApi.Action method) {
                                        LogUtils.d("savor:opr 通讯录重传添加客户失败\n "+item.getContactFormat()+"\n；失败原因：onNetworkFailed");
                                        mHandler.sendEmptyMessage(MSG_FAILED);
                                    }
                                });
                    }
                    break;

            }
        }
    }

}
