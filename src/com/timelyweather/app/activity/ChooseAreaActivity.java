package com.timelyweather.app.activity;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.HttpHandler;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.timelyweather.app.R;
import com.timelyweather.app.db.TimelyWeatherDB;
import com.timelyweather.app.model.City;
import com.timelyweather.app.model.County;
import com.timelyweather.app.model.Province;
import com.timelyweather.app.util.HttpCallbackListener;
import com.timelyweather.app.util.HttpUtil;
import com.timelyweather.app.util.Utility;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 遍历省市区的活动
 * @author Administrator
 *
 */
public class ChooseAreaActivity extends Activity {

	//初始化的实例
	private long max;
	private long progress;
	private ProgressDialog progressDialog;
	private TextView title_text;
	private ListView list_view;
	private ArrayAdapter<String> adapter;
	private TimelyWeatherDB db;
	private List<String> dataList=new ArrayList<String>();
	//设置级别
	public static int LEVEL_PROVINCE=0;
	public static int LEVEL_CITY=1;
	public static int LEVEL_COUNTY=2;
	/**
	 * 设置省列表
	 */
	private List<Province>provinceList;
	/**
	 * 设置市列表
	 */
	private List<City>cityList;
	/**
	 * 设置区县列表
	 */
	private List<County>countyList;
	/**
	 * 选中的省份
	 */
	private Province selectProvince;
	/**
	 * 选中的市
	 */
	private City selectCity;
	/**
	 * 设置当前级别
	 */
	private int currentLevel;
	
	private String address;
	/**
	 * 判断是否从WeatherActivity跳转过来的
	 */
	private boolean isFromWeatherActivity=false;
	private String target;
	
	Handler handler=new Handler(){
		public void handleMessage(Message msg){
			super.handleMessage(msg);
			switch (msg.what) {
			case 0:
				progressDialog.setProgress(50);
				
				break;

			default:
				break;
			}
			progressDialog.setProgress((int) progress);
			
		}
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		isFromWeatherActivity=getIntent().getBooleanExtra("fromWeatherActivity", false);
		//如果上一次已经选择了一个城市，则直接跳转到天气活动
		SharedPreferences spf=this.getSharedPreferences("weather_"+0, MODE_PRIVATE);
		//已经选择了城市而且不是从WeatherActivity跳转过来的才会调用if里面的语句，跳转到WeatherActivity
		if(spf.getBoolean("boolean_select", false)&&!isFromWeatherActivity){
			Intent intent=new Intent(this,WeatherActivity.class);
			startActivity(intent);
			finish();
			return;
		}
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.choose_area);
		//设置ListView的布局和适配器
		title_text = (TextView) findViewById(R.id.title_text);
		list_view = (ListView) findViewById(R.id.list_view);
		adapter = new ArrayAdapter<String>(
				this, android.R.layout.simple_list_item_1, dataList);
		list_view.setAdapter(adapter);
		//开启数据库操作
		db = TimelyWeatherDB.getInstance(this);
		//设置ListView点击事件
		list_view.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				if(currentLevel==LEVEL_PROVINCE){
					selectProvince=provinceList.get(position);
					queryCity();
//					queryCounty();
				}else if(currentLevel==LEVEL_CITY){
					selectCity=cityList.get(position);
					queryCounty();		
				}else if(currentLevel==LEVEL_COUNTY){
					String countyCode=countyList.get(position).getCountyCode();
					Intent intent=new Intent();
					intent.setClass(ChooseAreaActivity.this, WeatherActivity.class);
					intent.putExtra("countyCode", countyCode);
					intent.putExtra("fromChooseAreaActivity", true);
					startActivity(intent);
					finish();
					
				}
			}
			
		});
		//加载省级数据
		queryProvince();
	}
	/**
	 * 优先从数据库查询，找不到在向服务器请求
	 */
	private void queryProvince() {
		// TODO Auto-generated method stub
		provinceList=db.loadProvince();
		if(provinceList.size()>0){
			//清空应该在ListView中呈现的数据
			dataList.clear();
			for(Province pro:provinceList){
				dataList.add(pro.getProvinceName());
			}
			//监听ListView动态变化
			adapter.notifyDataSetChanged();
			list_view.setSelection(0);
			title_text.setText("中国");
			//设置当前状态为省
			currentLevel=LEVEL_PROVINCE;
		}
		else {
			queryFromServer(null,"Province");
			
		}
	}


	

	private void queryCounty() {
		// TODO Auto-generated method stub
		countyList=db.loadCounty(selectCity.getCityCode());
		if(countyList.size()>0){
			//清空应该在ListView中呈现的数据
			dataList.clear();
			for(County co:countyList){
				dataList.add(co.getCountyName());
			}
			//监听ListView动态变化
			adapter.notifyDataSetChanged();
			list_view.setSelection(0);
			title_text.setText(selectProvince.getProvinceName());
			//设置当前状态为省
			currentLevel=LEVEL_COUNTY;
		}
		else {
			queryFromServer(null,"County");
		}	// TODO Auto-generated method stub
		
		
	}

	private void queryCity() {
		// TODO Auto-generated method stub
		cityList=db.loadCity(selectProvince.getProvinceCode());
		if(cityList.size()>0){
			//清空应该在ListView中呈现的数据
			dataList.clear();
			for(City ci:cityList){
				dataList.add(ci.getCityName());
			}
			//监听ListView动态变化
			adapter.notifyDataSetChanged();
			list_view.setSelection(0);
			title_text.setText(selectProvince.getProvinceName());
			//设置当前状态为市
			currentLevel=LEVEL_CITY;
		}
		else {
			queryFromServer(null,"City");
		}
	}
	/**
	 * 向服务器查询地区信息
	 */
	private void queryFromServer(final String code,final String type) {
		// TODO Auto-generated method stub
		//判断是请求当前地区信息还是天气信息
		if(!TextUtils.isEmpty(code)){
			address = "https://api.heweather.com/x3/weather?cityid="+code+
					"&key=fa9b707316e7417eada569925d37dab0";
		}
		else{
			address="https://api.heweather.com/x3/citylist?search=allchina&key=fa9b707316e7417eada569925d37dab0";
			target = "/storage/sdcard1/timelyweather/allCities.txt";
		}
		//显示对话框
		showProgressDialog();
		
		//用导入的jar包的api向服务器发送请求下载数据（速度快）
		HttpUtils xUtils =new HttpUtils();
		HttpHandler<File> xHandler=xUtils.download(
				address,//下载地址 
				target,  //保存地址
				true, //是否支持断点传续 
				true, 
				new RequestCallBack<File>() {
					

					/**
					 * 下载成功时调用该方法
					 */
			public void onSuccess(ResponseInfo<File> arg0) {
				boolean result=false;
				FileReader fr;
				BufferedReader reader=null;
				try {
					//创建文件实例
					File file=new File("storage/sdcard1/timelyweather/allCities.txt");
					if(!file.exists()){
						file.createNewFile();
					}
					fr = new FileReader(file);
					reader=new BufferedReader(fr );
					
					//获取下载的文本
					StringBuilder sb=new StringBuilder();
					String line=null;
					while((line=reader.readLine())!=null){
						sb.append(line);
					}
					String response=sb.toString();
					
					//向Utility发送数据
					result=Utility.handleCityResponse(db, response);
					
					if(result){
						//通过runOnUiThread回到主线程处理逻辑
						runOnUiThread(new Runnable() {
							@Override
							public void run() {
								// TODO Auto-generated method stub
								closeProgressDialog();
								if("Province".equals(type)){
									queryProvince();
								}
								else if("City".equals(type)){
									queryCity();
								}
								else if("County".equals(type)){
									queryCounty();
								}
							}
						});
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				finally{
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
			
			@Override
			public void onFailure(HttpException arg0, String arg1) {
				// TODO Auto-generated method stub
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						// TODO Auto-generated method stub
						
						closeProgressDialog();
						Toast.makeText(ChooseAreaActivity.this, "加载失败！", 0).show();
					}

					
				});
			}

			@Override
			public void onLoading(final long total, final long current, boolean isUploading) {
				// TODO Auto-generated method stub
				super.onLoading(total, current, isUploading);
						// TODO Auto-generated method stub
						max = total;
						progress=current;
//						progress = current*100/total+"%";

			}
			
		});
		
		
		//向服务器发送请求（下载速度慢）
//		HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {
//			
//			@Override
//			public void onFinish(String response) {
//				// TODO Auto-generated method stub
//				boolean result=false;
//				result=Utility.handleCityResponse(db, response);
//				if(result){
//					//通过runOnUiThread回到主线程处理逻辑
//					runOnUiThread(new Runnable() {
//						@Override
//						public void run() {
//							// TODO Auto-generated method stub
//							closeProgressDialog();
//							if("Province".equals(type)){
//								queryProvince();
//							}
//							else if("City".equals(type)){
//								queryCity();
//							}
//							else if("County".equals(type)){
//								queryCounty();
//							}
//						}
//					});
//				}
//			}
//			
//			@Override
//			public void onError(Exception e) {
//				// TODO Auto-generated method stub
//				//通过runOnUiThread回到主线程更新UI
//				runOnUiThread(new Runnable() {
//					@Override
//					public void run() {
//						// TODO Auto-generated method stub
//						
//						closeProgressDialog();
//						Toast.makeText(ChooseAreaActivity.this, "加载失败！", 0).show();
//					}
//
//					
//				});
//			}
//		});
		
		
	}
	/**
	 * 显示进度对话框
	 */
	private void showProgressDialog() {
		// TODO Auto-generated method stub
		
		if(progressDialog==null){
			progressDialog=new ProgressDialog(this);
//			progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
			progressDialog.setMessage("正在加载所有城市信息，请稍等...");
			progressDialog.setCancelable(false);
//			progressDialog.setIndeterminate(false);
//			progressDialog.setProgress(50);
//			Message msg=handler.obtainMessage();
//			msg.what=0;
//			handler.sendMessage(msg);
			
		}
		progressDialog.show();
	}
	/**
	 * 关闭进度对话框
	 */
	private void closeProgressDialog() {
		// TODO Auto-generated method stub
		if(progressDialog!=null){
			progressDialog.dismiss();
		}
	}
	/**
	 * 捕获Back按键，根据当前的级别来判断此时应该返回省列表，市列表，县列表还是退出
	 */
	public void onBackPressed(){
		if(currentLevel==LEVEL_COUNTY){
//			queryCity();
			queryCity();
		}
		else if(currentLevel==LEVEL_CITY){
			queryProvince();
		}
		else{
			if(isFromWeatherActivity) {
				Intent i=new Intent(this,WeatherActivity.class);
				startActivity(i);
			}
			finish();
		}
	}

	
}
