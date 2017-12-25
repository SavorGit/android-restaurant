package com.savor.resturant.service;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;

import com.common.api.utils.LogUtils;
import com.google.gson.Gson;
import com.savor.resturant.bean.OperationFailedItem;
import com.savor.resturant.core.ApiRequestListener;
import com.savor.resturant.core.AppApi;
import com.savor.resturant.core.Session;

import java.util.List;

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
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_UPLOAD_FAILE_LIST.equals(action)) {
                handleActionRequest();
            }
        }
    }

    /**
     * 发起请求
     */
    private void handleActionRequest() {
        Session session = Session.get(this);
        final List<OperationFailedItem> opFailedList = session.getOpFailedList();
        for(final OperationFailedItem item : opFailedList) {
            OperationFailedItem.OpType type = item.getType();
            switch (type) {
                case TYPE_IMPORT:
                    // 导入通讯录
                    String importInfo = new Gson().toJson(item.getContactFormat());
                    String invitation = session.getHotelBean().getInvite_id();
                    String tel = session.getHotelBean().getTel();
                    AppApi.importInfo(this, importInfo, invitation, tel, new ApiRequestListener() {
                        @Override
                        public void onSuccess(AppApi.Action method, Object obj) {
                            LogUtils.d("savor:opr 通讯录重传成功\n "+item.getContactFormat());
                            opFailedList.remove(item);
                        }

                        @Override
                        public void onError(AppApi.Action method, Object obj) {

                        }

                        @Override
                        public void onNetworkFailed(AppApi.Action method) {

                        }
                    });
                    break;
                case TYPE_ADD_CUSTOMER:
                    // 添加客户
                    break;
                case TYPE_EDIT_CUSTOMER:
                    // 编辑客户
                    break;
            }
        }
    }

}
