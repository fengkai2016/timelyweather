package com.timelyweather.app.activity;

import java.util.ArrayList;
import java.util.List;

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
import android.os.Bundle;
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
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
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
//					queryCity();
					queryCounty();
				}else if(currentLevel==LEVEL_CITY){
					selectCity=cityList.get(position);
					queryCounty();		
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
//		countyList=db.loadCounty(selectCity.getId());
		countyList=db.loadCounty(Integer.parseInt(selectProvince.getProvinceCode()));
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
		cityList=db.loadCity(selectProvince.getId());
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
			//���õ�ǰ״̬Ϊʡ
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
			address="https://api.heweather.com/x3/citylist?search=allchina" +
					"&key=fa9b707316e7417eada569925d37dab0";
		}
		//��ʾ�Ի���
		showProgressDialog();
		//���������������
		HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {
			
			@Override
			public void onFinish(String response) {
				// TODO Auto-generated method stub
				boolean result=false;
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
			}
			
			@Override
			public void onError(Exception e) {
				// TODO Auto-generated method stub
				//ͨ��runOnUiThread�ص����̸߳���UI
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						// TODO Auto-generated method stub
						
						closeProgressDialog();
						Toast.makeText(ChooseAreaActivity.this, "����ʧ�ܣ�", 0).show();
					}

					
				});
			}
		});
		
	}
	/**
	 * ��ʾ���ȶԻ���
	 */
	private void showProgressDialog() {
		// TODO Auto-generated method stub
		System.out.println();
		if(progressDialog==null){
			progressDialog=new ProgressDialog(this);
			progressDialog.setMessage("���ڼ���...");
			progressDialog.setCancelable(false);
			
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
			queryProvince();
		}
		else if(currentLevel==LEVEL_CITY){
			queryProvince();
		}
		else{
			finish();
		}
	}

	
}
