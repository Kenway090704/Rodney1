package com.iwit.rodney.ui.view;

import java.io.File;

import android.content.Context;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.iwit.rodney.App;
import com.iwit.rodney.R;
import com.iwit.rodney.comm.utils.MusicPlayer;
import com.iwit.rodney.comm.utils.Recorder;
import com.iwit.rodney.entity.Music;
import com.iwit.rodney.entity.Record;
import com.iwit.rodney.entity.Story;
import com.iwit.rodney.interfaces.IManager;
import com.iwit.rodney.interfaces.IRecorder;
import com.iwit.rodney.ui.view.CheckRelativeLayout.OnCheckedChangeListener;
import com.iwit.rodney.ui.view.SaveDialog.OnSavedListener;

/**
 * 封装record_panel_ready和record_panel_recording布局功能的类
 */
public class RecordPanel implements OnClickListener, OnCheckedChangeListener,
		OnCompletionListener, OnSavedListener {
	public static final String TAG = "RecordPanel";

	public interface OnControlListener {
		void requestTextSizeChange(float delta);

		void onColor(int color);

		void onBgMusic();
		void onDisBgMusic();
	}

	public interface LyricGetter {
		String getLrcTitle();

		String getLrcContent();
	}

	private LinearLayout layoutRecording;
	private LinearLayout layoutReady;
	// ready view
	private View vIncTextSize;
	private View vDecTextSize;
	private View vYellow;
	private View vGreen;
	private View vRed;
	private View vBlue;
	private View vBgMusic;
	private View vRecord;
	// recording view
	private TimeProgressTextView vProgress;
	private String currentRecordFilePath;

	private CheckRelativeLayout cbListen;
	private CheckRelativeLayout cbPlay;
	private IncludingRelativeLayout includeReRecord;
	private IncludingRelativeLayout includeSave;

	public static final int STATE_READY = 0;
	public static final int STATE_RECORDING = 1;
	public static final int STATE_PAUSING = 2;
	public static final int STATE_LISTENING = 3;

	private int layoutState = STATE_READY;

	private Handler handler = new Handler(Looper.getMainLooper());
	private IRecorder recorder;
	private MediaPlayer mp;
	private TimeCounter tc = new TimeCounter();
	private SaveDialog sd;
	private Story story;
	private OnControlListener controlLis;
	private LyricGetter lrcGetter;

	/**
	 * 
	 * @param story
	 *            相对应的story。若为null，则表示无对应的story，则此录音没有配图，有无歌词取决于getter参数。若不为null
	 *            ，则此录音的配图和歌词都是来自该story。
	 * @param rootView
	 *            控制面板的根视图，此视图应该包含布局 record_panel_ready和record_panel_recording
	 * @param controlLis
	 *            控制按钮监听器。
	 * @param getter
	 *            获取歌词内容的回调接口。当story为null的情况下才会被用到。若不为null则表示可以获取歌词但不一定有歌词;
	 *            若为null则表示没有歌词。
	 */
	public RecordPanel(Story story, View rootView,
			OnControlListener controlLis, LyricGetter getter) {
		this.story = story;
		this.controlLis = controlLis;
		this.lrcGetter = getter;

		layoutReady = (LinearLayout) rootView
				.findViewById(R.id.ll_record_panel_ready);
		layoutRecording = (LinearLayout) rootView
				.findViewById(R.id.ll_record_panel_recording);

		// ready view
		vIncTextSize = layoutReady
				.findViewById(R.id.rl_record_panel_top_textsize_inc);
		vDecTextSize = layoutReady
				.findViewById(R.id.rl_record_panel_top_textsize_dec);
		vYellow = layoutReady
				.findViewById(R.id.rl_record_panel_top_color_yello);
		vGreen = layoutReady.findViewById(R.id.rl_record_panel_top_color_green);
		vRed = layoutReady.findViewById(R.id.rl_record_panel_top_color_red);
		vBlue = layoutReady.findViewById(R.id.rl_record_panel_top_color_blue);
		vBgMusic = layoutReady
				.findViewById(R.id.rl_record_panel_ready_bg_music);
		vRecord = layoutReady.findViewById(R.id.v_record_story_ready_record);

		// recording view
		vProgress = (TimeProgressTextView) layoutRecording
				.findViewById(R.id.tv_record_panel_recording_progress);
		View vListen = layoutRecording
				.findViewById(R.id.rl_record_panel_recording_listen);
		View vPlay = layoutRecording
				.findViewById(R.id.rl_record_panel_recording_play);

		View vReRecord = layoutRecording
				.findViewById(R.id.rl_record_panel_recording_rerecord);
		includeReRecord = new IncludingRelativeLayout(vReRecord,
				R.id.v_record_panel_bottom_rerecord);
		View vSave = layoutRecording
				.findViewById(R.id.rl_record_panel_recording_save);
		includeSave = new IncludingRelativeLayout(vSave,
				R.id.v_record_panel_bottom_save);

		cbListen = new CheckRelativeLayout(vListen,
				vListen.findViewById(R.id.v_record_panel_bottom_listen));
		cbPlay = new CheckRelativeLayout(vPlay,
				vPlay.findViewById(R.id.v_record_panel_bottom_play));

		includeReRecord.setOnClickListener(this);
		includeSave.setOnClickListener(this);

		setViewsClickListener(this, vIncTextSize, vDecTextSize, vYellow,
				vGreen, vRed, vBlue, vBgMusic, vRecord, vReRecord, vSave);
		setViewsCheckedListener(this, cbListen, cbPlay);

		recorder = Recorder.getInstance();

		sd = new SaveDialog(rootView.getContext(), this);

		updateLayoutByState();
	}

	private void setViewsCheckedListener(OnCheckedChangeListener lis,
			CheckRelativeLayout... views) {
		for (int i = 0; i < views.length; i++) {
			views[i].setOnCheckedChangeListener(lis);
		}
	}

	private void setViewsClickListener(OnClickListener lis, View... vs) {
		for (int i = 0; i < vs.length; i++) {
			vs[i].setOnClickListener(lis);
		}
	}

	@SuppressWarnings("deprecation")
	@Override
	public void onClick(View v) {
		final int id = v.getId();
		switch (id) {
		case R.id.v_record_story_ready_record:
			onStartRecord();
			return;
		case R.id.rl_record_panel_recording_rerecord:
			onReRecord();
			return;
		case R.id.rl_record_panel_recording_save:
			onSave();
			return;
		case R.id.rl_record_panel_top_color_yello:
			controlLis.onColor(vProgress.getResources().getColor(
					R.color.record_yellow));
			return;
		case R.id.rl_record_panel_top_color_green:
			controlLis.onColor(vProgress.getResources().getColor(
					R.color.record_green));
			return;
		case R.id.rl_record_panel_top_color_red:
			controlLis.onColor(vProgress.getResources().getColor(
					R.color.record_red));
			return;
		case R.id.rl_record_panel_top_color_blue:
			controlLis.onColor(vProgress.getResources().getColor(
					R.color.record_blue));
			return;
		case R.id.rl_record_panel_top_textsize_inc:
			controlLis.requestTextSizeChange(3);
			return;
		case R.id.rl_record_panel_top_textsize_dec:
			controlLis.requestTextSizeChange(-3);
			return;
		case R.id.rl_record_panel_ready_bg_music:
			controlLis.onBgMusic();
			return;
		}
	}

	private void onReRecord() {
		if (layoutState != STATE_PAUSING) {
			throw new IllegalStateException();
		}
		controlLis.onDisBgMusic();
		layoutState = STATE_READY;
		updateLayoutByState();

		stopCountingTime();
		recorder.stop();
		
	}

	private void onSave() {
		if(time > 5){
			sd.show();
		}else{
			Toast.makeText(App.ctx, "录音时间过短.", 1).show();
		}
		
	}

	private void onStartRecord() {
		if (layoutState != STATE_READY) {
			throw new IllegalStateException();
		}
		layoutState = STATE_RECORDING;

		updateLayoutByState();

		cbPlay.setChecked(true);
	}

	private void updateLayoutByState() {
		Log.e(TAG, "updateLayoutByState:" + layoutState);
		boolean ready = layoutState == STATE_READY;
		layoutReady.setVisibility(ready ? View.VISIBLE : View.GONE);
		layoutRecording.setVisibility(ready ? View.GONE : View.VISIBLE);

		switch (layoutState) {
		case STATE_RECORDING:
			cbListen.setEnabled(false);
			includeReRecord.setEnabled(false);
			includeSave.setEnabled(false);
			break;
		case STATE_PAUSING:
			cbListen.setEnabled(true);
			includeReRecord.setEnabled(true);
			includeSave.setEnabled(true);
			cbPlay.setEnabled(true);
			break;
		case STATE_LISTENING:
			includeReRecord.setEnabled(false);
			includeSave.setEnabled(false);
			cbPlay.setEnabled(false);
			break;
		}
	}

	@Override
	public void onCheckedChanged(View v, boolean checked) {
		switch (v.getId()) {
		case R.id.rl_record_panel_recording_play:
			if (checked) {
				if (layoutState == STATE_RECORDING) {
					startRecord();
				} else if (layoutState == STATE_PAUSING) {
					onResumeRecord();
				}
			} else {
				onPauseRecord();
			}
			return;
		case R.id.rl_record_panel_recording_listen:
			// 试听功能只能在暂停录音时使用
			if (checked) {
				onStartListen();
			} else {
				onStopListen();
			}
			return;
		}
	}

	private void startRecord() {
		recorder.start();
		startCountingTime();
	}

	private void onStartListen() {
		if (layoutState != STATE_PAUSING) {
			throw new IllegalStateException();
		}

		layoutState = STATE_LISTENING;

		updateLayoutByState();

		try {
			mp = new MediaPlayer();
			mp.setOnCompletionListener(this);
			mp.setDataSource(currentRecordFilePath);
			mp.prepare();
			mp.start();
			startRepeater(TimeRepeater.TYPE_PLAYER);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void onStopListen() {
		if (layoutState != STATE_LISTENING) {
			throw new IllegalStateException();
		}

		layoutState = STATE_PAUSING;

		updateLayoutByState();

		mp.stop();
		mp.release();
		mp = null;
		stopRepeater();
	}

	private void onResumeRecord() {
		if (layoutState != STATE_PAUSING) {
			throw new IllegalStateException();
		}

		layoutState = STATE_RECORDING;

		updateLayoutByState();

		MusicPlayer.get(vProgress.getContext()).resume();

		recorder.resume();

		resumeCountingTime();

	}

	/**
	 * 开始计时
	 */
	private void startCountingTime() {
		tc.start();
		startRepeater(TimeRepeater.TYPE_RECORDER);
	}

	private void pauseCountintTime() {
		stopRepeater();
		tc.pause();
	}

	private void resumeCountingTime() {
		tc.resume();
		startRepeater(TimeRepeater.TYPE_RECORDER);
	}

	private void stopCountingTime() {
		stopRepeater();
	}

	private void onPauseRecord() {
		if (layoutState != STATE_RECORDING) {
			throw new IllegalStateException();
		}

		layoutState = STATE_PAUSING;

		updateLayoutByState();

		pauseCountintTime();

		currentRecordFilePath = recorder.pause();
		MusicPlayer.get(vProgress.getContext()).pause();
	}

	@Override
	public void onCompletion(MediaPlayer mp) {
		cbListen.setChecked(false);
	}

	private TimeRepeater timeRepeater = new TimeRepeater();

	private void startRepeater(int type) {
		timeRepeater.setType(type);
		handler.post(timeRepeater);
	}

	private void stopRepeater() {
		handler.removeCallbacks(timeRepeater);
	}
	private int time;
	private class TimeRepeater implements Runnable {
		public static final int TYPE_PLAYER = 1;
		public static final int TYPE_RECORDER = 2;

		private int type;

		private void setType(int type) {
			if (type != TYPE_PLAYER && type != TYPE_RECORDER) {
				throw new IllegalArgumentException();
			}
			this.type = type;
		}

		@Override
		public void run() {
			switch (type) {
			case TYPE_PLAYER:
				vProgress.setTimes(mp.getDuration() / 1000,
						mp.getCurrentPosition() / 1000);
				break;
			case TYPE_RECORDER:
				vProgress.setTimes(0, (int) (tc.get() / 1000));
				time = (int) (tc.get() / 1000);
				Log.v("record", "time::"+time);
				break;
			}

			handler.postDelayed(this, 500);
		}
	}

	public void stopWhatever() {
		handler.removeCallbacks(timeRepeater);
		if (mp != null && mp.isPlaying()) {
			mp.stop();
			mp.release();
			mp = null;
		}
		String tempFilePath = recorder.stop();
		new File(tempFilePath).delete();
	}

	@Override
	public void onSave(String name) {
		if (!checkRecordName(vProgress.getContext(), name)) {
			return;
		}

		if (layoutState != STATE_PAUSING) {
			throw new IllegalStateException();
		}
		Record record = null;
		String lrcTitle = null;
		String lrcContent = null;
		if (story == null) {
			record = new Record(name, null, null);
			if (lrcGetter != null) {
				lrcTitle = lrcGetter.getLrcTitle();
				lrcContent = lrcGetter.getLrcContent();
			}
		} else {
			Music m = IManager.getIRecord().getCachedMusic(story.getMid());
			record = new Record(name, m.getLyric(), m.getCoverpic());
		}

		String lrcName = null;
		if (!TextUtils.isEmpty(lrcTitle) || !TextUtils.isEmpty(lrcContent)) {
			lrcName = IManager.getIRecord().saveLrc(name,
					lrcTitle + "\r\n" + lrcContent);
			record.setLyric(lrcName + ".lrc");
		}

		boolean result = IManager.getIRecord().save(currentRecordFilePath,
				record);

		String resultStr = "保存失败，请不要使用相同名字保存";
		if (result) {
			resultStr = "保存成功";
			recorder.stop();
			layoutState = STATE_READY;
			updateLayoutByState();
		}
		Toast.makeText(vProgress.getContext(), resultStr, Toast.LENGTH_SHORT)
				.show();
	}

	public static boolean checkRecordName(Context context, String name) {
		if (name == null) {
			throw new NullPointerException();
		}
		if (name.length() < 1) {
			Toast.makeText(context, "不能使用空名字", Toast.LENGTH_SHORT).show();
			return false;
		}
		// 仅可使用字母、数字、下划线
		if (name.matches("^\\w{1,12}$"))
			return true;
		else {
			Toast.makeText(context, "请不要输入特殊字符", Toast.LENGTH_SHORT).show();
			return false;
		}

	}

}
