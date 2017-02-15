package com.iwit.rodney.bussess;

import java.io.File;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import android.database.Cursor;
import android.os.Environment;
import android.util.Log;

import com.iwit.rodney.App;
import com.iwit.rodney.comm.CommConst;
import com.iwit.rodney.comm.utils.Json2BeanUtil;
import com.iwit.rodney.comm.utils.JsonUtil;
import com.iwit.rodney.comm.web.EnumLang;
import com.iwit.rodney.entity.Account;
import com.iwit.rodney.entity.Music;
import com.iwit.rodney.event.EventStory;
import com.iwit.rodney.exception.IwitApiException;
import com.iwit.rodney.interfaces.IManager;

import de.greenrobot.event.EventBus;

public class MusicDas {
	public static final String ALL_TYPE = "2,5";

	/**
	 * @description: 从服务器下载数据，保存到本地数据库 对比插入
	 * @param data
	 * @return
	 */
	public static String pushMusicTypeData2Db(String data) {
		String ret = "";
		try {
			Map<String, Object> mResult = JsonUtil.jsonString2Map(data);
			int result = Integer.parseInt((String.valueOf(mResult
					.get(CommConst.KEY_RESLUT))));
			if (result > 0) {
				Object jMtypes = mResult.get("mtypes");
				List<Map<String, Object>> mtypes = JsonUtil
						.object2Maps(jMtypes);
				int lid = EnumLang.getLidBaseAndroid();
				for (Map<String, Object> type : mtypes) {
					boolean insertsuccess = CommDas.insertOrUpdate(
							"lw_music_type",
							new Object[][] {
									{ "typeval", type.get("typeval") },
									{ "typename", type.get("typename") },
									{ "deleted", 0 }, { "lid", lid } },
							new String[] { "typeval", "lid" });
				}
			}
		} catch (IwitApiException e) {
			e.printStackTrace();
		}
		return ret;
	}

	/**
	 * 更改本地的字段
	 */
	public static boolean upDateMusicState(Music music, int downloaded) {
		// 排除掉录音
		if (music.getMid() != null) {
			CommDas.insertOrUpdate("lw_music",
					new Object[][] { { "mid", music.getMid() },
							{ "downloaded", downloaded } },
					new String[] { "mid" });
		}
		return true;
	}

	/**
	 * 如果是录音 则删除 是音乐表就更新为未下载状态
	 * 
	 * @param music
	 * @return
	 */
	public static boolean deleteLocalMusic(Music music) {
		String sql = "delete from lw_music where mid = ?";
		String id = music.getMid();
		try {
			App.getDbUtil().cud(sql, new Object[] { id });
		} catch (IwitApiException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		deleteNativeSource(music);
		return true;
	}

	public static void deleteNativeSource(Music music) {
		// 删除文件
		String sdPath = Environment.getExternalStorageDirectory().toString();
		String path = sdPath + CommConst.MUSIC_FILE_PATH + music.getMfname();
		File file = new File(path);
		if (file.isFile()) {
			file.delete();
		}
	}

	/**
	 * @description: 取类型
	 * @return
	 */
	public static List<Map<String, Object>> getMType() {
		List<Map<String, Object>> types = null;
		int lid = EnumLang.getLidBaseAndroid();
		String sql = "select * from lw_music_type where deleted = 0 and lid = ?";
		Cursor cur = App.getDbUtil().db
				.rawQuery(sql, new String[] { "" + lid });
		if (cur != null && cur.getCount() > 0) {
			types = new ArrayList<Map<String, Object>>();
			Map<String, Object> map = null;
			int fcount = cur.getColumnCount();
			while (cur.moveToNext()) {
				map = new HashMap<String, Object>();
				for (int i = 0; i < fcount; i++) {
					map.put(cur.getColumnName(i), cur.getString(i));
				}
				if (map.get("typename") != null
						&& !map.get("typename").equals("")) {
					types.add(map);
				}
			}
		}
		return types;
	}

	public static boolean UpdateDownloadMusic(Music music) {
		try {
			App.getInstance()
					.getDbUtil()
					.cud("update lw_music set downloaded = 4 where mid = ?",
							new Object[] { music.getMid() });
			return true;
		} catch (IwitApiException e) {
			e.printStackTrace();
		}
		return false;
	}

	public static boolean insertOrUpdateFavMusic(Music music) {
		String sql = "select * from lw_music where mid = ?";
		try {
			@SuppressWarnings("static-access")
			List<Music> musics = App
					.getInstance()
					.getDbUtil()
					.queryMulti(Music.class, sql,
							new Object[] { music.getMid() });
			// 如果不存在 主要用于搜索
			if (com.iwit.rodney.comm.utils.StringUtils.isListEmpty(musics)) {
				music.setMtype("0");
				insertMusicBean2Db(music);
			} else {
				CommDas.insertOrUpdate("lw_music", new Object[][] {
						{ "mid", music.getMid() }, { "f", music.getF() } },
						new String[] { "mid" });
			}
			return true;
		} catch (IwitApiException e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * 插入一个Bean
	 */
	public static boolean insertMusicBean2Db(Music music, String... args) {
		int lid = 32;
		if (music.getLid() == null) {
			lid = EnumLang.getLidBaseAndroid();// 语言
			if (args != null && args.length != 0) {
				lid = 32;
			}
		} else {
			lid = music.getLid();
		}
		boolean insertsuccess;
		if (music.getF() == null) {
			insertsuccess = CommDas.insertOrUpdate(
					"lw_music",
					new Object[][] { { "mid", music.getMid() },
							{ "mname", music.getMname() },
							{ "mfname", music.getMfname() },
							{ "author", music.getAuthor() },
							{ "performer", music.getPerformer() },
							{ "favourites", music.getFavourites() },
							{ "coverpic", music.getCoverpic() },
							{ "lyric", music.getLyric() },
							{ "recsupport", music.getRecsupport() },
							{ "mtype", music.getMtype() },
							{ "downloaded", music.getDownloaded() },
							{ "restype", music.getRestype() },
							{ "sort", music.getSort() },
							{ "ordinal", music.getOrdinal() } 
					},

					new String[] { "mid" });
		} else {
			insertsuccess = CommDas.insertOrUpdate(
					"lw_music",
					new Object[][] { { "mid", music.getMid() },
							{ "mname", music.getMname() },
							{ "mfname", music.getMfname() },
							{ "author", music.getAuthor() },
							{ "performer", music.getPerformer() },
							{ "f", music.getF() },
							{ "favourites", music.getFavourites() },
							{ "coverpic", music.getCoverpic() },
							{ "lyric", music.getLyric() },
							{ "recsupport", music.getRecsupport() },
							{ "mtype", music.getMtype() },
							{ "downloaded", music.getDownloaded() },
							{ "restype", music.getRestype() },
							{ "sort", music.getSort() },
							{ "ordinal", music.getOrdinal()}
					},
					new String[] { "mid" });
		}

		if (insertsuccess) {
			CommDas.insertOrUpdate("lw_music_language",
					new Object[][] { { "mid", music.getMid() }, { "lid", lid },
							{ "deleted", 0 } }, new String[] { "mid", "lid" });
			// 保存到type 列表中
			CommDas.insertOrUpdate("lw_music_type", new Object[][] {
					{ "mtype", music.getMtype() }, { "mid", music.getMid() },
					{ "deleted", 0 } }, new String[] { "mid", "mtype" });
		}
		return insertsuccess;
	}

	/**
	 * @description: 依据是否下载完成，检索出下载和未下载的
	 * @param mtypes
	 * @param mDownloaded
	 */
	public static List<Music> getMusics(String mtypes, int mDownloaded,
			int sort, String... args) {
		List<Music> musics = null;
		StringBuilder sb = new StringBuilder(
				"select m.mid, m.mtype, mname, f, favourites, author, lyric, mfname, sort, restype, performer, recsupport, downloaded, isrecord, ml.lid, ordinal , ");
		sb.append("coverpic from lw_music m, lw_music_language ml ,lw_music_type tl ");
		sb.append("where m.deleted = 0");
		sb.append(" and tl.mtype in (")
		.append(mtypes).append(")");
		sb.append(" and m.mtype in (")
				.append(mtypes)
				.append(") and downloaded >= ? and m.mid = ml.mid and m.mid = tl.mid ");
		Object[] objs;
		if (args != null && args.length != 0) {
			if ("all".equals(args[0])) {
				if (sort != 0) {
					sb.append(" and m.sort = ?");
					objs = new Object[] { mDownloaded, sort };
				} else {
					objs = new Object[] { mDownloaded };
				}
			} else {
				sb.append(" and ml.lid = ? ");
				if (sort != 0) {
					sb.append(" and m.sort = ?");
					objs = new Object[] { mDownloaded,
							EnumLang.getLidBaseAndroidBySubUrl(args[0]), sort };
				} else {
					objs = new Object[] { mDownloaded,
							EnumLang.getLidBaseAndroid() };
				}
			}
		} else {
			if (sort != 0) {
				sb.append(" and ml.lid = ? ");
				sb.append(" and m.sort = ?"); 
				objs = new Object[] { mDownloaded,
						EnumLang.getLidBaseAndroid(), sort };
			} else {
				sb.append(" and ml.lid = ? ");
				objs = new Object[] { mDownloaded, EnumLang.getLidBaseAndroid() };
			}
		}
		sb.append(" order by ordinal");
		try { 
			long old = System.currentTimeMillis();
			Log.v("timetest", "time1:" + old);
			musics = App.getDbUtil().queryMulti(Music.class, sb.toString(),
					objs);
			Log.v("timetest", "time2:" + System.currentTimeMillis());
			Log.e("timetest", "distime:" + (System.currentTimeMillis() - old));
		} catch (IwitApiException e) {
			e.printStackTrace();
		}
		if(args != null && args.length != 0 && "en".equals(args[0])){
			
		}else{
			removeActiveMusic(musics, null, mtypes);
		}
		
		for(Music music : musics){
			setMusicF( music);
		}
		if(mtypes.equals("1")){
			sortMusics(musics);
		}
		return musics;
	}

	public static List<Music> getRecSupportedMusics() {
		List<Music> musics = null;
		StringBuilder sb = new StringBuilder(
				"select m.mid, m.mtype, mname, f, favourites, author, lyric, mfname, sort, restype, performer, recsupport, downloaded, isrecord, ");
		sb.append("coverpic from lw_music m, lw_music_language ml ");
		sb.append("where m.deleted = 0 ");
		sb.append("and recsupport = 1 ");
		sb.append("and m.mid = ml.mid ");
		sb.append("and ml.lid = " + EnumLang.getLidBaseAndroid());
		try {
			musics = App.getDbUtil().queryMulti(Music.class, sb.toString());
		} catch (IwitApiException e) {
			e.printStackTrace();
		}
		return musics;
	}

	/**
	 * 从网络请求
	 * 
	 * @param js
	 * @param type
	 * @param sort
	 * @return
	 */
	public static List<Music> getMusicFromWeb(String js, final String type,
			final int sort, final String... args) {
		try {
			List<Music> mMusicList = Json2BeanUtil.jsonArrayString2Bean(
					Music.class, js, "music");
			if (mMusicList != null && !mMusicList.isEmpty()) {
				/*
				 * new Thread() {
				 * 
				 * @Override public void run() { super.run();
				 */

				try {
					App.getDbUtil().beginTranscation();
					for (Music music : mMusicList) {
						music.setF(null);
						if(type.equals(ALL_TYPE)){
							music.setMtype(music.getTypeid() ==null ? "0":""+music.getTypeid());
						}else{
							music.setMtype(type);
						}
						
						music.setSort(sort);
						if (args != null && args.length != 0) {
							music.setLid(32);
						} else {
							music.setLid(EnumLang.getLidBaseAndroid());
						}
						insertMusicBean2Db(music, args);
					}
					App.getDbUtil().setTransactionSuccessful();
				} catch (Exception e) {

				} finally {
					App.getDbUtil().endTransaction();
				}
				// 为节省时间，发给录音界面的通知暂时放在这里
				EventBus.getDefault().post(new EventStory(true, null));
				// }

				// }.start();

			}
			if (args != null && args.length != 0) {
				mMusicList = MusicDas.getMusics("" + type, 0, sort,
						new String[] { "en" });
			} else {
				mMusicList = MusicDas.getMusics("" + type, 0, sort);
				removeActiveMusic(mMusicList, sort, type);
			}

			
			for(Music music : mMusicList){
				setMusicF( music);
			}
			if(type.equals("1")){
				sortMusics(mMusicList);
			}
			return mMusicList;
		} catch (IwitApiException e) {
			e.printStackTrace();
		}
		return null;
	}

	private static void sortMusics(List<Music> mMusicList) {
		Collections.sort(mMusicList, new Comparator<Music>() {
		    @Override
		    public int compare(Music o1, Music o2) {
		        return Collator.getInstance(Locale.CHINESE).compare(o1.getMname(), o2.getMname());
		    }
		});
	}

	/**
	 * @type 删除type
	 * @param mMusicList
	 * @param sort
	 * @param type
	 */
	private static void removeActiveMusic(List<Music> mMusicList, Integer sort,
			String type) {
		if (mMusicList != null && !mMusicList.isEmpty()) {
			String userRetypes = IManager.getIAccount()
					.getCurrentAccountRestypes();
			Iterator<Music> miter = mMusicList.iterator();
			Music m;
			while (miter.hasNext()) {
				m = miter.next();
				if (sort != null) {
					m.setSort(sort);
				}
				if (type != null) {
					m.setMtype(type);
				}
				if (m.getRestype() == null
						|| StringUtils.isEmpty(m.getRestype())) {
					continue;
				}

				if (userRetypes.contains("b1") ) {
					if(!m.getRestype().equals("b1")){
						miter.remove();
					}
				}else{
					miter.remove();
				}
			}
		}
	}

	public static Music getMusic(String mid) {
		StringBuilder sb = new StringBuilder(
				"select m.mid, m.mtype, mname, f, favourites, author, lyric, mfname, sort, restype, performer, recsupport, downloaded, isrecord, ordinal , ");
		sb.append("coverpic from lw_music m, lw_music_language ml ");
		sb.append("where m.deleted = 0 ");
		sb.append("and m.mid = '" + mid + "' ");
		sb.append("and m.mid = ml.mid ");
		sb.append("and ml.lid = " + EnumLang.getLidBaseAndroid());
		try {
			return App.getDbUtil().queryFirst(Music.class, sb.toString());
		} catch (IwitApiException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static boolean updateCollection(Music music, Account account) {
		return CommDas.insertOrUpdate("lw_music_account", new Object[][] {
				{ "mid", music.getMid() }, { "uid", account.getUid() },
				{ "choose", music.getF() } }, new String[] { "mid", "uid" });
	}

	public static List<Music> getMusicListFav(int sort, String mtypes) {
		StringBuilder sb = new StringBuilder(
				"select m.mid, m.mtype, mname, f, favourites, author, lyric, mfname, sort, restype, performer, recsupport, downloaded, isrecord, ");
		sb.append("coverpic from lw_music m, lw_music_account mc ");
		sb.append("where m.deleted = 0 ");
		sb.append("and m.mid = mc.mid ");
		sb.append("and m.sort = " + sort + " ");
		sb.append(" and m.mtype in (").append(mtypes).append(")");
		sb.append(" and mc.choose = 1 ");
		sb.append(" and mc.uid = ").append(
				IManager.getIAccount().getCurrentUid());
		try {
			List<Music> mMusicList = (List<Music>) App.getDbUtil().queryMulti(Music.class,
					sb.toString());
			for(Music music : mMusicList){
				music.setF(1);
			}
			return mMusicList;
		} catch (IwitApiException e) {
			e.printStackTrace();
		}
		return null;
	}

	private static void setMusicF(Music music) {
		if (music != null) {
			StringBuilder sb = new StringBuilder(
					"select choose from lw_music_account ");
			sb.append("where mid = '").append(music.getMid()).append("' ");
			sb.append("and uid = ").append(
					IManager.getIAccount().getCurrentUid());
			Cursor cur = null;
			try {
				cur = App.getDbUtil().query(sb.toString());
				if (cur != null && cur.getCount() > 0) {
					while (cur.moveToNext()) {
						Integer f = cur.getInt(0);
						music.setF(f == null ? 0 : f);
					}
				}else{
					music.setF(0);
				}
			} catch (IwitApiException e) {
				e.printStackTrace();
			}finally{
				if(cur != null){
					cur.close();
					cur = null;
				}
			}
		}
	}
}
