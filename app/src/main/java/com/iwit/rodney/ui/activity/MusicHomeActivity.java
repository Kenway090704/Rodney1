package com.iwit.rodney.ui.activity;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.iwit.rodney.App;
import com.iwit.rodney.R;
import com.iwit.rodney.ui.adapter.GeneralFragmentPagerAdapter;
import com.iwit.rodney.ui.fragment.CommonListFragment;
import com.iwit.rodney.ui.fragment.LazyFragment;
import com.iwit.rodney.ui.fragment.SongSearchFragment;
import com.iwit.rodney.ui.view.Navigation;
import com.iwit.rodney.ui.view.Navigation.OnNavigationChangeListener;

/**
 * 
 * @author qh
 * @date 2016-03-17
 * @desc1 分类请求网络音乐 暂时写死；
 * @desc2 将请求的音乐保存到数据库中
 * @desc3 界面展示网络音乐，对比下载库中的状态，分fragment
 * @desc4 下载网络音乐
 * @desc5 保存音乐状态
 * @desc6 播放展示
 */
public class MusicHomeActivity extends BaseActivity implements
		OnNavigationChangeListener , OnPageChangeListener,OnClickListener{
	private Navigation mNavi;
	private ViewPager mVpMusic;
	private static final String TAG = "MusicHomeActivity";
	private List<Fragment> frgs = new ArrayList<Fragment>();
	private void initView() {
		mNavi = new Navigation((RadioGroup) findViewById(R.id.rg_titles), 2,
				new int[] { R.id.radio_music_zh, 
						R.id.radio_music_search }, new String[] { "中文儿歌","儿歌搜索" }, this);
		frgs.add(new CommonListFragment("1",1,"SongChineseFragment"));
	//	frgs.add(new CommonListFragment("1",1,"SongEnglishFragment"));
		frgs.add(new SongSearchFragment());
		mVpMusic = (ViewPager) findViewById(R.id.vp_music);
		mVpMusic.setAdapter(new GeneralFragmentPagerAdapter(
				getSupportFragmentManager(), frgs));
		mVpMusic.addOnPageChangeListener(this);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_music_home);
		initView();
		initPanelView();
	}
	
	
	@Override
	public void onNavigationChanged(int itemIndex) {
		mVpMusic.setCurrentItem(itemIndex, false);
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
	public void updateMusic(int position) {
		Log.v(TAG, "position::"+position);
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
//		Toast.makeText(App.ctx, "缓冲结束，开始播放音乐.", 1).show();
		for(Fragment frg : frgs){
			if(frg instanceof CommonListFragment){
				CommonListFragment fg = (CommonListFragment) frg;
				fg.setTvVisible(View.GONE);
			}
		}
	}

	@Override
	public void startLoadMusic() {
//		Toast.makeText(App.ctx, "开始缓冲音乐.", 1).show();
		for(Fragment frg : frgs){
			if(frg instanceof CommonListFragment){
				CommonListFragment fg = (CommonListFragment) frg;
				fg.setTvVisible(View.VISIBLE);
			}
		}
	}
	
}
