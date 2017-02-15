package com.iwit.rodney.comm.utils;

import java.io.File;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.widget.Toast;

/**
 * 单例模式
 */
public class MusicPlayer {
	private static MusicPlayer rmp;
	private Context context;
	private MediaPlayer mp;

	public MusicPlayer(Context context) {
		this.context = context;
	}

	public synchronized static MusicPlayer get(Context context) {
		if (rmp == null) {
			rmp = new MusicPlayer(context);
		}
		return rmp;
	}

	/**
	 * @param musicRawId--自带的资源Asset　中
     */
	public void start(int musicRawId) {
		if (musicRawId == 0) {
			stop();
			return;
		}
		AssetFileDescriptor fd = context.getResources().openRawResourceFd(
				musicRawId);
		if (fd == null) {
			throw new IllegalArgumentException("invalid music raw id");
		}
		if (mp != null && mp.isPlaying()) {
			stop();
		}
		mp = new MediaPlayer();
		try {
			mp.setDataSource(fd.getFileDescriptor(), fd.getStartOffset(),
					fd.getLength());
			mp.prepare();
			mp.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 这个是播放录音文件
	 * @param file
     */
	public void start(File file) {
		if (file == null) {
			throw new IllegalArgumentException();
		}
		if (!file.exists()) {
			Toast.makeText(context, "录音文件不存在", Toast.LENGTH_SHORT).show();
			return;
		}
		if (mp != null && mp.isPlaying()) {
			stop();
		}
		mp = new MediaPlayer();
		try {
			mp.setDataSource(file.getAbsolutePath());
			mp.prepare();
			mp.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void pause() {
		if (mp != null && mp.isPlaying()) {
			mp.pause();
		}
	}

	public void resume() {
		if (mp != null) {
			mp.start();
		}
	}

	public void stop() {
		if (mp != null) {
			mp.stop();
			mp.release();
			mp = null;
		}
	}

	public void release() {
		stop();
		rmp = null;
	}
}
