package com.iwit.rodney.imp;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.http.Header;

import android.app.DownloadManager;
import android.util.Log;
import android.widget.Toast;
import com.iwit.rodney.App;
import com.iwit.rodney.R;
import com.iwit.rodney.bussess.MusicDas;
import com.iwit.rodney.comm.CommConst;
import com.iwit.rodney.comm.utils.Json2BeanUtil;
import com.iwit.rodney.comm.utils.JsonUtil;
import com.iwit.rodney.comm.utils.StringUtils;
import com.iwit.rodney.comm.web.EnumLang;
import com.iwit.rodney.comm.web.WebRequest;
import com.iwit.rodney.entity.Account;
import com.iwit.rodney.entity.Music;
import com.iwit.rodney.event.Event;
import com.iwit.rodney.event.EventMusic;
import com.iwit.rodney.exception.IwitApiException;
import com.iwit.rodney.interfaces.IManager;
import com.iwit.rodney.interfaces.IMusic;
import com.loopj.android.http.AsyncHttpResponseHandler;

public class IMusicImp implements IMusic {
	private final static String TAG = "IMusicImp";
	private final static long disTime = 5 * 60 * 1000;
	private String erroMsg = "网络连接错误";
	public Map<String, Boolean> map = new HashMap<String, Boolean>();
	public long enTime = 0;

	private boolean JudgeIsNewMap(final String type, final int sort,
			long currentTime, Set keys) {
		boolean isRequest = true;
		if (keys != null) {
			Iterator iterator = keys.iterator();
			// 不继续添加
			while (iterator.hasNext()) {
				String key = String.valueOf(iterator.next());
				String[] sql = key.split("@");
				int temSort = Integer.parseInt(sql[0]);
				String temType = String.valueOf(sql[1]);
				long temTime = Long.parseLong(sql[2]);
				if (temType != null && temSort == sort
						&& temType.contains(type)
						&& temTime - currentTime < disTime) {
					isRequest = false;
				}
			}
		}
		return isRequest;
	}

	private void updateMapType(final String type, final int sort,
			long currentTime, Set keys) {
		String types = "";
		String tempRemoveKey = null;
		if (keys != null) {
			Iterator iterator = keys.iterator();
			// 不继续添加
			while (iterator.hasNext()) {
				String key = String.valueOf(iterator.next());
				String[] sql = key.split("@");
				int temSort = Integer.parseInt(sql[0]);
				types = String.valueOf(sql[1]);
				if (sort == temSort) {
					tempRemoveKey = key;
					String[] now = type.split(",");
					for (String ty : now) {
						if (!types.contains(ty)) {
							types += ("," + ty);
						}
					}
				}
			}
		}
		if (tempRemoveKey != null) {
			map.remove(tempRemoveKey);
		}
		map.put(sort + "@" + types + "@" + currentTime, true);
	}

	@Override
	public void getMusicByType(final String type, final int sort) {
		final long currentTime = System.currentTimeMillis();
		final Set keys = map.keySet();
		final boolean isRequest = JudgeIsNewMap(type, sort, currentTime, keys);
		if (!isRequest) {
			List<Music> mMusicList = MusicDas.getMusics("" + type, 0, sort);
			postMusicList2UI(true,mMusicList, Event.EVENT_GET_MUSIC_BY_TYPE,
					"not request from web");
			return;
		}
		WebRequest.requestFromWeb(
				new AsyncHttpResponseHandler() {

					@Override
					public void onSuccess(int statusCode, Header[] arg1,
							byte[] responseBody) {
						String result = new String(responseBody);
						String msg = "null";
						try {
							msg = String.valueOf(JsonUtil
									.jsonString2Map(result).get("msg"));
						} catch (IwitApiException e) {
							e.printStackTrace();
						}
						if (statusCode == 200) {
							Log.v(TAG, "result::" + result);
							updateMapType(type, sort, currentTime, keys);
							List<Music> mMusicList = MusicDas.getMusicFromWeb(
									result, "" + type, sort);
							postMusicList2UI(true,mMusicList,
									Event.EVENT_GET_MUSIC_BY_TYPE, msg);
						} else {
							List<Music> mMusicList = MusicDas.getMusics(""
									+ type, 0, sort);
							postMusicList2UI(true,mMusicList,
									Event.EVENT_GET_MUSIC_BY_TYPE, msg);
						}
					}

					@Override
					public void onFailure(int arg0, Header[] arg1, byte[] arg2,
							Throwable erro) {
						List<Music> mMusicList = MusicDas.getMusics("" + type,
								0, sort);
						postMusicList2UI(false,mMusicList,
								Event.EVENT_GET_MUSIC_BY_TYPE, erroMsg);

					}
				},
				CommConst.REQUEST_MUSICLIST_METHOD,
				new String[][] {
						{
								"lang",
								EnumLang.getSubUrlByCountryCode(App
										.getCountryCode()) },
						{ "mtypes", type }, { "synctime", "0" },
						{ "sort", String.valueOf(sort) },
						{ "uid", IManager.getIAccount().getCurrentUid() } });

	}

	@Override
	public void updateMusic(Music music) {
		MusicDas.insertOrUpdateFavMusic(music);
	}

	@Override
	public void deleteMusic(Music music, DownloadManager manager) {

	}

	@Override
	public void getMusicListFromWeb() {
		// TODO Auto-generated method stub

	}

	@Override
	public List<Music> getMusicListFromDb(String types, int sort, int download,
			String... args) {
		return MusicDas.getMusics(types, download, sort, args);
	}

	// 将结果集分类处理
	private void postMusicList2UI(boolean istrue,List<Music> mMusicList, int eventType,
			String msg) {
		Event event;
		if (StringUtils.isListEmpty(mMusicList)) {
			if(istrue){
				event = new Event(eventType, App.getStringR(R.string.empty_list),
						false);
			}else{ 
				event = new Event(eventType, App.getStringR(R.string.request_exception_list),
						false);
			}
			
		} else {
			event = new EventMusic(eventType, "success", true, mMusicList);
		}
		event.postEventToUI();
	}

	@Override
	public void getSearchMusicList(String where, final int sort) {
		WebRequest.requestFromWeb2(new AsyncHttpResponseHandler() {

			@Override
			public void onSuccess(int statusCode, Header[] arg1,
					byte[] responseBody) {
				String result = new String(responseBody);
				String msg = "erro";
				try {
					msg = String.valueOf(JsonUtil.jsonString2Map(result).get(
							"msg"));

				} catch (IwitApiException e) {
					e.printStackTrace();
				}
				if (statusCode == 200) {

					Log.v(TAG, "result::" + result);
					List<Music> mMusicList = null;
					try {
						mMusicList = Json2BeanUtil.jsonArrayString2Bean(
								Music.class, result, "music");
						if (!StringUtils.isListEmpty(mMusicList)) {
							for (Music music : mMusicList) {
								music.setMtype(music.getTypeid());
								music.setSort(sort);
							}
							postMusicList2UI(true,mMusicList,
									Event.EVENT_GET_MUSIC_BY_SEARCH, msg);
						} else {
							Event event = new Event(
									Event.EVENT_GET_MUSIC_BY_SEARCH, msg, false);
						}
						// 添加sort和type
					} catch (IwitApiException e) {
						e.printStackTrace();
						postMusicList2UI(true,mMusicList,
								Event.EVENT_GET_MUSIC_BY_SEARCH, msg);
					}
				} else {
					postMusicList2UI(false,null, Event.EVENT_GET_MUSIC_BY_SEARCH, msg);
				}
			}

			@Override
			public void onFailure(int arg0, Header[] arg1, byte[] arg2,
					Throwable erro) {
				postMusicList2UI(false,null, Event.EVENT_GET_MUSIC_BY_SEARCH, erroMsg);

			}
		}, CommConst.REQUEST_MUSICLIST_METHOD, new String[][] {
				{ "key", where }, 
//				{ "sort", String.valueOf(sort) },
				{ "uid", IManager.getIAccount().getCurrentUid() } });
	}

	@Override
	public void updateFavorite(String v, String mid) {
		WebRequest.requestFromWeb(new AsyncHttpResponseHandler() {

			@Override
			public void onSuccess(int statusCode, Header[] arg1,
					byte[] responseBody) {
				Event event;
				try {
					String msg = String.valueOf(JsonUtil.jsonString2Map(
							new String(responseBody)).get("msg"));
					if (statusCode == 200) {
						event = new Event(Event.EVENT_UPDATE_FAV, msg, true);
					} else {
						event = new Event(Event.EVENT_UPDATE_FAV, msg, false);
					}
				} catch (IwitApiException e) {
					event = new Event(Event.EVENT_UPDATE_FAV, e.getMessage(),
							false);
				}
				event.postEventToUI();
			}

			@Override
			public void onFailure(int arg0, Header[] arg1, byte[] arg2,
					Throwable erro) {
				Event event = new Event(Event.EVENT_UPDATE_FAV, erroMsg, false);
				event.postEventToUI();
			}
		}, CommConst.SEND_MUSIC_FAVORITE, new String[][] {
				{ "uid", IManager.getIAccount().getCurrentUid() },
				{ "mid", mid }, { "v", v }, { "lang", App.getCountryCode() } });
	}

	@Override
	public void getMusicByType(final String type, final int sort,
			final String en) {
		long nowTime = System.currentTimeMillis();
		if (nowTime - enTime < 5 * 60 * 1000) {
			List<Music> mMusicList = MusicDas.getMusics("" + type, 0, sort,
					new String[] { "en" });
			postMusicList2UI(true,mMusicList, Event.EVENT_GET_MUSIC_BY_TYPE,
					"succuss");
		}
		WebRequest.requestFromWeb2(new AsyncHttpResponseHandler() {

			@Override
			public void onSuccess(int statusCode, Header[] arg1,
					byte[] responseBody) {
				String result = new String(responseBody);
				String msg = "null";
				try {
					msg = String.valueOf(JsonUtil.jsonString2Map(result).get(
							"msg"));
				} catch (IwitApiException e) {
					e.printStackTrace();
				}
				if (statusCode == 200) {
					Log.v(TAG, "result::" + result);

					List<Music> mMusicList = MusicDas.getMusicFromWeb(result,
							"" + type, sort, new String[] { "en" });
					postMusicList2UI(true,mMusicList, Event.EVENT_GET_MUSIC_BY_TYPE,
							msg);
				} else {
					List<Music> mMusicList = MusicDas.getMusics("" + type, 0,
							sort, new String[] { "en" });
					postMusicList2UI(true,mMusicList, Event.EVENT_GET_MUSIC_BY_TYPE,
							msg);
				}
			}

			@Override
			public void onFailure(int arg0, Header[] arg1, byte[] arg2,
					Throwable erro) {
				List<Music> mMusicList = MusicDas.getMusics("" + type, 0, sort,
						new String[] { "en" });
				postMusicList2UI(false,mMusicList, Event.EVENT_GET_MUSIC_BY_TYPE,
						erroMsg);

			}
		}, CommConst.REQUEST_MUSICLIST_METHOD, new String[][] {
				{ "lang", "en" }, { "mtypes", type }, { "synctime", "0" },
				{ "sort", String.valueOf(sort) },
				{ "uid", IManager.getIAccount().getCurrentUid() } });
	}

	@Override
	public boolean updateCollection(Music music) {

		Account account = IManager.getIAccount().getCurrentAccount();
		if (account == null) {
			return false;
		}
		boolean success = MusicDas.updateCollection(music, account);
		return success;
	}

	@Override
	public List<Music> getMusicListFavirate(int sort) {

		return null;
	}
}
