package com.iwit.rodney.ui.activity;

import java.util.ArrayList;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.iwit.rodney.App;
import com.iwit.rodney.R;
import com.iwit.rodney.comm.tools.PlayMediaInstance;
import com.iwit.rodney.service.FileInputMusic;
import com.iwit.rodney.service.IMusicManager;
import com.iwit.rodney.service.MusicListControl;
import com.iwit.rodney.service.MusicManagerService;
import com.iwit.rodney.ui.adapter.IntroduceAdapter;
import com.iwit.rodney.ui.view.ArarmDialog;
import com.iwit.rodney.ui.view.AutoScrollViewPager;
import com.iwit.rodney.ui.view.SaveDialog;
import com.iwit.rodney.ui.view.SaveDialog.OnSavedListener;

@TargetApi(Build.VERSION_CODES.GINGERBREAD)
public class HomeActivity extends BaseActivity implements OnClickListener,
		OnPageChangeListener {
	private static final String TAG = "HomeActivity";
	private static final int[] pics = { R.drawable.ic_home_introduce,
			R.drawable.ic_home_introduce1, R.drawable.ic_home_introduce2 };
	private ArrayList<View> views = new ArrayList<View>();
	private AutoScrollViewPager mVpIntrodece;
	private LinearLayout mLiPointContainer;

	private void initView() {
		mVpIntrodece = (AutoScrollViewPager) findViewById(R.id.vp_introduce);
		mLiPointContainer = (LinearLayout) findViewById(R.id.li_point_container);
	}

	private void initData() {
		LinearLayout.LayoutParams mParams = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.MATCH_PARENT,
				LinearLayout.LayoutParams.MATCH_PARENT);
		for (int i = 0; i < pics.length; i++) {
			ImageView iv = new ImageView(this);
			iv.setLayoutParams(mParams);
			iv.setImageResource(pics[i]);
			views.add(iv);
		}
		IntroduceAdapter adapter = new IntroduceAdapter(views);
		mVpIntrodece.setAdapter(adapter);
		mVpIntrodece.setOnPageChangeListener(this);

		for (int i = 0; i < pics.length; i++) {
			ImageView iv = new ImageView(this);
			// iv.setLayoutParams(mParams);
			if (0 == i) {
				iv.setImageResource(R.drawable.ic_home_point_1);
			} else {
				iv.setImageResource(R.drawable.ic_home_point_2);
			}
			iv.setPadding(10, 0, 0, 0);
			mLiPointContainer.addView(iv);
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);
		initView();
		initPanelView();
		initData();
	}

	@Override
	protected void onPause() {
		super.onPause();
		PlayMediaInstance.getInstance(App.ctx).releaseMediaPlayer();
		mVpIntrodece.stopAutoScroll();
	}

	@Override
	protected void onResume() {
		super.onResume();
		mVpIntrodece.startAutoScroll(5000);
		if (!MusicManagerService.getPlayerStatus()) {
			boolean isOpen = true;
			Object obj = App.getSp().get("bg_switch");
			if(obj == null){
				isOpen = true;
			}else{
				isOpen = Boolean.valueOf(String.valueOf(obj));
			}
			if(isOpen){
				PlayMediaInstance.getInstance(App.ctx).playMusicMedia(R.raw.rondey_background);
			}
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	@Override
	public void onClick(View v) {
		super.onClick(v);
		switch (v.getId()) {
		case R.id.btn_music:
			startActivity(new Intent(this, MusicHomeActivity.class));
			break;
		case R.id.btn_story:
			startActivity(new Intent(this, StoryHomeActivity.class));
			break;
		case R.id.btn_library:
			startActivity(new Intent(this, LibraryActivity.class));
			break;
		case R.id.btn_create:
			startActivity(new Intent(this, CreationActivity.class));
			break;
		case R.id.btn_favorite:
			startActivity(new Intent(this, FavoriteActivity.class));
			break;
		case R.id.btn_account:
			startActivity(new Intent(this, UserCenterActivity.class));
			break;
		}

	}

	@Override
	public void updateMusic(int position) {
		// Log.v(TAG, "position::"+position);
	}

	@Override
	public void onPageScrollStateChanged(int position) {

	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {

	}

	@Override
	public void onPageSelected(int position) {
		Log.v(TAG, "position::" + position);
		int count = mLiPointContainer.getChildCount();
		position = position % count;
		for (int i = 0; i < count; i++) {
			ImageView iv = (ImageView) mLiPointContainer.getChildAt(i);
			if (position == i) {
				iv.setImageResource(R.drawable.ic_home_point_1);
			} else {
				iv.setImageResource(R.drawable.ic_home_point_2);
			}
		}
	}

	@Override
	public void updateDuration() {

	}

	@Override
	public void startLoadMusic() {

	}
}
