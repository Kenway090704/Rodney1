package com.iwit.rodney.ui.adapter;

import java.util.ArrayList;

import com.iwit.rodney.App;

import android.content.Intent;
import android.net.Uri;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.View.OnClickListener;

public class IntroduceAdapter extends PagerAdapter {

	// 界面列表
	private ArrayList<View> views;

	public IntroduceAdapter(ArrayList<View> views) {
		this.views = views;
		for (int i = 0; i < views.size(); i++) {
			views.get(i).setTag(i);
		}
	}

	@Override
	public int getCount() {
		return 1000;// Integer.MAX_VALUE;
	}

	@Override
	public boolean isViewFromObject(View arg0, Object arg1) {
		return arg0 == arg1;
	}

	@Override
	public void destroyItem(View container, int position, Object object) {

	}

	/**
	 * 载入图片进去，用当前的position 除以 图片数组长度取余数
	 */
	@Override
	public Object instantiateItem(View container, final int position) {
		((ViewPager) container).removeView(views.get(position % views.size()));
		((ViewPager) container).addView(views.get(position % views.size()));
		View view = views.get(position % views.size());
		view.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Uri uri;
				if(position == 1){
					uri = Uri
					.parse("https://detail.tmall.com/item.htm?id=531276088353");
				}else{
					uri = Uri
							.parse("https://detail.tmall.com/item.htm?id=524450450852");
				}
				Intent intent = new Intent(Intent.ACTION_VIEW, uri);
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				App.ctx.startActivity(intent);
			}
		});
		return view;
	}

}