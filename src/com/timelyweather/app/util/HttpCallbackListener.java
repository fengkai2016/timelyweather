package com.timelyweather.app.util;
/**
 * 数据返回监听器接口
 * @author Administrator
 *
 */
public interface HttpCallbackListener {

	void onFinish(String response);
	
	void onError(Exception e);
}
