package com.savor.resturant.interfaces;


import com.savor.resturant.bean.CustomerLabel;

/**
 * 心跳请求成功,更新心跳请求的倒计时为0
 * 
 * @author savor
 * 
 */
public interface SetLabel {

	void setLabelLight(CustomerLabel clickLabel);
	void setLabelOff(CustomerLabel clickLabel);


}
