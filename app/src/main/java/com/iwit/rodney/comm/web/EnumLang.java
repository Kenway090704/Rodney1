package com.iwit.rodney.comm.web;

import com.iwit.rodney.App;


/**
 * 管理系统语言环境与url/语言编号的关系
 * @author tao
 *
 */
public enum EnumLang {
	china("china","zh-CN", 1, "CN"),//简体
	en("en", "en", 32, "US"),//美式英语
	nofound("en", "en", 32, null);//默认
	
	String key; //标识enum元素的
	String subUrl;//不同语言对应的url中的部分不同
	String countryCode; //地区代码，比如CN/US/HK等
	int lid; //对应的语言编号（这个编号是后台定义的，主要用作本地数据查询时过滤条件）
	
	EnumLang(String skey, String subUrl, int lid, String countryCode){
		this.key = skey;
		this.subUrl = subUrl;
		this.lid = lid;
		this.countryCode = countryCode;
	}
	/**
	 * 根据地区找sub url
	 * @param loc
	 * @return
	 */
	public static String getSubUrlByCountryCode(String countryCode){
		for (EnumLang item : EnumLang.values()) {
			if(item.getCountryCode() == null){
				continue;
			}
			if (item.getCountryCode().equals(countryCode)) {
				return item.getSubUrl();
			}
		}
		return nofound.subUrl;
	}
	
	public static EnumLang getLangEnum(String countryCode){
		
		for (EnumLang item : EnumLang.values()) {
			if(item.getCountryCode() == null){
				continue;
			}
			if (item.getCountryCode().equals(countryCode)) {
				return item;
			}
		}
		return nofound;
	}

	public static int getLidBaseAndroid() {
		String countryCode = App.getCountryCode();
		
		for (EnumLang item : EnumLang.values()) {
			if(item.getCountryCode() == null){
				continue;
			}
			if (item.getCountryCode().equals(countryCode)) {
				return item.getLid();
			}
		}
		return nofound.lid;
	}
	
	public static int getLidBaseAndroid(String countryCode) {
		for (EnumLang item : EnumLang.values()) {
			if(item.getCountryCode() == null){
				continue;
			}
			if (item.getCountryCode().equals(countryCode)) {
				return item.getLid();
			}
		}
		return nofound.lid;
	}
	public static int getLidBaseAndroidBySubUrl(String subUrl) {
		for (EnumLang item : EnumLang.values()) {
			if(item.getSubUrl() == null){
				continue;
			}
			if (item.getSubUrl().equals(subUrl)) {
				return item.getLid();
			}
		}
		return nofound.lid;
	}
	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getSubUrl() {
		return subUrl;
	}

	public void setSubUrl(String subUrl) {
		this.subUrl = subUrl;
	}
	
	public String getCountryCode() {
		return countryCode;
	}
	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}
	public int getLid() {
		return lid;
	}
	public void setLid(int lid) {
		this.lid = lid;
	}
}
