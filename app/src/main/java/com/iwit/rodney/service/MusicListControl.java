package com.iwit.rodney.service;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.widget.Toast;

import com.iwit.rodney.App;
import com.iwit.rodney.comm.utils.StringUtils;
import com.iwit.rodney.entity.Music;
import com.iwit.rodney.entity.Record;

public class MusicListControl {
	private List<Music> musics = new ArrayList<Music>();
	private static MusicListControl mMusicListControl;// 单例模式
	private int curPosition = 0;

	public static MusicListControl getInstance() {
		if (null == mMusicListControl) {
			mMusicListControl = new MusicListControl();
		}
		return mMusicListControl;
	}

	public List<Music> getMusics() {
		return musics;
	}

	public void setMusics(List<Music> musiclist) {
		this.musics = musiclist;
		new Thread(new Runnable() {

			@Override
			public void run() {
				new FileInputMusic(App.ctx).saveMusics(musics);
			}
		}).start();
		
	}

	public boolean setSavedMusic(final Context ctx) {
		if (StringUtils.isListEmpty(musics)) {
			new Thread(new Runnable() {

				@Override
				public void run() {
				FileInputMusic manager = new FileInputMusic(ctx);
					List<Music> list = manager.getMusics();
					if (!StringUtils.isListEmpty(list)) {
						musics = list;
					}
					setCurPosition(manager.getCurPos());
				}
			}).start();
			return true;
		}
		return false;
	}

	public int getCurPosition() {
		return curPosition;
	}

	public void setCurPosition(int curPosition) {
		this.curPosition = curPosition;
		if(this.curPosition < 0){
			this.curPosition = 0; 
		}
		savePos();
	}
	private void savePos(){
		FileInputMusic manager = new FileInputMusic(App.ctx);
		manager.saveCurPos(curPosition);
	}
	public void addCurPos() {
		curPosition++;
		savePos();
	}

	public void delCurPos() {
		curPosition--;
		if(curPosition < 0){
			curPosition = 0;
		}
		savePos();
	}

	public void previousPos() {
		curPosition = curPosition - 1 < 0 ? size() - 1 : curPosition - 1;
		if(curPosition < 0){
			curPosition = 0;
		}
		savePos();
	}

	public void nextPos() {
		curPosition = (curPosition + 1) % size();
		savePos();
	}

	public void setRecords(List<Record> records) {
		List<Music> musics = new ArrayList<Music>();
		for (Record r : records) {
			Music music = r.toMusic();
			musics.add(music);
		}
		setMusics(musics);
	}

	public boolean isEmptyMusics() {
		return StringUtils.isListEmpty(musics);
	}

	public int size() {
		return musics == null ? 0 : musics.size();
	}

	public Music getCurMusic() {
		if (!isEmptyMusics() && curPosition < size()) {
			return musics.get(curPosition);
		}
		return null;
	}
	public void removeMusic(){
		if(!StringUtils.isListEmpty(musics) && musics.size() >= curPosition){
			musics.remove(curPosition);
		}
		new Thread(new Runnable() {

			@Override
			public void run() {
				new FileInputMusic(App.ctx).saveMusics(musics);
			}
		}).start();
	}
	
	public void clearList() {
		if (!isEmptyMusics()) {
			musics.clear();
		}
	}
}
