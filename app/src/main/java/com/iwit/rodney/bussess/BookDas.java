package com.iwit.rodney.bussess;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import android.util.Log;

import com.iwit.rodney.App;
import com.iwit.rodney.comm.CommConst;
import com.iwit.rodney.comm.utils.JsonUtil;
import com.iwit.rodney.comm.web.EnumLang;
import com.iwit.rodney.entity.Book;
import com.iwit.rodney.exception.IwitApiException;
import com.iwit.rodney.interfaces.IManager;

public class BookDas {
	private static final String T = "BookDas";

	/**
	 * 从服务器下载数据，保存到本地数据库 对比插入 需要插入主表 和对应的语言关系表
	 * 
	 * @param data
	 * @return
	 */
	public static String pushBookData2Db(String data) {
		String ret = "";

		try {
			Map<String, Object> mResult = JsonUtil.jsonString2Map(data);
			int result = Integer.parseInt((String.valueOf(mResult
					.get(CommConst.KEY_RESLUT))));
			if (result > 0) { // 取到数据
				saveBooks(mResult);

			}
			ret = (String) mResult.get("synctime");
			Log.e(T, "ret:" + ret);
		} catch (IwitApiException e) {
			ret = "";
			e.printStackTrace();
		}
		return ret;
	}

	public static void saveBooks(Map<String, Object> mResult) {
		Object jaBooks = mResult.get("books");// 得到book的数据
		List<Map<String, Object>> mBooks = null;
		try {
			mBooks = JsonUtil.object2Maps(jaBooks);
		} catch (IwitApiException e) {
			e.printStackTrace();
		}
		if (mBooks == null) {
			return;
		}
		int lid = EnumLang.getLidBaseAndroid();// 语言
		for (Map<String, Object> mBook : mBooks) {
			// 插入书籍
			boolean insertBookSuccess = CommDas.insertOrUpdate(
					"lw_book",
					new Object[][] { { "bid", mBook.get("bid") },
							{ "bname", mBook.get("bname") },
							{ "bfname", mBook.get("bfname") },
							{ "size", mBook.get("size") },
							{ "bdesc", mBook.get("bdesc") },
							{ "coverpic", mBook.get("coverpic") },
							{ "rackpic", mBook.get("rackpic") },
							{ "p1", mBook.get("p1") },
							{ "p2", mBook.get("p2") },
							{ "p3", mBook.get("p3") },
							{ "p4", mBook.get("p4") },
							{ "restype", mBook.get("restype") },
							{ "sid", mBook.get("sid") },
							{ "deleted", mBook.get("deleted") } },
					new String[] { "bid" });
			// 如果书籍插入成功 那么插入语言对应关系
			if (insertBookSuccess) {
				CommDas.insertOrUpdate("lw_book_language", new Object[][] {
						{ "bid", mBook.get("bid") }, { "lid", lid },
						{ "deleted", mBook.get("deleted") } }, new String[] {
						"bid", "lid" });
			}
		}
	}

	/**
	 * 查询后的结果 要判断是否为空 empty 遍历的时候最好用 for(Object o : objectlist){}形式
	 * 
	 * @param mSid
	 * @param restype
	 * @param mDownloaded
	 * @return
	 */
	public static List<Book> getBooks(String restype, int mDownloaded) {

		List<Book> books = null;
		StringBuilder sb = new StringBuilder(
				"select b.bid,  bname, bdesc, bfname, coverpic, p1, p2, p3, p4, size, restype, ");
		sb.append("rackpic from lw_book b, lw_book_language bl ");
		sb.append("where b.deleted = 0");
		sb.append(" and downloaded >= ? and b.bid = bl.bid and bl.lid = ?");
		try {
			books = App.getDbUtil().queryMulti(Book.class, sb.toString(),
					new Object[] { mDownloaded, EnumLang.getLidBaseAndroid() });
		} catch (IwitApiException e) {
			// TODO Auto-generated catch block
			Log.e(T, "查询书籍数据异常：" + e.getMessage());
		}
		if (books != null && !books.isEmpty()) {
			String userRetypes = IManager.getIAccount().getCurrentAccountRestypes();
			Iterator<Book> miter = books.iterator();
			Book b;
			Log.v("qh", "userRetypes:" + userRetypes);
			while (miter.hasNext()) {

				b = miter.next();
				Log.v("qh", "b.getRestype():" + b.getRestype());
				if (b.getRestype() == null
						|| StringUtils.isEmpty(b.getRestype())) {
					continue;
				}
				if (userRetypes.contains("b1") ) {
					if(!b.getRestype().equals("b1")){
						miter.remove();
					}
				}else{
					miter.remove();
				}
			}
		}
		return books;
	}

	/**
	 * 更新书籍的下载状态 下载完成后
	 * 
	 * @param bid
	 * @param downloaded
	 */
	public static void updateBookDownState(Integer bid, Integer downloaded) {
		try {
			App.getDbUtil().cud(
					"update lw_book set downloaded = ? where bid =  ?",
					new Object[] { downloaded, bid });
		} catch (IwitApiException e) {
			Log.e(T, "更新书籍的下载状态异常：" + e.getMessage());
		}
	}

	/**
	 * 获取当前登录用户能访问的资源类型集合
	 * 
	 * @return a0a1...
	 */

	public static String getCurrentUserRestypes() {
		Object isTabletActived = App.getSp().getKvOfRegion(
				CommConst.SP_REGION_ISTABLETACTIVED,
				App.getSp().get(CommConst.SP_KEY_CURRENT_USER_UID));

		if (isTabletActived == null
				|| CommConst.DEV_NO_ACTIVATE == (Integer) isTabletActived) {
			return CommConst.RESTYPE_FREE;
		}
		Object restypes = App.getSp().get(CommConst.SP_KEY_CURRENT_RESTYPES);

		if (restypes == null) {
			return CommConst.RESTYPE_FREE;
		}
		return String.valueOf(restypes);
	}

	/**
	 * 获取当前用户是否登录
	 */
	public static boolean isLogin() {
		Object ouid = App.getSp().get(CommConst.SP_KEY_CURRENT_USER_UID);
		if (ouid != null) {
			return true;
		}
		return false;
	}

	/**
	 * 从SP获取上次发送请求的时间，用于增量请求，这个时间的[保存(请求完书籍后要回写保存)、获取]都要依据sid/language区分开，防串用<br>
	 * 否则服务器依据这两个参数作过滤查询，只是取到部分数据，如果synctime串用，下次改变这两个参数，<br>
	 * 将取不到相应的数据，因为synctime已经往后推了。
	 * 
	 * @return
	 */
	public static String getLastSyncTime() {
		Object lastSyncTime = App.getSp().getKvOfRegion(
				CommConst.SP_REGION_SYNCTIME, EnumLang.getLidBaseAndroid());
		return lastSyncTime == null ? "0" : String.valueOf(lastSyncTime);
	}

	/**
	 * 将当前请求服务器返回的同步时间存到sp中，参数涵义参见getLastSyncTime方法
	 * 
	 * @param sid
	 * @param lid
	 * @param syncTime
	 */
	public static void updateSynctime(String syncTime) {

		App.getSp().putKv2Region(CommConst.SP_REGION_SYNCTIME,
				EnumLang.getLidBaseAndroid(), syncTime);
	}
}
