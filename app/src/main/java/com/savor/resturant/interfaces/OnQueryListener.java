package com.savor.resturant.interfaces;


import com.savor.resturant.bean.QuerySeekResponse;

public interface OnQueryListener extends OnBaseListenner {
	/**
	 * 更新进度
	 * 
	 * @param posBySessionIdResponseVo
	 */
	void updateSeek(QuerySeekResponse posBySessionIdResponseVo);

	/**
	 * 查询失败
	 */
	void queryFailed(QuerySeekResponse bySessionIdResponseVo);
}
