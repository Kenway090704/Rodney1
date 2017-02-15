package com.iwit.rodney.db;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import com.iwit.rodney.exception.IwitApiException;

public class DBUtil {
	private final List EMPTY_LIST = new ArrayList(0);
	private static DBUtil util;

	public static SQLiteDatabase db = null;

	public static DBUtil getInstance(Context ctx, String dbName, int dbVersion) {
		if (util == null) {
			util = new DBUtil(ctx, dbName, dbVersion);
		}
		return util;
	}

	private DBUtil(Context ctx, String dbName, int dbVersion) {
		db = DBHelper.getInstance(ctx, dbName, dbVersion).getReadableDatabase();
	}

	public void beginTranscation() {
		if (db != null) {
			db.beginTransaction();
		}

	}

	public void setTransactionSuccessful() {
		if (db != null) {
			db.setTransactionSuccessful();
		}
	}

	public void endTransaction() {
		if (db != null) {
			db.endTransaction();
		}
	}
	/**
	 * 一般的查询  用于夺标查询结果 返回一个Cursor
	 * @param sql
	 * @param args
	 * @return
	 * @throws IwitApiException
	 */
	public Cursor query(String sql,Object... args) throws IwitApiException{
		Cursor cur = db.rawQuery(sql, convertArgs(args));
		return cur;
	}
	/**
	 * 查询第一�?br> (1)比如查记录是否存�?>select 1 from tab where bid =?<br>
	 * (2)查第�?��记录，并转化为bean->select a, b, ... from tab where ...
	 * 
	 * @param cl
	 * @param sql
	 * @return
	 */
	public <T> T queryFirst(Class<T> cl, String sql, Object... args)
			throws IwitApiException {
		List<T> list = queryMulti(cl, sql, true, args);
		return list == null || list.isEmpty() ? null : list.get(0);
	}

	/**
	 * 查询多行记录，并转化为bean的列
	 * 
	 * @param cls
	 *            指定返回类对
	 * @param sql
	 *            sqlite执行语句
	 * @param args
	 *            参数
	 * @return
	 * @throws IwitApiException
	 */
	public <T> List<T> queryMulti(Class<T> cls, String sql, Object... args)
			throws IwitApiException {
		return queryMulti(cls, sql, false, args);
	}

	private <T> List<T> queryMulti(Class<T> cl, String sql, boolean onlyOne,
			Object... args) throws IwitApiException {
		List<T> list = null;

		if (db.isOpen()) {
			Cursor cur = db.rawQuery(sql, convertArgs(args));
			if (cur != null && cur.getCount() > 0) {
				list = new ArrayList<T>(cur.getCount());
				Map<String, Object> mapFields = null;
				int fcount = cur.getColumnCount();
				int i = 0;
				T bean;

				while (cur.moveToNext()) {
					mapFields = new HashMap<String, Object>();

					for (i = 0; i < fcount; i++) {
						mapFields.put(cur.getColumnName(i), cur.getString(i));
					}
					bean = fillBean(cl, mapFields);
					list.add(bean);

					if (onlyOne) {
						break;
					}
				}
			}
			if(cur != null){
				cur.close();
				cur = null;
			}
		}
		if (list == null || list.isEmpty()) {
			return new ArrayList(0);
		}
		return list;
	}

	/**
	 * 增删改cud/create/update/delete
	 * 
	 * @param sql
	 *            执行语句，参数用通配�?代替，如insert into tab (name) values (?)
	 * @throws IwitApiException
	 */
	public int cud(String sql, Object... args) throws IwitApiException {
		if (db.isOpen()) {
			SQLiteStatement stmt = db.compileStatement(sql);

			if (args != null && args.length > 0) {
				for (int i = 0; i < args.length; i++) {
					bundleArg(stmt, i + 1, args[i]);
				}
			}
			return execStmt(sql, stmt);
		} else {
			return 0;
		}
	}

	/**
	 * 关闭数据�?
	 */
	public void closeDB() {
		if (null != db && db.isOpen()) {
			db.acquireReference();
			db.close();
		}
	}

	private final List<Class> _primitiveClasses = Arrays.asList(new Class[] {
			Long.class, Integer.class, String.class });

	private boolean _isPrimitive(Class<?> cls) {
		return cls.isPrimitive() || _primitiveClasses.contains(cls);
	}

	/**
	 * 将cursor中的数据填写到Bean�?
	 * 
	 * @param cls
	 * @param mapData
	 * @return
	 * @throws IwitApiException
	 */
	private <T> T fillBean(Class<T> cls, Map<String, Object> mapData)
			throws IwitApiException {
		T bean = null;

		if (_isPrimitive(cls)) {
			Object o;

			for (Map.Entry<String, Object> item : mapData.entrySet()) {
				o = mapData.get(item.getKey());
				bean = typeConvert(cls, o);
				break;
			}
		} else {
			try {
				bean = cls.newInstance();
				Field[] fs = cls.getDeclaredFields();
				String fName;
				Method m;

				for (Field f : fs) {
					fName = f.getName();

					if (mapData.containsKey(fName)) {
						m = cls.getMethod(getSetterName(fName),
								new Class[] { f.getType() });
						m.invoke(
								bean,
								new Object[] { typeConvert(f.getType(),
										mapData.get(fName)) });
					}
				}
			} catch (InstantiationException e) {
				throw new IwitApiException(
						"[DB模块]对bean赋值时，试图用构造方法创建对象，但目标类是接口或抽象类，创建失败", e);
			} catch (IllegalAccessException e) {
				throw new IwitApiException("[DB模块]对bean赋值时，但方法是私有的，处理失败", e);
			} catch (NoSuchMethodException e) {
				throw new IwitApiException("[DB模块]对bean赋值时，没有这个Setter方法，处理失败",
						e);
			} catch (IllegalArgumentException e) {
				throw new IwitApiException("[DB模块]对bean赋值时，但参数类型有误，处理失败", e);
			} catch (InvocationTargetException e) {
				throw new IwitApiException("[DB模块]对bean赋值时，内部出现有异常，处理失败", e);
			}
		}
		return bean;
	}

	/**
	 * 将string型转化为integer/long�?
	 * 
	 * @param target
	 * @param o
	 * @return
	 * @throws IwitApiException
	 */
	private <T> T typeConvert(Class<T> target, Object o)
			throws IwitApiException {
		try {
			if (o == null) {
				return null;
			}
			if (target.getName().equals(o.getClass().getName())) {
				return (T) o;
			}
			Constructor<T> con = target
					.getConstructor(new Class[] { String.class });
			T bean = con.newInstance((String) o);
			return bean;
		} catch (NoSuchMethodException e) {
			throw new IwitApiException(
					"[DB模块]将数据结果String转型时，目标类型没有string作为参数的构造方法，创建失败" + " o:"
							+ o, e);
		} catch (InstantiationException e) {
			throw new IwitApiException(
					"[DB模块]将数据结果String转型时，试图用构造方法创建对象，但目标类是接口或抽象类，创建失败" + " o:"
							+ o, e);
		} catch (IllegalAccessException e) {
			throw new IwitApiException(
					"[DB模块]将数据结果String转型时，试图用构造方法创建对象，但方法是私有的，创建失败" + " o:" + o,
					e);
		} catch (IllegalArgumentException e) {
			throw new IwitApiException(
					"[DB模块]将数据结果String转型时，试图用构造方法创建对象，但参数类型有误，创建失败" + " o:" + o,
					e);
		} catch (InvocationTargetException e) {
			throw new IwitApiException(
					"[DB模块]将数据结果String转型时，试图用构造方法创建对象，内部出现有异常，创建失败" + " o:" + o,
					e);
		}
	}

	/**
	 * 获得set方法
	 * 
	 * @param fName
	 * @return
	 */
	private String getSetterName(String fn) {
		String firstC = fn.substring(0, 1);
		return "set" + fn.replaceFirst(firstC, firstC.toUpperCase());
	}

	/**
	 * �?��传入的sql语句中的参数是否符合要求�?br> 1、不能为�? 2、只能是String/Long/Integer
	 * 
	 * @param args
	 * @return
	 * @throws IwitApiException
	 */
	private String[] convertArgs(Object[] args) throws IwitApiException {
		if (args == null || args.length == 0) {
			return null;
		}
		int len = args.length;
		String[] result = new String[len];
		Object o;

		for (int i = 0; i < len; i++) {
			o = args[i];

			if (checkArg(o, i)) {
				result[i] = String.valueOf(o);
			}
		}
		return result;
	}

	/**
	 * �?��sql参数的类别是否为string/long/integer
	 * 
	 * @param o
	 * @return
	 * @throws IwitApiException
	 */
	private boolean checkArg(Object o, int index) throws IwitApiException {
		if (o == null
				|| !((o instanceof String) || (o instanceof Integer) || (o instanceof Long))) {
			throw new IwitApiException("[DB模块]绑定sql时，第[" + index + "]个参数类型错误:"
					+ o.getClass().getName());
		}
		return true;
	}

	/**
	 * 执行cud(create/update/delete)语句
	 * 
	 * @param sql
	 * @param stmt
	 * @return
	 */
	@SuppressLint("NewApi")
	private int execStmt(String sql, SQLiteStatement stmt) {
		sql = sql.toLowerCase();

		if (sql.contains("insert")) {
			Long r = stmt.executeInsert();
			return r.intValue();
		} else if (sql.contains("delete") || sql.contains("update")) {
			return stmt.executeUpdateDelete();
		}
		return 0;
	}

	/**
	 * 绑定参数到stmt
	 * 
	 * @param stmt
	 * @param arg
	 * @throws IwitApiException
	 */
	private void bundleArg(SQLiteStatement stmt, int index, Object arg)
			throws IwitApiException {
		if (arg == null) {
			stmt.bindNull(index);
		} else if (arg instanceof String) {
			stmt.bindString(index, (String) arg);
		} else if (arg instanceof Long || arg instanceof Integer) {
			stmt.bindLong(index, Long.parseLong(String.valueOf(arg)));
		} else {
			throw new IwitApiException("[DB模块]bundleArg时，参数类型错误:"
					+ arg.getClass().getName());
		}
	}
}
