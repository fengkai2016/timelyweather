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
	public static final String DB_NAME="timely_weather.db";
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
	 * ɾ���ظ���ʡ��
	 */
	public void deleteProvince(){
		db.delete("Province", "_id = ? and province_code = ?", new String[]{"1","31"});
	}
	/**
	 * ���³����ʡ��
	 */
	public void updateProvince(){
		ContentValues values=new ContentValues();
		
		values.put("province_name", "����");
		db.update("Province", values, "province_code =?", new String[]{"01"});
		values.clear();
		values.put("province_name", "�Ϻ�");
		db.update("Province", values, "province_code =?", new String[]{"02"});
		values.clear();
		values.put("province_name", "���");
		db.update("Province", values, "province_code =?", new String[]{"03"});
		values.clear();
		values.put("province_name", "����");
		db.update("Province", values, "province_code =?", new String[]{"04"});
		values.clear();
		values.put("province_name", "���");
		db.update("Province", values, "province_code =?", new String[]{"32"});
		values.clear();
		values.put("province_name", "����");
		db.update("Province", values, "province_code =?", new String[]{"33"});
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
			values.put("province_code", city.getProvinceCode());
			db.insert("City", null, values);
		}
	}
	
	/**
	 * �����ݿ��ȡĳʡ���г�����Ϣ
	 * @param provinceId
	 * @return List<City>
	 */
	public List<City> loadCity(String province_code){
		List<City> listCity=new ArrayList<City>();
		
		//����ʡ��idΪprovinceId�ĳ�����Ϣ
		Cursor cursor=db.query("City", null, "province_code=?", 
				new String[]{province_code}, null, null, null);
		
		//�жϱ��е������Ƿ�Ϊ��
		if(cursor.moveToFirst()){
			do {
				//��ȡ������Ϣ����ӵ�������
				City city =new City();
				String cityName=cursor.getString(cursor.getColumnIndex("city_name"));
				String cityCode=cursor.getString(cursor.getColumnIndex("city_code"));
				city.setCityCode(cityCode);
				city.setCityName(cityName);
				city.setProvinceCode(province_code);
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
			values.put("city_code", county.getCityCode());
			values.put("province_code", county.getProvinceCode());
			db.insert("County", null, values);
		}
	}
	/**
	 * �����ݿ��ȡĳ��������������Ϣ
	 * @param cityId
	 * @return List<County
	 */
	public List<County> loadCounty(String city_code){
		List<County> listCounty=new ArrayList<County>();
		
		//���ҳ���idΪcityId��������Ϣ
		Cursor cursor=db.query("County", null, "city_code=?", 
				new String[]{city_code}, null, null, null);
		
		//�жϱ��е������Ƿ�Ϊ��
		if(cursor.moveToFirst()){
			do {
				//��ȡ������Ϣ����ӵ�������
				County county =new County();
				String countyName=cursor.getString(cursor.getColumnIndex("county_name"));
				String countyCode=cursor.getString(cursor.getColumnIndex("county_code"));
				county.setCountyCode(countyCode);
				county.setCountyName(countyName);
				county.setProvinceCode(city_code);
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














