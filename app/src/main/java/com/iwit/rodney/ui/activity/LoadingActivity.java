package com.iwit.rodney.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.ScrollView;

import com.iwit.rodney.App;
import com.iwit.rodney.R;
import com.iwit.rodney.comm.CommConst;
import com.iwit.rodney.comm.tools.PlayMediaInstance;
import com.iwit.rodney.service.MusicListControl;
import com.iwit.rodney.service.MusicManagerService;

public class LoadingActivity extends Activity {
	private ImageView mIvEarth, mIvSurper;
	private ScrollView mScrEarth;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Intent service = new Intent(this, MusicManagerService.class);
		startService(service);
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		Log.e("qh", "width:" + dm.widthPixels + ",height:" + dm.heightPixels);
		setContentView(R.layout.activity_loading);
		mIvEarth = (ImageView) findViewById(R.id.iv_earth);
		mScrEarth = (ScrollView) findViewById(R.id.scv_earth);
		mIvSurper = (ImageView) findViewById(R.id.iv_surper);
		mScrEarth.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				return true;
			}
		});
		RotateAnimation rotateAnimation = new RotateAnimation(0, 360,
				Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
		rotateAnimation.setDuration(20000);
		mIvEarth.startAnimation(rotateAnimation);
		ScaleAnimation scaleAnimation = new ScaleAnimation(0.2f, 1.0f, 0.2f,
				1.0f, Animation.RELATIVE_TO_SELF, 0.5f,
				Animation.RELATIVE_TO_SELF, 0.5f);
		scaleAnimation.setDuration(4000);
		mIvSurper.startAnimation(scaleAnimation);
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				startActivity(new Intent(LoadingActivity.this,
						HomeActivity.class));
				LoadingActivity.this.finish();
			}
		}, 4500);
		CommConst.isSet = MusicListControl.getInstance().setSavedMusic(this);
	}

}
