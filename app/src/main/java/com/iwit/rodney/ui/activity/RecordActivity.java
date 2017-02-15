package com.iwit.rodney.ui.activity;

import java.io.File;
import java.io.UnsupportedEncodingException;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.TypedValue;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Toast;

import com.iwit.rodney.R;
import com.iwit.rodney.comm.tools.FileUtils;
import com.iwit.rodney.comm.utils.MusicPlayer;
import com.iwit.rodney.entity.Story;
import com.iwit.rodney.event.Event;
import com.iwit.rodney.interfaces.IManager;
import com.iwit.rodney.ui.view.BgMusicDialog;
import com.iwit.rodney.ui.view.BgMusicDialog.OnMusicSelectListener;
import com.iwit.rodney.ui.view.RecordPanel;
import com.iwit.rodney.ui.view.RecordPanel.LyricGetter;
import com.iwit.rodney.ui.view.RecordPanel.OnControlListener;

import de.greenrobot.event.EventBus;

public class RecordActivity extends Activity implements OnControlListener,
		OnMusicSelectListener, LyricGetter {
	/**
	 * 录音类型。录音功能目前有3种情况，一种是固定歌词，一种是编辑歌词，一种是没有歌词。
	 * <p>
	 * 要跳转到此Activity必须在Intent中此key下附带录音功能的类型。类型参照
	 * {@link #RECORD_TYPE_CREATE_STORY}等
	 */
	public static final String INTENT_EXTRA_KEY_RECORD_TYPE = "record_type";
	public static final int RECORD_TYPE_INVALID = -1;
	/**
	 * 录音类型之一。创作故事或者儿歌。可以输入歌词的标题，内容。在保存录音时，也会将输入的歌词保存，保存文件名与录音名相同。 如果用户输入为空则不保存。
	 */
	public static final int RECORD_TYPE_CREATE_STORY = 0;
	/**
	 * 录音类型之一。仅仅是录音，不需要显示歌词。
	 */
	public static final int RECORD_TYPE_RECORD_STORY = 1;
	/**
	 * 录音类型之一。显示歌词但不编辑。不需要保存歌词。如果是此类型，那Intent还应该附带一个key为
	 * {@value #INTENT_EXTRA_KEY_MID}的参数。
	 */
	public static final int RECORD_TYPE_SHOW_STORY = 2;
	/**
	 * 录音类型之一。和{@link #RECORD_TYPE_CREATE_STORY} 类型功能完全一样，只相差标题。
	 */
	public static final int RECORD_TYPE_CREATE_SONG = 3;
	/**
	 * 录音类型之一。和{@link #RECORD_TYPE_RECORD_STORY} 类型功能完全一样，只相差标题。
	 */
	public static final int RECORD_TYPE_RECORD_SONG = 4;
	/**
	 * 当录音类型为 {@link #RECORD_TYPE_SHOW_STORY}
	 * 时，Intent还应该附带此Key的参数。因为此录音类型的歌词和封面来源于音乐，而mid是对应到音乐的id，
	 * 所以通过此key获取mid来获取对应的音乐的相关参数。
	 */
	public static final String INTENT_EXTRA_KEY_MID = "mid";

	private EditText etLrcContent;
	private EditText etLrcTitle;
	private Story story;
	private RecordPanel panel;
	private LinearLayout llBg;
	private BgMusicDialog dialog;
	private int mRecordType;
	private String mid;
	private ScrollView sv;
	private ImageView ivTitle;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_record);
		getIntentParameterAndCheck();
		initViews();
		findViewById(R.id.iv_back).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
	}

	private void getIntentParameterAndCheck() {
		Intent intent = getIntent();
		mRecordType = intent.getIntExtra(INTENT_EXTRA_KEY_RECORD_TYPE,
				RECORD_TYPE_INVALID);

		if (mRecordType == RECORD_TYPE_INVALID)
			throw new IllegalArgumentException("invalid record type");

		if (mRecordType == RECORD_TYPE_SHOW_STORY) {
			mid = intent.getStringExtra(INTENT_EXTRA_KEY_MID);
			if (mid == null) {
				throw new IllegalArgumentException(
						"mid should not be null when its record type is show-story");
			}
		}
	}

	private void initViews() {
		llBg = (LinearLayout) findViewById(R.id.ll_record_bg);
		etLrcContent = (EditText) findViewById(R.id.et_record_lrc_content);
		etLrcTitle = (EditText) findViewById(R.id.et_record_lrc_title);
		sv = (ScrollView) findViewById(R.id.sv_record_lyric);
		ivTitle = (ImageView) findViewById(R.id.iv_record_title);

		initTitleImage();

		initLyricRelative();

		panel = new RecordPanel(story, findViewById(R.id.rl_record_panel_area),
				this, this);
	}

	private void initTitleImage() {
		switch (mRecordType) {
		case RECORD_TYPE_CREATE_STORY:
			ivTitle.setImageResource(R.drawable.ic_story_creation);
			break;
		case RECORD_TYPE_CREATE_SONG:
			ivTitle.setImageResource(R.drawable.ic_song_creation);
			break;
		case RECORD_TYPE_RECORD_STORY:
			ivTitle.setImageResource(R.drawable.ic_story_record);
			break;
		case RECORD_TYPE_RECORD_SONG:
			ivTitle.setImageResource(R.drawable.ic_song_record);
			break;
		}
	}

	private void initLyricRelative() {
		switch (mRecordType) {
		case RECORD_TYPE_SHOW_STORY:
			getAndShowLyric();
			break;
		case RECORD_TYPE_CREATE_STORY:
		case RECORD_TYPE_CREATE_SONG:
			enableEditText();
			break;
		case RECORD_TYPE_RECORD_STORY:
		case RECORD_TYPE_RECORD_SONG:
			hideEditText();
			showLyricCustomBg();
			break;
		default:
			break;
		}
	}

	private void hideEditText() {
		etLrcContent.setVisibility(View.GONE);
		etLrcTitle.setVisibility(View.GONE);
	}

	private void showLyricCustomBg() {
		sv.setBackgroundDrawable(getResources().getDrawable(
				R.drawable.bg_record_lyric));
	}

	private void enableEditText() {
		etLrcContent.setEnabled(true);
		etLrcTitle.setEnabled(true);
	}

	private void getAndShowLyric() {
		if (mid == null) {
			throw new NullPointerException();
		}

		story = IManager.getIStory().getStoryByMid(mid);
		if (story == null) {
			throw new RuntimeException("null-story");
		}
		String path = Environment.getExternalStorageDirectory()
				.getAbsolutePath() + story.getLocalPath() + story.getLrcName();
		File file = new File(path);
		if (!file.exists()) {
			Toast.makeText(this, "该故事不存在，请点击重新下载", Toast.LENGTH_SHORT).show();
			IManager.getIDownload(getApplicationContext())
					.removeDownload(story);
			finish();
		}

		String lrc[] = FileUtils.getLrcFromFile(file, "gbk");
		if (lrc == null) {
			Toast.makeText(this, "获取不到歌词文件，请稍后重试", Toast.LENGTH_SHORT).show();
		}
		if (lrc[0] != null)
			etLrcTitle.setText(lrc[0]);
		if (lrc[1] != null)
			etLrcContent.setText(lrc[1]);
	}

	@Override
	protected void onResume() {
		EventBus.getDefault().post(
				new Event(Event.EVENT_MUSIC_STOP, null, false));
		super.onResume();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		panel.stopWhatever();
		MusicPlayer.get(this).release();
	}

	@Override
	public void requestTextSizeChange(float delta) {
		float titleSize = etLrcTitle.getTextSize();
		if ((titleSize > 100 && delta > 0) || (titleSize < 20 && delta < 0)) {
			return;
		}
		etLrcTitle.setTextSize(TypedValue.COMPLEX_UNIT_PX, titleSize + delta);
		etLrcContent.setTextSize(TypedValue.COMPLEX_UNIT_PX,
				etLrcContent.getTextSize() + delta);
	}

	@Override
	public void onColor(int color) {
		llBg.setBackgroundColor(color);
	}

	@Override
	public void onBgMusic() {
		if(dialog == null){
			dialog = new BgMusicDialog(this, this);
			dialog.show();
		}else{
			dialog.show();
		}
	}
	
	@Override
	public void onDisBgMusic() {
		if(dialog != null){
			if(dialog.isShowing()){
				dialog.dismiss();
			}
			dialog = null;
		}
	}

	@Override
	public void dismiss() {
		if(dialog != null){
			if(dialog.isShowing()){
				dialog.dismiss();
			}
			dialog = null;
		}
	}
	
	@Override
	public void back() {
		if(dialog != null){
			dialog.dismiss();
		}
	}
	
	@Override
	public void onMusicSelected(int rawId) {
		MusicPlayer.get(this).start(rawId);
	}

	@Override
	public String getLrcTitle() {
		return getTrimGBKText(etLrcTitle);
	}

	/**
	 * 使用gbk是为了和下载的lrc保证相同的字符集。
	 */
	private static String getTrimGBKText(EditText et) {
		String title = et.getText().toString();
		title = title.trim();
/*		try {
			title = new String(title.getBytes(), "gbk");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}*/
		return title;
	}

	@Override
	public String getLrcContent() {
		return getTrimGBKText(etLrcContent);
	}

	public static void jumpInto(Context context, int type, String mid) {
		Intent intent = new Intent(context, RecordActivity.class);
		intent.putExtra(RecordActivity.INTENT_EXTRA_KEY_RECORD_TYPE, type);
		if (mid != null) {
			intent.putExtra(RecordActivity.INTENT_EXTRA_KEY_MID, mid);
		}
		context.startActivity(intent);
	}

}
