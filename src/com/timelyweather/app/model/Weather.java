package com.timelyweather.app.model;

public class Weather {
	int i; 
	String string_City;
	String string_Id;
	String string_Loc;
	String string_Date;
	String string_Txt_d;
	String string_Txt_n;
	String string_Max;
	String string_Min;
	public Weather(int i, String string_City, String string_Id,
			String string_Loc, String string_Date, String string_Txt_d,
			String string_Txt_n, String string_Max, String string_Min) {
		super();
		this.i = i;
		this.string_City = string_City;
		this.string_Id = string_Id;
		this.string_Loc = string_Loc;
		this.string_Date = string_Date;
		this.string_Txt_d = string_Txt_d;
		this.string_Txt_n = string_Txt_n;
		this.string_Max = string_Max;
		this.string_Min = string_Min;
	}
	public int getI() {
		return i;
	}
	public void setI(int i) {
		this.i = i;
	}
	public String getString_City() {
		return string_City;
	}
	public void setString_City(String string_City) {
		this.string_City = string_City;
	}
	public String getString_Id() {
		return string_Id;
	}
	public void setString_Id(String string_Id) {
		this.string_Id = string_Id;
	}
	public String getString_Loc() {
		return string_Loc;
	}
	public void setString_Loc(String string_Loc) {
		this.string_Loc = string_Loc;
	}
	public String getString_Date() {
		return string_Date;
	}
	public void setString_Date(String string_Date) {
		this.string_Date = string_Date;
	}
	public String getString_Txt_d() {
		return string_Txt_d;
	}
	public void setString_Txt_d(String string_Txt_d) {
		this.string_Txt_d = string_Txt_d;
	}
	public String getString_Txt_n() {
		return string_Txt_n;
	}
	public void setString_Txt_n(String string_Txt_n) {
		this.string_Txt_n = string_Txt_n;
	}
	public String getString_Max() {
		return string_Max;
	}
	public void setString_Max(String string_Max) {
		this.string_Max = string_Max;
	}
	public String getString_Min() {
		return string_Min;
	}
	public void setString_Min(String string_Min) {
		this.string_Min = string_Min;
	}
	
}
