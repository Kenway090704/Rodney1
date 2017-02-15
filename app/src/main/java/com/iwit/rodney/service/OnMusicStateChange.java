package com.iwit.rodney.service;

import java.util.List;
import com.iwit.rodney.entity.Music;

public interface OnMusicStateChange {
	public abstract void startLoadMusic();
	public abstract void updateDuration();
	public abstract void updateMusic(int position);//更改list和pos的位置 例如下一首时候 列表显示和底部展示一致
	public void getMusicInformation(Music music,int playMode);//展示music歌词图片播放时间和播放模式
	public void updateMusicStatus(long currentTime);//获取播放时间
	public void getMediaStatePlayer(boolean state,int playMode);//获取media播放状态  暂停中还是播放中；获取播放模式
}
