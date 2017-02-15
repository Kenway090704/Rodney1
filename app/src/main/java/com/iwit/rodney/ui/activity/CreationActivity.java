package com.iwit.rodney.ui.activity;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RadioGroup;

import com.iwit.rodney.R;
import com.iwit.rodney.ui.adapter.GeneralFragmentPagerAdapter;
import com.iwit.rodney.ui.fragment.RecordListFragment;
import com.iwit.rodney.ui.fragment.RecordLocalFragment;
import com.iwit.rodney.ui.fragment.RecordSongFragment;
import com.iwit.rodney.ui.view.Navigation;
import com.iwit.rodney.ui.view.Navigation.OnNavigationChangeListener;

public class CreationActivity extends BaseActivity implements
		OnNavigationChangeListener, OnPageChangeListener {
	private Navigation mNavi;
	private ViewPager mVp;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_creation);
		initPanelView();
		initServiceData();

		findViewById(R.id.iv_back).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		mNavi = new Navigation(
				(RadioGroup) findViewById(R.id.rg),
				3,
				new int[] { R.id.radio_record, R.id.radio_song, R.id.radio_mine },
				new String[] { "录制故事", "录制儿歌", "我的故事" }, this);
		mVp = (ViewPager) findViewById(R.id.vp_creation);
		mVp.addOnPageChangeListener(this);

		List<Fragment> frgs = new ArrayList<Fragment>();
		frgs.add(new RecordListFragment());
		frgs.add(new RecordSongFragment());
		frgs.add(new RecordLocalFragment());
		mVp.setAdapter(new GeneralFragmentPagerAdapter(
				getSupportFragmentManager(), frgs));
	}

	@Override
	public void onNavigationChanged(int itemIndex) {
		mVp.setCurrentItem(itemIndex, false);
		RecordListFragment.closeInputKeyboard(this);
	}

	@Override
	public void onPageScrolled(int paramInt1, float paramFloat, int paramInt2) {
	}

	@Override
	public void onPageSelected(int paramInt) {
		mNavi.checkItem(paramInt);
	}

	@Override
	public void onPageScrollStateChanged(int paramInt) {
	}

	@Override
	public void updateMusic(int position) {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateDuration() {
		// TODO Auto-generated method stub

	}

	@Override
	public void startLoadMusic() {
		// TODO Auto-generated method stub

	}

}
