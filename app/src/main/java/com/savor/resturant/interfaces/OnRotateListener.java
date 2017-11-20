package com.savor.resturant.interfaces;


import com.savor.resturant.bean.RotateProResponse;

public interface OnRotateListener extends OnBaseListenner {
	/**
	 * 图片旋转
	 * 
	 * @param responseVo
	 */
	void rotate(RotateProResponse responseVo);

	/**
	 * 旋转失败
	 */
	void rotateFailed(RotateProResponse responseVo);
}
