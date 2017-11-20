package com.savor.resturant.utils;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 各个接口的请求参数
 * 
 * @author bichao
 * 
 */
public class ParamsUtils {

	private static final String TAG = "ParamsUtils";
	public static Map<String, String> map = new HashMap<String, String>();
	public static final String REQUEST_KEY = "request_key";
	public static final String OFFSET_KEY = "offset_key";
	public static final String PAGESIZE_KEY = "pageSize_key";
	

	/**
	 * 请求参数的map集合拼接成为 json 串。
	 * 
	 * @return
	 */
	public static String getJsonParamsString(Object obj) {
		if(obj!=null&&obj instanceof HashMap){
			HashMap<String, Object> params=(HashMap<String, Object>) obj;
			JSONObject jsonObject = new JSONObject();
			for (Map.Entry<String, Object> entry : params.entrySet()) {
				String key = entry.getKey();
				Object value = entry.getValue();
	
				try {
					if(value==null){
						value="";
					}
					jsonObject.put(key, value);
				} catch (JSONException e) {
					e.printStackTrace();
				}
	
			}
	
			return jsonObject.toString();
		}else{
			return "";
		}
			

	}

	
	
}
