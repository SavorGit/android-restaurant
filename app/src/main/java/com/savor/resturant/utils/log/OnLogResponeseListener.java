package com.savor.resturant.utils.log;

public interface OnLogResponeseListener {
	void onLogNull();

	void onLogSuccess(int actionType, LogRespVo result);

}
