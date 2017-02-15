package com.iwit.rodney.comm.utils;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.iwit.rodney.exception.IwitApiException;

public class Json2BeanUtil {
	private static final String T = "Json2BeanUtil";
	//	 可以多添加几种类型例如可以添加大数
	private static final List<Class> _primitiveClasses = Arrays
			.asList(new Class[] { Long.class, Integer.class, String.class ,BigDecimal.class});

	private static boolean _isPrimitive(Class<?> cls) {
		return cls.isPrimitive() | _primitiveClasses.contains(cls);
	}

	/**
	 * 将string型转化为integer/long?
	 * @param target
	 * @param o
	 * @return
	 * @throws IwitApiException 
	 */
	private static <T> T typeConvert(Class<T> target, Object o) throws IwitApiException {
		try {
			if (o == null) {
				return null;
			}
			if (target.getName().equals(o.getClass().getName())) {
				return (T)o;
			}
			Constructor<T> con = target.getConstructor(new Class[]{String.class});
			T bean = con.newInstance((String)o);
			return bean;
		} catch (NoSuchMethodException e) {
			throw new IwitApiException("[JSON模块]将数据结果String转型时，目标类型没有string作为参数的构造方法，创建失败"+" o:"+o, e);
		} catch (InstantiationException e) {
			throw new IwitApiException("[JSON模块]将数据结果String转型时，试图用构造方法创建对象，但目标类是接口或抽象类，创建失败"+" o:"+o, e);
		} catch (IllegalAccessException e) {
			throw new IwitApiException("[JSON模块]将数据结果String转型时，试图用构造方法创建对象，但方法是私有的，创建失败"+" o:"+o, e);
		} catch (IllegalArgumentException e) {
			throw new IwitApiException("[JSON模块]将数据结果String转型时，试图用构造方法创建对象，但参数类型有误，创建失败"+" o:"+o, e);
		} catch (InvocationTargetException e) {
			throw new IwitApiException("[JSON模块]将数据结果String转型时，试图用构造方法创建对象，内部出现有异常，创建失败"+" o:"+o, e);
		}
	}

	public static <T> List<T> jsonArrayString2Bean(Class<T> t, String js,
			String key) throws IwitApiException {
		List<T> result = null;
		try {
			JSONObject jo = new JSONObject(js);
			Object o = jo.get(key);
			JSONObject jotemp;
			if (o instanceof String | o instanceof JSONArray) {
				JSONArray jas = new JSONArray(o.toString());
				result = new ArrayList<T>();
				for (int i = 0; i < jas.length(); i++) {
					T bean = null;
					jotemp = jas.getJSONObject(i);
					// json 转成map
					Map<String, Object> map = new HashMap<String, Object>();
					String mKey;
					for (Iterator<String> iter = jotemp.keys(); iter.hasNext();) {
						mKey = iter.next();
						map.put(mKey, jotemp.get(mKey));
					}
					// map 转化成bean
					if (_isPrimitive(t)) {// 基础类型
						Object oj;
						// map entry item.getKey key item.getValue() �?
						for (Map.Entry<String, Object> item : map.entrySet()) {
							oj = map.get(item.getKey());
							bean = typeConvert(t, oj);//直接赋值给bean
						}
					} else {
						bean = t.newInstance();
						Field[] fs = t.getDeclaredFields();// 得到bean的所有字段
						String fName;// 字段名称
						Method m;// 字段方法

						for (Field f : fs) {
							fName = f.getName();
							// 如果map里面包含bean字段的字段名称的key 取出来赋值给bean
							if (map.containsKey(fName)) {
								m = t.getMethod(getSetterName(fName),
										new Class[] { f.getType() });
								Object os = map.get(fName);
								m.invoke(bean, new Object[] { typeConvert(f.getType(), os) });// 填充
							}
						}

					}
					result.add(bean);
				}

			} else {
				Log.e(T, "非法参数");
			}
		} catch (NoSuchMethodException e) {
			throw new IwitApiException("[JSON模块]将数据结果String转型时，目标类型没有string作为参数的构造方法，创建失败", e);
		} catch (InstantiationException e) {
			throw new IwitApiException("[JSON模块]将数据结果String转型时，试图用构造方法创建对象，但目标类是接口或抽象类，创建失败", e);
		} catch (IllegalAccessException e) {
			throw new IwitApiException("[JSON模块]将数据结果String转型时，试图用构造方法创建对象，但方法是私有的，创建失败", e);
		} catch (IllegalArgumentException e) {
			throw new IwitApiException("[JSON模块]将数据结果String转型时，试图用构造方法创建对象，但参数类型有误，创建失败", e);
		} catch (InvocationTargetException e) {
			throw new IwitApiException("[JSON模块]将数据结果String转型时，试图用构造方法创建对象，内部出现有异常，创建失败", e);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			throw new IwitApiException("[JSON模块]将数据转换为json时候创建失败"+" o:", e);
		}
		return result;
	}

	/**
	 * 获得set方法
	 * 
	 * @param fName
	 * @return
	 */
	private static String getSetterName(String fn) {
		String firstC = fn.substring(0, 1);
		return "set" + fn.replaceFirst(firstC, firstC.toUpperCase());
	}
}
