/**
 * copyright @ iwit,. android team. 2014-2015
 */
package com.iwit.rodney.bussess;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

import android.util.Log;

import com.iwit.rodney.App;
import com.iwit.rodney.exception.IwitApiException;

/**
 * 公有的data access service
 * @author tao
 *
 */
public class CommDas {
	private final static String T = "CommDas";
	
	/**
	 * 查询是否存在符合条件的记录，注意：只能处理键值划等号的条件。
	 * @param tableName 表名
	 * @param filterKvs 过滤条件，键值对kv，键k为列名，v为值，比如new Object[][]{{"bid", 1}, {"lid", 2}}
	 * @return
	 */
	public static boolean isRowExists(String tableName, Object[]... filterKvs){ 
		if (StringUtils.isEmpty(tableName)) {
			Log.e(T, "请求isRowExists，非法的参数tableName");
			return true;
		}
		StringBuilder sb = new StringBuilder("select 1 from ").append(tableName);
		List<Object> listFilterV = null;
		
		if (filterKvs != null) {
			listFilterV = new ArrayList<Object>(filterKvs.length);
			sb.append(genWhereSql(filterKvs, listFilterV));
		}
		try {	
			Integer exists = App.getDbUtil().queryFirst(Integer.class, sb.toString(), (listFilterV == null ? null : listFilterV.toArray()));
			return (exists != null) && (exists.intValue() > 0);
		} catch (IwitApiException e) {
			Log.e(T, e.getMessage());
			return true;//有异常，情况不明认为存在
		}
	}
	
	/**
	 * 生成where条件的sql语句，同时将通配符?对应的值加到list中
	 * @param filterKvs where条件项
	 * @param filterV 将通配符?对应的值加到list中
	 * @return
	 */
	private static String genWhereSql(Object[][] filterKvs, List<Object> filterV){
		StringBuilder sb = new StringBuilder();
		//加过滤条件
		if (filterKvs != null) {
			sb.append(" where 1 = 1");
					
			for (Object[] arg : filterKvs) {
				if (arg == null || arg.length != 2) {
					Log.e(T, "请求genWhereSql，非法的参数kvs");
				} else {
					sb.append(" and ").append(arg[0]).append(" = ?");
					filterV.add(arg[1]);
				}
			}
		}
		return sb.toString();
	}
	
	/**
	 * 插入或更新记录，当记录不存在插入，存在则更新。<br>
	 * 调用示例：CommDas.insertOrUpdate("ee_book", new Object[][]{{"bid", 1}, {"name", "tao"}}, new Object[]{"bid"});
	 * @param tableName 目标表
	 * @param cuItems create/update相关的数据项，为单或多个键值对，比如new Object[][]{{"bid", 1}, {"name", "猴子捞月亮"}...}
	 * @param unqiueByFields 过滤条件的字段名数组，是判断是记录是否存在的过滤条件。决定记录唯一的字段组，以上cuItems插入是的书籍表，则unqiueByFields为new Object[]{"bid"}，它里面的元素是字段名
	 * 又比如ee_book_image表，<br>
	 * 它需要bid/imagetype决定这条记录是否存在，虽然实际上biid也能确认一条唯一性，但前者更严密<br>
	 * 这样unqiueByField就应该是new Object[]{"bid", "imagetype"}
	 * @return 返回这条插入或更新是否成功
	 */
	public static boolean insertOrUpdate(String tableName, Object[][] cuItems, Object[] unqiueByFields) {
		if (StringUtils.isEmpty(tableName)) {
			Log.e(T, "请求insertOrUpdate参数非法，tableName为空");
			return false;
		}
		if (ArrayUtils.isEmpty(cuItems)) {
			Log.e(T, "请求insertOrUpdate参数非法，cuItems为空");
			return false;
		}
		if (ArrayUtils.isEmpty(unqiueByFields)) {
			Log.e(T, "请求insertOrUpdate参数非法，unqiueByFields为空");
			return false;
		}
		int itemNum = cuItems.length;
		StringBuilder sbUpdate = new StringBuilder("update ").append(tableName).append(" set ");
		
		List<Object> inOrUpParams = new ArrayList<Object>(); //用于更新的或插入参数
		List<Object[]> listFilterKvs = new ArrayList<Object[]>();//用于决定一条纪录的唯一性
		
		List<String> insertFields = new ArrayList<String>(itemNum);//用于保存插入的字段名
		List<String> insertFieldsChar = new ArrayList<String>(itemNum);//插入字段对应的通配符
		
		for (Object[] cuItem : cuItems) { 
			if (cuItem == null || cuItem.length != 2) {
				Log.e(T, "请求insertOrUpdate，非法的参数cuItem");
			} else {
				
				//当前插入或更新字段同时也是唯一键，则加入到数组，等待用作查询纪录是否存在
				if (ArrayUtils.contains(unqiueByFields, cuItem[0])) {
					listFilterKvs.add(cuItem);
				}
				sbUpdate.append(cuItem[0]).append(" = ?").append(",");
				
				//为生成插入的Sql语句
				insertFields.add(String.valueOf(cuItem[0]));//添加字段名
				insertFieldsChar.add("?");
				
				inOrUpParams.add(cuItem[1]);
			}
		}
		Object[][] kvsArray = null; //决定纪录的唯一性的一个或多个条件对
		
		if (listFilterKvs != null && listFilterKvs.size() > 0) {
			kvsArray = new Object[listFilterKvs.size()][2];
		
			for (int i = 0; i < listFilterKvs.size(); i++) {
				kvsArray[i] = listFilterKvs.get(i);
			}
		}
		String sql;
		
		//查询记录是否存在，跑了一次genWhereSql
		if (isRowExists(tableName, kvsArray)) {//存在即更新
			 //删除最后的,号
			sbUpdate.deleteCharAt(sbUpdate.length() - 1);
			String sWhere = null;
			List<Object> subWhereParams = null;
			
			if (kvsArray != null) {
				subWhereParams = new ArrayList<Object>();
				//又要跑一次genWhereSql，没办法啊，因为isRowExists要公开
				sWhere = genWhereSql(kvsArray, subWhereParams);
			}
			if (!StringUtils.isEmpty(sWhere)) {
				sbUpdate.append(sWhere);
				inOrUpParams.addAll(subWhereParams);
			}
			sql = sbUpdate.toString();
		} else {//不存在即插入
			StringBuilder sbInsert = new StringBuilder("insert into ").append(tableName);
			sbInsert.append("(").append(StringUtils.join(insertFields, ", ")).append(")");
			sbInsert.append("values (").append(StringUtils.join(insertFieldsChar, ", ")).append(")");
			sql = sbInsert.toString();
		}
		try {
			App.getDbUtil().cud(sql, inOrUpParams.toArray());
			Log.i(T, "insertOrUpdate方法成功执行一次插入或更新操作: " + sql);
		} catch (IwitApiException e) {
			Log.e(T, "insertOrUpdate方法异常，执行插入或更新异常：" + e.getMessage());
			return false;
		}
		return true;
	}
	/**
	 * 插入新记录，当记录不存在插入，存在则不操作。<br>
	 * @param tableName
	 * @param cuItems
	 * @param unqiueByFields
	 * @return
	 */
	public static boolean insertIfNotExists(String tableName, Object[][] cuItems, Object[] unqiueByFields){
		if (StringUtils.isEmpty(tableName)) {
			Log.e(T, "请求insertIfNotExists参数非法，tableName为空");
			return false;
		}
		if (ArrayUtils.isEmpty(cuItems)) {
			Log.e(T, "请求insertIfNotExists参数非法，cuItems为空");
			return false;
		}
		if (ArrayUtils.isEmpty(unqiueByFields)) {
			Log.e(T, "请求insertIfNotExists参数非法，unqiueByFields为空");
			return false;
		}
		int itemNum = cuItems.length;
		
		List<Object> inParams = new ArrayList<Object>(); //用于保存插入的参数值
		List<Object[]> listFilterKvs = new ArrayList<Object[]>();//用于决定一条纪录的唯一性
		
		List<String> insertFields = new ArrayList<String>(itemNum);//用于保存插入的字段名
		List<String> insertFieldsChar = new ArrayList<String>(itemNum);//用于保存插入字段对应的通配符
		
		for (Object[] cuItem : cuItems) { 
			if (cuItem == null || cuItem.length != 2) {
				Log.e(T, "请求insertIfNotExists，非法的参数cuItem");
			} else {
				
				//当前插入字段同时也是唯一键，则加入到数组，等待用作查询纪录是否存在
				if (ArrayUtils.contains(unqiueByFields, cuItem[0])) {
					listFilterKvs.add(cuItem);
				}
				//为生成插入的Sql语句
				insertFields.add(String.valueOf(cuItem[0]));//添加字段名
				insertFieldsChar.add("?");
				inParams.add(cuItem[1]);
			}
		}
		Object[][] kvsArray = null; //决定纪录的唯一性的一个或多个条件对
		
		if (listFilterKvs != null && listFilterKvs.size() > 0) {
			kvsArray = new Object[listFilterKvs.size()][2];
		
			for (int i = 0; i < listFilterKvs.size(); i++) {
				kvsArray[i] = listFilterKvs.get(i);
			}
		}
		String sql;
		//查询记录是否存在，跑了一次genWhereSql
		if (isRowExists(tableName, kvsArray)) {//存在不操作
			return false;
		} else {//不存在即插入
			StringBuilder sbInsert = new StringBuilder("insert into ").append(tableName);
			sbInsert.append("(").append(StringUtils.join(insertFields, ", ")).append(")");
			sbInsert.append("values (").append(StringUtils.join(insertFieldsChar, ", ")).append(")");
			sql = sbInsert.toString();
			try {
				int result = App.getDbUtil().cud(sql, inParams.toArray());
				Log.i(T, result + "insertIfNotExists方法成功执行一次插入或更新操作: " + sql);
			} catch (IwitApiException e) {
				Log.e(T, "insertIfNotExists方法异常，执行插入或更新异常：" + e.getMessage());
				return false;
			}
		}
		return true;
	}
}
