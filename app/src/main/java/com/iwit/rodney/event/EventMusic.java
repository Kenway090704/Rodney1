package com.iwit.rodney.event;

import java.util.List;

import com.iwit.rodney.entity.Music;

public class EventMusic extends Event{
	List<Music> mMusicList;
	public EventMusic(int type, String msg, boolean result,List<Music> mMusicList){
		super(type,msg,result);
		this.mMusicList = mMusicList;
	}
	public List<Music> getmMusicList() {
		return mMusicList;
	}
	public void setmMusicList(List<Music> mMusicList) {
		this.mMusicList = mMusicList;
	}
}
