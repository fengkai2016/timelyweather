package com.timelyweather.app.util;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.http.HttpConnection;

/**
 * 服务服务器请求工具类
 * @author Administrator
 *
 */

public class HttpUtil {

	public static void sendHttpRequest(final String address,final HttpCallbackListener listener){
		/**
		 * 多线程服务网络
		 */
		Thread thread=new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				
				HttpURLConnection connection=null;
				BufferedReader reader=null;
				try {
					//连接网络初始化
					URL url=new URL(address);
					connection=(HttpURLConnection) url.openConnection();
					connection.setRequestMethod("GET");
					connection.setReadTimeout(20000);
					connection.setConnectTimeout(20000);
					//连接网络，获得数据流
					InputStream is=connection.getInputStream();
					reader=new BufferedReader(new InputStreamReader(is));
					StringBuilder response=new StringBuilder();
					String line=null;
					while((line=reader.readLine())!=null){
						response.append(line);
					}
					//回调onFinish()方法
					if(listener!=null){
						listener.onFinish(response.toString());
					}
					
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					//回调onError()方法
					listener.onError(e);
					if(listener!=null){
						listener.onError(e);
					}
				}
				finally{
					if(connection!=null){
						connection.disconnect();
					}
					if(reader!=null){
						try {
							reader.close();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
				
			}
		});
		thread.start();
	}
}
