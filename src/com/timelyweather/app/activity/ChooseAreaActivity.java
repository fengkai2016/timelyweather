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
 * ����ʡ�����Ļ
 * @author Administrator
 *
 */
public class ChooseAreaActivity extends Activity {

	//��ʼ����ʵ��
	private long max;
	private long progress;
	private ProgressDialog progressDialog;
	private TextView title_text;
	private ListView list_view;
	private ArrayAdapter<String> adapter;
	private TimelyWeatherDB db;
	private List<String> dataList=new ArrayList<String>();
	//���ü���
	public static int LEVEL_PROVINCE=0;
	public static int LEVEL_CITY=1;
	public static int LEVEL_COUNTY=2;
	/**
	 * ����ʡ�б�
	 */
	private List<Province>provinceList;
	/**
	 * �������б�
	 */
	private List<City>cityList;
	/**
	 * ���������б�
	 */
	private List<County>countyList;
	/**
	 * ѡ�е�ʡ��
	 */
	private Province selectProvince;
	/**
	 * ѡ�е���
	 */
	private City selectCity;
	/**
	 * ���õ�ǰ����
	 */
	private int currentLevel;
	
	private String address;
	/**
	 * �ж��Ƿ��WeatherActivity��ת������
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
		//�����һ���Ѿ�ѡ����һ�����У���ֱ����ת�������
		SharedPreferences spf=this.getSharedPreferences("weather_"+0, MODE_PRIVATE);
		//�Ѿ�ѡ���˳��ж��Ҳ��Ǵ�WeatherActivity��ת�����ĲŻ����if�������䣬��ת��WeatherActivity
		if(spf.getBoolean("boolean_select", false)&&!isFromWeatherActivity){
			Intent intent=new Intent(this,WeatherActivity.class);
			startActivity(intent);
			finish();
			return;
		}
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.choose_area);
		//����ListView�Ĳ��ֺ�������
		title_text = (TextView) findViewById(R.id.title_text);
		list_view = (ListView) findViewById(R.id.list_view);
		adapter = new ArrayAdapter<String>(
				this, android.R.layout.simple_list_item_1, dataList);
		list_view.setAdapter(adapter);
		//�������ݿ����
		db = TimelyWeatherDB.getInstance(this);
		//����ListView����¼�
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
		//����ʡ������
		queryProvince();
	}
	/**
	 * ���ȴ����ݿ��ѯ���Ҳ����������������
	 */
	private void queryProvince() {
		// TODO Auto-generated method stub
		provinceList=db.loadProvince();
		if(provinceList.size()>0){
			//���Ӧ����ListView�г��ֵ�����
			dataList.clear();
			for(Province pro:provinceList){
				dataList.add(pro.getProvinceName());
			}
			//����ListView��̬�仯
			adapter.notifyDataSetChanged();
			list_view.setSelection(0);
			title_text.setText("�й�");
			//���õ�ǰ״̬Ϊʡ
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
			//���Ӧ����ListView�г��ֵ�����
			dataList.clear();
			for(County co:countyList){
				dataList.add(co.getCountyName());
			}
			//����ListView��̬�仯
			adapter.notifyDataSetChanged();
			list_view.setSelection(0);
			title_text.setText(selectProvince.getProvinceName());
			//���õ�ǰ״̬Ϊʡ
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
			//���Ӧ����ListView�г��ֵ�����
			dataList.clear();
			for(City ci:cityList){
				dataList.add(ci.getCityName());
			}
			//����ListView��̬�仯
			adapter.notifyDataSetChanged();
			list_view.setSelection(0);
			title_text.setText(selectProvince.getProvinceName());
			//���õ�ǰ״̬Ϊ��
			currentLevel=LEVEL_CITY;
		}
		else {
			queryFromServer(null,"City");
		}
	}
	/**
	 * ���������ѯ������Ϣ
	 */
	private void queryFromServer(final String code,final String type) {
		// TODO Auto-generated method stub
		//�ж�������ǰ������Ϣ����������Ϣ
		if(!TextUtils.isEmpty(code)){
			address = "https://api.heweather.com/x3/weather?cityid="+code+
					"&key=fa9b707316e7417eada569925d37dab0";
		}
		else{
			address="https://api.heweather.com/x3/citylist?search=allchina&key=fa9b707316e7417eada569925d37dab0";
			target = "/storage/sdcard1/timelyweather/allCities.txt";
		}
		//��ʾ�Ի���
		showProgressDialog();
		
		//�õ����jar����api����������������������ݣ��ٶȿ죩
		HttpUtils xUtils =new HttpUtils();
		HttpHandler<File> xHandler=xUtils.download(
				address,//���ص�ַ 
				target,  //�����ַ
				true, //�Ƿ�֧�ֶϵ㴫�� 
				true, 
				new RequestCallBack<File>() {
					

					/**
					 * ���سɹ�ʱ���ø÷���
					 */
			public void onSuccess(ResponseInfo<File> arg0) {
				boolean result=false;
				FileReader fr;
				BufferedReader reader=null;
				try {
					//�����ļ�ʵ��
					File file=new File("storage/sdcard1/timelyweather/allCities.txt");
					if(!file.exists()){
						file.createNewFile();
					}
					fr = new FileReader(file);
					reader=new BufferedReader(fr );
					
					//��ȡ���ص��ı�
					StringBuilder sb=new StringBuilder();
					String line=null;
					while((line=reader.readLine())!=null){
						sb.append(line);
					}
					String response=sb.toString();
					
					//��Utility��������
					result=Utility.handleCityResponse(db, response);
					
					if(result){
						//ͨ��runOnUiThread�ص����̴߳����߼�
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
						Toast.makeText(ChooseAreaActivity.this, "����ʧ�ܣ�", 0).show();
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
		
		
		//��������������������ٶ�����
//		HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {
//			
//			@Override
//			public void onFinish(String response) {
//				// TODO Auto-generated method stub
//				boolean result=false;
//				result=Utility.handleCityResponse(db, response);
//				if(result){
//					//ͨ��runOnUiThread�ص����̴߳����߼�
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
//				//ͨ��runOnUiThread�ص����̸߳���UI
//				runOnUiThread(new Runnable() {
//					@Override
//					public void run() {
//						// TODO Auto-generated method stub
//						
//						closeProgressDialog();
//						Toast.makeText(ChooseAreaActivity.this, "����ʧ�ܣ�", 0).show();
//					}
//
//					
//				});
//			}
//		});
		
		
	}
	/**
	 * ��ʾ���ȶԻ���
	 */
	private void showProgressDialog() {
		// TODO Auto-generated method stub
		
		if(progressDialog==null){
			progressDialog=new ProgressDialog(this);
//			progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
			progressDialog.setMessage("���ڼ������г�����Ϣ�����Ե�...");
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
	 * �رս��ȶԻ���
	 */
	private void closeProgressDialog() {
		// TODO Auto-generated method stub
		if(progressDialog!=null){
			progressDialog.dismiss();
		}
	}
	/**
	 * ����Back���������ݵ�ǰ�ļ������жϴ�ʱӦ�÷���ʡ�б����б����б����˳�
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
