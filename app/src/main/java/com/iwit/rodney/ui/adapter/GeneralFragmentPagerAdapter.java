package com.iwit.rodney.ui.adapter;

import java.util.List;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class GeneralFragmentPagerAdapter extends FragmentPagerAdapter {
	private List<Fragment> frgs;

	public GeneralFragmentPagerAdapter(FragmentManager fm, List<Fragment> frgs) {
		super(fm);
		this.frgs = frgs;
	}

	@Override
	public Fragment getItem(int paramInt) {
		return frgs == null ? null : frgs.get(paramInt);
	}

	@Override
	public int getCount() {
		return frgs == null ? null : frgs.size();
	}

}
