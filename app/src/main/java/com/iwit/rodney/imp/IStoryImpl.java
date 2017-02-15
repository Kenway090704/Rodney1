package com.iwit.rodney.imp;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.Header;

import com.iwit.rodney.App;
import com.iwit.rodney.bussess.MusicDas;
import com.iwit.rodney.comm.CommConst;
import com.iwit.rodney.comm.utils.JsonUtil;
import com.iwit.rodney.comm.web.EnumLang;
import com.iwit.rodney.comm.web.WebRequest;
import com.iwit.rodney.entity.Music;
import com.iwit.rodney.entity.Story;
import com.iwit.rodney.event.EventStory;
import com.iwit.rodney.exception.IwitApiException;
import com.iwit.rodney.interfaces.IManager;
import com.iwit.rodney.interfaces.IStory;
import com.loopj.android.http.AsyncHttpResponseHandler;

import de.greenrobot.event.EventBus;

public class IStoryImpl implements IStory {
	private long lastRequestTime;

	@Override
	public List<Story> getStories() {
		List<Music> ms = MusicDas.getRecSupportedMusics();
		boolean isrequest = requestForNewStory();
		if(!isrequest){
			EventBus.getDefault().post(new EventStory(false, null));
		}
		if (ms == null || ms.size() < 1) {
			return null;
		}
		int size = ms.size();
		List<Story> ss = new ArrayList<Story>();
		for (int i = 0; i < size; i++) {
			Music m = ms.get(i);
			ss.add(fillStory(m));
		}
		return ss;
	}

	private Story fillStory(Music m) {
		Story s = new Story();
		s.setName(m.getMname());
		s.setPicName(m.getCoverpic());
		s.setMid(m.getMid());
		s.setLrcName(m.getLyric());
		return s;
	}

	private boolean requestForNewStory() {
		if (System.currentTimeMillis() - lastRequestTime < 30 * 60 * 1000)
			return false;

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
							lastRequestTime = System.currentTimeMillis();
							MusicDas.getMusicFromWeb(result, MusicDas.ALL_TYPE,
									1);
							return;
						}
						EventBus.getDefault().post(new EventStory(false, msg));
					}

					@Override
					public void onFailure(int arg0, Header[] arg1, byte[] arg2,
							Throwable erro) {
						EventBus.getDefault().post(
								new EventStory(false, "网络状态不佳，请稍后重试"));
					}
				},
				CommConst.REQUEST_MUSICLIST_METHOD,
				new String[][] {
						{
								"lang",
								EnumLang.getSubUrlByCountryCode(App
										.getCountryCode()) },
						{ "synctime", "0" }, { "sort", "1" },
						{ "uid", IManager.getIAccount().getCurrentUid() } });
		return true;
	}

	@Override
	public Story getStoryByMid(String mid) {
		Music m = MusicDas.getMusic(mid);
		return fillStory(m);
	}

}
