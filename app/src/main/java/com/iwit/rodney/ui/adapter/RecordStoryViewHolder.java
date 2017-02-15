package com.iwit.rodney.ui.adapter;

import com.iwit.rodney.R;

import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class RecordStoryViewHolder extends ViewHolder {
	ImageView ivTitle;
	TextView tvTitle;
	ImageView ivMic;
	View view;

	public RecordStoryViewHolder(View v) {
		super(v);
		view = v;
		ivTitle = (ImageView) v.findViewById(R.id.iv_item_story_title);
		tvTitle = (TextView) v.findViewById(R.id.tv_item_story_title);
		ivMic = (ImageView) v.findViewById(R.id.iv_item_story_mic);
	}

}
