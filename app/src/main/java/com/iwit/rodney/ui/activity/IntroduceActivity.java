package com.iwit.rodney.ui.activity;

import android.app.Activity;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;

import com.iwit.rodney.R;

public class IntroduceActivity extends Activity {
	private ListView lvShow;
	private int[] pics = new int[24];
	private Context ctx;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_introduce);
		ctx = this;
		lvShow = (ListView) findViewById(R.id.lv_pic);
		findViewById(R.id.iv_back).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});
		initData();
		bindAdapter();
	}

	private void bindAdapter() {
		lvShow.setAdapter(new ShowAdapter());
	}

	private void initData() {
		for (int i = 1; i < 25; i++) {
			pics[i - 1] = getResources().getIdentifier("ic_introduce_" + i,
					"drawable", getPackageName());
		}
	}

	class ShowAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return pics.length;
		}

		@Override
		public Object getItem(int position) {
			return pics[position];
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			final ViewHolder holder;
			if (convertView == null) {
				convertView = View.inflate(ctx, R.layout.item_show, null);
				holder = new ViewHolder(convertView);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			int drawable = pics[position];
			//holder.mIvShow.setImageBitmap(BitmapFactory.decodeResource(getResources(), drawable));
			holder.mIvShow.setBackgroundDrawable(getResources().getDrawable(drawable));
			return convertView;
		}
	}
	class ViewHolder {
		ImageView mIvShow;
		ViewHolder(View view) {
			mIvShow = (ImageView)view.findViewById(R.id.show);
		}
	}
}
