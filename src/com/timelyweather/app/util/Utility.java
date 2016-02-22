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
 * Json�������ص�����
 * @author Administrator
 *
 */
public class Utility {

	private static String lastProvinceCode="";
	private static String lastCityCode="";
	public synchronized static boolean handleCityResponse(TimelyWeatherDB db,String startResponse){
		//�жϷ��ص������Ƿ�Ϊ��
		if(!TextUtils.isEmpty(startResponse)){
			try {
				String response=getResponse(startResponse);
				JSONArray jsonArray=new JSONArray(response);
				for (int i = 0; i < jsonArray.length(); i++) {
					//��ȡÿ�����еĻ�����Ϣ
					JSONObject jsonObject=jsonArray.getJSONObject(i);
					String id=jsonObject.getString("id");
					String provinceName=jsonObject.getString("prov");
					String countyName=jsonObject.getString("city");
					
					//��ȡ�ַ�����ȡ�õ�����ʡ������id
					String provinceCode=getProvinceCode(id);
					String cityCode=getCityCode(id);
					String countyCode=getCountyCode(id);
					
					//��������Ϣ��ӵ����ݿ���
					County county=new County();
					county.setCountyCode(countyCode);
					county.setCountyName(countyName);
					county.setCityId(Integer.parseInt(cityCode));
					county.setProvinceId(Integer.parseInt(provinceCode));
					db.saveCounty(county);
					
					//�ж�����һ�������Ƿ�����ͬһ������
					if(!cityCode.equals(lastCityCode)){
						lastCityCode=cityCode;
						City city=new City();
						city.setProvinceId(Integer.parseInt(provinceCode));
						city.setCityCode(cityCode);
						//û�з��س������֣����õ������ִ���
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
	 * �õ���ʡ���
	 * @param id
	 * @return provinceId
	 */
	public static String getProvinceCode(String id){
		String provinceId=id.substring(5, 7);
		//int provinceCode=Integer.parseInt(provinceId);
		return provinceId;
	}
	/**
	 * �õ����б��
	 * @param id
	 * @return cityId
	 */
	public static String getCityCode(String id){
		String cityId=id.substring(5, 9);
//		int cityCode=Integer.parseInt(cityId);
		return cityId;
	}
	/**
	 * �õ��������
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
