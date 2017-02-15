package com.iwit.rodney.comm.utils;

import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class SpUtil {
	private static final String CONNECTOR = "@|";
	private static Map<String, SpUtil> _utils = new HashMap<String, SpUtil>();
	private SharedPreferences _sp;
	private Editor _editor;
	
	/**
	 * 构造函数
	 * 
	 * @param ctx
	 * @param fileName
	 * @param mode
	 */
	private SpUtil(Context ctx, String fileName, int mode) {
		this._sp = ctx.getSharedPreferences(fileName, mode);
		this._editor = _sp.edit();
	}

	/**
	 * 获取Sp的处理类对象
	 * @param ctx 
	 * @param fileName sp保存到文件，文件名
	 * @return
	 */
	public static SpUtil getInstance(Context ctx, String fileName){
		SpUtil spUtil = _utils.get(fileName);
		
		if (spUtil == null) {
			spUtil = new SpUtil(ctx, fileName, 2);
			_utils.put(fileName, spUtil);
		}
		return spUtil;
	}

	/**
	 * 写入带区域标识的多个键值对
	 * @param region
	 * @param kvs
	 * @throws IllegalArgumentException
	 */
	public void putMultiKv2Region(String region, Object[][] kvs)  throws IllegalArgumentException{
		if (StringUtils.isEmpty(region)) {
			putMultiKv(kvs);
		}
		
		for (Object[] kv : kvs) {
			if (checkParamPair(kv)) {
				put(concat(region, kv[0]), kv[1], false);
			}
		}
		_editor.commit();
	}
	
	/**
	 * 开放：写入多个键值对
	 * @param kvs 多个键值对 kvs = new Object[][]{{key1, value1}, {key2, valu2}, ...}
	 * @return
	 */
	public void putMultiKv(Object[][] kvs)  throws IllegalArgumentException {
		for (Object[]  kv : kvs) {
			put(kv[0], kv[1], false);
		}
		_editor.commit();
	}
	
	/**
	 * 开放：写入一个键值对
	 * @param key
	 * @param value
	 */
	public void putKv(Object key, Object value) throws IllegalArgumentException  {
		if (checkParam(key, value)) {
			executeWrite(String.valueOf(key), value, true);
		}
	}
	
	/**
	 * 写入带区域标识的单个键值对
	 * @param region
	 * @param key
	 * @param value
	 */
	public void putKv2Region(String region, Object key, Object value) {
		if (!StringUtils.isEmpty(region)) {
			putKv(concat(region, key), value);
		}
	}

	public Object getKvOfRegion(String region, Object key) throws IllegalArgumentException {
		if (StringUtils.isEmpty(region)) {
			throw new IllegalArgumentException("不合法的参数，region为空");
		}
		if (key == null || StringUtils.isEmpty(String.valueOf(key))) {
			return null;
		}
 		return get(concat(region, String.valueOf(key)));
	}
	
	public Object get(String key) {
		return getAll().get(key);
	}
	
	public Map<String, ?> getAll(){
		Map<String, ?> all = _sp.getAll();
		return all;
	}
	/**
	 * 获取region下的所有kv
	 * @param region
	 * @return
	 */
	public Map<String, ?> geMultiKvOfRegion(String region) {
		if (StringUtils.isEmpty(region)) {
			return null;
		}
		
		String partKey = region + CONNECTOR;
		Map<String, Object> result = new HashMap<String, Object>();
		
		for (Map.Entry<String, ?> entry : getAll().entrySet()){
			if (entry.getKey().startsWith(partKey)) {
				result.put(entry.getKey(), (Object)entry.getValue());
			}
		}
		return result;
	}
	
	public void remove(String region, String key) {
		if (StringUtils.isEmpty(region) || StringUtils.isEmpty(key)) {
			throw new IllegalArgumentException("不合法的参数，region或key为空");
		}
		
		remove(concat(region, key));
	}
	
	public void remove(String key) {
		_editor.remove(key);
		_editor.commit();
	}
	
	/**
	 * 内部：写入一个键值对
	 * @param key
	 * @param value
	 * @param commit 是否马上提交
	 * @throws IllegalArgumentException
	 */
	private void put(Object key, Object value, boolean commit) throws IllegalArgumentException  {
		if (checkParam(key, value)) {
			executeWrite(String.valueOf(key), value, commit);
		}
	}

	/**
	 * @param part1
	 * @param part2
	 * @return
	 */
	private String concat(String part1, Object part2) {
		return part1 + CONNECTOR + part2;
	}

	/**
	 * 多个键值对作检查
	 * @param kv
	 * @return
	 */
	private boolean checkParamPair(Object[] kv) throws IllegalArgumentException {
		if (kv == null || kv.length != 2) {
			throw new IllegalArgumentException("键值对列表不合法，为空或长度不是2");
		}
		return checkParam(kv[0], kv[1]);
	}
	
	/**
	 * 单个键值对作检查
	 * @param k
	 * @param v
	 */
	private boolean checkParam(Object k, Object v) throws IllegalArgumentException {
		if (k == null || v == null) {
			throw new IllegalArgumentException("键值对存在空元素");
		}
		return true;
	}

	/**
	 * 执行写入
	 * @param key
	 * @param value
	 * @param commit 是否马上提交
	 */
	private void executeWrite(String key, Object value, boolean commit ) {
		if (value instanceof Integer) {
			_editor.putInt(key, (Integer) value);
		} else if (value instanceof Boolean) {
			_editor.putBoolean(key, (Boolean) value);
		} else if (value instanceof String) {
			_editor.putString(key, (String) value);
		} else if (value instanceof Float) {
			_editor.putFloat(key, (Float) value);
		} else if (value instanceof Long) {
			_editor.putFloat(key, (Long) value);
		} else {
			_editor.putString(key, (String) value);
		}
		
		if (commit) {
			_editor.commit();
		}
	}	

	/**
	 * 清除整个文件内容
	 */
	public void clear() {
		_editor.clear();
		_editor.commit();
	}

	/**
	 * 清除指定key的记录
	 * 
	 * @param key
	 * @return
	 */
	public boolean contains(String key) {
		return _sp.contains(key);
	}
}
