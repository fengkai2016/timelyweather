package com.timelyweather.app.db;

import java.util.ArrayList;
import java.util.List;

import com.timelyweather.app.model.City;
import com.timelyweather.app.model.County;
import com.timelyweather.app.model.Province;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
/**
 * ���ݿ�����ķ�װ��
 * @author Administrator
 * 
 */
public class TimelyWeatherDB {

	/**
	 * ���ݿ���
	 */
	public static final String DB_NAME="timely_weather";
	/**
	 * �汾��
	 */
	public static final int VERSION=1;
	
	private static TimelyWeatherDB timelyWeatherDB;
	
	private static SQLiteDatabase db;
	
	/**
	 * �����캯��˽�л�
	 * @param context
	 */
	private TimelyWeatherDB(Context context){
		TimelyWeatherOpenHelper dbHelper=
				new TimelyWeatherOpenHelper(context, DB_NAME, null, VERSION);
		db=dbHelper.getWritableDatabase();
	}
	
	/**
	 * ����ģʽ����ȡTimelyWeatherDB��ʵ��
	 * @param context
	 * @return TimelyWeatherDB
	 */
	public synchronized static TimelyWeatherDB getInstance(Context context){
		
		if(timelyWeatherDB==null){
			timelyWeatherDB=new TimelyWeatherDB(context);
		}
		return timelyWeatherDB;
		
	}
	
	/**
	 * ��Province���浽���ݿ�
	 * @param province
	 */
	public void saveProvince(Province province){
		if(province!=null){
			ContentValues values=new ContentValues();
			values.put("province_name", province.getProvinceName());
			values.put("province_code", province.getProvinceCode());
			db.insert("Province", null, values);
		}
	}
	
	/**
	 * �����ݿ��ȡȫ������ʡ����Ϣ
	 * @return List<Province>
	 */
	public List<Province> loadProvince(){
		List<Province> listProvince=new ArrayList<Province>();
		Cursor cursor=db.query("Province", null, null, null, null, null, null);
		//�жϱ��е������Ƿ�Ϊ��
		if(cursor.moveToFirst()){
			do {
				//��ȡʡ����Ϣ����ӵ�������
				Province province =new Province();
				String provinceName=cursor.getString(cursor.getColumnIndex("province_name"));
				String provinceCode=cursor.getString(cursor.getColumnIndex("province_code"));
				province.setProvinceName(provinceName);
				province.setProvinceCode(provinceCode);
				listProvince.add(province);
				//ʹ�ñ��ָ���ƶ�����һ��
			} while (cursor.moveToNext());
		}
		if(cursor!=null){
			cursor.close();
		}
		
		return listProvince;
		
	}
	/**
	 * ��City���浽���ݿ�
	 * @param province
	 */
	public void saveCity(City city){
		if(city!=null){
			ContentValues values=new ContentValues();
			values.put("city_name", city.getCityName());
			values.put("city_code", city.getCityCode());
			values.put("province_id", city.getProvinceId());
			db.insert("City", null, values);
		}
	}
	
	/**
	 * �����ݿ��ȡĳʡ���г�����Ϣ
	 * @param provinceId
	 * @return List<City>
	 */
	public List<City> loadCity(int provinceId){
		List<City> listCity=new ArrayList<City>();
		
		//����ʡ��idΪprovinceId�ĳ�����Ϣ
		Cursor cursor=db.query("City", null, "province_id=?", 
				new String[]{String.valueOf(provinceId)}, null, null, null);
		
		//�жϱ��е������Ƿ�Ϊ��
		if(cursor.moveToFirst()){
			do {
				//��ȡ������Ϣ����ӵ�������
				City city =new City();
				String cityName=cursor.getString(cursor.getColumnIndex("city_name"));
				String cityCode=cursor.getString(cursor.getColumnIndex("city_code"));
				city.setCityCode(cityCode);
				city.setCityName(cityName);
				city.setProvinceId(provinceId);
				listCity.add(city);
				//ʹ�ñ��ָ���ƶ�����һ��
			} while (cursor.moveToNext());
		}
		if(cursor!=null){
			cursor.close();
		}
		
		return listCity;
		
	}
	/**
	 * ��County���浽���ݿ�
	 * @param county
	 */
	public void saveCounty(County county){
		if(county!=null){
			ContentValues values=new ContentValues();
			values.put("county_name", county.getCountyName());
			values.put("county_code", county.getCountyCode());
			values.put("city_id", county.getCityId());
			values.put("province_id", county.getProvinceId());
			db.insert("County", null, values);
		}
	}
	/**
	 * �����ݿ��ȡĳ��������������Ϣ
	 * @param cityId
	 * @return List<County
	 */
//	public List<County> loadCounty(int cityId){
	public List<County> loadCounty(int provinceId){
		List<County> listCounty=new ArrayList<County>();
		
		//���ҳ���idΪcityId��������Ϣ
		Cursor cursor=db.query("County", null, "province_id=?", 
				new String[]{String.valueOf(provinceId)}, null, null, null);
		
		//�жϱ��е������Ƿ�Ϊ��
		if(cursor.moveToFirst()){
			do {
				//��ȡ������Ϣ����ӵ�������
				County county =new County();
				String countyName=cursor.getString(cursor.getColumnIndex("county_name"));
				String countyCode=cursor.getString(cursor.getColumnIndex("county_code"));
				county.setCountyCode(countyCode);
				county.setCountyName(countyName);
//				county.setCityId(cityId);
				county.setProvinceId(provinceId);
				listCounty.add(county);
				//ʹ�ñ��ָ���ƶ�����һ��
			} while (cursor.moveToNext());
		}
		if(cursor!=null){
			cursor.close();
		}
		
		return listCounty;
		
	}
	
}














