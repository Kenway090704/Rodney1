package com.iwit.rodney.service;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.AudioManager.OnAudioFocusChangeListener;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnBufferingUpdateListener;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.Binder;
import android.os.Build.VERSION;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v4.app.NotificationCompat.Builder;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.iwit.rodney.App;
import com.iwit.rodney.R;
import com.iwit.rodney.comm.CommConst;
import com.iwit.rodney.comm.tools.FileUtils;
import com.iwit.rodney.comm.utils.StringUtils;
import com.iwit.rodney.entity.Music;
import com.iwit.rodney.entity.Record;
import com.iwit.rodney.receiver.MediaButtonIntentReceiver;
import com.iwit.rodney.ui.activity.BaseActivity;
import com.iwit.rodney.ui.activity.HomeActivity;

/**
 * 
 * @author qh
 * @desc **因为aidl只能传输几个基础的数据类型 所以不能直接设置与界面互动的反调接口 所以需要service 自己设置回调接口
 *       **所以Service对象需要让界面获取调用
 * @desc 关于music的更新 例如面板可以直接更新 service里面的music 就行了
 * @date 20160520
 * 
 * 
 * 
 */
public class MusicManagerService extends Service implements
		OnBufferingUpdateListener, OnCompletionListener, OnPreparedListener {

	private boolean mReflectFlg = false;

	private static final Class<?>[] mSetForegroundSignature = new Class[] { boolean.class };
	private static final Class<?>[] mStartForegroundSignature = new Class[] {
			int.class, Notification.class };
	private static final Class<?>[] mStopForegroundSignature = new Class[] { boolean.class };

	private Method mSetForeground;
	private Method mStartForeground;
	private Method mStopForeground;
	private Object[] mSetForegroundArgs = new Object[1];
	private Object[] mStartForegroundArgs = new Object[2];
	private Object[] mStopForegroundArgs = new Object[1];

	public static final int ALL = 0;// 全部循环
	public static final int SINGLE = 1;// 单曲循环
	public static final int RANDOM = 2;// 随机
	private final static String TAG = "MusicManagerService";

	private Context mContext;
	public static MediaPlayer mPlayer;//方便全局提取状态  连接后过于缓慢
	private Map<String, OnMusicStateChange> mOnMusicStateChangeMap = new HashMap<String, OnMusicStateChange>();
	private String sdPath = Environment.getExternalStorageDirectory()
			.toString();
	// private static List<Music> mMusicItemList = new ArrayList<Music>();
	/**
	 * 做为时间定时器
	 */
	private int playMode = 0;// 播放的模式
//	private int mCurrentPos = 0;// 播放的位置
	private long duration = 1000;
	private long curTime = 0;
	private boolean run = true;
	private Handler mHandler = new Handler();
	private int oldPosition = 0;
	/**
	 * 每秒中更新状态 这里还可以优化 将事件分级处理
	 */
	Runnable updateSb = new Runnable() {

		@Override
		public void run() {
			//如果有音乐表 将
			if (mPlayer == null || MusicListControl.getInstance().isEmptyMusics()) {
				return;
			}
			Set<Map.Entry<String, OnMusicStateChange>> entrySet = mOnMusicStateChangeMap 
					.entrySet();
			Iterator<Map.Entry<String, OnMusicStateChange>> it = entrySet
					.iterator();
			Music mc = MusicListControl.getInstance().getCurMusic();
			if (mPlayer != null && mPlayer.isPlaying()) {
				curTime = mPlayer.getCurrentPosition();
				Log.v(TAG, "curTime::" + curTime);
			}
			boolean isuptate = false;
			if (MusicListControl.getInstance().getCurPosition() != oldPosition) {
				isuptate = true;
			}
			while (it.hasNext()) {
				Map.Entry<String, OnMusicStateChange> me = it.next();// 获取Map.Entry关系对象me
				String key2 = me.getKey();// 通过关系对象获取key
				OnMusicStateChange mOnMusicStateChange = me.getValue();// 通过关系对象获取value
				// 只更新最上面的一个接口
				if (mOnMusicStateChange != null) {
					if (mOnMusicStateChange != null) {
						mOnMusicStateChange.getMusicInformation(mc, playMode);
						mOnMusicStateChange.getMediaStatePlayer(
								getPlayerStatus(), playMode);
						mOnMusicStateChange.updateMusicStatus(curTime);
						if (isuptate) {
							Log.v(TAG, "mCurrentPos:" + MusicListControl.getInstance().getCurPosition());
							mOnMusicStateChange.updateMusic(MusicListControl.getInstance().getCurPosition());
							oldPosition = MusicListControl.getInstance().getCurPosition();
						}
					}
				}
			}
			mHandler.postDelayed(updateSb, 1000);
		}
	};


	@Override
	public IBinder onBind(Intent intent) {
		Toast.makeText(this, "绑定", 1).show();
		return new MsgBinder();
	}
	
	@Override
	public void onRebind(Intent intent) {
		Toast.makeText(this, "重新绑定", 1).show();
		super.onRebind(intent);
	}
	/**
	 * @author qh
	 * @desc 获取service对象 和aidl接口对象
	 */

	public class MsgBinder extends Binder {

		public MusicManagerService getService() {
			return MusicManagerService.this;
		}

		public IMusicManager.Stub getIMusicManager() {
			return mBinder;
		}
	}

	@Override
	public void onCreate() {
		super.onCreate();
		mContext = MusicManagerService.this;
		Log.v(TAG, "service onCreate");
		registerNotificationReceiver();
		createMusicPlayer();
		startTimeUpdate();
		com = new ComponentName(this, MediaButtonIntentReceiver.class);
		mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		initMethod();
		registerMediaButtonReciver();
	}

	private ComponentName com;
	private AudioManager mAudioManager;

	public void registerMediaButtonReciver() {
		// Toast.makeText(mContext, "注册了媒体按键广播.", 1).show();
		mAudioManager = ((AudioManager) getSystemService(AUDIO_SERVICE));
		mAudioManager.registerMediaButtonEventReceiver(com);
		mAudioManager.requestAudioFocus(new OnAudioFocusChangeListener() {

			@Override
			public void onAudioFocusChange(int focusChange) {
				// Toast.makeText(mContext, "焦点：" + focusChange, 1).show();

				switch (focusChange) {
				case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
					// Toast.makeText(mContext, "AUDIOFOCUS_LOSS_TRANSIENT", 1)
					// .show();
					break;
				case AudioManager.AUDIOFOCUS_LOSS:
					// Toast.makeText(mContext, "AUDIOFOCUS_LOSS", 1).show();
					break;
				case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
					// Toast.makeText(mContext,
					// "AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK", 1).show();
					break;
				case AudioManager.AUDIOFOCUS_GAIN:
					// Toast.makeText(mContext, "AUDIOFOCUS_GAIN", 1).show();
					break;
				}
			}
		}, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_REQUEST_GRANTED);
	}

	public void unregisterMediaButtonReciver() {
		// Toast.makeText(mContext, "取消了媒体按键广播.", 1).show();
		((AudioManager) getSystemService(AUDIO_SERVICE))
				.unregisterMediaButtonEventReceiver(com);
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		super.onStartCommand(intent, flags, startId);
		Log.d(TAG, "onStartCommand");
		return START_NOT_STICKY;
	}

	@Override
	public void onDestroy() {
		if (mPlayer != null) {
			MusicManagerService.this.mPlayer.release();
			MusicManagerService.this.mPlayer = null;
		}
		unregisterNotificationReceiver();
		unregisterMediaButtonReciver();
		stopForegroundCompat(NOTICE_ID);
		// Toast.makeText(this, "Service关闭了", Toast.LENGTH_SHORT).show();
		super.onDestroy();
	}

	@Override
	public void onPrepared(MediaPlayer mp) {
		Log.v(TAG, "onPrepared start");
		mp.start();
		duration = mp.getDuration();
			Music music = MusicListControl.getInstance().getCurMusic();
			if(music != null){
				initNotification(mContext, music.getMname(), mp);
			}
		updateDuration();
		// 更新
		Log.v(TAG, "onPrepared start");
	}

	// 接口
	private void updateDuration() {
		Set<Map.Entry<String, OnMusicStateChange>> entrySet = mOnMusicStateChangeMap
				.entrySet();
		Iterator<Map.Entry<String, OnMusicStateChange>> it = entrySet
				.iterator();

		while (it.hasNext()) {
			Map.Entry<String, OnMusicStateChange> me = it.next();// 获取Map.Entry关系对象me
			String key2 = me.getKey();// 通过关系对象获取key
			OnMusicStateChange mOnMusicStateChange = me.getValue();// 通过关系对象获取value
			// 只更新最上面的一个接口
			if (mOnMusicStateChange != null) {
				mOnMusicStateChange.updateDuration();
			}
		}
	}

	// 接口
	private void startLoadMusic() {
		Set<Map.Entry<String, OnMusicStateChange>> entrySet = mOnMusicStateChangeMap
				.entrySet();
		Iterator<Map.Entry<String, OnMusicStateChange>> it = entrySet
				.iterator();

		while (it.hasNext()) {
			Map.Entry<String, OnMusicStateChange> me = it.next();// 获取Map.Entry关系对象me
			String key2 = me.getKey();// 通过关系对象获取key
			OnMusicStateChange mOnMusicStateChange = me.getValue();// 通过关系对象获取value
			// 只更新最上面的一个接口
			if (mOnMusicStateChange != null) {
				mOnMusicStateChange.startLoadMusic();
			}
		}
	}

	public long getDuration() {
		return duration;
	}

	/**
	 * @desc playMode == ALL 循环播放 ***** playMode == RANDOM 随机播放 ***** playMode
	 *       == SINGLE 单曲循环 mCurrentPos不变
	 */
	@Override
	public void onCompletion(MediaPlayer mp) {
		int size = MusicListControl.getInstance().size();
		if (playMode == ALL) {
			if (MusicListControl.getInstance().getCurPosition() == size - 1) {
				MusicListControl.getInstance().setCurPosition(0);
			} else {
				MusicListControl.getInstance().addCurPos();
			}
		} else if (playMode == RANDOM) {
			MusicListControl.getInstance().setCurPosition((int) (Math.random() * size));
		}
		Log.e(TAG, "onCompletion mCurrentPos:" + MusicListControl.getInstance().getCurPosition());
		playUrlReset();
	}

	/**
	 * 
	 */
	@Override
	public void onBufferingUpdate(MediaPlayer mp, int percent) {

	}

	/**
	 * @desc 创建player
	 */
	private void createMusicPlayer() {
		try {
			if (mPlayer != null) {
				MusicManagerService.this.mPlayer.release();
				MusicManagerService.this.mPlayer = null;
			}
			mPlayer = new MediaPlayer();
			mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
			mPlayer.setOnBufferingUpdateListener(this);
			mPlayer.setOnPreparedListener(this);
			mPlayer.setOnCompletionListener(this);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * @param url
	 * @desc 播放音乐
	 */
	public void playMusic(String url, String name) {
		try {

			if (mPlayer == null) {
				return;
			}
			if (mPlayer.isPlaying())
				mPlayer.stop();
			mPlayer.reset();
			mPlayer.setDataSource(url);
			Log.e(TAG, "playMusic mCurrentPos:" + MusicListControl.getInstance().getCurPosition());
			initNotification(mContext, name, mPlayer);
			new Thread() {

				@Override
				public void run() {
					super.run();
					try {
						mPlayer.prepare();
					} catch (IllegalStateException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}

			}.start();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * @desc 恢复播放音乐
	 */
	private void resume1Music() {
		if (mPlayer != null) {
			mPlayer.start();
			Music music = MusicListControl.getInstance().getCurMusic(); 
			Log.e(TAG, "mPlayer.isPlaying():" + mPlayer.isPlaying());
			if(music != null){
				initNotification(mContext, music.getMname(), mPlayer);
			}
		}
	};

	/**
	 * @desc 暂停音乐
	 */
	public void pause1Music() {
		if (mPlayer != null && mPlayer.isPlaying()) {
			mPlayer.pause();
			Music music = MusicListControl.getInstance().getCurMusic();
			Log.e(TAG, "mPlayer.isPlaying():" + mPlayer.isPlaying());
			if(music != null){
				initNotification(mContext, music.getMname(), mPlayer);
			}
		}
	}

	/**
	 * @desc 前一首
	 */
	private void previousMusic() {
		MusicListControl.getInstance().previousPos();
		playUrlReset();
	}

	/**
	 * @desc 后一首
	 */
	private void next1Music() {
		MusicListControl.getInstance().nextPos();
		playUrlReset();
	}

	/**
	 * @desc 先要移除Runnable 才可以启动新的定时器 防止多次调用引起混乱
	 */
	private void startTimeUpdate() {
		mHandler.removeCallbacks(updateSb);
		mHandler.post(updateSb);
	}

	private void stopTimeUpdate() {
		mHandler.removeCallbacks(updateSb);
	}

	/**
	 * @desc 停止音乐
	 */
	@SuppressWarnings("static-access")
	public void stop() {
		if (mPlayer != null) {
			mPlayer.stop();
			MusicManagerService.this.mPlayer.release();
			MusicManagerService.this.mPlayer = null;
		}
	}

	/**
	 * @desc 释放音乐
	 */
	private void releaseMusic() {
		if (mPlayer != null) {
			MusicManagerService.this.mPlayer.release();
			MusicManagerService.this.mPlayer = null;
		}
		stopTimeUpdate();
	}

	/**
	 * @desc 指定音乐进度
	 * @param progress
	 */
	public void seek(int progress) {
		mPlayer.seekTo(progress);
	}

	/**
	 * @desc 获取播放状态
	 * @return
	 */
	public static boolean getPlayerStatus() {
		if (mPlayer == null) {
			return false;
		}
		if (mPlayer.isPlaying()) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * @desc 获取播放时间
	 * @return
	 */
	private int getMusicDuration() {
		return mPlayer == null ? 0 : mPlayer.getDuration();
	}

	/**
	 * 重新播放音乐
	 */
	private void playUrlReset() {
		if (MusicListControl.getInstance().isEmptyMusics()) {
			return; 
		}
		final Music music = MusicListControl.getInstance().getCurMusic();
		curTime = 0;
		if (music != null) {
			//如果是录音
			if (music.getIsrecord() != null && music.getIsrecord() == 1) {

				playMusic(
						sdPath + CommConst.RECORD_FILE_PATH + music.getMname()
								+ ".amr", music.getMname());
			} else {
				// 如果是未下载音乐
				if (music.getDownloaded() < 4) {
					if (music.getMid().startsWith("yb")) {
						playMusic(music.getMfname(), music.getMname());
					} else {
						playMusic(
								CommConst.SIT_ROOT_FILE_URL + music.getMfname(),
								music.getMname());
					}
					startLoadMusic();
				} else {
					pause1Music();
					if (music.getMid().startsWith("s1")) {
						new Handler().postDelayed(new Runnable() {
							
							@Override
							public void run() {
								playMusic(
										sdPath + CommConst.MUSIC_FILE_PATH
												+ music.getMfname(), music.getMname());
							}
						}, 1000);
						
					} else {
						new Handler().postDelayed(new Runnable() {
							
							@Override
							public void run() {
								playMusic(sdPath + CommConst.MUSIC_FILE_PATH
										+ FileUtils.getFilename(music.getMfname()),
										music.getMname());
							}
						}, 1000);
						
					}
				}
			}

		}
	}

	private long oldtime = 0;
	public final IMusicManager.Stub mBinder = new IMusicManager.Stub() {

		@Override
		public void updateMusic() throws RemoteException {

		}

		@Override
		public void setPlayMode(int model) throws RemoteException {
			playMode = model;
		}

		@Override
		public void seekMusic(int progress) throws RemoteException {
			seek(progress);
		}

		@Override
		public void resumeMusicNotification() throws RemoteException {

		}

		@Override
		public void resumeMusic() throws RemoteException {
			resume1Music();
		}

		@Override
		public void releaseMusicNotificaton() throws RemoteException {

		}

		@Override
		public void releaseMedia() throws RemoteException {
			releaseMusic();
		}

		@Override
		public void priorMusic() throws RemoteException {
			previousMusic();
			startTimeUpdate();
		}

		@Override
		public void preMusicNotification() throws RemoteException {

		}

		@Override
		public void playMusic(int pos) throws RemoteException {
			MusicListControl.getInstance().setCurPosition(pos);
			playUrlReset();
			startTimeUpdate();
		}

		@Override
		public void pauseMusic() throws RemoteException {
			pause1Music();
		}

		@Override
		public void nextMusisNotification() throws RemoteException {

		}

		@Override
		public void nextMusic() throws RemoteException {
			next1Music();
			startTimeUpdate();
		}

		@Override
		public void exit() throws RemoteException {

		}

		@Override
		public void stopMusic() throws RemoteException {
			stop();
		}
	};


	public void setmOnMusicStateChange(OnMusicStateChange mOnMusicStateChange,
			String activity) {
		mOnMusicStateChangeMap.put(activity, mOnMusicStateChange);
	}

	public void clearList() {
		MusicListControl.getInstance().clearList();
		if (mPlayer == null) {
			return;
		}
		if (mPlayer.isPlaying())
			mPlayer.stop();
	}


	// 将notification 放进去
	private static final int NOTICE_ID = 1566;
	private NotificationManager mNotificationManager;
	private Notification mNotification;
	public static final int PRIORER = 1000;
	public static final int RESUME = 2000;
	public static final int PAUSE = 3000;
	public static final int NEXT = 4000;
	public static final int CLOASE = 5000;
	private NotificationReceiver mNotificationReceiver;
	public final static String action = "com.iwit.receiver.notification";

	private void registerNotificationReceiver() {
		if (mNotificationReceiver == null) {
			mNotificationReceiver = new NotificationReceiver();
			IntentFilter filter = new IntentFilter();
			filter.addAction(action);
			registerReceiver(mNotificationReceiver, filter);
		}
	}

	private void unregisterNotificationReceiver() {
		if (mNotificationReceiver != null) {
			unregisterReceiver(mNotificationReceiver);
			mNotificationReceiver = null;
		}
	}

	public class NotificationReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			int action = intent.getIntExtra("action", 0);
			Log.e(TAG, "action::" + action);
			Log.e(TAG, "IsEmptyMusicList()::" + MusicListControl.getInstance().isEmptyMusics());
			long nowtime = System.currentTimeMillis();
			if (nowtime - oldtime < 200) {
				return;
			}
			oldtime = nowtime;
			switch (action) {
			case PRIORER:
				previousMusic();
				break;
			case RESUME:
				resume1Music();
				break;
			case PAUSE:
				pause1Music();
				break;
			case NEXT:
				next1Music();
				break;
			case CLOASE:
				mHandler.removeCallbacks(updateSb);
				mNotificationManager.cancel(NOTICE_ID);
				App.getInstance().exitApplication();
				stopSelf();
				break;
			}
		}

	}

	public NotificationManager initNotification(Context context, String name,
			MediaPlayer mediaplayer) {
		RemoteViews mRemoteViews = new RemoteViews(getPackageName(),
				R.layout.music_notification);

		// 进入主界面
		Intent homeIntent = new Intent(this, HomeActivity.class);
		homeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		PendingIntent pIntent = PendingIntent.getActivity(this, 0, homeIntent,
				0);
		mRemoteViews.setOnClickPendingIntent(R.id.ll_parent, pIntent);
		mRemoteViews.setTextViewText(R.id.title_music_name, name);
		if (mediaplayer.isPlaying()) {
			Intent pauseIntent = new Intent(action);
			pauseIntent.setAction(action);
			pauseIntent.putExtra("action", PAUSE);
			PendingIntent pausepi = PendingIntent.getBroadcast(this, 5,
					pauseIntent, PendingIntent.FLAG_UPDATE_CURRENT);
			mRemoteViews
					.setOnClickPendingIntent(R.id.paly_pause_music, pausepi);
			mRemoteViews.setImageViewResource(R.id.paly_pause_music,
					R.drawable.ic_music_play_pause);

		} else {
			Intent resumeIntent = new Intent(action);
			resumeIntent.putExtra("action", RESUME);
			PendingIntent pausepi = PendingIntent.getBroadcast(this, 6,
					resumeIntent, PendingIntent.FLAG_UPDATE_CURRENT);
			mRemoteViews
					.setOnClickPendingIntent(R.id.paly_pause_music, pausepi);
			mRemoteViews.setImageViewResource(R.id.paly_pause_music,
					R.drawable.ic_music_play_play);
		}

		Intent preIntent = new Intent(action);
		preIntent.putExtra("action", PRIORER);
		PendingIntent prepi = PendingIntent.getBroadcast(this, 1, preIntent,
				PendingIntent.FLAG_UPDATE_CURRENT);
		mRemoteViews.setOnClickPendingIntent(R.id.last_music, prepi);

		Intent nextIntent = new Intent(action);
		nextIntent.putExtra("action", NEXT);
		PendingIntent nextpi = PendingIntent.getBroadcast(this, 3, nextIntent,
				PendingIntent.FLAG_UPDATE_CURRENT);
		mRemoteViews.setOnClickPendingIntent(R.id.next_music, nextpi);

		Intent closeIntent = new Intent(action);
		closeIntent.putExtra("action", CLOASE);
		PendingIntent closepi = PendingIntent.getBroadcast(this, 4,
				closeIntent, PendingIntent.FLAG_UPDATE_CURRENT);
		mRemoteViews.setOnClickPendingIntent(R.id.close, closepi);

		Builder builder = new Builder(context);
		builder.setContent(mRemoteViews).setSmallIcon(R.drawable.ic_launcher)
				.setOngoing(true).setTicker("music is playing");
		mNotification = builder.build();
		startForegroundCompat(NOTICE_ID, mNotification);
		mNotification.contentView = mRemoteViews;
		return mNotificationManager;
	}

	private void initMethod() {
		try {
			mStartForeground = MusicManagerService.class.getMethod(
					"startForeground", mStartForegroundSignature);
			mStopForeground = MusicManagerService.class.getMethod(
					"stopForeground", mStopForegroundSignature);
		} catch (NoSuchMethodException e) {
			mStartForeground = mStopForeground = null;
		}
		try {
			mSetForeground = getClass().getMethod("setForeground",
					mSetForegroundSignature);
		} catch (NoSuchMethodException e) {
			throw new IllegalStateException(
					"OS doesn't have Service.startForeground OR Service.setForeground!");
		}
	}

	void invokeMethod(Method method, Object[] args) {
		try {
			method.invoke(this, args);
		} catch (InvocationTargetException e) {
			// Should not happen.
			Log.w("ApiDemos", "Unable to invoke method", e);
		} catch (IllegalAccessException e) {
			// Should not happen.
			Log.w("ApiDemos", "Unable to invoke method", e);
		}
	}

	void startForegroundCompat(int id, Notification notification) {
		if (mReflectFlg) {
			// If we have the new startForeground API, then use it.
			if (mStartForeground != null) {
				mStartForegroundArgs[0] = Integer.valueOf(id);
				mStartForegroundArgs[1] = notification;
				invokeMethod(mStartForeground, mStartForegroundArgs);
				return;
			}

			// Fall back on the old API.
			mSetForegroundArgs[0] = Boolean.TRUE;
			invokeMethod(mSetForeground, mSetForegroundArgs);
			mNotificationManager.notify(id, notification);
		} else {

			if (VERSION.SDK_INT >= 5) {
				startForeground(id, notification);
			} else {
				// Fall back on the old API.
				mSetForegroundArgs[0] = Boolean.TRUE;
				invokeMethod(mSetForeground, mSetForegroundArgs);
				mNotificationManager.notify(id, notification);
			}
		}
	}

	/**
	 * This is a wrapper around the new stopForeground method, using the older
	 * APIs if it is not available.
	 */
	void stopForegroundCompat(int id) {
		if (mReflectFlg) {
			if (mStopForeground != null) {
				mStopForegroundArgs[0] = Boolean.TRUE;
				invokeMethod(mStopForeground, mStopForegroundArgs);
				return;
			}
			mNotificationManager.cancel(id);
			mSetForegroundArgs[0] = Boolean.FALSE;
			invokeMethod(mSetForeground, mSetForegroundArgs);
		} else {
			if (VERSION.SDK_INT >= 5) {
				stopForeground(true);
			} else {
				mNotificationManager.cancel(id);
				mSetForegroundArgs[0] = Boolean.FALSE;
				invokeMethod(mSetForeground, mSetForegroundArgs);
			}
		}
	}
}
