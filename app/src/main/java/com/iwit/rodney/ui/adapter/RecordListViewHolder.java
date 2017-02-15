package com.iwit.rodney.ui.adapter;

import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.iwit.rodney.R;

public class RecordListViewHolder extends ViewHolder {
	RelativeLayout rl;
	View cover;
	TextView tv;
	private ImageView ivPic;

	public RecordListViewHolder(View view) {
		super(view);
		rl = (RelativeLayout) view.findViewById(R.id.rl_record_story);
		cover = view.findViewById(R.id.v_record_story_cover);
		tv = (TextView) view.findViewById(R.id.tv_record_story_title);
		tv.getPaint().setFakeBoldText(true);
		ivPic = (ImageView) view.findViewById(R.id.iv_story);
	}

	public void setViewDownloaded(boolean value) {
		cover.setVisibility(value ? View.GONE : View.VISIBLE);
	}

	public void setTitle(String title) {
		tv.setText(title);
	}

	public ImageView getImagePic() {
		return ivPic;
	}

}
