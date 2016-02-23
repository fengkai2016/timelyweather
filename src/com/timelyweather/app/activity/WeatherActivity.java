package com.timelyweather.app.activity;

import com.timelyweather.app.R;
import com.timelyweather.app.service.AutoUpdateService;
import com.timelyweather.app.util.HttpCallbackListener;
import com.timelyweather.app.util.HttpUtil;
import com.timelyweather.app.util.Utility;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;
/**
 * 天气界面活动
 * @author Administrator
 *
 */
public class WeatherActivity extends Activity {

	private LinearLayout weather_info_layout;
	private TextView title_text;
	private TextView publish_text;
	private TextView current_date;
	private TextView weather_desp;
	private TextView temp_desp;
	private String countyCode;
	
	/**
	 * 判断是否从ChooseAreaActivity跳转过来的
	 */
	private boolean isFromChooseAreaActivity=false;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.weather_layout);
		//判断是否从ChooseAreaActivity跳转过来的
		isFromChooseAreaActivity=getIntent().getBooleanExtra(
				"fromChooseAreaActivity", false);
		
		//初始化布局
		weather_info_layout = (LinearLayout) findViewById(R.id.weather_info_layout);
		title_text = (TextView) findViewById(R.id.title_text);
		publish_text = (TextView) findViewById(R.id.publish_text);
		current_date = (TextView) findViewById(R.id.current_date);
		weather_desp = (TextView) findViewById(R.id.weather_desp);
		temp_desp = (TextView) findViewById(R.id.temp_desp);
		
		countyCode = getIntent().getStringExtra("countyCode");
		if(!TextUtils.isEmpty(countyCode)){
			publish_text.setText("同步中...");
			title_text.setVisibility(View.INVISIBLE);
			weather_info_layout.setVisibility(View.INVISIBLE);
			queryWeatherCode(countyCode);
		}
		else{
			showWeather();
		}
		
	}

	
	private void showWeather() {
		//TODO Auto-generated method stub
		SharedPreferences spf=this.getSharedPreferences("weather_"+0, MODE_PRIVATE);
		
		title_text.setText(spf.getString("string_City", ""));
		publish_text.setText("今天"+spf.getString("string_Loc", "")+"发布");
		current_date.setText(spf.getString("string_Date", ""));
		weather_desp.setText("白天："+spf.getString("string_Txt_d", "")+"\r\n"+
							 "晚上："+spf.getString("string_Txt_n", ""));
		temp_desp.setText(spf.getString("string_Min", "")+"℃~"+spf.getString("string_Max", "")+"℃");
		title_text.setVisibility(View.VISIBLE);
		weather_info_layout.setVisibility(View.VISIBLE);
		//激活后台服务
		Intent i=new Intent(this,AutoUpdateService.class);
		startService(i);
	}

	private void queryWeatherCode(String countyCode) {
		// TODO Auto-generated method stub

		String address="https://api.heweather.com/x3/weather?cityid=CN101"+
				countyCode+"&key=fa9b707316e7417eada569925d37dab0";
		HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {
			
			@Override
			public void onFinish(String response) {
				// TODO Auto-generated method stub
				if(!TextUtils.isEmpty(response)){
					Utility.headleWeaherResponse(WeatherActivity.this, response);
					runOnUiThread(new Runnable() {
						
						@Override
						public void run() {
							// TODO Auto-generated method stub
							showWeather();
						}
					});
				}
			}
			
			@Override
			public void onError(Exception e) {
				// TODO Auto-generated method stub
				runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
						// TODO Auto-generated method stub
						publish_text.setText("同步失败！");
					}
				});
			}
		});
	}

	/**
	 * 切换城市
	 * @param v
	 */
	public void switchCity(View v){
		
		Intent intent=new Intent();
		intent.setClass(this, ChooseAreaActivity.class);
		intent.putExtra("fromWeatherActivity", true);
		startActivity(intent);
		finish();
	}
	/**
	 * 刷新天气
	 * @param v
	 */
	public void refreshWeather(View v){
		publish_text.setText("同步中...");
		queryWeatherCode(countyCode);
	}
	public void onBackPressed(){
//		if(isFromChooseAreaActivity) {
//			Intent i=new Intent(this,ChooseAreaActivity.class);
//			i.putExtra("fromWeatherActivity", true);
//			startActivity(i);
//		}
		finish();
	}

}
