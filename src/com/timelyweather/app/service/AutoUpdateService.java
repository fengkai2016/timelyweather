package com.timelyweather.app.service;

import com.timelyweather.app.util.HttpCallbackListener;
import com.timelyweather.app.util.HttpUtil;
import com.timelyweather.app.util.Utility;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.SystemClock;
import android.preference.PreferenceManager;


/**
 * �Զ�����������Ϣ�ķ���
 * @author Administrator
 *
 */
public class AutoUpdateService extends Service {

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		
		
		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		//�����̸߳�������
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				updateWeather();
			}
		}).start();
		
		//ÿ��8Сʱ����һ��
		AlarmManager alarmManager=(AlarmManager) getSystemService(ALARM_SERVICE);
		int hours=6*60*60*1000;//6��Сʱ�ĺ�����
		long triggertAtTime=SystemClock.elapsedRealtime()+hours;
		Intent i=new Intent();
		PendingIntent pi=PendingIntent.getBroadcast(this, 0, i, 0);
		alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggertAtTime, pi);
		
		return super.onStartCommand(intent, flags, startId);
	}

	protected void updateWeather() {
		// TODO Auto-generated method stub
		String countyCode=PreferenceManager.getDefaultSharedPreferences(this).getString("string_Id", "");
		String address="https://api.heweather.com/x3/weather?cityid="+
				countyCode+"&key=fa9b707316e7417eada569925d37dab0";
		HttpUtil.sendHttpRequest(address,new HttpCallbackListener() {
			
			@Override
			public void onFinish(String response) {
				// TODO Auto-generated method stub
				Utility.headleWeaherResponse(AutoUpdateService.this, response);
			}
			
			@Override
			public void onError(Exception e) {
				// TODO Auto-generated method stub
				e.printStackTrace();
			}
		} );
	}
	

}
