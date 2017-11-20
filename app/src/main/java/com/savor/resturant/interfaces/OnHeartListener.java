package com.savor.resturant.interfaces;


import com.savor.resturant.bean.HeartBeatResponeseVo;

/**
 * 心跳请求成功,更新心跳请求的倒计时为0
 * 
 * @author savor
 * 
 */
public interface OnHeartListener extends OnBaseListenner {
	/**
	 * 重置心跳倒计时为0
	 */
	void resetHeartTime();

	/**
	 * 心跳失败
	 */
	void heartFailed(HeartBeatResponeseVo beatResponeseVo);

}
