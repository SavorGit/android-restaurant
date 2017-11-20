package com.savor.resturant.interfaces;


import com.savor.resturant.bean.SeekProResponseVo;

public interface OnSeekListener extends OnBaseListenner {

	/**
	 * seek失败
	 */
	void seekFailed(SeekProResponseVo responseVo);
}
