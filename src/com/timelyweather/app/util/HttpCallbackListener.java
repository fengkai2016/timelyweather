package com.timelyweather.app.util;
/**
 * ���ݷ��ؼ������ӿ�
 * @author Administrator
 *
 */
public interface HttpCallbackListener {

	void onFinish(String response);
	
	void onError(Exception e);
}
