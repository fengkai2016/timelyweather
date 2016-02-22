package com.timelyweather.app.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
/**
 * 数据库和表
 * @author Administrator
 *
 */
public class TimelyWeatherOpenHelper extends SQLiteOpenHelper {

	/**
	 * Province建表语句
	 */
	private static final String CREATE_PROVINCE=
			"create table Province(_id integer primary key autoincrement, " +
			"province_name text, province_code text )";  
	/**
	 * City建表语句
	 */
	private static final String CREATE_CITY=
			"create table City(_id integer primary key autoincrement, " +
					"city_name text, city_code text, province_id integer)";  
	/**
	 * County建表语句
	 */
	private static final String CREATE_COUNTY=
			"create table County(_id integer primary key autoincrement, " +
					"county_name text, county_code text, city_id integer)";  
	public TimelyWeatherOpenHelper(Context context, String name,
			CursorFactory factory, int version) {
		super(context, name, factory, version);
		// TODO Auto-generated constructor stub
	}

	@Override
	/**
	 * 建表
	 */
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		db.execSQL(CREATE_PROVINCE);
		db.execSQL(CREATE_CITY);
		db.execSQL(CREATE_COUNTY);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		
	}

}
