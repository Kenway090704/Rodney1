package com.iwit.rodney.ui.activity;

import java.util.ArrayList;
import java.util.List;

import com.iwit.rodney.R;
import com.iwit.rodney.ui.adapter.GeneralFragmentPagerAdapter;
import com.iwit.rodney.ui.fragment.CommonListFragment;
import com.iwit.rodney.ui.fragment.FavoriteFragment;
import com.iwit.rodney.ui.fragment.LazyFragment;
import com.iwit.rodney.ui.fragment.SongSearchFragment;
import com.iwit.rodney.ui.view.Navigation;
import com.iwit.rodney.ui.view.Navigation.OnNavigationChangeListener;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RadioGroup;

public class FavoriteActivity extends BaseActivity implements
OnNavigationChangeListener , OnPageChangeListener,OnClickListener{
	private Navigation mNavi;
	private ViewPager mVpMusic;
	private static final String TAG = "MusicHomeActivity";
	private List<Fragment> frgs = new ArrayList<Fragment>();
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_favorite);
		initView();
		initPanelView();
		findViewById(R.id.iv_music_back).setOnClickListener(this);
	}

	private void initView() {
		mNavi = new Navigation((RadioGroup) findViewById(R.id.rg_titles), 2,
				new int[] { R.id.radio_music, R.id.radio_story}, new String[] { "音乐馆",
						"故事屋"}, this);
		frgs.add(new FavoriteFragment("1,3,4",1,"FavoriteMusicFragment",new String[]{"all"}));
		frgs.add(new FavoriteFragment("2,6,7",1,"FavoriteStoryFragment"));
		mVpMusic = (ViewPager) findViewById(R.id.vp_music);
		mVpMusic.setAdapter(new GeneralFragmentPagerAdapter(
				getSupportFragmentManager(), frgs));
		mVpMusic.addOnPageChangeListener(this);
	}

	@Override
	public void updateMusic(int position) {
		for(Fragment frg : frgs){
			LazyFragment fg = (LazyFragment) frg;
			fg.updatePannel(position);
		}
		
	}
	@Override
	protected void onResume() {
		super.onResume();
		for(Fragment frg : frgs){
			LazyFragment fg = (LazyFragment) frg;
			fg.notifyView();
		}
	}
	@Override
	public void onPageScrollStateChanged(int arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onPageSelected(int paramInt) {
		mNavi.checkItem(paramInt);	
		
	}

	@Override
	public void onNavigationChanged(int itemIndex) {
		mVpMusic.setCurrentItem(itemIndex, false);
		
	}
	@Override
	public void onClick(View v) {
		super.onClick(v);
		switch(v.getId()){
		case R.id.iv_music_back:
			finish();
			break;
		}
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
