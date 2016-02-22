package com.timelyweather.app.util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.text.TextUtils;

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
