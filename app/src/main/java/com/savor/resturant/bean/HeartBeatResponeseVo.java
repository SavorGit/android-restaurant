package com.savor.resturant.bean;

public class HeartBeatResponeseVo extends BaseProResponse {

	private static final long serialVersionUID = 4335752996695755914L;
	/** 此session当前的保活周期（in second） **/
	private int currcycletime;

	public int getCurrcycletime() {
		return currcycletime;
	}

	public void setCurrcycletime(int currcycletime) {
		this.currcycletime = currcycletime;
	}

}
