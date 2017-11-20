package com.savor.resturant.bean;

import java.io.Serializable;

public class PlayRequstVo implements Serializable {

	private static final long serialVersionUID = 4445870756166886894L;
	/**请求类型，比如播放功能*/
	private String function;
	/**会话id*/
	private int sessionid;
	/**命令0暂停1播放*/
	private int rate;

	public int getSessionid() {
		return sessionid;
	}

	public void setSessionid(int sessionid) {
		this.sessionid = sessionid;
	}

	public int getRate() {
		return rate;
	}

	public void setRate(int rate) {
		this.rate = rate;
	}

	public String getFunction() {
		return function;
	}

	public void setFunction(String function) {
		this.function = function;
	}

}
