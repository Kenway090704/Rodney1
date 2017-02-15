package com.iwit.rodney.ui.activity;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RadioGroup;

import com.iwit.rodney.R;
import com.iwit.rodney.ui.adapter.GeneralFragmentPagerAdapter;
import com.iwit.rodney.ui.fragment.BookFragment;
import com.iwit.rodney.ui.fragment.BookStoreFragment;
import com.iwit.rodney.ui.fragment.CommonListFragment;
import com.iwit.rodney.ui.fragment.RecordListFragment;
import com.iwit.rodney.ui.fragment.RecyclerViewFragment;
import com.iwit.rodney.ui.view.Navigation;
import com.iwit.rodney.ui.view.Navigation.OnNavigationChangeListener;

public class LibraryActivity extends BaseActivity implements
		OnPageChangeListener, OnNavigationChangeListener {
	private Navigation mNavi;
	private ViewPager mVp;
	List<Fragment> frgs;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_library);
		initPanelView();
		findViewById(R.id.ll_back).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		
		mNavi = new Navigation((RadioGroup) findViewById(R.id.rg), 2,
				new int[] { R.id.radio_record, R.id.radio_mine }, new String[] {
						"故事书", "书城" }, this);
		mVp = (ViewPager) findViewById(R.id.vp_creation);
		mVp.addOnPageChangeListener(this);

		frgs = new ArrayList<Fragment>();
		frgs.add(new BookFragment());
		frgs.add(new BookStoreFragment());
		mVp.setAdapter(new GeneralFragmentPagerAdapter(
				getSupportFragmentManager(), frgs));
	}

	@Override
	public void onPageScrollStateChanged(int arg0) {
	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {
	}

	@Override
	public void onPageSelected(int position) {
		mNavi.checkItem(position);
	}

	@Override
	public void onNavigationChanged(int itemIndex) {
		mVp.setCurrentItem(itemIndex, false);
		RecordListFragment.closeInputKeyboard(this);
	}

	@Override
	public void updateMusic(int position) {
	}

	@Override
	public void updateDuration() {
	}

	@Override
	public void startLoadMusic() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		switch (keyCode) {
		case KeyEvent.KEYCODE_BACK:
			Log.e("back", "back");
			for(Fragment frg : frgs){
				if(frg instanceof BookFragment){
					RecyclerViewFragment fg = (RecyclerViewFragment) frg;
					fg.deleteCancel();
					return true;
				}
			}
		}
		return super.onKeyDown(keyCode, event);
	}
	
}
