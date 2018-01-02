package com.savor.resturant.core;

import android.content.Context;
import android.os.Build;
import android.text.TextUtils;

import com.common.api.utils.AppUtils;
import com.savor.resturant.bean.SmallPlatInfoBySSDP;
import com.savor.resturant.bean.SmallPlatformByGetIp;
import com.savor.resturant.bean.TvBoxSSDPInfo;
import com.savor.resturant.utils.STIDUtil;
import com.savor.resturant.utils.SlideManager;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class AppApi {
    public static final String APK_DOWNLOAD_FILENAME = "NewApp.apk";

    /**云平台php接口*/
  public static final String CLOUND_PLATFORM_PHP_URL = "http://devp.mobile.littlehotspot.com/";
// public static final String CLOUND_PLATFORM_PHP_URL = "http://mobile.littlehotspot.com/";

    /**
     * 常用的一些key值 ,签名、时间戳、token、params
     */
    public static final String SIGN = "sign";
    public static final String TIME = "time";
    public static final String TOKEN = "token";
    public static final String PARAMS = "params";

    public static String LocalUrl;
    public static String LocalIp;
    /**这是一个临时值，以请求时传入的值为准*/
    public static String tvBoxUrl;
    /**这是一个临时值，以请求时传入的值为准*/
    public static String smallPlatformUrl;
    public static int hotelid;
    public static int roomid;

    /**
     * Action-自定义行为 注意：自定义后缀必须为以下结束 _FORM:该请求是Form表单请求方式 _JSON:该请求是Json字符串
     * _XML:该请求是XML请求描述文件 _GOODS_DESCRIPTION:图文详情 __NOSIGN:参数不需要进行加密
     */
    public static enum Action {
        TEST_POST_JSON,
        TEST_GET_JSON,
        TEST_DOWNLOAD_JSON,

        /**升级*/
        POST_UPGRADE_JSON,
        /**上传幻灯片配置*/
        POST_IMAGE_SLIDESETTINGS_JSON,
        /**上传视频幻灯片设置*/
        POST_VIDEO_SLIDESETTINGS_JSON,
        /**图片投屏*/
        POST_IMAGE_PROJECTION_JSON,
        /**视频投屏*/
        POST_VIDEO_PROJECTION_JSON,
        /**机顶盒退出投屏*/
        POST_NOTIFY_TVBOX_STOP_JSON,
        /**获取小平台地址*/
        GET_SAMLL_PLATFORMURL_JSON,
        /**小平台呼码*/
        GET_CALL_QRCODE_JSON,
        /**点对点呼码*/
        GET_CALL_CODE_BY_BOXIP_JSON,
        /**获取机顶盒信息通过数字*/
        POST_BOX_INFO_JSON,
        /**点对点校验三位数字*/
        GET_VERIFY_CODE_BY_BOXIP_JSON,
        /**日志上报*/
        POST_REPORT_LOG_JSON,
        /**用户登录*/
        POST_LOGIN_JSON,
        /**获取手机验证码*/
        POST_VERIFY_CODE_JSON,
        /**获取酒楼包间信息*/
        GET_HOTEL_BOX_JSON,
        /**获取推荐菜*/
        GET_RECOMMEND_FOODS_JSON,
        /**获取宣传片*/
        GET_ADVERT_JSON,
        /**宣传片投屏*/
        GET_ADVERT_PRO_JSON,
        /**推荐菜投屏*/
        GET_RECOMMEND_PRO_JSON,
        /**欢迎词投屏*/
        GET_WORD_PRO_JSON,
        /**新增预订*/
        POST_ADD_ORDER_JSON,
        /**获取酒楼包间列表*/
        POST_ROOM_LIST_JSON,
        /**添加包间*/
        POST_ADD_ROOM_JSON,
        /**预订列表*/
        POST_ORDER_LIST_JSON,
        /**修改预订信息*/
        POST_UPDATE_ORDER_JSON,
        /**删除预订信息*/
        POST_DELETE_ORDER_JSON,
        /**更新预订信息服务*/
        POST_UPDATE_ORDER_SERVICE_JSON,
        /**点亮熄灭标签*/
        POST_LIGHT_LABEL_JSON,
        /**获取客户最近历史*/
        POST_CUS_HISTORY_JSON,
        /**添加客户*/
        POST_ADD_CUS_JSON,
        /**导入通讯录*/
        POST_IMPORT_INFO_JSON,
        /**修改客户*/
        POST_EDIT_CUS_JSON,
        /**消费能力列表*/
        POST_CON_ABILITY_JSON,
        /**导入通讯录非首次*/
        POST_IMPORT_INFO_NEW_JSON,
        /**获取客户基本信息*/
        POST_CUSTOMER_INFO_JSON,
        /**添加带预订信息截图消费记录*/
        POST_ADD_CONSUME_RECORD_JSON,
        /**添加无预定信息消费记录*/
        POST_ADD_SIGNLE_CONSUME_RECORD_JSON,
        /**上下拉消费记录*/
        POST_TOP_LIST_JSON,
        /**获取标签列表(全量包含点亮和未点亮)*/
        POST_LABEL_List_JSON,
        /**添加标签*/
        POST_ADD_LABEL_JSON,
        /**获取客户标签(点亮的)*/
        POST_CUSTOMER_LABELS_JSON,
    }

    /**
     * API_URLS:(URL集合)
     */
    public static final HashMap<Action, String> API_URLS = new HashMap<Action, String>() {
        private static final long serialVersionUID = -8469661978245513712L;

        {
            put(Action.TEST_GET_JSON, "https://www.baidu.com/");

            //升级
            put(Action.POST_UPGRADE_JSON, formatPhpUrl("version/HotelUpgrade/index"));
            put(Action.POST_IMAGE_SLIDESETTINGS_JSON,tvBoxUrl);
            put(Action.POST_VIDEO_SLIDESETTINGS_JSON,tvBoxUrl);
            put(Action.POST_IMAGE_PROJECTION_JSON,tvBoxUrl);
            put(Action.POST_VIDEO_PROJECTION_JSON,tvBoxUrl);
            put(Action.POST_NOTIFY_TVBOX_STOP_JSON,tvBoxUrl);
            put(Action.GET_SAMLL_PLATFORMURL_JSON, formatPhpUrl("basedata/Ip/getIp"));
            put(Action.GET_CALL_QRCODE_JSON,tvBoxUrl);
            put(Action.GET_CALL_CODE_BY_BOXIP_JSON,tvBoxUrl);
            put(Action.POST_BOX_INFO_JSON,tvBoxUrl);
            put(Action.GET_VERIFY_CODE_BY_BOXIP_JSON,tvBoxUrl);
            put(Action.POST_REPORT_LOG_JSON, formatPhpUrl("Dinnerapp/Touping/reportLog"));
            put(Action.POST_LOGIN_JSON, formatPhpUrl("Dinnerapp2/Login/dologin"));
            put(Action.POST_VERIFY_CODE_JSON, formatPhpUrl("Dinnerapp/sms/getverifyCode"));
            put(Action.GET_HOTEL_BOX_JSON,smallPlatformUrl);
            put(Action.GET_RECOMMEND_FOODS_JSON, formatPhpUrl("Dinnerapp/Recfood/getHotelRecFoods"));
            put(Action.GET_ADVERT_JSON, formatPhpUrl("Dinnerapp/Adv/getAdvList"));
            put(Action.GET_ADVERT_PRO_JSON,smallPlatformUrl);
            put(Action.GET_RECOMMEND_PRO_JSON,smallPlatformUrl);
            put(Action.GET_WORD_PRO_JSON,smallPlatformUrl);
            put(Action.POST_ADD_ORDER_JSON, formatPhpUrl("Dinnerapp2/Order/addOrder"));
            put(Action.POST_ROOM_LIST_JSON, formatPhpUrl("Dinnerapp2/Room/getList"));
            put(Action.POST_ADD_ROOM_JSON, formatPhpUrl("Dinnerapp2/Room/addRoom"));
            put(Action.POST_ORDER_LIST_JSON, formatPhpUrl("Dinnerapp2/Order/getOrderList"));
            put(Action.POST_UPDATE_ORDER_JSON, formatPhpUrl("Dinnerapp2/Order/updateOrder"));
            put(Action.POST_DELETE_ORDER_JSON, formatPhpUrl("Dinnerapp2/Order/deleteOrder"));
            put(Action.POST_UPDATE_ORDER_SERVICE_JSON, formatPhpUrl("Dinnerapp2/Order/upateOrderService"));
            put(Action.POST_IMPORT_INFO_JSON, formatPhpUrl("Dinnerapp2/Customer/importInfoFirst"));
            put(Action.POST_LIGHT_LABEL_JSON, formatPhpUrl("Dinnerapp2/Label/lightLabel"));
            put(Action.POST_CUS_HISTORY_JSON, formatPhpUrl("Dinnerapp2/Customer/getCustomerHistory"));
            put(Action.POST_ADD_CUS_JSON, formatPhpUrl("Dinnerapp2/Customer/addCustomer"));
            put(Action.POST_EDIT_CUS_JSON, formatPhpUrl("Dinnerapp2/Customer/editCustomer"));
            put(Action.POST_CON_ABILITY_JSON, formatPhpUrl("Dinnerapp2/Customer/getConAbility"));
            put(Action.POST_IMPORT_INFO_NEW_JSON, formatPhpUrl("Dinnerapp2/Customer/importNewInfo"));
            put(Action.POST_CUSTOMER_INFO_JSON, formatPhpUrl("Dinnerapp2/Customer/getCustomerBaseInfo"));
            put(Action.POST_ADD_CONSUME_RECORD_JSON, formatPhpUrl("Dinnerapp2/Customer/addConsumeRecord"));
            put(Action.POST_ADD_SIGNLE_CONSUME_RECORD_JSON, formatPhpUrl("Dinnerapp2/Customer/addSignleConsumeRecord"));
            put(Action.POST_TOP_LIST_JSON, formatPhpUrl("Dinnerapp2/Customer/getConRecTopList"));
            put(Action.POST_LABEL_List_JSON, formatPhpUrl("Dinnerapp2/Label/getCustomerLable"));
            put(Action.POST_ADD_LABEL_JSON, formatPhpUrl("Dinnerapp2/Label/addLabel"));
            put(Action.POST_CUSTOMER_LABELS_JSON, formatPhpUrl("Dinnerapp2/Customer/getOnlyLabel"));
        }
    };



    /**
     * php后台接口
     * @param url
     * @return
     */
    private static String formatPhpUrl(String url) {
        return CLOUND_PLATFORM_PHP_URL +url;
    }

    public static void testPost(Context context, String orderNo, ApiRequestListener handler) {
        final HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("loginfield", "15901559579");
        params.put("password", "123456");
        params.put("dr_rg_cd", "86");
        params.put("version_code", 19 + "");
        new AppServiceOk(context, Action.TEST_POST_JSON, handler, params).post(false, false, true, true);

    }

    public static void testGet(Context context, ApiRequestListener handler) {
        SmallPlatInfoBySSDP smallPlatInfoBySSDP = Session.get(context).getSmallPlatInfoBySSDP();
        API_URLS.put(Action.TEST_GET_JSON,"http://"+ smallPlatInfoBySSDP.getServerIp()+":"+ smallPlatInfoBySSDP.getCommandPort()+"/small-platform-1.0.0.0.1-SNAPSHOT/com/execute/call-tdc");
        final HashMap<String, Object> params = new HashMap<String, Object>();
        new AppServiceOk(context, Action.TEST_GET_JSON, handler, params).get();

    }

    public static void testDownload(Context context, String url, ApiRequestListener handler) {
        final HashMap<String, Object> params = new HashMap<String, Object>();
        String target = AppUtils.getPath(context, AppUtils.StorageFile.file);

        String targetApk = target + "123.apk";
        File tarFile = new File(targetApk);
        if (tarFile.exists()) {
            tarFile.delete();
        }
        new AppServiceOk(context, Action.TEST_DOWNLOAD_JSON, handler, params).downLoad(url, targetApk);
    }

    public static void downApp(Context context, String url, ApiRequestListener handler) {
        final HashMap<String, Object> params = new HashMap<String, Object>();
        String target = AppUtils.getPath(context, AppUtils.StorageFile.file);

        String targetApk = target + APK_DOWNLOAD_FILENAME;
        File tarFile = new File(targetApk);
        if (tarFile.exists()) {
            tarFile.delete();
        }
        new AppServiceOk(context, Action.TEST_DOWNLOAD_JSON, handler, params).downLoad(url, targetApk);

    }

    /**
     * 上传图片幻灯片配置信息
     * @param context
     * @param url
     * @param params
     * @param handler
     */
    public static void postImageSlideSettingToServer(Context context, String url, HashMap<String, Object> params, int force, ApiRequestListener handler){
        HashMap<String, String> p = new HashMap<>();
        p.put("deviceName",Build.MODEL);
        p.put("force",force+"");
        new AppServiceOk(context, formatImageProUrl(context,url+"/restaurant/ppt?",p),Action.POST_IMAGE_SLIDESETTINGS_JSON, handler, params).post();
    }

    /**
     * 上传视频幻灯片配置信息
     * @param context
     * @param url
     * @param params
     * @param handler
     */
    public static void postVideoSlideSettingToServer(Context context, String url, HashMap<String, Object> params, int force, ApiRequestListener handler){
        HashMap<String, String> p = new HashMap<>();
        p.put("deviceName",Build.MODEL);
        p.put("force",force+"");
        new AppServiceOk(context, formatImageProUrl(context,url+"/restaurant/v-ppt?",p),Action.POST_VIDEO_SLIDESETTINGS_JSON, handler, params).post();
    }

    /**
     * 请求机顶盒投屏通过上传图片的方式
     * @param context
     * @param url
     * @param filePath
     * @param handler
     */
    public static void updateImageFile(Context context, String url, String filePath, HashMap<String,Object> params, ApiRequestListener handler) {
        new AppServiceOk(context, formatImageProUrl(context,url+"/restaurant/picUpload?",
                new HashMap<String, String>()), Action.POST_IMAGE_PROJECTION_JSON,
                handler, params).uploadFile(filePath,true,true, SlideManager.SlideType.IMAGE);

    }

    /**
     * 请求机顶盒投屏通过上传图片的方式
     * @param context
     * @param url
     * @param filePath
     * @param handler
     */
    public static void updateVideoFile(Context context, String url, String filePath, HashMap<String,Object> params,ApiRequestListener handler) {
        new AppServiceOk(context, formatImageProUrl(context,url+"/restaurant/vidUpload?",
                new HashMap<String, String>()), Action.POST_VIDEO_PROJECTION_JSON,
                handler, params).uploadFile(filePath,true,true, SlideManager.SlideType.VIDEO);

    }

    /**通过sessionid获取当前机顶盒播放进度*/
    public static void getSeekBySessionId(Context context, String url,String projectId,ApiRequestListener handler) {
        if(TextUtils.isEmpty(url))
            return;
        final HashMap<String, String> params = new HashMap<>();
        params.put("projectId", projectId);
        final HashMap<String, String> bodyParams = new HashMap<>();
        if(!TextUtils.isEmpty(url)) {
//            new AppServiceOk(context,formatProUrl(context,url+"/query?",params),Action.GET_QUERY_SEEK_JSON,handler,bodyParams).get();
        }
    }

    /**通知机顶盒停止播放*/
    public static void notifyTvBoxStop(Context context, String url,ApiRequestListener handler) {
        final HashMap<String, String> params = new HashMap<>();
        final HashMap<String, String> realParams = new HashMap<>();
        if(!TextUtils.isEmpty(url)) {
            new AppServiceOk(context,formatProUrl(context,url+"/restaurant/stop?",params),Action.POST_NOTIFY_TVBOX_STOP_JSON,handler,realParams).post();
        }
    }

    /**升级*/
    public static void Upgrade(Context context,ApiRequestListener handler,int versionCode) {
        final HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("versioncode", versionCode+"");
        params.put("clientname", "android");
        new AppServiceOk(context,Action.POST_UPGRADE_JSON,handler,params).post();
    }

    /**
     * 格式化请求url添加deviceId
     * @param url
     * @param mParameter
     * @return
     */
    private static String formatProUrl(Context context,String url, HashMap<String, String> mParameter) {
        StringBuilder sb = new StringBuilder();
        url+="deviceId="+STIDUtil.getDeviceId(context);
        sb.append(url);
        if(mParameter!=null&&mParameter.size()>0) {
            Set<Map.Entry<String, String>> entries = mParameter.entrySet();
            for(Map.Entry<String, String> entry :entries) {
                String key = entry.getKey();
                String value = entry.getValue();
                sb.append("&"+key+"="+value);
            }
        }
        return sb.toString();
    }

    /**
     * 格式化请求url添加deviceId
     * @param url
     * @param mParameter
     * @return
     */
    private static String formatImageProUrl(Context context,String url, HashMap<String, String> mParameter) {
        StringBuilder sb = new StringBuilder();
        url+="deviceId="+STIDUtil.getDeviceId(context)+"&deviceName="+Build.MODEL;
        sb.append(url);
        if(mParameter!=null&&mParameter.size()>0) {
            Set<Map.Entry<String, String>> entries = mParameter.entrySet();
            for(Map.Entry<String, String> entry :entries) {
                String key = entry.getKey();
                String value = entry.getValue();
                sb.append("&"+key+"="+value);
            }
        }
        return sb.toString();
    }

    /**获取小平台地址*/
    public static void getSmallPlatformIp(Context context, ApiRequestListener handler) {
        final HashMap<String, Object> params = new HashMap<String, Object>();
        new AppServiceOk(context,Action.GET_SAMLL_PLATFORMURL_JSON,handler,params).get();
    }

    /**
     * 通过小平台ssdp提供的小平台地址进行呼玛
     * @param context
     * @param handler
     */
    public static void callQrcodeBySPSSDP(Context context, ApiRequestListener handler) {
        final HashMap<String, Object> params = new HashMap<String, Object>();
        Session session = Session.get(context);
        SmallPlatInfoBySSDP smallPlatInfoBySSDP = session.getSmallPlatInfoBySSDP();
        if(smallPlatInfoBySSDP !=null) {
            String url = getSPlatUrl(smallPlatInfoBySSDP)+"call-tdc";
            new AppServiceOk(context,url,Action.GET_CALL_QRCODE_JSON,handler,params).get();
        }
    }

    /**
     * 通过云平台获取的小平台地址进行呼码
     * @param context
     * @param handler
     */
    public static void callQrcodeByClound(Context context, ApiRequestListener handler) {
        final HashMap<String, Object> params = new HashMap<String, Object>();
        Session session = Session.get(context);
        SmallPlatformByGetIp smallPlatformByGetIp = session.getmSmallPlatInfoByIp();
        if(smallPlatformByGetIp!=null) {
            String localIp = smallPlatformByGetIp.getLocalIp();
            String type = smallPlatformByGetIp.getType();
            String command_port = smallPlatformByGetIp.getCommand_port();
            if(!TextUtils.isEmpty(localIp)
                    &&!TextUtils.isEmpty(type)
                    &&!TextUtils.isEmpty(command_port)) {
                String url = "http://"+localIp+":"+command_port+"/"+type.toLowerCase()+"/command/execute/call-tdc";
                new AppServiceOk(context,url,Action.GET_CALL_QRCODE_JSON,handler,params).get();
            }
        }
    }

    /**
     * 通过盒子ssdp获取的小平台地址进行呼码
     * @param context
     * @param info
     * @param handler
     */
    public static void callQrcodeFromBoxInfo(Context context, TvBoxSSDPInfo info,ApiRequestListener handler) {
        final HashMap<String, Object> params = new HashMap<String, Object>();
        String serverIp = info.getServerIp();
        String commandPort = info.getCommandPort();
        String type = "small";
        String url = "http://"+serverIp+":"+commandPort+"/"+type.toLowerCase()+"/command/execute/call-tdc";
        new AppServiceOk(context,url,Action.GET_CALL_QRCODE_JSON,handler,params).get();
    }

    public static void callCodeByBoxIp(Context context, String boxUrl,ApiRequestListener handler) {
        final HashMap<String, String> params = new HashMap<>();
        String url = formatProUrl(context,boxUrl + "/showCode?",params);
        final HashMap<String, String> bodyPramas = new HashMap<>();
        new AppServiceOk(context,url,Action.GET_CALL_CODE_BY_BOXIP_JSON,handler,bodyPramas).get();
    }

    /**
     * 通过机顶盒ssdp返回的小平台地址进行校验
     */
    public static void verifyNumByBoxSSDP(Context context, String number, ApiRequestListener handler) {
        final HashMap<String, Object> params = new HashMap<String, Object>();
        Session session = Session.get(context);
        TvBoxSSDPInfo tvBoxSSDPInfo = session.getTvBoxSSDPInfo();
        if(tvBoxSSDPInfo!=null) {
            String serverIp = tvBoxSSDPInfo.getServerIp();
            String commandPort = tvBoxSSDPInfo.getCommandPort();
            String type = "small";
            String url = "http://"+serverIp+":"+commandPort+"/"+type.toLowerCase()+"/command/box-info/"+number;
            new AppServiceOk(context,url,Action.POST_BOX_INFO_JSON,handler,params).get();
        }
    }

    /**
     * 通过云平台返回的小平台地址进行校验
     */
    public static void verifyNumByClound(Context context, String number, ApiRequestListener handler) {
        final HashMap<String, Object> params = new HashMap<String, Object>();
        Session session = Session.get(context);
        SmallPlatformByGetIp smallPlatformByGetIp = session.getmSmallPlatInfoByIp();
        if(smallPlatformByGetIp !=null) {
            String type = smallPlatformByGetIp.getType();
            String serverIp = smallPlatformByGetIp.getLocalIp();
            String commandPort = smallPlatformByGetIp.getCommand_port();
            String url = "http://"+serverIp+":"+commandPort+"/"+type.toLowerCase()+"/command/box-info/"+number;
            new AppServiceOk(context,url,Action.POST_BOX_INFO_JSON,handler,params).get();
        }
    }

    public static void verifyNumByBoxIp(Context context, String boxUrl, String number, ApiRequestListener handler) {
        final HashMap<String, String> params = new HashMap<>();
        params.put("code",number);
        String url = formatProUrl(context,boxUrl + "/verify?",params);
        final HashMap<String, String> bodyPramas = new HashMap<>();
        if(!TextUtils.isEmpty(url)) {
            new AppServiceOk(context,url,Action.GET_VERIFY_CODE_BY_BOXIP_JSON,handler,bodyPramas).get();
        }
    }

    /**
     * 通过数字获取机顶盒信息
     */
    public static void verifyNumBySpSSDP(Context context, String number, ApiRequestListener handler) {
        final HashMap<String, Object> params = new HashMap<String, Object>();
        Session session = Session.get(context);
        SmallPlatInfoBySSDP smallPlatInfoBySSDP = session.getSmallPlatInfoBySSDP();
        if(smallPlatInfoBySSDP !=null) {
            String type = smallPlatInfoBySSDP.getType();
            String serverIp = smallPlatInfoBySSDP.getServerIp();
            String commandPort = smallPlatInfoBySSDP.getCommandPort();
            String url = "http://"+serverIp+":"+commandPort+"/"+type.toLowerCase()+"/command/box-info/"+number;
//            String url = "http://192.168.2.154:"+commandPort+"/small/command/box-info/"+number;
            new AppServiceOk(context,url,Action.POST_BOX_INFO_JSON,handler,params).get();
        }
    }

    public static String getSPlatUrl(SmallPlatInfoBySSDP info) {
        String type = info.getType();
        String serverIp = info.getServerIp();
        String commandPort = info.getCommandPort();
        return "http://"+serverIp+":"+commandPort+"/"+type.toLowerCase()+"/command/execute/";
    }

    /**日志上传*/
    public static void reportLog(Context context,String hotel_id,
                                 String info,
                                 String invite_code,
                                 String mobile,
                                 String room_id,
                                 String screen_num,
                                 String screen_result,
                                 String screen_time,
                                 String screen_type,
                                 String welcome_template,
                                 String welcome_word,
                                ApiRequestListener handler) {
        final HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("hotel_id", hotel_id);
        params.put("info", info);
        params.put("invite_code", invite_code);
        params.put("mobile", mobile);
        params.put("room_id", room_id);
        params.put("screen_num", screen_num);
        params.put("screen_result", screen_result);
        params.put("screen_time", screen_time);
        params.put("screen_type", screen_type);
        params.put("welcome_template", welcome_template);
        params.put("welcome_word", welcome_word);
        new AppServiceOk(context,Action.POST_REPORT_LOG_JSON,handler,params).post();
    }


    /**用户登录*/
    public static void doLogin(Context context,String invite_code, String mobile, String verify_code, ApiRequestListener handler) {
        final HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("invite_code", invite_code);
        params.put("mobile", mobile);
        params.put("verify_code", verify_code);
        new AppServiceOk(context,Action.POST_LOGIN_JSON,handler,params).post();
    }

    /**获取酒楼包间列表*/
    public static void getHotelRoomList(Context context,String url,String hotelId,ApiRequestListener handler) {
//        url = "http://192.168.1.104:8080";
        final HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("hotelId", hotelId);
        new AppServiceOk(context,url+"/small/command/getHotelBox",Action.GET_HOTEL_BOX_JSON,handler,params).get();
//        new AppServiceOk(context,"http://"+url+":8080/command/getHotelBox",Action.GET_HOTEL_BOX_JSON,handler,params).get();
    }

    /**获取手机验证码*/
    public static void getverifyCode(Context context, String mobile,  ApiRequestListener handler) {
        final HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("mobile", mobile);
        new AppServiceOk(context,Action.POST_VERIFY_CODE_JSON,handler,params).post();
    }

    /**获取推荐菜*/
    public static void getRecommendFoods(Context context, String hotel_id,  ApiRequestListener handler) {
        final HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("hotel_id", hotel_id);
        new AppServiceOk(context,Action.GET_RECOMMEND_FOODS_JSON,handler,params).post();
    }

    /**获取推荐菜*/
    public static void getAdvertList(Context context, String hotel_id,  ApiRequestListener handler) {
        final HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("hotel_id", hotel_id);
        new AppServiceOk(context,Action.GET_ADVERT_JSON,handler,params).post();
    }

    /**宣传片投屏*/
    public static void adverPro(Context context,String url,String boxMac,String vid,ApiRequestListener handler) {
//        url = "http://192.168.1.104:8080";
        final HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("deviceId", STIDUtil.getDeviceId(context));
        params.put("boxMac", boxMac);
        params.put("deviceName", Build.MODEL);
        params.put("vid", vid);
        new AppServiceOk(context,url+"/small/command/screend/adv",Action.GET_ADVERT_PRO_JSON,handler,params).get();
//        new AppServiceOk(context,"http://"+url+":8080/command/getHotelBox",Action.GET_HOTEL_BOX_JSON,handler,params).get();
    }

    /**推荐菜投屏*/
    public static void recommendPro(Context context,String url,String boxMac,String interval,String specialtyId,ApiRequestListener handler) {
//        url = "http://192.168.1.104:8080";
        final HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("deviceId", STIDUtil.getDeviceId(context));
        params.put("boxMac", boxMac);
        params.put("deviceName", Build.MODEL);
        params.put("specialtyId", specialtyId);
        params.put("interval", interval);
        new AppServiceOk(context,url+"/small/command/screend/recommend",Action.GET_RECOMMEND_PRO_JSON,handler,params).get();
//        new AppServiceOk(context,"http://"+url+":8080/command/getHotelBox",Action.GET_HOTEL_BOX_JSON,handler,params).get();
    }
    /**欢迎词投屏*/
    public static void wordPro(Context context,String url,String boxMac,String templateId,String word,ApiRequestListener handler) {
//        url = "http://192.168.1.104:8080";
        final HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("deviceId", STIDUtil.getDeviceId(context));
        params.put("boxMac", boxMac);
        params.put("deviceName", Build.MODEL);
        params.put("templateId", templateId);
        params.put("word", word);
        new AppServiceOk(context,url+"/small/command/screend/word",Action.GET_WORD_PRO_JSON,handler,params).get();
//        new AppServiceOk(context,"http://"+url+":8080/command/getHotelBox",Action.GET_HOTEL_BOX_JSON,handler,params).get();
    }


    /**新增预订*/
    public static void addOrder(Context context,
                                String invite_id,
                                String mobile,
                                String order_mobile,
                                String order_name,
                                String order_time,
                                String person_nums,
                                String room_id,
                                String room_type,
                                ApiRequestListener handler) {
        final HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("invite_id", invite_id);
        params.put("mobile", mobile);
        params.put("order_mobile", order_mobile);
        params.put("order_name", order_name);
        params.put("order_time", order_time);
        params.put("person_nums", person_nums);
        params.put("room_id", room_id);
        params.put("room_type", room_type);
        new AppServiceOk(context,Action.POST_ADD_ORDER_JSON,handler,params).post();
    }

    /**获取酒楼包间列表*/
    public static void getList(Context context,String invite_id, String mobile, ApiRequestListener handler) {
        final HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("invite_id", invite_id);
        params.put("mobile", mobile);
        new AppServiceOk(context,Action.POST_ROOM_LIST_JSON,handler,params).post();
    }

    /**添加包间*/
    public static void addRoom(Context context,String invite_id, String mobile, String room_name, ApiRequestListener handler) {
        final HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("invite_id", invite_id);
        params.put("mobile", mobile);
        params.put("room_name", room_name);
        new AppServiceOk(context,Action.POST_ADD_ROOM_JSON,handler,params).post();
    }
    /**预订列表*/
    public static void getOrderList(Context context,String invite_id, String mobile, String order_date,String page_num, ApiRequestListener handler) {
        final HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("invite_id", invite_id);
        params.put("mobile", mobile);
        params.put("order_date", order_date);
        params.put("page_num", page_num);
        new AppServiceOk(context,Action.POST_ORDER_LIST_JSON,handler,params).post();
    }

    /**修改预订信息*/
    public static void updateOrder(Context context,
                                String invite_id,
                                String mobile,
                                String order_mobile,
                                String order_id,
                                String order_name,
                                String order_time,
                                String person_nums,
                                String room_id,
                                String room_type,
                                ApiRequestListener handler) {
        final HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("invite_id", invite_id);
        params.put("mobile", mobile);
        params.put("order_mobile", order_mobile);
        params.put("order_name", order_name);
        params.put("order_time", order_time);
        params.put("person_nums", person_nums);
        params.put("room_id", room_id);
        params.put("room_type", room_type);
        params.put("order_id", order_id);

        new AppServiceOk(context,Action.POST_UPDATE_ORDER_JSON,handler,params).post();
    }

    /**删除预订信息*/
    public static void deleteOrder(Context context,String invite_id, String mobile, String order_id, ApiRequestListener handler) {
        final HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("invite_id", invite_id);
        params.put("mobile", mobile);
        params.put("order_id", order_id);
        new AppServiceOk(context,Action.POST_DELETE_ORDER_JSON,handler,params).post();
    }

    /**更新预订信息服务*/
    public static void upateOrderService(Context context,
                                         String invite_id,
                                         String mobile,
                                         String order_id,
                                         String ticket_url,
                                         String type,
                                         ApiRequestListener handler) {
        final HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("invite_id", invite_id);
        params.put("mobile", mobile);
        params.put("order_id", order_id);
        params.put("ticket_url", ticket_url);
        params.put("type", type);
        new AppServiceOk(context,Action.POST_UPDATE_ORDER_SERVICE_JSON,handler,params).post();
    }

    /**首次导入通讯录*/
    public static void importInfoFirst(Context context,
                                       String book_info,
                                       String invite_id,
                                       String mobile,
                                       ApiRequestListener handler) {
        final HashMap<String, Object> params = new HashMap<>();
        params.put("invite_id", invite_id);
        params.put("mobile", mobile);
        params.put("book_info", book_info);
        new AppServiceOk(context,Action.POST_IMPORT_INFO_JSON,handler,params).post();
    }

    /**导入通讯录非首次*/
    public static void importInfoNew(Context context,
                                       String book_info,
                                       String invite_id,
                                       String mobile,
                                       ApiRequestListener handler) {
        final HashMap<String, Object> params = new HashMap<>();
        params.put("invite_id", invite_id);
        params.put("mobile", mobile);
        params.put("book_info", book_info);
        new AppServiceOk(context,Action.POST_IMPORT_INFO_NEW_JSON,handler,params).post();
    }


    /**点亮熄灭标签*/
    public static void lightLabel(Context context,String customer_id, String invite_id, String label_id,String mobile,String type, ApiRequestListener handler) {
        final HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("customer_id", customer_id);
        params.put("invite_id", invite_id);
        params.put("label_id", label_id);
        params.put("mobile", mobile);
        params.put("type", type);
        new AppServiceOk(context,Action.POST_LIGHT_LABEL_JSON,handler,params).post();

    }

    /**获取客户最近历史*/
    public static void getCustomerHistory(Context context,String customer_id, String invite_id, String mobile, ApiRequestListener handler) {
        final HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("invite_id", invite_id);
        params.put("mobile", mobile);
        params.put("customer_id", customer_id);
        new AppServiceOk(context,Action.POST_CUS_HISTORY_JSON,handler,params).post();
    }

    /**添加客户*/
    public static void addCustomer(Context context,
                                         String bill_info,
                                         String birthday,
                                         String birthplace,
                                         String consume_ability,
                                         String face_url,
                                         String invite_id,
                                         String mobile,
                                         String name,
                                         String remark,
                                         String sex,
                                         String usermobile,
                                         ApiRequestListener handler) {
        final HashMap<String, Object> params = new HashMap<>();
        params.put("bill_info", bill_info);
        params.put("birthday", birthday);
        params.put("birthplace", birthplace);
        params.put("consume_ability", consume_ability);
        params.put("face_url", face_url);
        params.put("invite_id", invite_id);
        params.put("mobile", mobile);
        params.put("remark", remark);
        params.put("name", name);
        params.put("sex", sex);
        params.put("usermobile", usermobile);
        new AppServiceOk(context,Action.POST_ADD_CUS_JSON,handler,params).post();
    }


    /**修改客户*/
    public static void editCustomer(Context context,
                                   String bill_info,
                                   String birthday,
                                   String birthplace,
                                   String consume_ability,
                                   String face_url,
                                   String invite_id,
                                   String mobile,
                                   String name,
                                   String remark,
                                   String sex,
                                   String usermobile,
                                   ApiRequestListener handler) {
        final HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("bill_info", bill_info);
        params.put("birthday", birthday);
        params.put("birthplace", birthplace);
        params.put("consume_ability", consume_ability);
        params.put("face_url", face_url);
        params.put("invite_id", invite_id);
        params.put("mobile", mobile);
        params.put("remark", remark);
        params.put("name", name);
        params.put("sex", sex);
        params.put("usermobile", usermobile);
        new AppServiceOk(context,Action.POST_EDIT_CUS_JSON,handler,params).post();
    }

    /**获取客户基本信息*/
    public static void getCustomerBaseInfo(Context context,String customer_id, String invite_id, String mobile, ApiRequestListener handler) {
        final HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("invite_id", invite_id);
        params.put("mobile", mobile);
        params.put("customer_id", customer_id);
        new AppServiceOk(context,Action.POST_CUSTOMER_INFO_JSON,handler,params).post();
    }
    /**获取消费能力列表*/
    public static void getConAbilityList(Context context,
                                    ApiRequestListener handler) {
        final HashMap<String, Object> params = new HashMap<String, Object>();
        new AppServiceOk(context,Action.POST_CON_ABILITY_JSON,handler,params).post();
    }

    /**添加带预订信息截图消费记录*/
    public static void addConsumeRecord(Context context,
                                        String customer_id,
                                        String invite_id,
                                        String order_id,
                                        String mobile,
                                        String recipt,
                                        ApiRequestListener handler) {
        final HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("customer_id", customer_id);
        params.put("invite_id", invite_id);
        params.put("order_id", order_id);
        params.put("mobile", mobile);
        params.put("recipt", recipt);
        new AppServiceOk(context,Action.POST_ADD_CONSUME_RECORD_JSON,handler,params).post();

    }
    /**添加无预定信息消费记录*/
    public static void addSignleConsumeRecord(Context context,
                                        String bill_info,
                                        String birthday,
                                        String birthplace,
                                        String consume_ability,
                                        String customer_id,
                                        String face_url,
                                        String invite_id,
                                        String lable_id_str,
                                        String mobile,
                                        String name,
                                        String recipt,
                                        String usermobile,
                                        String remark,
                                        String sex,
                                        ApiRequestListener handler) {
        final HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("bill_info", bill_info);
        params.put("birthday", birthday);
        params.put("consume_ability", consume_ability);
        params.put("customer_id", customer_id);
        params.put("face_url", face_url);
        params.put("invite_id", invite_id);
        params.put("lable_id_str", lable_id_str);
        params.put("mobile", mobile);
        params.put("name", name);
        params.put("recipt", recipt);
        params.put("usermobile", usermobile);
        params.put("remark", remark);
        params.put("sex", sex);
        new AppServiceOk(context,Action.POST_ADD_SIGNLE_CONSUME_RECORD_JSON,handler,params).post();

    }


    /**上下拉消费记录*/
    public static void reportLog(Context context,
                                 String customer_id,
                                 String invite_id,
                                 String max_id,
                                 String min_id,
                                 String mobile,
                                 String order_id,
                                 String recipt,
                                 String type,
                                 ApiRequestListener handler) {
        final HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("hotel_id", customer_id);
        params.put("invite_id", invite_id);
        params.put("max_id", max_id);
        params.put("min_id", min_id);
        params.put("mobile", mobile);
        params.put("order_id", order_id);
        params.put("recipt", recipt);
        params.put("type", type);

        new AppServiceOk(context,Action.POST_TOP_LIST_JSON,handler,params).post();
    }

    /**获取标签列表(全量)*/
    public static void getLabelList(Context context,
                                    String customer_id,
                                    String invite_id,
                                    String mobile,
                                    ApiRequestListener handler) {
        final HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("customer_id", customer_id);
        params.put("invite_id", invite_id);
        params.put("mobile", mobile);

        new AppServiceOk(context,Action.POST_LABEL_List_JSON,handler,params).post();
    }

    /**添加标签*/
    public static void addLabel(Context context,
                                            String customer_id,
                                            String invite_id,
                                            String label_name,
                                            String mobile,
                                            ApiRequestListener handler) {
        final HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("customer_id", customer_id);
        params.put("invite_id", invite_id);
        params.put("label_name", label_name);
        params.put("mobile", mobile);

        new AppServiceOk(context,Action.POST_ADD_LABEL_JSON,handler,params).post();
    }

    /**获取指定客户标签列表*/
    public static void getCustomerLabelList(Context context,
                                String customer_id,
                                String invite_id,
                                String mobile,
                                ApiRequestListener handler) {
        final HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("customer_id", customer_id);
        params.put("invite_id", invite_id);
        params.put("mobile", mobile);

        new AppServiceOk(context,Action.POST_CUSTOMER_LABELS_JSON,handler,params).post();
    }

    // 超时（网络）异常
    public static final String ERROR_TIMEOUT = "3001";
    // 业务异常
    public static final String ERROR_BUSSINESS = "3002";
    // 网络断开
    public static final String ERROR_NETWORK_FAILED = "3003";

    public static final String RESPONSE_CACHE = "3004";

    /**
     * 从这里定义业务的错误码
     */
    public static final int CMS_RESPONSE_STATE_SUCCESS = 1001;
    public static final int CLOUND_RESPONSE_STATE_SUCCESS = 10000;

    /**机顶盒返回响应码*/
    public static final int TVBOX_RESPONSE_STATE_SUCCESS = 0;
    public static final int TVBOX_RESPONSE_STATE_ERROR = -1;
    public static final int TVBOX_RESPONSE_STATE_FORCE = 4;
    /**大小图不匹配失败*/
    public static final int TVBOX_RESPONSE_NOT_MATCH = 10002;
    public static final int TVBOX_RESPONSE_VIDEO_COMPLETE = 10003;

    /**
     * 数据返回错误
     */
    public static final int HTTP_RESPONSE_STATE_ERROR = 101;
    /**没有更多数据响应码*/
    public static final int HTTP_RESPONSE_CODE_NO_DATA = 10060;
}
