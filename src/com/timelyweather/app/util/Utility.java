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
 * Json�������ص�����
 * @author Administrator
 *
 */
public class Utility {

	private static String lastProvinceCode="";
	private static String lastCityCode="";
	private static List<JSONObject> list_Object_DailyForecast;
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
	 * �������������ص�JSON���ݣ������ݴ����ڱ���
	 * @param context
	 * @param startResponse
	 */
	public static void headleWeaherResponse(Context context,String startResponse){
		
		try {
			//��ȡ����JSON����
			JSONObject object_HeWeather=new JSONObject(startResponse);
			//��ȡ����ΪHeWeather data service 3.0������
			JSONArray array_HeWeather=object_HeWeather.getJSONArray("HeWeather data service 3.0");
			//��ȡ��һ���������ڵ�Object
			JSONObject object_FirstHeWeather=array_HeWeather.getJSONObject(0);
			//��ȡ����Ϊdaily_forecast������
			JSONArray array_DailyForecast=object_FirstHeWeather.getJSONArray("daily_forecast");
			JSONObject object_Basic=object_FirstHeWeather.getJSONObject("basic");
			
			//��ȡ�������ƺ�ID
			String string_City=object_Basic.getString("city");
			String string_Id=object_Basic.getString("id");
			//��ȡ������������ʱ��
			JSONObject object_Update=object_Basic.getJSONObject("update");
			String string_Loc=object_Update.getString("loc");
			
			
			
			//�ֱ��ȡ���죬���죬���������
			JSONObject object_Today_DailyForecast=array_DailyForecast.getJSONObject(0);
			JSONObject object_Tomorrow_DailyForecast=array_DailyForecast.getJSONObject(1);
			JSONObject object_TheDayAfterTomorrow_DailyForecast=array_DailyForecast.getJSONObject(2);
			
			//��������ӽ�������
			list_Object_DailyForecast = new ArrayList<JSONObject>();
			list_Object_DailyForecast.add(object_Today_DailyForecast);
			list_Object_DailyForecast.add(object_Tomorrow_DailyForecast);
			list_Object_DailyForecast.add(object_TheDayAfterTomorrow_DailyForecast);
			
			//�������ϣ���ȡ��ϸ��Ϣ
//			for(JSONObject object_DailyForecast:list_Object_DailyForecast){
				//String string_Date=object_DailyForecast.getString("date");
			
				//��ȡ��ǰ����
				String string_Date=object_Today_DailyForecast.getString("date");
				
				//��ȡ����״��
				JSONObject object_Cond=object_Today_DailyForecast.getJSONObject("cond");
				//��ȡ������������
				String string_Txt_d=object_Cond.getString("txt_d");
				//��ȡҹ����������
				String string_Txt_n=object_Cond.getString("txt_n");
				
				//��ȡ�¶���Ϣ
				JSONObject object_Tmp=object_Today_DailyForecast.getJSONObject("tmp");
				//��ȡ�����
				String string_Max=object_Tmp.getString("max");
				//��ȡ�����
				String string_Min=object_Tmp.getString("min");
				
//			}
			//���ô��浱ǰ�ڼ��죬���ڣ�����״�����¶ȵķ���
			saveWeatherInfo(0, string_City, string_Id, string_Loc, string_Date, 
					string_Txt_d, string_Txt_n, string_Max, string_Min,context);
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	

	/**
	 * ���浱ǰ���У�id������ʱ�䣬�ڼ��죬���ڣ�����״�����¶ȵķ���
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
		//�����xml��ʽ
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
