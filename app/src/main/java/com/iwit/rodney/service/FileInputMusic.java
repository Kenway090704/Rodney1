package com.iwit.rodney.service;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.iwit.rodney.comm.utils.StringUtils;
import com.iwit.rodney.entity.Music;

public class FileInputMusic {
	Context context;

	public FileInputMusic(Context context) {
		this.context = context;
	}

	public void saveMusics(List<Music> musics) {
		SharedPreferences sp = context.getSharedPreferences("rondey_musics",
				Context.MODE_PRIVATE);
		for(Music music :musics){
			music.toInteger();
		}
		// 先将List转为json 字符串
		Gson gson = new GsonBuilder().serializeNulls().create();
		String jsonstr = gson.toJson(musics);
		sp.edit().putString("musics", jsonstr).commit();
	}

	public void saveCurPos(int pos) {
		SharedPreferences sp = context.getSharedPreferences("rondey_musics",
				Context.MODE_PRIVATE);
		sp.edit().putInt("pos", pos).commit();
	}

	public int getCurPos() {
		SharedPreferences sp = context.getSharedPreferences("rondey_musics",
				Context.MODE_PRIVATE);
		return sp.getInt("pos", 0);
	}

	private int getJsonInteger(JSONObject object, String key)
			throws JSONException {
		String str = String.valueOf(object.get(key));
		if (StringUtils.isEmpty(str)) {
			return 0;
		}
		return Integer.parseInt(str);
	}

	public List<Music> getMusics() {
		try {
			SharedPreferences sp = context.getSharedPreferences(
					"rondey_musics", Context.MODE_PRIVATE);
			String jsonstr = sp.getString("musics", null);
			if (!StringUtils.isEmpty(jsonstr)) {
				List<Music> musics = new ArrayList<Music>();
				Music music;
				JSONObject jotemp;
				JSONArray jas = new JSONArray(jsonstr);
				for (int i = 0; i < jas.length(); i++) {
					jotemp = jas.getJSONObject(i);
					music = new Music();
					String coverpic = String.valueOf(jotemp.get("coverpic"));
					music.setCoverpic(coverpic);
					Integer downloaded = getJsonInteger(jotemp, "downloaded");
					music.setDownloaded(downloaded);
					 Integer f = getJsonInteger(jotemp,"f");
					 music.setF(f);
					Integer favourites = getJsonInteger(jotemp, "favourites");
					music.setFavourites(favourites);
					int ischoose = getJsonInteger(jotemp, "ischoose");
					music.setIschoose(ischoose);
					Integer isrecord = getJsonInteger(jotemp, "isrecord");
					music.setIsrecord(isrecord);
					Integer lid = getJsonInteger(jotemp, "lid");
					music.setLid(lid);
					String lyric = String.valueOf(jotemp.get("lyric"));
					music.setLyric(lyric);
					String mfname = String
							.valueOf(jotemp.get("mfname") == null ? "" : jotemp
									.get("mfname"));
					music.setMfname(mfname);
					String mname = String.valueOf(jotemp.get("mname"));
					music.setMname(mname);
					String mid = String.valueOf(jotemp.get("mid") == null ?"":jotemp.get("mid"));
					music.setMid(mid);
					String mtype = String.valueOf(jotemp.get("mtype") == null ?"":jotemp.get("mtype"));
					music.setMtype(mtype);
					Integer recsupport = getJsonInteger(jotemp, "recsupport");
					music.setRecsupport(recsupport);
					Integer sort = getJsonInteger(jotemp, "sort");
					music.setSort(sort);
					String typeid = String.valueOf(jotemp.get("typeid") == null ? "":jotemp.get("typeid"));
					music.setTypeid(typeid);
					musics.add(music);
				}
				return musics;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
