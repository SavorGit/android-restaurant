package com.savor.resturant.utils;

/**
 * Created by hezd on 2016/12/9.
 */

public class ConstantValues {
    //阿里oss配置参数
    /** endpoint是阿里云 OSS 服务在各个区域的地址*/
    public static final String ENDPOINT = "http://devp.oss.littlehotspot.com/";
//    public static final String ENDPOINT = "http://oss.littlehotspot.com/";
    /**阿里oss需要的keyid*/
    public static final String ACCESS_KEY_ID = "LTAIl7SuyV5LOrxT";
    /**阿里oss需要的key secret*/
    public static final String ACCESS_KEY_SECRET = "WrYH96JBGFmFTIE58JLOyos1vaZtPu";
    /**开发环境桶名*/
    public static final String BUCKET_DEV = "redian-development";

    /**正式环境桶名*/
    public static final String BUCKET_RELESE = "redian-produce";
    /**预发布环境桶名*/
    public static final String BUCKET_PRODUCT = "redian-development";
    /**oss桶名称*/
    public static final String BUCKET_NAME = BUCKET_PRODUCT;

    public static final String APP_KEY = "savor4321abcd1234";
    /**测试环境H5帮助页地址**/
    public static final String DEVP_H5_BASE_URL = "http://devp.h5.littlehotspot.com/Public/html/help/";
    /**正式环境H5帮助页地址**/
    public static final String H5_BASE_URL = "http://h5.littlehotspot.com/Public/html/help/";
    /**测试环境域名**/
    public static final String DEVP_HOST_URL = "http://devp.www.littlehotspot.com/";
    /**正式环境域名**/
    public static final String HOST_URL = "http://www.littlehotspot.com/";


    /**分类-图片和幻灯片**/
    public static final int CATEGORY_SLIDE = 0;
    /**分类-视频**/
    public static final int CATEGORY_VIDEO = 1;
    /**分类-文件**/
    public static final int CATEGORY_FILE = 2;

    /**
     * 首页Tab下标
     */
    public class HomeTabIndex {
        public static final int TAB_INDEX_BOOK = 0;
        public static final int TAB_INDEX_CUSTOMER = 1;
        public static final int TAB_INDEX_SERVICE = 2;
        public static final int TAB_INDEX_MY = 3;
    }
}
