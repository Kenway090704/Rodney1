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
import com.iwit.rodney.ui.fragment.CommonListFragment;
import com.iwit.rodney.ui.fragment.LazyFragment;
import com.iwit.rodney.ui.fragment.StorySearchFragment;
import com.iwit.rodney.ui.view.Navigation;
import com.iwit.rodney.ui.view.Navigation.OnNavigationChangeListener;


public class StoryHomeActivity extends BaseActivity implements
OnNavigationChangeListener , OnPageChangeListener,OnClickListener{
	private Navigation mNavi;
	private ViewPager mVpMusic;
	private static final String TAG = "StoryHomeActivity";
	private List<Fragment> frgs = new ArrayList<Fragment>();
	private void initView() {
		mNavi = new Navigation((RadioGroup) findViewById(R.id.rg_titles), 4,
				new int[] { R.id.radio_story_tales, R.id.radio_music_animals,
						R.id.radio_music_classics,R.id.radio_story_search }, new String[] { "飞侠故事",
						"童话故事", "经典故事", "故事搜索" }, this);
		mVpMusic = (ViewPager) findViewById(R.id.vp_music);
		frgs.add(new CommonListFragment("6",1,"StoryTalesFragment"));
		frgs.add(new CommonListFragment("2",1,"StoryAnimalsFragment"));
		frgs.add(new CommonListFragment("7",1,"StoryAnimalsFragment"));
		frgs.add(new StorySearchFragment());
		
		mVpMusic.setAdapter(new GeneralFragmentPagerAdapter(
				getSupportFragmentManager(), frgs));
		mVpMusic.addOnPageChangeListener(this);
	}

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.activity_story_home);
		initView();
		initPanelView();
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
		
	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {
		
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
		for(Fragment frg : frgs){
			if(frg instanceof CommonListFragment){
				CommonListFragment fg = (CommonListFragment) frg;
				fg.setTvVisible(View.GONE);
			}
		}
	}

	@Override
	public void startLoadMusic() {
		for(Fragment frg : frgs){
			if(frg instanceof CommonListFragment){
				CommonListFragment fg = (CommonListFragment) frg;
				fg.setTvVisible(View.VISIBLE);
			}
		}
	}
}
