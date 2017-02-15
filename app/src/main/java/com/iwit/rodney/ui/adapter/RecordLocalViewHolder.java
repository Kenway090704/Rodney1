package com.iwit.rodney.ui.adapter;

import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.iwit.rodney.R;

public class RecordLocalViewHolder extends ViewHolder {
	// EditText et;
	// RelativeLayout rlLayout;
	// RelativeLayout rlDelete;
	// View cover;
	// ImageView iv;

	TextView tvTitle;
	ImageView ivTitle;
	View view;

	public RecordLocalViewHolder(View v) {
		super(v);
		// et = (EditText) v.findViewById(R.id.et_my_story_title);
		// rlLayout = (RelativeLayout) v.findViewById(R.id.rl_my_story);
		// rlDelete = (RelativeLayout) v.findViewById(R.id.rl_my_story_delete);
		// cover = v.findViewById(R.id.v_my_story_cover);
		// iv = (ImageView) v.findViewById(R.id.iv_my_story_frame);
		// et.setTag(R.id.tag_key_viewholder, this);
		// et.getPaint().setFakeBoldText(true);
		view = v;
		tvTitle = (TextView) v.findViewById(R.id.tv_item_story_title);
		ivTitle = (ImageView) v.findViewById(R.id.iv_item_story_title);
		v.findViewById(R.id.iv_item_story_mic).setVisibility(View.GONE);
		v.findViewById(R.id.iv_item_story_play).setVisibility(View.VISIBLE);
	}

	// public void setStoryName(String name) {
	// et.setText(name);
	// }

	// public void setViewDeleting(boolean b) {
	// int vis = b ? View.VISIBLE : View.GONE;
	// if (rlDelete.getVisibility() != vis) {
	// rlDelete.setVisibility(vis);
	// cover.setVisibility(vis);
	// }
	// }
}
