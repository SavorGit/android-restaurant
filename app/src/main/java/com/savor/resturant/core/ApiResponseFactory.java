package com.savor.resturant.core;

/*
 * Copyright (C) 2010 mAPPn.Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import android.content.Context;
import android.support.annotation.Nullable;

import com.common.api.utils.DesUtils;
import com.common.api.utils.LogUtils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.savor.resturant.bean.AddBookResponse;
import com.savor.resturant.bean.AddCustomerResponse;
import com.savor.resturant.bean.AddSpendTicketNoBookInfo;
import com.savor.resturant.bean.BaseProResponse;
import com.savor.resturant.bean.BookListResult;
import com.savor.resturant.bean.ConAbilityList;
import com.savor.resturant.bean.CustomerBean;
import com.savor.resturant.bean.CustomerHistory;
import com.savor.resturant.bean.CustomerLabelList;
import com.savor.resturant.bean.Hotel;
import com.savor.resturant.bean.HotelBean;
import com.savor.resturant.bean.ImportInfoResponse;
import com.savor.resturant.bean.LabelAddRessponse;
import com.savor.resturant.bean.OrderListBean;
import com.savor.resturant.bean.ProResponse;
import com.savor.resturant.bean.RecTopList;
import com.savor.resturant.bean.RecommendFoodAdvert;
import com.savor.resturant.bean.RoomInfo;
import com.savor.resturant.bean.RoomListBean;
import com.savor.resturant.bean.SlideSettingsMediaBean;
import com.savor.resturant.bean.SmallPlatformByGetIp;
import com.savor.resturant.bean.TvBoxInfo;
import com.savor.resturant.bean.UpgradeInfo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import okhttp3.Response;


import static com.savor.resturant.core.AppApi.Action.POST_UPGRADE_JSON;

/**
 * API 响应结果解析工厂类，所有的API响应结果解析需要在此完成。
 *
 * @author andrew
 * @date 2011-4-22
 */
public class ApiResponseFactory {
    public final static String TAG = "ApiResponseFactory";
    // 当前服务器时间
    private static String webtime = "";

    @Nullable
    public static Object getResponse(Context context, AppApi.Action action,
                                     Response response, String key, boolean isCache, String payType) {
        //转换器

        String requestMethod = "";
        Object result = null;
        boolean isDes = false;
        Session session = Session.get(context);
        String jsonResult = null;
        try {
            jsonResult = (String) response.body().string();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        if (jsonResult == null) {
            return null;
        }
        String header = response.header("des");
        if (header != null && Boolean.valueOf(header)) {
            isDes = true;
        }
        if (isDes) {
            jsonResult = DesUtils.decrypt(jsonResult);
        }
        LogUtils.i("action:"+action.toString()+",jsonResult:" + jsonResult);
        JSONObject rSet;
        JSONObject info = null;
        JSONArray infoArray = null;
        String infoJson = "";
        ResponseErrorMessage error;
        try {
            rSet = new JSONObject(jsonResult);
            if(action == POST_UPGRADE_JSON) {
                int code = rSet.getInt("code");
                if(AppApi.CLOUND_RESPONSE_STATE_SUCCESS!=code) {
                    String msg = rSet.getString("msg");
                    error = new ResponseErrorMessage();
                    error.setCode(code);
                    error.setMessage(msg);
                    return error;
                }
                infoJson = rSet.getString("result");
            }else if(action == AppApi.Action.GET_CALL_CODE_BY_BOXIP_JSON
                    || action == AppApi.Action.GET_CALL_QRCODE_JSON
                    ||action == AppApi.Action.POST_BOX_INFO_JSON
                    || action == AppApi.Action.GET_VERIFY_CODE_BY_BOXIP_JSON
                    || action == AppApi.Action.POST_LOGIN_JSON
                    || action == AppApi.Action.POST_VERIFY_CODE_JSON
                    || action == AppApi.Action.GET_SAMLL_PLATFORMURL_JSON
                    || action == AppApi.Action.GET_HOTEL_BOX_JSON
                    || action == AppApi.Action.GET_RECOMMEND_FOODS_JSON
                    || action == AppApi.Action.GET_ADVERT_JSON
                    || action == AppApi.Action.GET_ADVERT_PRO_JSON
                    || action == AppApi.Action.GET_RECOMMEND_PRO_JSON
                    || action == AppApi.Action.GET_WORD_PRO_JSON
                    || action == AppApi.Action.POST_REPORT_LOG_JSON
//                    || action == AppApi.Action.POST_IMPORT_INFO_JSON
                    || action == AppApi.Action.POST_IMPORT_INFO_NEW_JSON
                    || action == AppApi.Action.POST_CON_ABILITY_JSON
                    || action == AppApi.Action.POST_ADD_CUS_JSON
                    || action == AppApi.Action.POST_LABEL_List_JSON
                    || action == AppApi.Action.POST_CUSTOMER_INFO_JSON
                    || action == AppApi.Action.POST_ADD_LABEL_JSON
                    || action == AppApi.Action.POST_ADD_SIGNLE_CONSUME_RECORD_JSON
                    || action == AppApi.Action.POST_CUSTOMER_LABELS_JSON
                    || action == AppApi.Action.POST_CUS_HISTORY_JSON
                    || action == AppApi.Action.POST_CUSTOMER_EDIT_REMARK_JSON
                    || action == AppApi.Action.POST_TOP_LIST_JSON
                    || action == AppApi.Action.POST_ADD_ROOM_JSON
                    || action == AppApi.Action.POST_ADD_ORDER_JSON
                    || action == AppApi.Action.POST_ROOM_LIST_JSON
                    || action == AppApi.Action.POST_ORDER_LIST_JSON
                    || action == AppApi.Action.POST_DELETE_ORDER_JSON
                    || action == AppApi.Action.POST_UPDATE_ORDER_SERVICE_JSON
                    || action == AppApi.Action.POST_UPDATE_ORDER_JSON
                    || action == AppApi.Action.POST_EDIT_CUS_JSON
                    || action == AppApi.Action.POST_LIGHT_LABEL_JSON
                    || action == AppApi.Action.POST_ORDER_DETAIL_JSON
                    || action == AppApi.Action.POST_HOTEL_INFO_JSON
                    || action == AppApi.Action.POST_ADD_CONSUME_RECORD_JSON
                    || action == AppApi.Action.GET_WEL_RECOMMEND_JSON
                    || action == AppApi.Action.GET_STOP_BY_SMALL_JSON
                    ){
                int code = rSet.getInt("code");
                if(rSet.has("result")) {
                    infoJson = rSet.getString("result");
                }else {
                    infoJson = "";
                }
                if(AppApi.CLOUND_RESPONSE_STATE_SUCCESS != code) {
                    String msg = rSet.getString("msg");
                    error = new ResponseErrorMessage();
                    error.setCode(code);
                    error.setMessage(msg);
                    return error;
                }
            }else {
                int code = rSet.getInt("result");
                if (AppApi.TVBOX_RESPONSE_STATE_SUCCESS != code) {
                    int relt = rSet.getInt("result");
                    Gson gson = new Gson();
                    BaseProResponse prepareResponseVo = gson.fromJson(jsonResult,BaseProResponse.class);
                    error = new ResponseErrorMessage();
                    error.setCode(relt);
                    error.setMessage(prepareResponseVo.getInfo());
                    return error;
                }
            }
            result = parseResponse(action, infoJson, rSet, payType);
        } catch (Exception e) {
            LogUtils.d(requestMethod + " has other unknown Exception", e);
            e.printStackTrace();
        }finally {
            response.body().close();
        }

        return result;
    }

    public static Object parseResponse(AppApi.Action action, String info, JSONObject ret, String payType) {
        Object result = null;
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
//		LogUtils.i("info:-->" + info);
        if (info == null) {
            return result;
        }
        switch (action) {
            case TEST_POST_JSON:
                System.out.println(info);
                break;
            case TEST_GET_JSON:
                System.out.println(info);
                break;
            case POST_UPGRADE_JSON:
                result = gson.fromJson(info, new TypeToken<UpgradeInfo>() {
                }.getType());
                break;
            case POST_IMAGE_SLIDESETTINGS_JSON:
                try {
                    JSONArray jsonArray = ret.getJSONArray("images");
                    result = gson.fromJson(jsonArray.toString(), new TypeToken<List<SlideSettingsMediaBean>>() {
                    }.getType());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            case POST_VIDEO_SLIDESETTINGS_JSON:
                try {
                    JSONArray jsonArray = ret.getJSONArray("videos");
                    result = gson.fromJson(jsonArray.toString(), new TypeToken<List<SlideSettingsMediaBean>>() {
                    }.getType());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            case POST_IMAGE_PROJECTION_JSON:
                result = info;
                break;
            case POST_VIDEO_PROJECTION_JSON:
                result = info;
                break;
            case POST_NOTIFY_TVBOX_STOP_JSON:
                result = info;
                break;
            case GET_CALL_QRCODE_JSON:
                result = info;
                break;
            case GET_CALL_CODE_BY_BOXIP_JSON:
                result = info;
                break;
            case POST_BOX_INFO_JSON:
                result = gson.fromJson(info, TvBoxInfo.class);
                break;
            case GET_VERIFY_CODE_BY_BOXIP_JSON:
                result = gson.fromJson(info, TvBoxInfo.class);
                break;
            case GET_SAMLL_PLATFORMURL_JSON:
                result = gson.fromJson(info, new TypeToken<SmallPlatformByGetIp>() {
                }.getType());
                break;
            case POST_REPORT_LOG_JSON:
                result = "success";
                break;
            case POST_LOGIN_JSON:
                result = gson.fromJson(info, new TypeToken<HotelBean>() {
                }.getType());
                break;
            case POST_VERIFY_CODE_JSON:
                result = "success";
                break;
            case GET_HOTEL_BOX_JSON:
                result = gson.fromJson(info, new TypeToken<ArrayList<RoomInfo>>() {
                }.getType());
                break;
            case GET_RECOMMEND_FOODS_JSON:
                result = gson.fromJson(info, new TypeToken<List<RecommendFoodAdvert>>() {
                }.getType());
                break;
            case GET_ADVERT_JSON:
                result = gson.fromJson(info, new TypeToken<List<RecommendFoodAdvert>>() {
                }.getType());
                break;
            case GET_ADVERT_PRO_JSON:
                result = "success";
                break;
            case GET_RECOMMEND_PRO_JSON:
                result = gson.fromJson(info, new TypeToken<ProResponse>() {
                }.getType());
                break;
            case GET_WORD_PRO_JSON:
                result = "success";
                break;
            case POST_ADD_ORDER_JSON:
                result = gson.fromJson(info, new TypeToken<AddBookResponse>() {
                }.getType());
                break;
            case POST_ROOM_LIST_JSON:
                result = gson.fromJson(info, new TypeToken<List<RoomListBean>>() {
                }.getType());
                break;
            case POST_ADD_ROOM_JSON:
                result = gson.fromJson(info, new TypeToken<RoomListBean>() {
                }.getType());
                break;
            case POST_ORDER_LIST_JSON:
                result = gson.fromJson(info, new TypeToken<BookListResult>() {
                }.getType());;
                break;
            case POST_UPDATE_ORDER_JSON:
                result = "success";
                break;
            case POST_DELETE_ORDER_JSON:
                result = "success";
                break;
            case POST_UPDATE_ORDER_SERVICE_JSON:
                result = "success";
                break;
//            case POST_IMPORT_INFO_JSON:
//                result = gson.fromJson(info,new TypeToken<ImportInfoResponse>(){}.getType());
//                break;
            case POST_IMPORT_INFO_NEW_JSON:
                result = gson.fromJson(info,new TypeToken<ImportInfoResponse>(){}.getType());
                break;
            case POST_CUS_HISTORY_JSON:
                result = gson.fromJson(info, new TypeToken<CustomerHistory>() {
                }.getType());
                break;
            case POST_LIGHT_LABEL_JSON:
                result = "success";
                break;
            case POST_ADD_CUS_JSON:
                result = gson.fromJson(info, new TypeToken<AddCustomerResponse>() {
                }.getType());
                break;
            case POST_EDIT_CUS_JSON:
                result = "success";
                break;
            case POST_CON_ABILITY_JSON:
                result = gson.fromJson(info, new TypeToken<ConAbilityList>() {
                }.getType());
                break;
            case POST_CUSTOMER_INFO_JSON:
                result = gson.fromJson(info, new TypeToken<CustomerBean>() {
                }.getType());
                break;
            case POST_ADD_CONSUME_RECORD_JSON:
                result = "success";
                break;
            case POST_ADD_SIGNLE_CONSUME_RECORD_JSON:
                result = gson.fromJson(info, new TypeToken<AddSpendTicketNoBookInfo>() {
                }.getType());
                break;
            case POST_TOP_LIST_JSON:
                result = gson.fromJson(info, new TypeToken<RecTopList>() {
                }.getType());
                break;
            case POST_LABEL_List_JSON:
                result = gson.fromJson(info, new TypeToken<CustomerLabelList>() {
                }.getType());
                break;
            case POST_ADD_LABEL_JSON:
                result = gson.fromJson(info, new TypeToken<LabelAddRessponse>() {
                }.getType());
                break;
            case POST_CUSTOMER_LABELS_JSON:
                result = gson.fromJson(info, new TypeToken<CustomerLabelList>() {
                }.getType());
                break;
            case POST_CUSTOMER_EDIT_REMARK_JSON:
                result = "success";
                break;
            case POST_ORDER_DETAIL_JSON:
                result = gson.fromJson(info, new TypeToken<OrderListBean>() {
                }.getType());
                break;
            case POST_HOTEL_INFO_JSON:
                result = gson.fromJson(info, new TypeToken<Hotel>() {
                }.getType());
                break;
            case GET_WEL_RECOMMEND_JSON:
                result = gson.fromJson(info, new TypeToken<ProResponse>() {
                }.getType());
                break;
            case GET_STOP_BY_SMALL_JSON:
                result = info;
                break;
            default:
                break;
        }
        return result;
    }

    private static Object changeToPayBean(String info, Gson gson, String payType) {
        Object result = null;
//		try {
//			JSONObject jsonResult = new JSONObject(info);
//			if (ConstantValues.ALIPAY_PLUGIN_TYPE.equals(jsonResult.getString("payType"))) {
//				AlipayPluginEntity pluginEntity = gson.fromJson(info, new TypeToken<AlipayPluginEntity>(){}.getType());
//				result = pluginEntity;
//			}else if (ConstantValues.WEIXIN_PAY_TYPE.equals(jsonResult.getString("payType"))) {
//				JSONObject json=null;
//				try {
//					json = new JSONObject(URLDecoder.decode(jsonResult.getString("json"), "utf-8"));
//				} catch (UnsupportedEncodingException e) {
//					e.printStackTrace();
//				}
//				//微信支付
//				WeiXinPayEntity weiXinPayEntity = new WeiXinPayEntity();
//				weiXinPayEntity = gson.fromJson(jsonResult.toString(), new TypeToken<WeiXinPayEntity>(){}.getType());
//				weiXinPayEntity.setPayType(payType);
//				PayReq payReq = new PayReq();
//				payReq.appId = json.getString("appid");
//				payReq.partnerId = json.getString("partnerid");
//				payReq.prepayId = json.getString("prepayid");
//				payReq.packageValue = json.getString("package");
//				payReq.nonceStr = json.getString("noncestr");
//				payReq.timeStamp = json.getString("timestamp");
//				payReq.sign = json.getString("sign");
//				weiXinPayEntity.setPayReq(payReq);
//				result = weiXinPayEntity;
//			}else if (ConstantValues.ALIPAY_WAP_TYPE.equals(jsonResult.getString("payType"))) {
//				try {
//					
//					PayWapEntity entity = new PayWapEntity();
//					entity = gson.fromJson(jsonResult.toString(), new TypeToken<PayWapEntity>(){}.getType());
//					String payUrl = URLDecoder.decode(jsonResult.getString("json"), "utf-8");
//					entity.setUrl(payUrl);
//					result = entity;
//					
//				} catch (UnsupportedEncodingException e) {
//					e.printStackTrace();
//				}
//			}
//			
//		} catch (JSONException e) {
//			e.printStackTrace();
//		}
        return result;
    }
}