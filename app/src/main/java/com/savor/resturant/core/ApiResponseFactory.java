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

import com.common.api.utils.DesUtils;
import com.common.api.utils.LogUtils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.savor.resturant.bean.BaseProResponse;
import com.savor.resturant.bean.BottomHotelVodList;
import com.savor.resturant.bean.CategoryBottomList;
import com.savor.resturant.bean.CategoryItemVo;
import com.savor.resturant.bean.CheckApInfo;
import com.savor.resturant.bean.GameResult;
import com.savor.resturant.bean.HotelMapBean;
import com.savor.resturant.bean.LastTopList;
import com.savor.resturant.bean.LocalVideoProPesponse;
import com.savor.resturant.bean.PictureBean;
import com.savor.resturant.bean.QuerySeekResponse;
import com.savor.resturant.bean.RotateProResponse;
import com.savor.resturant.bean.SeekProResponseVo;
import com.savor.resturant.bean.SmallPlatformByGetIp;
import com.savor.resturant.bean.StartUpSettingsBean;
import com.savor.resturant.bean.TopHotelVodList;
import com.savor.resturant.bean.TvBoxInfo;
import com.savor.resturant.bean.UpgradeInfo;
import com.savor.resturant.bean.VodListBean;
import com.savor.resturant.bean.VodProResponse;
import com.savor.resturant.bean.VolProResponseVo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
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
                    || action == AppApi.Action.GET_SAMLL_PLATFORMURL_JSON){
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
            case POST_UPLOAD_SLIDESETTINGS_JSON:
                try {
                    JSONArray jsonArray = ret.getJSONArray("images");
                    result = gson.fromJson(jsonArray.toString(), new TypeToken<List<PictureBean>>() {
                    }.getType());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            case POST_IMAGE_PROJECTION_JSON:
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