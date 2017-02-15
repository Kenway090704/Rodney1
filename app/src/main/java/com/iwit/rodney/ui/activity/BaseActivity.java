package com.iwit.rodney.ui.activity;

import java.util.List;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.iwit.rodney.App;
import com.iwit.rodney.R;
import com.iwit.rodney.comm.CommConst;
import com.iwit.rodney.comm.tools.PlayMediaInstance;
import com.iwit.rodney.comm.utils.StringUtils;
import com.iwit.rodney.comm.web.WebRequest;
import com.iwit.rodney.entity.Music;
import com.iwit.rodney.entity.Record;
import com.iwit.rodney.event.Event;
import com.iwit.rodney.service.IMusicManager;
import com.iwit.rodney.service.MusicListControl;
import com.iwit.rodney.service.MusicManagerService;
import com.iwit.rodney.service.OnMusicStateChange;

import de.greenrobot.event.EventBus;

/**
 * 
 * @author qh
 * @version 1.0
 * @desc 基础类主要是用于音乐播放面板状态更新
 * @add 可以将service接口回调方法分离 分为分界面使用和公共使用部分
 */
public abstract class BaseActivity extends FragmentActivity implements
		OnClickListener, OnMusicStateChange {
	private IMusicManager mIMusicManager;
	private MusicManagerService msgService;

	private static final String TAG = "BaseActivity";
	private LinearLayout mLiMusicPannel;
	private ImageView mIvMusicIcon;
	private ImageView mIvMusicPrior;
	private ImageView mIvMusicPlay;
	private ImageView mIvMusicNext;
	private TextView mTvMusicName;
	private TextView mTvMusicType;
	private boolean state;
	private int[] cds = new int[] { R.drawable.ic_cd1, R.drawable.ic_cd2,
			R.drawable.ic_cd3 };

	protected String getName() {
		return this.getClass().getSimpleName();
	}

	private boolean isConnected;
	private ServiceConnection conn = new ServiceConnection() {

		@Override
		public void onServiceDisconnected(ComponentName name) {
			Log.v(TAG, "service is disconnected");
		}

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			Log.v(TAG, "service is connected");
			// Toast.makeText(BaseActivity.this, ""+getName(), 1).show();
			msgService = ((MusicManagerService.MsgBinder) service).getService();
			mIMusicManager = IMusicManager.Stub.asInterface(msgService.mBinder);
			initServiceData();
			initMusicView();
		}
	};

	private void bindService() {
		Intent intent = new Intent(this, MusicManagerService.class);
		isConnected = getApplicationContext().bindService(intent, conn,
				Context.BIND_AUTO_CREATE);
		Log.v(TAG, "" + isConnected);
	}

	private Context ctx;

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		ctx = this;
		App.getInstance().addActivity(this);
		bindService();
		EventBus.getDefault().register(this);
	}

	@Override
	protected void onResume() {
		super.onResume();
		initMusicView();
	}

	public void onEventMainThread(Event event) {
		switch (event.getType()) {
		case Event.EVENT_MUSIC_STOP:
			try {
				if (mIMusicManager != null) {
					mIMusicManager.pauseMusic();
				}

			} catch (RemoteException e) {
				e.printStackTrace();
			}
			break;
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		EventBus.getDefault().unregister(this);
		App.getInstance().removeActivity(this);
		if (isConnected) {
			getApplicationContext().unbindService(conn);
		}
	}

	public IMusicManager getmIMusicManager() {
		return mIMusicManager;
	}

	public MusicManagerService getMsgService() {
		return msgService;
	}

	public void clearService() {
		if (msgService != null) {
			msgService.clearList();
		}
	}

	/**
	 * 继承之后必须调用此函数
	 */
	public void initPanelView() {
		mLiMusicPannel = (LinearLayout) findViewById(R.id.li_music_pannel);
		mIvMusicIcon = (ImageView) findViewById(R.id.iv_music_icon);
		mIvMusicPrior = (ImageView) findViewById(R.id.iv_music_prior);
		mIvMusicPlay = (ImageView) findViewById(R.id.iv_music_play);
		mIvMusicNext = (ImageView) findViewById(R.id.iv_music_next);
		mTvMusicName = (TextView) findViewById(R.id.tv_music_name);
		mTvMusicType = (TextView) findViewById(R.id.tv_music_type);

		mLiMusicPannel.setOnClickListener(this);
		mIvMusicPrior.setOnClickListener(this);
		mIvMusicNext.setOnClickListener(this);
		mIvMusicPlay.setOnClickListener(this);

	}

	public void initServiceData() {
		Log.v(TAG, "msgService::" + msgService);
		if (msgService != null) {
			Log.v(TAG, "msgService" + getName());
			msgService.setmOnMusicStateChange(this, getName());
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.li_music_pannel:
			startActivity(new Intent(this, MusicPlayActivity.class));
			break;
		case R.id.iv_music_prior:
			try {
				mIMusicManager.priorMusic();
			} catch (Exception e) {
				e.printStackTrace();
			}
			break;
		case R.id.iv_music_play:
			try {
				if (CommConst.isSet) {
					mIMusicManager.playMusic(MusicListControl.getInstance()
							.getCurPosition());
					CommConst.isSet = false;
				} else {

					if (state) {
						mIMusicManager.pauseMusic();
					} else {
						mIMusicManager.resumeMusic();
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			PlayMediaInstance.getInstance(App.ctx).releaseMediaPlayer();
			break;
		case R.id.iv_music_next:
			try {
				mIMusicManager.nextMusic();
			} catch (Exception e) {
				e.printStackTrace();
			}
			break;
		}

	}

	private int degree = 0;
	private Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			mIvMusicIcon.setRotation(degree);
		}

	};
	private boolean isLooper = false;
	private Thread mThread;

	private void startAnimation() {
		// 旋转
		isLooper = true;
		if (mThread == null) {
			mThread = new Thread(runnable);
			mThread.start();
		}
	}

	private void stopAnimation() {
		// 旋转
		if (mThread != null) {
			isLooper = false;
			degree = 0;
			mThread = null;
		}

	}

	Runnable runnable = new Runnable() {

		@Override
		public void run() {
			while (isLooper) {
				degree = degree + 15;
				handler.sendEmptyMessage(0);
				try {
					Thread.sleep(70);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	};

	private void initMuscNameAndType(Music music) {
		if (music == null) {
			stopAnimation();
			mTvMusicName.setText("");
			mTvMusicType.setText("");
			return;
		}
		mTvMusicName.setText(music.getMname());
		mTvMusicType.setText(music.getMtype());
		if (music.getSort() == null) {
			return;
		}
		// TODU稍后需要修改
		if (music.getSort() == 1) {
			if (music.getMtype().equals("1")) {
				mTvMusicType.setText("儿歌");
			} else if (music.getMtype().equals("2")) {
				mTvMusicType.setText("儿歌");
			} else {
				mTvMusicType.setText("儿歌");
			}
		} else if (music.getMtype().equals("10")) {
			mTvMusicType.setText("录音");
		} else {
			if (music.getMtype().equals("2")) {
				mTvMusicType.setText("童话故事");
			} else if (music.getMtype().equals("3")) {
				mTvMusicType.setText("动物百科");
			} else if (music.getMtype().equals("4")) {
				mTvMusicType.setText("国学经典");
			}
		}
	}

	private void initMusicView() {
		Music music = MusicListControl.getInstance().getCurMusic();
		initMuscNameAndType(music);
	}

	String oldName = "";

	@Override
	public void getMusicInformation(Music music, int playMode) {
		if (music != null) {
			initMuscNameAndType(music);
			String name = music.getMname();
			if (name == null) {
				return;
			}
			/**
			 * 总体的界面刷新
			 */
			if (StringUtils.isEmpty(CommConst.name)
					|| !CommConst.name.equals(name)) {
//				Toast.makeText(this, "" + getName(), 1).show();
				CommConst.CDNUMBER = (int) (Math.random() * 3);
				mIvMusicIcon.setImageBitmap(BitmapFactory.decodeResource(
						ctx.getResources(), cds[CommConst.CDNUMBER]));
				degree = 0;
				if (state) {
					startAnimation();
				}
				CommConst.name = name;
			}
			/**
			 * 自己的界面刷新 保持图片一致
			 */
			if (StringUtils.isEmpty(oldName) || !oldName.equals(name)) {
				mIvMusicIcon.setImageBitmap(BitmapFactory.decodeResource(
						ctx.getResources(), cds[CommConst.CDNUMBER]));
				oldName = name;
			}
		}
	}

	@Override
	public void updateMusicStatus(long currentTime) {

	}

	@SuppressWarnings("deprecation")
	@Override
	public void getMediaStatePlayer(boolean state, int playMode) {
		if (this.state != state) {
			if (state) {
				startAnimation();
			} else {
				stopAnimation();
			}
		}
		this.state = state;
		if (state) {
			mIvMusicPlay.setBackgroundDrawable(getResources().getDrawable(
					R.drawable.ic_common_music_pause));
		} else {
			mIvMusicPlay.setBackgroundDrawable(getResources().getDrawable(
					R.drawable.ic_common_music_play));
		}
	};

	private final int MUSIC_LIST = 1;
	private final int MUSIC_STATE = 2;
	private final int MUSIC_DETAIL = 3;

	/*
	 * private Handler handler = new Handler(){
	 * 
	 * @Override public void handleMessage(Message msg) {
	 * super.handleMessage(msg); switch(msg.what){ case MUSIC_LIST:
	 * 
	 * break; case MUSIC_STATE: if (state) {
	 * mIvMusicPlay.setBackgroundDrawable(getResources().getDrawable(
	 * R.drawable.ic_common_music_pause)); } else {
	 * mIvMusicPlay.setBackgroundDrawable(getResources().getDrawable(
	 * R.drawable.ic_common_music_play)); } break; case MUSIC_DETAIL:
	 * mTvMusicName.setText(music.getMname());
	 * mTvMusicType.setText(music.getMtype()); break; } }
	 * 
	 * };
	 */

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		Log.v("MainActivity", "ss:" + keyCode);
		try {
			switch (keyCode) {
			case KeyEvent.KEYCODE_MEDIA_PLAY:
				mIMusicManager.resumeMusic();
				return true;
			case KeyEvent.KEYCODE_MEDIA_PAUSE:
				mIMusicManager.pauseMusic();
				return true;
			case KeyEvent.KEYCODE_MEDIA_NEXT:
				// mIMusicManager.nextMusic();
				return true;
			case KeyEvent.KEYCODE_MEDIA_PREVIOUS:
				// mIMusicManager.priorMusic();
				return true;
			}
			return super.onKeyDown(keyCode, event);
		} catch (Exception e) {
			return super.onKeyDown(keyCode, event);
		}
	}

	public void playRecord(List<Record> records, int position) {
		MusicListControl.getInstance().setRecords(records);
		try {
			getmIMusicManager().playMusic(position);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}
}
