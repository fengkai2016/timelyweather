package com.timelyweather.app.util;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.text.TextUtils;

import com.google.gson.JsonObject;
import com.timelyweather.app.db.TimelyWeatherDB;
import com.timelyweather.app.model.City;
import com.timelyweather.app.model.County;
import com.timelyweather.app.model.Province;
/**
 * Json解析返回的数据
 * @author Administrator
 *
 */
public class Utility {

	private static String lastProvinceCode="";
	private static String lastCityCode="";
	private static List<JSONObject> list_Object_DailyForecast;
	public synchronized static boolean handleCityResponse(TimelyWeatherDB db,String startResponse){
		//判断返回的数据是否为空
		if(!TextUtils.isEmpty(startResponse)){
			try {
				String response=getResponse(startResponse);
				JSONArray jsonArray=new JSONArray(response);
				for (int i = 0; i < jsonArray.length(); i++) {
					//获取每个城市的基本信息
					JSONObject jsonObject=jsonArray.getJSONObject(i);
					String id=jsonObject.getString("id");
					String provinceName=jsonObject.getString("prov");
					String countyName=jsonObject.getString("city");
					
					//截取字符串获取该地区的省市区的id
					String provinceCode=getProvinceCode(id);
					String cityCode=getCityCode(id);
					String countyCode=getCountyCode(id);
					
					//将地区信息添加到数据库中
					County county=new County();
					county.setCountyCode(countyCode);
					county.setCountyName(countyName);
					county.setCityId(Integer.parseInt(cityCode));
					county.setProvinceId(Integer.parseInt(provinceCode));
					db.saveCounty(county);
					
					//判断与上一个地区是否属于同一个城市
					if(!cityCode.equals(lastCityCode)){
						lastCityCode=cityCode;
						City city=new City();
						city.setProvinceId(Integer.parseInt(provinceCode));
						city.setCityCode(cityCode);
						//没有返回城市名字，先用地区名字代替
						city.setCityName(countyName);
						db.saveCity(city);         
						if(!provinceCode.equals(lastProvinceCode)){
							lastProvinceCode=provinceCode;
							Province province=new Province();
							province.setProvinceCode(provinceCode);
							province.setProvinceName(provinceName);
							
							db.saveProvince(province);
						}
					}
					
				}				
				return true;
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		return false;
		
	}
	
	/**
	 * 解析服务器返回的JSON数据，将数据储存在本地
	 * @param context
	 * @param startResponse
	 */
	public static void headleWeaherResponse(Context context,String startResponse){
		
		try {
			//获取整个JSON数据
			JSONObject object_HeWeather=new JSONObject(startResponse);
			//截取名字为HeWeather data service 3.0的数组
			JSONArray array_HeWeather=object_HeWeather.getJSONArray("HeWeather data service 3.0");
			//截取第一个大括号内的Object
			JSONObject object_FirstHeWeather=array_HeWeather.getJSONObject(0);
			//截取名字为daily_forecast的数组
			JSONArray array_DailyForecast=object_FirstHeWeather.getJSONArray("daily_forecast");
			JSONObject object_Basic=object_FirstHeWeather.getJSONObject("basic");
			
			//获取城市名称和ID
			String string_City=object_Basic.getString("city");
			String string_Id=object_Basic.getString("id");
			//获取当地天气更新时间
			JSONObject object_Update=object_Basic.getJSONObject("update");
			String string_Loc=object_Update.getString("loc");
			
			
			
			//分别获取今天，明天，后天的数据
			JSONObject object_Today_DailyForecast=array_DailyForecast.getJSONObject(0);
			JSONObject object_Tomorrow_DailyForecast=array_DailyForecast.getJSONObject(1);
			JSONObject object_TheDayAfterTomorrow_DailyForecast=array_DailyForecast.getJSONObject(2);
			
			//将数据添加进集合中
			list_Object_DailyForecast = new ArrayList<JSONObject>();
			list_Object_DailyForecast.add(object_Today_DailyForecast);
			list_Object_DailyForecast.add(object_Tomorrow_DailyForecast);
			list_Object_DailyForecast.add(object_TheDayAfterTomorrow_DailyForecast);
			
			//遍历集合，获取详细信息
//			for(JSONObject object_DailyForecast:list_Object_DailyForecast){
				//String string_Date=object_DailyForecast.getString("date");
			
				//获取当前日期
				String string_Date=object_Today_DailyForecast.getString("date");
				
				//获取天气状况
				JSONObject object_Cond=object_Today_DailyForecast.getJSONObject("cond");
				//获取白天天气描述
				String string_Txt_d=object_Cond.getString("txt_d");
				//获取夜间天气描述
				String string_Txt_n=object_Cond.getString("txt_n");
				
				//获取温度信息
				JSONObject object_Tmp=object_Today_DailyForecast.getJSONObject("tmp");
				//获取最高温
				String string_Max=object_Tmp.getString("max");
				//获取最低温
				String string_Min=object_Tmp.getString("min");
				
//			}
			//调用储存当前第几天，日期，天气状况，温度的方法
			saveWeatherInfo(0, string_City, string_Id, string_Loc, string_Date, 
					string_Txt_d, string_Txt_n, string_Max, string_Min,context);
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	

	/**
	 * 储存当前城市，id，更新时间，第几天，日期，天气状况，温度的方法
	 * @param i
	 * @param string_City
	 * @param string_Id
	 * @param string_Loc
	 * @param string_Date
	 * @param string_Txt_d
	 * @param string_Txt_n
	 * @param string_Max
	 * @param string_Min
	 */
	private static void saveWeatherInfo(int i, String string_City,
			String string_Id, String string_Loc, String string_Date,
			String string_Txt_d, String string_Txt_n, String string_Max,
			String string_Min,Context context) {
		
		SharedPreferences.Editor editor=
				PreferenceManager.getDefaultSharedPreferences(context).edit();
		//储存成xml格式
		editor.putBoolean("boolean_select", true);
		editor.putString("string_City", string_City);
		editor.putString("string_Id", string_Id);
		editor.putString("string_Loc", string_Loc);
		editor.putString("string_Date", string_Date);
		editor.putString("string_Txt_d", string_Txt_d);
		editor.putString("string_Txt_n", string_Txt_n);
		editor.putString("string_Max", string_Max);
		editor.putString("string_Min", string_Min);
		editor.commit();
		
	}

	/**
	 * 得到该省编号
	 * @param id
	 * @return provinceId
	 */
	public static String getProvinceCode(String id){
		String provinceId=id.substring(5, 7);
		//int provinceCode=Integer.parseInt(provinceId);
		return provinceId;
	}
	/**
	 * 得到该市编号
	 * @param id
	 * @return cityId
	 */
	public static String getCityCode(String id){
		String cityId=id.substring(5, 9);
//		int cityCode=Integer.parseInt(cityId);
		return cityId;
	}
	/**
	 * 得到该区编号
	 * @param id
	 * @return countyId
	 */
	public static String getCountyCode(String id){
		String countyId=id.substring(5);
//		int countyCode=Integer.parseInt(countyId);
		return countyId;
	}
	
	public static String getResponse(String startResponse){
		
		int index_1=startResponse.indexOf("[");
		int index_2=startResponse.lastIndexOf("]");
		String response=startResponse.substring(index_1, index_2+1);
		return response;
		
	}

}
