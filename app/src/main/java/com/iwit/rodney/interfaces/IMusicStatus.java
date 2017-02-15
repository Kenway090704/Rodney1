package com.iwit.rodney.interfaces;

import java.util.List;

import com.iwit.rodney.entity.Music;


public interface IMusicStatus {
	public void getMusicItemList(List<Music> musicList,int position);
	public void getMusicTitleAndDuration(Music music,long duration,int playMode);
	public void updateMusicStatus(long currentTime);
	public void getMediaStatus(boolean status,int mode);
}
