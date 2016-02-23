package com.timelyweather.app.receiver;

import com.timelyweather.app.service.AutoUpdateService;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.sax.StartElementListener;

/**
 * 自动开启服务的广播
 * @author Administrator
 *
 */
public class AutoUpdateReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		Intent i=new Intent(context,AutoUpdateService.class);
		context.startService(i);
	}

}
