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
 * 数据库操作的封装类
 * @author Administrator
 * 
 */
public class TimelyWeatherDB {

	/**
	 * 数据库名
	 */
	public static final String DB_NAME="timely_weather.db";
	/**
	 * 版本号
	 */
	public static final int VERSION=1;
	
	private static TimelyWeatherDB timelyWeatherDB;
	
	private static SQLiteDatabase db;
	
	/**
	 * 将构造函数私有化
	 * @param context
	 */
	private TimelyWeatherDB(Context context){
		TimelyWeatherOpenHelper dbHelper=
				new TimelyWeatherOpenHelper(context, DB_NAME, null, VERSION);
		db=dbHelper.getWritableDatabase();
	}
	
	/**
	 * 单例模式，获取TimelyWeatherDB的实例
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
	 * 将Province储存到数据库
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
	 * 删除重复的省份
	 */
	public void deleteProvince(){
		db.delete("Province", "_id = ? and province_code = ?", new String[]{"1","31"});
	}
	/**
	 * 更新出错的省份
	 */
	public void updateProvince(){
		ContentValues values=new ContentValues();
		
		values.put("province_name", "北京");
		db.update("Province", values, "province_code =?", new String[]{"01"});
		values.clear();
		values.put("province_name", "上海");
		db.update("Province", values, "province_code =?", new String[]{"02"});
		values.clear();
		values.put("province_name", "天津");
		db.update("Province", values, "province_code =?", new String[]{"03"});
		values.clear();
		values.put("province_name", "重庆");
		db.update("Province", values, "province_code =?", new String[]{"04"});
		values.clear();
		values.put("province_name", "香港");
		db.update("Province", values, "province_code =?", new String[]{"32"});
		values.clear();
		values.put("province_name", "澳门");
		db.update("Province", values, "province_code =?", new String[]{"33"});
	}
	
	/**
	 * 从数据库读取全国所有省份信息
	 * @return List<Province>
	 */
	public List<Province> loadProvince(){
		List<Province> listProvince=new ArrayList<Province>();
		Cursor cursor=db.query("Province", null, null, null, null, null, null);
		//判断表中的数据是否为空
		if(cursor.moveToFirst()){
			do {
				//获取省份信息，添加到集合中
				Province province =new Province();
				String provinceName=cursor.getString(cursor.getColumnIndex("province_name"));
				String provinceCode=cursor.getString(cursor.getColumnIndex("province_code"));
				province.setProvinceName(provinceName);
				province.setProvinceCode(provinceCode);
				listProvince.add(province);
				//使得表的指针移动到下一行
			} while (cursor.moveToNext());
		}
		if(cursor!=null){
			cursor.close();
		}
		
		return listProvince;
		
	}
	/**
	 * 将City储存到数据库
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
	 * 从数据库读取某省所有城市信息
	 * @param provinceId
	 * @return List<City>
	 */
	public List<City> loadCity(String province_code){
		List<City> listCity=new ArrayList<City>();
		
		//查找省份id为provinceId的城市信息
		Cursor cursor=db.query("City", null, "province_code=?", 
				new String[]{province_code}, null, null, null);
		
		//判断表中的数据是否为空
		if(cursor.moveToFirst()){
			do {
				//获取城市信息，添加到集合中
				City city =new City();
				String cityName=cursor.getString(cursor.getColumnIndex("city_name"));
				String cityCode=cursor.getString(cursor.getColumnIndex("city_code"));
				city.setCityCode(cityCode);
				city.setCityName(cityName);
				city.setProvinceCode(province_code);
				listCity.add(city);
				//使得表的指针移动到下一行
			} while (cursor.moveToNext());
		}
		if(cursor!=null){
			cursor.close();
		}
		
		return listCity;
		
	}
	/**
	 * 将County储存到数据库
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
	 * 从数据库读取某城市所有区县信息
	 * @param cityId
	 * @return List<County
	 */
	public List<County> loadCounty(String city_code){
		List<County> listCounty=new ArrayList<County>();
		
		//查找城市id为cityId的区县信息
		Cursor cursor=db.query("County", null, "city_code=?", 
				new String[]{city_code}, null, null, null);
		
		//判断表中的数据是否为空
		if(cursor.moveToFirst()){
			do {
				//获取城市信息，添加到集合中
				County county =new County();
				String countyName=cursor.getString(cursor.getColumnIndex("county_name"));
				String countyCode=cursor.getString(cursor.getColumnIndex("county_code"));
				county.setCountyCode(countyCode);
				county.setCountyName(countyName);
				county.setProvinceCode(city_code);
				listCounty.add(county);
				//使得表的指针移动到下一行
			} while (cursor.moveToNext());
		}
		if(cursor!=null){
			cursor.close();
		}
		
		return listCounty;
		
	}
	
}














