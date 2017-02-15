package com.iwit.rodney.comm.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.iwit.rodney.exception.IwitApiException;

/**
 * Json、与字符串形式、对象相互转换的工具类
 * 
 * @author tao
 *
 */
public class JsonUtil{
	/**
	 * 将JSONArray规格的数据转化为Map列表，当不确定是string还是JsonArray对象时调用此方法
	 * @param o
	 * @return
	 * @throws IwitApiException
	 */
	public static List<Map<String, Object>> object2Maps(Object o) throws IwitApiException{
		if (o instanceof String) {
			return jsonStrings2Maps((String)o);
		} else if (o instanceof JSONArray) {
			return jsonArray2Maps((JSONArray)o);
		} else {
			throw new IwitApiException("[json处理]将Object转化为Map列表异常，非法的参数，必须为String或JSONArray类型");
		}
	}
	/**
	 * 将JSON规格的数据转化为Map，当确定是string时调用此方法
	 * @param o
	 * @return
	 * @throws IwitApiException
	 */
	public static Map<String, Object> jsonString2Map(String jos) throws IwitApiException {
		JSONObject jo;
		try {
			jo = new JSONObject(jos);
			return jsonObject2Map(jo);
		} catch (JSONException e) {
			throw new IwitApiException("[json处理]将String转化为Map异常，String参数非法或JSON转Map异常", e);
		}
	}
	/**
	 * 将JSONArray规格的数据转化为Map列表，当确定是string时调用此方法
	 * @param o
	 * @return
	 * @throws IwitApiException
	 */
	public static List<Map<String, Object>> jsonStrings2Maps(String jas) throws IwitApiException{
		JSONArray ja;
		try {
			ja = new JSONArray(jas);
			return jsonArray2Maps(ja);
		} catch (JSONException e) {
			throw new IwitApiException("[json处理]将String串初始化为JSONArray异常，请检查String串是否合法", e);
		}
	}

	private static List<Map<String, Object>> jsonArray2Maps(JSONArray ja) throws IwitApiException{
		List<Map<String, Object>> l = new ArrayList<Map<String, Object>>();
		JSONObject jo;
		
		try {
			for (int i = 0; i < ja.length(); i++) {
				jo = ja.getJSONObject(i);
				l.add(jsonObject2Map(jo));
			}
		} catch (JSONException e) {
			throw new IwitApiException("[json处理]将JSONArray转化为Map列表异常，get json出错或json转化map出错", e);
		}
		return l;
	}
	
	/**
	 * 将对象集转化为json array的字符串形式（此方法在Android下暂不能用）
	 * @param <T>
	 * @param beans
	 * @return
	 * @throws JSONException 
	 */
	@Deprecated
	public static <T> String beans2JsonArrayString(T beans) throws JSONException {
		JSONArray ja = new JSONArray();
		
		if (beans instanceof List) {
			List<T> l = (List)beans;
			
			for (T bean : l) {
				Map<String, String> mapDatas = bean2Map(bean);
				ja.put(map2JsonObject(mapDatas));
			}
		}
		return ja.toString();
	}
	
	/**
	 * 将json array的字符串转化成对象数列（此方法在Android下暂不能用）
	 * @param <T>
	 * @param c 对象的类
	 * @param jas
	 * @return
	 * @throws JSONException 
	 */
	@Deprecated
	public static <T> List<T> jsonArrayString2Beans(Class<T> c, String jas) throws JSONException {
		JSONArray ja = new JSONArray(jas);
		List<T> result = new ArrayList<T>(ja.length());
		JSONObject jo;
		
		for (int i = 0; i < ja.length(); i++) {
			jo = ja.getJSONObject(i);
			result.add(jsonObject2Bean(c, jo));
		}
		return result;
	}

	/**
	 * 将bean对象转化成map格式数据（此方法在android下暂不用）
	 * @param <T>
	 * @param item
	 * @return
	 */
	@Deprecated
	public static <T> Map<String, String> bean2Map(T item) {
		try {
			Map<String, String> result = null;//BeanUtils.describe(item);
			result.remove("class");
			return result;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 将map格式数据保存到json对象中，并返回json对象
	 * @param properties
	 * @return
	 * @throws JSONException 
	 */
	private static JSONObject map2JsonObject(Map<String, String> properties) throws JSONException {
		JSONObject jo = new JSONObject();
		
		for (Map.Entry<String, String> item : properties.entrySet()) {
			jo.put(item.getKey(), item.getValue());
		}
		return jo;
	}

	/**
	 * 将json对象转化成实例对象（此方法在Android下暂不能用）
	 * @param <T> 泛型
	 * @param c 指定实例的类
	 * @param jo
	 * @return
	 */
	@Deprecated
	private static <T> T jsonObject2Bean(Class<T> c, JSONObject jo) {
		try {
			T result = c.newInstance();
			//BeanUtils.populate(result, jsonObject2Map(jo));
			return result;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * 将json对象转化成map对象，等待实例化对象的字段
	 * @param jo
	 * @return
	 * @throws JSONException 
	 */
	private static Map<String, Object> jsonObject2Map(JSONObject jo) throws JSONException {
		Map<String, Object> result = new HashMap<String, Object>();
		String sKey;
		
		for (Iterator<String> iter = jo.keys(); iter.hasNext();) {
			sKey = iter.next();
			result.put(sKey, jo.get(sKey));
		}
		return result;
	}
}

