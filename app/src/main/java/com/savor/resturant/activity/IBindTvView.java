package com.savor.resturant.activity;

import com.savor.resturant.bean.RotateProResponse;
import com.savor.resturant.interfaces.IBaseView;

/**
 * Created by hezd on 2016/12/16.
 */

public interface IBindTvView extends IBaseView{

    /**显示修改wifi设置弹窗
     * 1.不在同一网段
     * 2.二维码解析数据通过&符号分割不是4段
     * 3.二维码扫码结果为空
     * */
    void showChangeWifiDialog(String ssid);

    /**准备扫描二维码弹窗*/
    void readyForQrcode();

    /**关闭二维码提示窗*/
    void closeQrcodeDialog();

    /**开始扫连接电视*/
    void startLinkTv();

    /**绑定成功*/
    void initBindcodeResult();
}
