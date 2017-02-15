package com.iwit.rodney.interfaces;

import java.util.List;

import android.app.DownloadManager;

import com.iwit.rodney.entity.Music;

public interface IMusic {
	@Deprecated
	public void getMusicByType(String type,int sort);//如果种类都是确定的，可能废止此方法
	public void getMusicByType(String type,int sort,String en);
	public void getSearchMusicList(String where,int sort);
	public void updateMusic(Music music);//更新一个音乐状态
	public void deleteMusic(Music music,DownloadManager manager);//删除一个音乐 两个数据库同时删除
	/**
	 * 注意 这里的对比方式不是对比app持有的book 来判断音乐或者书籍的下载状态 而是通过
	 * 原生的下载数据库来判定下载的状态 
	 */
	public void getMusicListFromWeb();//获取网络的音乐列表
	
	public List<Music> getMusicListFromDb(String types,int sort,int download,String... args);//获取本地的音乐列表
	public void updateFavorite(String v,String mid);
	
	public boolean updateCollection(Music music);
	public List<Music> getMusicListFavirate(int sort);
}
