package com.iwit.rodney.comm.tools;

import android.content.Context;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.os.Handler;
import android.util.Log;

/**
 * 因为都是播放短暂的音乐文件 所以只需要播放 和 停止(释放)
 * @author Administrator
 *
 */
public class PlayMedia {
	private Context mContext;
	private static PlayMedia mPlayMedia;
	private MediaPlayer mMediaPlayer;
	private int i = 0;
	private int Id;
	/**
	 * 构造函数
	 * @param mContext
	 */
	public PlayMedia(Context mContext){
		this.mContext=mContext;
	} 
	
	/**
	 * 单例
	 * @param mContext
	 * @return
	 */
	public static PlayMedia getInstance(Context mContext){
		if(null==mPlayMedia){
			mPlayMedia = new PlayMedia(mContext);
		}
		return mPlayMedia;
	}
	
	/**
	 * 播放音乐
	 * @param id
	 */
	public void playMusicMedia(int  id){
		this.Id = id;
		try {
			if(mMediaPlayer!=null && mMediaPlayer.isPlaying()){
				mMediaPlayer.stop();
				mMediaPlayer.release();
				mMediaPlayer=null;
			}
			mMediaPlayer = MediaPlayer.create(mContext, id);
			mMediaPlayer.setLooping(true);//循环播放---Kenway 2,14
			mMediaPlayer.setVolume(2.0f, 2.0f);
			mMediaPlayer.start();
			//这一段是为了什么?
			new Handler().postDelayed(new Runnable() {
				@Override
				public void run() {
					if(mMediaPlayer!=null && mMediaPlayer.isPlaying()){
						mMediaPlayer.stop();
						mMediaPlayer.release();
						mMediaPlayer=null;
					}
				}
			}, 15000);
		} catch (IllegalStateException e) {
			e.printStackTrace();
		}
		//当播放中发生错误的时候回调
		mMediaPlayer.setOnErrorListener(new OnErrorListener() {
			
			@Override
			public boolean onError(MediaPlayer mp, int what, int extra) {
				Log.e("mMediaPlayer", "what::"+what);
				mp.reset();
				return false;
			}
		});
		mMediaPlayer.setOnCompletionListener(new OnCompletionListener() {
			@Override
			public void onCompletion(MediaPlayer mp) {
			}
		});
		
	}
	
	/**
	 * 人为的释放音乐
	 */ 
	public void releaseMediaPlayer(){
		if(mMediaPlayer!=null && mMediaPlayer.isPlaying()){
			mMediaPlayer.stop();
			mMediaPlayer.release();
			mMediaPlayer=null;
		}
	}
}
